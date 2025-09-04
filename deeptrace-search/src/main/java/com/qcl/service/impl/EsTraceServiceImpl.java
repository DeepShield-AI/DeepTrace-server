package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.entity.statistic.*;
import com.qcl.service.EsTraceService;
import com.qcl.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
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


    /**
     * 请求数时序统计
     * @param queryTracesParam 筛选条件
     */
    public List<TimeBucketResult> getTraceCountByMinute(QueryTracesParam queryTracesParam) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
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
     * @param queryTracesParam 筛选条件
     * @return 状态码分组的时序统计数据
     */
    public List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam queryTracesParam) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建嵌套聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
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
     * @param queryTracesParam 筛选条件
     * @return 延迟统计的时序数据
     */
    public List<LatencyTimeBucketResult> getLatencyStatsByMinute(QueryTracesParam queryTracesParam) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(queryTracesParam);

            // 2. 构建聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
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
                                    // 获取 90th 百分位值
                                    p90Duration = tdigestPercentiles.values().keyed().get("75.0");
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
                                    // 获取 90th 百分位值
                                    p90Duration = tdigestPercentiles.values().keyed().get("99.0");
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
     * @param param 查询参数
     * @return 分页结果对象，包含 Trace 列表和所有筛选项（endpoint、protocol、status_code）
     */
    @Override
    public PageResult<Traces> queryByPageResult(QueryTracesParam param) {
        try {
            Query query = buildQuery(param);
            int pageNum = param.getPageNo() != null ? param.getPageNo() : 0;
            int pageSize = param.getPageSize() != null ? param.getPageSize() : 10;
            int from = pageNum * pageSize;

            // 动态排序条件
            List<SortOptions> sortOptions = new ArrayList<>();
            if (param.getSortBy() != null && !param.getSortBy().isEmpty()) {
                SortOrder order = "desc".equalsIgnoreCase(param.getSortOrder()) ? SortOrder.Desc : SortOrder.Asc;
                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field(param.getSortBy())
                                .order(order)
                        )
                ));
            } else {
                // 默认按 start_time 降序排序
                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field("start_time")
                                .order(SortOrder.Desc)
                        )
                ));
            }

            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .query(query)
                            .from(from)
                            .size(pageSize)
                            .sort(sortOptions)
                            .source(src -> src
                                    .filter(f -> f
                                            .excludes("spans", "topology", "components", "nodes", "edges")
                                    )
                            ),
                    Traces.class
            );

            List<Traces> traces = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long totalElements = response.hits().total() != null ? response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            // 查询所有筛选项
            List<String> allEndpoints = getAllDistinctValues("endpoint.keyword");
            List<String> allProtocols = getAllDistinctValues("protocol.keyword");
            List<String> allStatusOptions = getAllDistinctValues("status_code.keyword");

            PageResult<Traces> pageResult = new PageResult<>(traces, pageNum, pageSize, totalElements, totalPages);
            pageResult.setAllEndpoints(allEndpoints);
            pageResult.setAllProtocols(allProtocols);
            pageResult.setAllStatusOptions(allStatusOptions);
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }

    /**
     * 查询ES某字段所有去重值
     */
    private List<String> getAllDistinctValues(String field) {
        try {
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .size(0)
                            .aggregations("distinct", a -> a.terms(t -> t.field(field).size(1000))),
                    Traces.class
            );
            List<String> result = new ArrayList<>();
            if (response.aggregations() != null && response.aggregations().containsKey("distinct")) {
                StringTermsAggregate agg = response.aggregations().get("distinct").sterms();
                for (StringTermsBucket bucket : agg.buckets().array()) {
                    result.add(bucket.key().stringValue());
                }
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // 滚动查询
    public Map<String, Object> scrollQuery(QueryTracesParam param, String scrollId, Integer pageSize) {
        try {
            Map<String, Object> result = new java.util.HashMap<>();
            List<Traces> traces;
            String nextScrollId;

            if (scrollId == null || scrollId.isEmpty()) {
                // 首次滚动查询
                Query query = buildQuery(param);
                SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                                .index("traces")
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

        // 3. 构建最终查询
        return Query.of(q -> q
                .bool(b -> b.must(mainMustConditions))
        );
    }

}
