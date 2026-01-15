package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.Traces;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryNodeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.exception.BizException;
import com.qcl.service.EsNodeService;
import com.qcl.utils.IndexNameResolver;
import com.qcl.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsNodeServiceImpl implements EsNodeService {

    private final ElasticsearchClient elasticsearchClient;

    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Override
    public List<TimeBucketResult> qpsByMinute(QueryNodeParam queryNodeParam, UserDTO user) {

        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }

        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryNodeParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);

            SearchResponse<Nodes> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("per_minute", a -> a
                                    .dateHistogram(d -> d
                                            .field("metric.start_time")
                                            .timeZone("Asia/Shanghai")
                                            .calendarInterval(CalendarInterval.Minute)
                                            .format("yyyy-MM-dd HH:mm")
                                            .minDocCount(0)
                                    )
                            ),
                    Nodes.class
            );

            // 3. 解析聚合结果
            List<TimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("per_minute")) {
                DateHistogramAggregate dateHistogram =
                        response.aggregations().get("per_minute").dateHistogram();

                for (DateHistogramBucket bucket :
                        dateHistogram.buckets().array()) {
                    double qps = new BigDecimal(bucket.docCount())
                            .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP)
                            .doubleValue();
                    result.add(new TimeBucketResult(bucket.key(), qps));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch aggregation query", e);
        }
    }

    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Override
    public List<TimeBucketResult> errorRateByMinute(QueryNodeParam queryNodeParam, UserDTO user) {

        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }

        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryNodeParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);
            SearchResponse<Nodes> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("per_minute", a -> a
                                    .dateHistogram(d -> d
                                            .field("metric.start_time")
                                            .calendarInterval(CalendarInterval.Minute)
                                            .timeZone("Asia/Shanghai")
                                    )
                                    .aggregations("error_requests", aa -> aa
                                            .filter(f -> f
                                                    .bool(b -> b
                                                            .mustNot(mn -> mn
                                                                    .term(t -> t
                                                                            .field("status_code")
                                                                            .value(200)
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                                    .aggregations("error_ratio", aa -> aa
                                            .bucketScript(bs -> bs
                                                    .bucketsPath(bp -> bp
                                                            .dict(Map.of(
                                                                    "total", "_count",
                                                                    "errors", "error_requests._count"
                                                            ))
                                                    )
                                                    .script(sc -> sc
                                                            .source("params.errors / params.total") //异常比例 eg:0.25
                                                    )
                                            )
                                    )
                            ),
                    Nodes.class
            );

            // 3. 解析聚合结果
            List<TimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("per_minute")) {
                DateHistogramAggregate dateHistogram = response.aggregations().get("per_minute").dateHistogram();

                for (DateHistogramBucket bucket : dateHistogram.buckets().array()) {
                    Long timeKey = bucket.key();
                    Double errorRatio = 0.0;

                    // 获取错误率 保留四位小数
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("error_ratio")) {
                        Aggregate aggregate = bucket.aggregations().get("error_ratio");
                        if (aggregate.isSimpleValue()) {
                            BigDecimal bd = new BigDecimal(aggregate.simpleValue().value());
                            errorRatio = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
                        }
                    }

                    result.add(new TimeBucketResult(timeKey, errorRatio));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch latency aggregation query", e);
        }
    }

    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Override
    public List<TimeBucketResult> latencyStatsByMinute(QueryNodeParam queryNodeParam, UserDTO user) {

        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }

        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryNodeParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);
            SearchResponse<Nodes> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("per_minute", a -> a
                                    .dateHistogram(d -> d
                                            .field("metric.start_time")  // 注意：使用 start_time 而不是 begin_time
                                            .timeZone("Asia/Shanghai")
                                            .calendarInterval(CalendarInterval.Minute)
                                            .format("yyyy-MM-dd HH:mm")
                                    )
                                    .aggregations("avg_duration",aa -> aa
                                            .avg( avg -> avg.field("metric.duration"))
                                    )
                            ),
                    Nodes.class
            );

            // 3. 解析聚合结果
            List<TimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("per_minute")) {
                DateHistogramAggregate dateHistogram =
                        response.aggregations().get("per_minute").dateHistogram();

                for (DateHistogramBucket bucket : dateHistogram.buckets().array()) {
                    Long timeKey = bucket.key();
                    Double avgDuration = 0.0;

                    // 解析平均延迟  保留四位小数
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("avg_duration")) {
                        BigDecimal bd = new BigDecimal(bucket.aggregations().get("avg_duration").avg().value());
                        avgDuration = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
                    }

                    result.add(new TimeBucketResult(
                            timeKey,
                            avgDuration
                    ));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch latency aggregation query", e);
        }
    }

    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2))
    @Override
    public List<StatusTimeBucketResult> getStatusCountByMinute(QueryNodeParam queryNodeParam, UserDTO user){

        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }


        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryNodeParam);

            // 2. 构建嵌套聚合查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);
            SearchResponse<Nodes> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("status_groups", a -> a
                                    .terms(t -> t
                                            .field("status_code.keyword") //
                                            .size(10)
                                    )
                                    .aggregations("per_minute",aa -> aa
                                            .dateHistogram( d -> d
                                                    .field("metric.start_time")
                                                    .timeZone("Asia/Shanghai")
                                                    .calendarInterval(CalendarInterval.Minute)
                                                    .format("yyyy-MM-dd HH:mm")
                                            )
                                    )
                            ),
                    Nodes.class
            );

            // 3. 解析聚合结果
            List<StatusTimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("status_groups")) {
                StringTermsAggregate statusGroups =
                        response.aggregations().get("status_groups").sterms();

                for (StringTermsBucket bucket :
                        statusGroups.buckets().array()) {

                    String statusCode = bucket.key().stringValue();
                    List<TimeBucketResult> timeBuckets = new ArrayList<>();

                    // 解析时间桶数据
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("per_minute")) {
                        DateHistogramAggregate dateHistogram =
                                bucket.aggregations().get("per_minute").dateHistogram();

                        for (DateHistogramBucket timeBucket : dateHistogram.buckets().array()) {
                            timeBuckets.add(new TimeBucketResult(timeBucket.key(), timeBucket.docCount()));
                        }
                    }

                    result.add(new StatusTimeBucketResult(statusCode, timeBuckets));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch nested aggregation query", e);
        }
    }




    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Override
    public PageResult<Nodes> queryByPage(QueryNodeParam queryNodeParam, UserDTO user) {
        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }

        try {
            Query finalQuery = buildQuery(queryNodeParam);

            // 4.构建动态排序条件
            List<SortOptions> sortOptions = new ArrayList<>();

            if (queryNodeParam.getSortBy() != null && !queryNodeParam.getSortBy().isEmpty()) {
                SortOrder order =
                        "desc".equalsIgnoreCase(queryNodeParam.getSortOrder()) ?
                                SortOrder.Desc :
                                SortOrder.Asc;

                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field(queryNodeParam.getSortBy())
                                .order(order)
                        )
                ));
            } else {
                // 默认排序
                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field("metric.start_time")
                                .order(SortOrder.Desc)
                        )
                ));
            }

            //临时深分页 TODO
            int pageNo = queryNodeParam.getPageNum() != null ? queryNodeParam.getPageNum() : 1;
            int pageSize = queryNodeParam.getPageSize() != null ? queryNodeParam.getPageSize() : 2;
            int from = (pageNo - 1) * pageSize;

            // 5. 执行查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);
            SearchResponse<Nodes> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(finalQuery)
                            .from(from)
                            .size(pageSize)
                            .sort(sortOptions)
                            .source(src -> src
                                    .filter(f -> f
                                            //k8s_tag目前没有数据，因此暂不在前端页面展示
                                            .excludes("tag.k8s_tag")
                                    )
                            )
                            ,
                    Nodes.class
            );

            // 6. 提取结果
            List<Nodes> nodes =  response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            // 获取总记录数
            long total = Optional.ofNullable(response.hits().total())
                    .map(totalHits -> totalHits.value())
                    .orElse(0L);

            PageResult<Nodes> result = new PageResult<>(
                    nodes,
                    pageNo,
                    pageSize,
                    total
            );
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }

    @Retryable(
            retryFor = {SocketException.class, ConnectException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Override
    /**
     * 按端点和协议分组统计信息
     * @param queryTracesParam 筛选条件
     * @return 端点和协议分组统计结果
     */
    public List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryNodeParam queryNodeParam, UserDTO user) {

        if(queryNodeParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }
        if (queryNodeParam.getEndTime() == null ||
                queryNodeParam.getEndTime() <= queryNodeParam.getStartTime()){
            throw new BizException("endTime is required or endTime must be greater than startTime");
        }
        if (queryNodeParam.getNodeId() == null){
            throw new BizException("nodeId is required");
        }


        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryNodeParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryNodeParam.getUserId(), "nodes");
            log.info("index = {} userId={} userInfo={}",index, queryNodeParam.getUserId(),user);
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("group_by_endpoint_protocol", a -> a
                                    .composite(c -> c
                                            .sources(List.of(
                                                    // 按 endpoint 分组
                                                    Map.of("endpoint", CompositeAggregationSource.of(cs -> cs
                                                            .terms(t -> t
                                                                    .field("tag.ebpf_tag.endpoint.keyword")
                                                            )
                                                    )),
                                                    // 按 protocol 分组
                                                    Map.of("protocol", CompositeAggregationSource.of(cs -> cs
                                                            .terms(t -> t
                                                                    .field("tag.ebpf_tag.protocol.keyword")
                                                            )
                                                    ))
                                            ))
                                            .size(1000) // 限定数据的数量，预防es崩溃
                                    )
                                    .aggregations("total_requests", aa -> aa
                                            .valueCount(vc -> vc
                                                    .field("metric.duration")
                                            )
                                    )
                                    .aggregations("time_range", aa -> aa
                                            .stats(st -> st
                                                    .field("metric.start_time")
                                            )
                                    )
                                    .aggregations("request_rate", aa -> aa
                                            .bucketScript(bs -> bs
                                                    .bucketsPath(bp -> bp
                                                            .dict(Map.of(
                                                                    "count", "total_requests",
                                                                    "min_time", "time_range.min",
                                                                    "max_time", "time_range.max"
                                                            ))
                                                    )
                                                    .script(sc -> sc
                                                            .source("(params.max_time - params.min_time) > 0 ? params.count / ((params.max_time - params.min_time)/1000) : 0")
                                                    )
                                            )
                                    )
                                    .aggregations("avg_duration", aa -> aa
                                            .avg(avg -> avg
                                                    .field("metric.duration")
                                            )
                                    )
                                    .aggregations("p75_duration", aa -> aa
                                            .percentiles(p -> p
                                                    .field("metric.duration")
                                                    .percents(75.0)
                                            )
                                    )
                                    .aggregations("p99_duration", aa -> aa
                                            .percentiles(p -> p
                                                    .field("metric.duration")
                                                    .percents(99.0)
                                            )
                                    )
                                    .aggregations("error_requests", aa -> aa
                                            .filter(f -> f
                                                    .bool(b -> b
                                                            .mustNot(mn -> mn
                                                                    .term(t -> t
                                                                            .field("status_code.keyword")
                                                                            .value("200")
                                                                    )
                                                            )
                                                    )
                                            )
                                            .aggregations("error_count", aaa -> aaa
                                                    .valueCount(vc -> vc
                                                            .field("status_code.keyword")
                                                    )
                                            )
                                    )
                                    .aggregations("error_rate", aa -> aa
                                            .bucketScript(bs -> bs
                                                    .bucketsPath(bp -> bp
                                                            .dict(Map.of(
                                                                    "total", "total_requests",
                                                                    "errors", "error_requests>error_count"
                                                            ))
                                                    )
                                                    .script(sc -> sc
                                                            .source("params.total > 0 ? params.errors / params.total * 100 : 0")
                                                    )
                                            )
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<EndpointProtocolStatsResult> result = new ArrayList<>();

            if (response.aggregations() != null &&
                    response.aggregations().containsKey("group_by_endpoint_protocol")) {

                CompositeAggregate compositeAgg = response.aggregations().get("group_by_endpoint_protocol").composite();

                for (CompositeBucket bucket : compositeAgg.buckets().array()) {
                    // 获取 endpoint 和 protocol
                    String endpoint = "";
                    String protocol = "";

                    if (bucket.key().containsKey("endpoint")) {
                        endpoint = String.valueOf(bucket.key().get("endpoint")._get());
                    }

                    if (bucket.key().containsKey("protocol")) {
                        protocol = String.valueOf(bucket.key().get("protocol")._get());
                    }

                    // 初始化各项指标
                    Double totalRequests = 0.0;
                    String minTime = null;
                    String maxTime = null;
                    Double requestRate = 0.0;
                    Double avgDuration = 0.0;
                    Object p75Duration = 0.0;
                    Object p99Duration = 0.0;
                    double errorCount = 0L;
                    Double errorRate = 0.0;

                    // 获取总请求数
                    if (bucket.aggregations().containsKey("total_requests")) {
                        totalRequests = bucket.aggregations().get("total_requests").valueCount().value();
                    }

                    // 获取时间范围统计
                    if (bucket.aggregations().containsKey("time_range")) {
                        StatsAggregate statsAgg = bucket.aggregations().get("time_range").stats();
                        minTime = statsAgg.minAsString();
                        maxTime = statsAgg.maxAsString();
                    }

                    // 获取请求率
                    if (bucket.aggregations().containsKey("request_rate")) {
                        Aggregate aggregate = bucket.aggregations().get("request_rate");
                        if (aggregate.isSimpleValue()) {
                            BigDecimal bd = new BigDecimal(aggregate.simpleValue().value());
                            requestRate = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
                        }
                    }

                    // 获取平均响应时延
                    if (bucket.aggregations().containsKey("avg_duration")) {
                        BigDecimal bd = new BigDecimal(bucket.aggregations().get("avg_duration").avg().value());
                        avgDuration = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
                    }

                    // 获取 75% 分位响应时延
                    if (bucket.aggregations().containsKey("p75_duration")) {
                        Aggregate percentilesAgg = bucket.aggregations().get("p75_duration");
                        if (percentilesAgg !=null) {
                            if (percentilesAgg.isTdigestPercentiles()) {
                                var tdigestPercentiles = percentilesAgg.tdigestPercentiles();
                                if (tdigestPercentiles.values() != null) {
                                    // 获取 75th 百分位值
                                    p75Duration = tdigestPercentiles.values().keyed().get("75.0");
                                }
                            }
                            // 原来的 PercentilesBucket 类型
                            else if (percentilesAgg.isPercentilesBucket()) {
                                var percentiles = percentilesAgg.percentilesBucket();
                                if (percentiles.values() != null) {
                                    p75Duration = percentiles.values()._get();
                                }
                            }
                        }
                    }


                    // 获取 99% 分位响应时延
                    if (bucket.aggregations().containsKey("p99_duration")) {
                        var percentilesAgg = bucket.aggregations().get("p99_duration");
                        if (percentilesAgg !=null) {
                            if (percentilesAgg.isTdigestPercentiles()) {
                                var tdigestPercentiles = percentilesAgg.tdigestPercentiles();
                                if (tdigestPercentiles.values() != null) {
                                    // 获取 90th 百分位值
                                    p99Duration = tdigestPercentiles.values().keyed().get("99.0");
                                }
                            }
                            // 原来的 PercentilesBucket 类型
                            else if (percentilesAgg.isPercentilesBucket()) {
                                var percentiles = percentilesAgg.percentilesBucket();
                                if (percentiles.values() != null) {
                                    p99Duration = percentiles.values()._get();
                                }
                            }
                        }
                    }

                    // 获取错误请求数
                    if (bucket.aggregations().containsKey("error_requests")) {
                        FilterAggregate filterAgg = bucket.aggregations().get("error_requests").filter();
                        if (filterAgg.aggregations() != null &&
                                filterAgg.aggregations().containsKey("error_count")) {
                            errorCount = filterAgg.aggregations().get("error_count").valueCount().value();
                        }
                    }

                    // 获取错误率
                    if (bucket.aggregations().containsKey("error_rate")) {
                        Aggregate aggregate = bucket.aggregations().get("error_rate");
                        if (aggregate.isSimpleValue()) {
                            BigDecimal bd = new BigDecimal(aggregate.simpleValue().value());
                            errorRate = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
                        }
                    }

                    // 创建结果对象
                    result.add(new EndpointProtocolStatsResult(
                            endpoint, protocol, totalRequests, minTime, maxTime,
                            requestRate,
                            avgDuration,
                            p75Duration,
                            p99Duration,
                            errorCount,
                            errorRate));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch endpoint-protocol stats aggregation query", e);//异常处理的方式
        }
    }

    private Query buildQuery(QueryNodeParam queryNodeParam) {
        // 构建过滤条件
        List<Query> filterConditions = new ArrayList<>();
        //容器ID
        if (queryNodeParam.getNodeId() != null) {
            filterConditions.add(Query.of(q -> q
                    .match(m -> m
                            .field("nodeId")
                            .query(queryNodeParam.getNodeId())
                    )
            ));
        }

        //容器名称 containerName
        if (queryNodeParam.getContainerName() != null && !queryNodeParam.getContainerName().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .match(m -> m
                            .field("tag.docker_tag.container_name.keyword")
                            .query(queryNodeParam.getContainerName())
                    )
            ));
        }


        // endpoints 条件
        if (queryNodeParam.getEndpoints() != null && !queryNodeParam.getEndpoints().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("tag.ebpf_tag.endpoint.keyword")
                            .terms(t2 -> t2.value(
                                    queryNodeParam.getEndpoints().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        } //end

        // protocols 条件
        if (queryNodeParam.getProtocols() != null && !queryNodeParam.getProtocols().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("tag.ebpf_tag.protocol.keyword")
                            .terms(t2 -> t2.value(
                                    queryNodeParam.getProtocols().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // status codes 条件
        if (queryNodeParam.getStatusCodes() != null && !queryNodeParam.getStatusCodes().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("status_code.keyword")
                            .terms(t2 -> t2.value(
                                    queryNodeParam.getStatusCodes().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }//end

        // start_time 范围条件
        if (queryNodeParam.getStartTime() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.start_time")
                                    .gte(queryNodeParam.getStartTime().toString())
                            )
                    )
            ));
        }
        //end_time 范围条件
        if (queryNodeParam.getEndTime() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.start_time")
                                    .lte(queryNodeParam.getEndTime().toString())
                            )
                    )
            ));
        }

        // e2e_duration 范围查询条件
        if (queryNodeParam.getMinE2eDuration() != null && queryNodeParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryNodeParam.getMinE2eDuration().toString())
                                    .lte(queryNodeParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryNodeParam.getMinE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryNodeParam.getMinE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryNodeParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .lte(queryNodeParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        }

        // 构建最终查询
        return Query.of(q -> q
                .bool(b -> b
                        .filter(filterConditions)
                )
        );
    }


}
