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
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

                    String statusCode = String.valueOf(bucket.key());
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
                            var percentiles = percentilesAgg.percentilesBucket();
                            if (percentiles.values() != null) {
                                p75Duration = percentiles.values()._get();
                            }
                        }
                    }


                    // 解析 90th 百分位延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("p90_duration")) {
                        var percentilesAgg = bucket.aggregations().get("p90_duration");
                        if (percentilesAgg !=null) {
                            var percentiles = percentilesAgg.percentilesBucket();
                            if (percentiles.values() != null) {
                                p90Duration = percentiles.values()._get();
                            }
                        }
                    }

                    // 解析 99th 百分位延迟
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("p99_duration")) {
                        var percentilesAgg = bucket.aggregations().get("p99_duration");
                        if (percentilesAgg !=null) {
                            var percentiles = percentilesAgg.percentilesBucket();
                            if (percentiles.values() != null) {
                                p99Duration = percentiles.values()._get();
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




    public List<Traces> queryByPage(QueryTracesParam queryTracesParam) {
        try {
            Query finalQuery = buildQuery(queryTracesParam);

            // 4.构建动态排序条件
            List<SortOptions> sortOptions = new ArrayList<>();

            if (queryTracesParam.getSortBy() != null && !queryTracesParam.getSortBy().isEmpty()) {
                SortOrder order =
                        "desc".equalsIgnoreCase(queryTracesParam.getSortOrder()) ?
                                SortOrder.Desc :
                                SortOrder.Asc;

                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field(queryTracesParam.getSortBy())
                                .order(order)
                        )
                ));
            } else {
                // 默认排序
                sortOptions.add(SortOptions.of(so -> so
                        .field(f -> f
                                .field("start_time")
                                .order(SortOrder.Desc)
                        )
                ));
            }

            //临时深分页 TODO
            int pageNo = queryTracesParam.getPageNo() != null ? queryTracesParam.getPageNo() : 1;
            int pageSize = queryTracesParam.getPageSize() != null ? queryTracesParam.getPageSize() : 10;
            int from = (pageNo - 1) * pageSize;

            // 5. 执行查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .query(finalQuery)
                            .from(from)
                            .size(pageSize)
                            .sort(so -> so
                                    .field(f -> f
                                            .field("start_time")
                                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                                    )
                            )
                            .size(50)
                            .source(src -> src
                                    .filter(f -> f
                                            .excludes("topology", "components")
                                    )
                            ),
                    Traces.class
            );

            // 6. 提取结果
            List<Traces> traces =  response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            return traces;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }




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
        if (queryTracesParam.getProtocol() != null && !queryTracesParam.getProtocol().isEmpty()) {
            mainMustConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("protocol.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getProtocol().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }
        // status 条件
        if (queryTracesParam.getStatus() != null && !queryTracesParam.getStatus().isEmpty()) {
            mainMustConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("status_code.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getStatus().stream()
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


        // 3. 构建最终查询
        return Query.of(q -> q
                .bool(b -> b.must(mainMustConditions))
        );
    }



}
