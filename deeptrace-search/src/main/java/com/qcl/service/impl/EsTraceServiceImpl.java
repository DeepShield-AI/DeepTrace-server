package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.entity.Traces;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.*;
import com.qcl.service.EsTraceService;
import com.qcl.utils.IndexNameResolver;
import com.qcl.vo.PageResult;
import com.qcl.utils.EsAggregationHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.ScrollResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EsTraceServiceImpl implements EsTraceService {
    private final ElasticsearchClient elasticsearchClient;
    // 添加 ObjectMapper 用于序列化查询
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EsTraceServiceImpl.class);
    private final EsAggregationHelper esAggregationHelper;


    /**
     * 请求数时序统计
     *
     * @param queryTracesParam 筛选条件
     * @param user
     */
    public List<TimeBucketResult> getTraceCountByMinute(QueryTracesParam queryTracesParam, UserDTO user) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryTracesParam.getUserId(), "traces");
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("per_minute", a -> a
                                    .dateHistogram(d -> d
                                            .field("start_time")
                                            .timeZone("Asia/Shanghai")
                                            .calendarInterval(CalendarInterval.Minute)
                                            .format("yyyy-MM-dd HH:mm")
                                            .minDocCount(0)
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<TimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("per_minute")) {
                DateHistogramAggregate dateHistogram =
                        response.aggregations().get("per_minute").dateHistogram();

                for (DateHistogramBucket bucket :
                        dateHistogram.buckets().array()) {
                    result.add(new TimeBucketResult(bucket.key(), bucket.docCount()));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch aggregation query", e);
        }
    }

    /**
     * 错误数量时序统计，按状态码分组并统计每分钟请求数
     *
     * @param queryTracesParam 筛选条件
     * @param user
     * @return 状态码分组的时序统计数据
     */
    public List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam queryTracesParam, UserDTO user) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建嵌套聚合查询
            String index = IndexNameResolver.generate(user, queryTracesParam.getUserId(), "traces");
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("status_groups", a -> a
                                    .terms(t -> t
                                            .field("status_code.keyword") // 按 status_code 分组
                                            .size(10)
                                    )
                                    .aggregations("per_minute",aa -> aa
                                            .dateHistogram( d -> d
                                                    .field("start_time")
                                                    .timeZone("Asia/Shanghai")
                                                    .calendarInterval(CalendarInterval.Minute)
                                                    .format("yyyy-MM-dd HH:mm")
                                            )
                                    )
                            ),
                    Traces.class
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
//                    result.add(new TimeBucketCountResult(statusCode, timeBuckets));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch nested aggregation query", e);
        }
    }

    /**
     * 获取每分钟的延迟统计信息（平均延迟、75th、90th、99th 百分位延迟）
     *
     * @param queryTracesParam 筛选条件
     * @param user
     * @return 延迟统计的时序数据
     */
    public List<LatencyTimeBucketResult> getLatencyStatsByMinute(QueryTracesParam queryTracesParam, UserDTO user) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建聚合查询
            String index = IndexNameResolver.generate(user, queryTracesParam.getUserId(), "traces");
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("per_minute", a -> a
                                    .dateHistogram(d -> d
                                            .field("start_time")  // 注意：使用 start_time 而不是 begin_time
                                            .timeZone("Asia/Shanghai")
                                            .calendarInterval(CalendarInterval.Minute)
                                            .format("yyyy-MM-dd HH:mm")
                                    )
                                    .aggregations("avg_duration",aa -> aa
                                            .avg( avg -> avg.field("e2e_duration"))
                                    )
                                    .aggregations("p75_duration",aa -> aa
                                            .percentiles( p -> p
                                                    .field("e2e_duration")
                                                    .percents(75.0)
                                            )
                                    )
                                    .aggregations("p90_duration", aa -> aa
                                            .percentiles(p -> p
                                                    .field("e2e_duration")
                                                    .percents(90.0)
                                            )
                                    )
                                    .aggregations("p99_duration",aa -> aa
                                            .percentiles( p -> p
                                                    .field("e2e_duration")
                                                    .percents(99.0)
                                            )
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<LatencyTimeBucketResult> result = new ArrayList<>();

            if (response.aggregations() != null && response.aggregations().containsKey("per_minute")) {
                DateHistogramAggregate dateHistogram =
                        response.aggregations().get("per_minute").dateHistogram();

                for (DateHistogramBucket bucket : dateHistogram.buckets().array()) {
                    Long timeKey = bucket.key();
                    Double avgDuration = 0.0;
                    Object p75Duration = 0.0;
                    Object p90Duration = 0.0;
                    Object p99Duration = 0.0;

                    // 解析平均延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("avg_duration")) {
                        avgDuration = bucket.aggregations().get("avg_duration").avg().value();
                    }

                    // 解析 75th 百分位延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("p75_duration")) {
                        Aggregate percentilesAgg = bucket.aggregations().get("p75_duration");
                        if (percentilesAgg !=null) {
                            if (percentilesAgg.isTdigestPercentiles()) {
                                var tdigestPercentiles = percentilesAgg.tdigestPercentiles();
                                if (tdigestPercentiles.values() != null) {
                                    // 获取 75th 百分位值
                                    p75Duration = tdigestPercentiles.values().keyed().get("75.0");
                                }
                            }
                            // 如果是原来的 PercentilesBucket 类型
                            else if (percentilesAgg.isPercentilesBucket()) {
                                var percentiles = percentilesAgg.percentilesBucket();
                                if (percentiles.values() != null) {
                                    p75Duration = percentiles.values()._get();
                                }
                            }
                        }
                    }


                    // 解析 90th 百分位延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("p90_duration")) {
                        var percentilesAgg = bucket.aggregations().get("p90_duration");
                        if (percentilesAgg !=null) {
                            if (percentilesAgg.isTdigestPercentiles()) {
                                var tdigestPercentiles = percentilesAgg.tdigestPercentiles();
                                if (tdigestPercentiles.values() != null) {
                                    // 获取 90th 百分位值
                                    p90Duration = tdigestPercentiles.values().keyed().get("90.0");
                                }
                            }
                            // 如果是原来的 PercentilesBucket 类型
                            else if (percentilesAgg.isPercentilesBucket()) {
                                var percentiles = percentilesAgg.percentilesBucket();
                                if (percentiles.values() != null) {
                                    p90Duration = percentiles.values()._get();
                                }
                            }
                        }
                    }

                    // 解析 99th 百分位延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("p99_duration")) {
                        var percentilesAgg = bucket.aggregations().get("p99_duration");
                        if (percentilesAgg !=null) {
                            if (percentilesAgg.isTdigestPercentiles()) {
                                var tdigestPercentiles = percentilesAgg.tdigestPercentiles();
                                if (tdigestPercentiles.values() != null) {
                                    // 获取 99th 百分位值
                                    p99Duration = tdigestPercentiles.values().keyed().get("99.0");
                                }
                            }
                            // 如果是原来的 PercentilesBucket 类型
                            else if (percentilesAgg.isPercentilesBucket()) {
                                var percentiles = percentilesAgg.percentilesBucket();
                                if (percentiles.values() != null) {
                                    p99Duration = percentiles.values()._get();
                                }
                            }
                        }
                    }

                    result.add(new LatencyTimeBucketResult(
                            timeKey,
                            avgDuration,
                            p75Duration,
                            p90Duration,
                            p99Duration
                    ));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch latency aggregation query", e);
        }
    }

    /**
     * 分页查询
     *
     * @param queryTracesParam 查询参数
     * @param user
     * @return 分页结果对象，包含 Trace 列表和所有筛选项（endpoint、protocol、status_code）
     */
    @Override
    public PageResult<Traces> queryByPageResult(QueryTracesParam queryTracesParam, UserDTO user) {
        try {
            // 1. 构建查询条件
            Query finalQuery = buildQuery(queryTracesParam);

            // 2.处理分页参数
            int pageNum = queryTracesParam.getPageNum() != null ? queryTracesParam.getPageNum() : 0;
            int pageSize = queryTracesParam.getPageSize() != null ? queryTracesParam.getPageSize() : 10;
            int from = pageNum * pageSize;   // 默认从0开始

            // 2.执行查询
            String index = IndexNameResolver.generate(user, queryTracesParam.getUserId(), "traces");
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(finalQuery)
                            .from(from)
                            .size(pageSize)  // 使用动态pageSize而非硬编码50
                            .sort(so -> so
                                    .field(f -> f
                                            .field("start_time")
                                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                                    )
                            )
                            .source(src -> src
                                            .filter(f -> f
//                                            .excludes("topology", "components", "nodes", "edges")
                                            .excludes("spans", "topology", "components", "nodes", "edges")
                                            )
                            ),
                    Traces.class
            );

            // 3.提取结果
            List<Traces> traces = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long totalElements = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            return new PageResult<>(traces, pageNum, pageSize, totalElements, totalPages);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }

//    public PageResult<Traces> traceDetailResult(QueryTracesParam param) {
//        return null;
//    }
//
//    public Map<String, List<String>> getAllFilterOptions() {
//        return Map.of();
//    }

    /**
     * 单条Trace详情查询
     * @param queryTracesParam 查询参数，必须包含 traceId
     * @return 分页结果对象，包含单个 Trace 详情
     */
    public PageResult<Traces> traceDetailResult(QueryTracesParam queryTracesParam) {
        try {
            Query finalQuery = buildQuery(queryTracesParam);

            // 只查一条Trace详情
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .query(finalQuery)
                            .size(1)
                            .source(src -> src
                                    .filter(f -> f
                                            .excludes("topology", "components", "nodes", "edges")
                                    )
                            ),
                    Traces.class
            );

            List<Traces> traces = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long totalElements = traces.size();
            int totalPages = 1;

            return new PageResult<>(traces, 0, 1, totalElements, totalPages);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }

    /**
     * 获取所有可用的筛选项
     */
    @Override
    public Map<String, List<String>> getAllFilterOptions() {
        Map<String, List<String>> result = new java.util.HashMap<>();
        try {
            List<String> allEndpoints = esAggregationHelper.getDistinctTerms("traces", "endpoint.keyword");
            List<String> allProtocols = esAggregationHelper.getDistinctTerms("traces", "protocol.keyword");
            List<String> allStatusOptions = esAggregationHelper.getDistinctTerms("traces", "status_code.keyword");

            result.put("allEndpoints", allEndpoints != null ? allEndpoints : new ArrayList<>());
            result.put("allProtocols", allProtocols != null ? allProtocols : new ArrayList<>());
            result.put("allStatusOptions", allStatusOptions != null ? allStatusOptions : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error while fetching filter options using EsAggregationHelper", e);
            result.put("allEndpoints", new ArrayList<>());
            result.put("allProtocols", new ArrayList<>());
            result.put("allStatusOptions", new ArrayList<>());
        }
        return result;
    }

    /**
     * 滚动查询
     */
    public Map<String, Object> scrollQuery(QueryTracesParam param, String scrollId, Integer pageSize) {
        try {
            Map<String, Object> result = new java.util.HashMap<>();
            List<Traces> traces;
            String nextScrollId;

            if (scrollId == null || scrollId.isEmpty()) {
                // 首次滚动查询
                Query query = buildQuery(param);
                SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                                .index("traces") //todo??? 数据与用户绑定
                                .query(query)
                                .size(pageSize != null ? pageSize : 10)
                                .scroll(t -> t.time("2m"))
                                .source(src -> src.filter(f -> f.excludes("topology", "components", "nodes", "edges"))),
                        Traces.class
                );
                traces = response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
                nextScrollId = response.scrollId();
            } else {
                // 用 scrollId 拉取下一页
                ScrollResponse<Traces> response = elasticsearchClient.scroll(s -> s
                                .scrollId(scrollId)
                                .scroll(t -> t.time("2m")),
                        Traces.class
                );
                traces = response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
                nextScrollId = response.scrollId();
            }

            result.put("scrollId", nextScrollId);
            result.put("traces", traces);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing scrollQuery", e);
        }
    }

    /**
     * 构建查询条件
     * @param queryTracesParam 查询参数
     * @return Elasticsearch 查询对象
     */
    private Query buildQuery(QueryTracesParam queryTracesParam) {
        // 1. 构建嵌套查询条件
        List<Query> spanMustConditions = new ArrayList<>();

        // containerName 条件
        if (queryTracesParam.getContainerName() != null && !queryTracesParam.getContainerName().isEmpty()) {
            spanMustConditions.add(Query.of(q -> q
                    .term(t -> t
                            .field("spans.tag.docker_tag.container_name.keyword")
                            .value(queryTracesParam.getContainerName())
                    )
            ));
        }

        // traceId条件
        if (queryTracesParam.getTraceId() != null && !queryTracesParam.getTraceId().isEmpty()) {
            spanMustConditions.add(Query.of(q -> q
                    .term(t -> t
                            .field("spans.context.trace_id")
                            .value(queryTracesParam.getTraceId())
                    )
            ));
        }

        // 2. 构建主查询条件
        List<Query> mainMustConditions = new ArrayList<>();

        // 嵌套查询
        if (!spanMustConditions.isEmpty()) {
            Query nestedQuery = Query.of(q -> q
                    .nested(n -> n
                            .path("spans")
                            .query(nq -> nq
                                    .bool(b -> b.must(spanMustConditions))
                            )
                    )
            );
            mainMustConditions.add(nestedQuery);
        }

        // protocol 条件
        if (queryTracesParam.getProtocols() != null && !queryTracesParam.getProtocols().isEmpty()) {
            mainMustConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("protocol.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getProtocols().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }
        // status 条件
        if (queryTracesParam.getStatusCodes() != null && !queryTracesParam.getStatusCodes().isEmpty()) {
            mainMustConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("status_code.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getStatusCodes().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // resp 条件（用于 e2e_duration 范围查询）
        // e2e_duration 范围查询条件
        if (queryTracesParam.getMinE2eDuration() != null && queryTracesParam.getMaxE2eDuration() != null) {
            mainMustConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("e2e_duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMinE2eDuration() != null) {
            mainMustConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("e2e_duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMaxE2eDuration() != null) {
            mainMustConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("e2e_duration")
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        }

        //start_time开始时间范围
        if (queryTracesParam.getStartTime() != null) {
            mainMustConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("start_time")
                                    .gte(queryTracesParam.getStartTime().toString())
                            )
                    )
            ));
        }

        // endpoints 条件（支持多选）
        if (queryTracesParam.getEndpoints() != null && !queryTracesParam.getEndpoints().isEmpty()) {
            mainMustConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("endpoint.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getEndpoints().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // 3. 构建最终查询
        return Query.of(q -> q
                .bool(b -> b.must(mainMustConditions))
        );
    }

}
