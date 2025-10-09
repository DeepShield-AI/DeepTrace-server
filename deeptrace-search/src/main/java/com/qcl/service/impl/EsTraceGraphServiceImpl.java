package com.qcl.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.qcl.exception.BizException;
import com.qcl.entity.graph.EdgeStatsResult;
import com.qcl.entity.graph.NodeStatsResult;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.service.EsTraceGraphService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.entity.Traces;
import lombok.RequiredArgsConstructor;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EsTraceGraphServiceImpl implements EsTraceGraphService {
    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper = new ObjectMapper();




    @Override
    /**
     * 按节点分组统计信息
     * @param queryTracesParam 筛选条件
     * @return 节点分组统计结果
     */
    public List<NodeStatsResult> getNodesStats(QueryTracesParam queryTracesParam) {
        try {
            // 1. 构建查询条件
            Query query = buildQueryToNode(queryTracesParam);

            // 计算时间窗口（秒）
            Long timeWindow = (System.currentTimeMillis() - queryTracesParam.getStartTime()) / 1000;

            // 2. 构建聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("nodes")
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("node_metrics", a -> a
                                    .terms(t -> t
                                            .field("nodeId")
                                            .size(100)
                                    )
                                    .aggregations("sample_doc", aa -> aa
                                            .topHits(th -> th
                                                    .size(1)
                                                    .source(src -> src
                                                            .filter(f -> f
                                                                    .includes("tag.docker_tag.container_name")
                                                            )
                                                    )
                                            )
                                    )
                                    .aggregations("avg_duration", aa -> aa
                                            .avg(avg -> avg
                                                    .field("metric.duration")
                                            )
                                    )
                                    .aggregations("total_count", aa -> aa
                                            .valueCount(vc -> vc
                                                    .field("nodeId")
                                            )
                                    )
                                    .aggregations("qps", aa -> aa
                                            .bucketScript(bs -> bs
                                                    .bucketsPath(bp -> bp
                                                            .dict(Map.of(
                                                                    "count", "total_count"
                                                            ))
                                                    )
                                                    .script(sc -> sc
                                                            .source("params.count / " + 3600)
                                                    )
                                            )
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<NodeStatsResult> result = new ArrayList<>();

            if (response.aggregations() != null &&
                    response.aggregations().containsKey("node_metrics")) {

                LongTermsAggregate termsAgg = response.aggregations().get("node_metrics").lterms();

                for (LongTermsBucket bucket : termsAgg.buckets().array()) {
                    String nodeId = String.valueOf(bucket.key());
                    String containerName = "";
                    Double avgDuration = 0.0;
                    Long errorCount = 0L; // 这个查询中没有错误计数
                    Double totalCount = 0.0;
                    Double errorRate = 0.0; // 这个查询中没有错误率
                    Double qps = 0.0;

                //todo



                    // 获取容器名称
                    if (bucket.aggregations().containsKey("sample_doc")) {
                        Aggregate aggregate = bucket.aggregations().get("sample_doc");
                        if (aggregate.isTopHits()) {
                            TopHitsAggregate topHitsAgg = aggregate.topHits();
                            HitsMetadata<JsonData> hitsMetadata = topHitsAgg.hits();

                            if (hitsMetadata.hits() != null && !hitsMetadata.hits().isEmpty()) {
                                Hit<JsonData> hit = hitsMetadata.hits().get(0);  // 获取第一个 hit
                                JsonData source = hit.source();

                                if (source != null) {
                                    try {
                                        // 使用 ObjectMapper 转换为 Map 进行解析
                                        Map<String, Object> sourceMap = objectMapper.readValue(source.toJson().toString(), Map.class);
                                        containerName = extractContainerNameFromMap(sourceMap);

                                    } catch (Exception e) {
                                        // 如果上面失败，尝试其他方式
                                        containerName = parseContainerNameFromJsonData(source);
                                    }
                                }
                            }
                        }
                    }

                    // 获取平均响应时延
                    if (bucket.aggregations().containsKey("avg_duration")) {
                        avgDuration = bucket.aggregations().get("avg_duration").avg().value();
                    }

                    // 获取总请求数
                    if (bucket.aggregations().containsKey("total_count")) {
                        totalCount = bucket.aggregations().get("total_count").valueCount().value();
                    }

                    // 获取 QPS
                    if (bucket.aggregations().containsKey("qps")) {
                        Aggregate aggregate = bucket.aggregations().get("qps");
                        if (aggregate.isSimpleValue()) {
                            qps = aggregate.simpleValue().value();
                        }
                    }

                    result.add(new NodeStatsResult(
                            nodeId, containerName, avgDuration, errorCount,
                            totalCount, errorRate, qps));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch node stats aggregation query", e);
        }
    }

    private Query buildQueryToNode(QueryTracesParam queryTracesParam) {
        // 构建过滤条件
        List<Query> filterConditions = new ArrayList<>();

        // endpoints 条件
        if (queryTracesParam.getEndpoints() != null && !queryTracesParam.getEndpoints().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.endpoints.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getEndpoints().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        } //end

        // protocols 条件
        if (queryTracesParam.getProtocols() != null && !queryTracesParam.getProtocols().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.protocols.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getProtocols().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // status codes 条件
        if (queryTracesParam.getStatusCodes() != null && !queryTracesParam.getStatusCodes().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.status_codes.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getStatusCodes().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }//end

        // start_time 范围条件
        if (queryTracesParam.getStartTime() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.start_time")
                                    .gte(queryTracesParam.getStartTime().toString())
                            )
                    )
            ));
        }

        // e2e_duration 范围查询条件
        if (queryTracesParam.getMinE2eDuration() != null && queryTracesParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMinE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
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



    /**
     * 按边（节点连接关系）分组统计信息
     * @param queryTracesParam 筛选条件
     * @return 边分组统计结果
     */
    public List<EdgeStatsResult> getEdgesStats(QueryTracesParam queryTracesParam) {

        if(queryTracesParam.getStartTime() == null){
            throw new BizException("startTime is required");
        }

        try {
            // 1. 构建查询条件
            Query query = buildQueryToNode(queryTracesParam);

            Long timeWindow = (System.currentTimeMillis() - queryTracesParam.getStartTime()) / 1000;

            // 2. 构建聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("edges")
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("node_metrics", a -> a
                                    .multiTerms(mt -> mt
                                            .terms(List.of(
                                                    MultiTermLookup.of(mtl -> mtl.field("src_nodeid")),
                                                    MultiTermLookup.of(mtl -> mtl.field("dst_nodeid"))
                                            ))
                                            .size(500)
                                    )
                                    .aggregations("avg_duration", aa -> aa
                                            .avg(avg -> avg
                                                    .field("metric.duration")
                                            )
                                    )
                                    .aggregations("total_count", aa -> aa
                                            .valueCount(vc -> vc
                                                    .field("src_nodeid")
                                            )
                                    )
                                    .aggregations("qps", aa -> aa
                                            .bucketScript(bs -> bs
                                                    .bucketsPath(bp -> bp
                                                            .dict(Map.of(
                                                                    "count", "total_count"
                                                            ))
                                                    )
                                                    .script(sc -> sc
                                                            .source("params.count / 3600")//todo: 改成 timeWindow
                                                    )
                                            )
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<EdgeStatsResult> result = new ArrayList<>();

            if (response.aggregations() != null &&
                    response.aggregations().containsKey("node_metrics")) {

                MultiTermsAggregate multiTermsAgg = response.aggregations().get("node_metrics").multiTerms();

                for (MultiTermsBucket bucket : multiTermsAgg.buckets().array()) {
                    // 获取源节点和目标节点ID
                    List<String> keys = new ArrayList<>();
                    for (FieldValue key : bucket.key()) {
                        if (key.isString()) {
                            keys.add(key.stringValue());
                        } else if (key.isLong()) {
                            keys.add(String.valueOf(key.longValue()));
                        } else if (key.isDouble()) {
                            keys.add(String.valueOf(key.doubleValue()));
                        } else {
                            keys.add(key.toString());
                        }
                    }

                    String srcNodeId = keys.size() > 0 ? keys.get(0) : "";
                    String dstNodeId = keys.size() > 1 ? keys.get(1) : "";

                    Double avgDuration = 0.0;
                    Double totalCount = 0.0;
                    Double qps = 0.0;

                    // 获取平均响应时延
                    if (bucket.aggregations().containsKey("avg_duration")) {
                        avgDuration = bucket.aggregations().get("avg_duration").avg().value();
                    }

                    // 获取总请求数
                    if (bucket.aggregations().containsKey("total_count")) {
                        totalCount = bucket.aggregations().get("total_count").valueCount().value();
                    }

                    // 获取 QPS
                    if (bucket.aggregations().containsKey("qps")) {
                        Aggregate aggregate = bucket.aggregations().get("qps");
                        if (aggregate.isSimpleValue()) {
                            qps = aggregate.simpleValue().value();
                        }
                    }

                    result.add(new EdgeStatsResult(
                            srcNodeId, dstNodeId, avgDuration, totalCount, qps));
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch edge stats aggregation query", e);
        }
    }

    private Query buildQueryToEdge(QueryTracesParam queryTracesParam) {
        // 构建过滤条件
        List<Query> filterConditions = new ArrayList<>();

        // endpoints 条件
        if (queryTracesParam.getEndpoints() != null && !queryTracesParam.getEndpoints().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.endpoints.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getEndpoints().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // protocols 条件
        if (queryTracesParam.getProtocols() != null && !queryTracesParam.getProtocols().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.protocols.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getProtocols().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // status codes 条件
        if (queryTracesParam.getStatusCodes() != null && !queryTracesParam.getStatusCodes().isEmpty()) {
            filterConditions.add(Query.of(q -> q
                    .terms(t -> t
                            .field("trace_tags.status_codes.keyword")
                            .terms(t2 -> t2.value(
                                    queryTracesParam.getStatusCodes().stream()
                                            .map(FieldValue::of)
                                            .collect(Collectors.toList())
                            ))
                    )
            ));
        }

        // start_time 范围条件
        if (queryTracesParam.getStartTime() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.start_times")
                                    .gte(queryTracesParam.getStartTime().toString())
                            )
                    )
            ));
        }

        // e2e_duration 范围查询条件
        if (queryTracesParam.getMinE2eDuration() != null && queryTracesParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMinE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .gte(queryTracesParam.getMinE2eDuration().toString())
                            )
                    )
            ));
        } else if (queryTracesParam.getMaxE2eDuration() != null) {
            filterConditions.add(Query.of(q -> q
                    .range(r -> r
                            .term(t-> t
                                    .field("metric.duration")
                                    .lte(queryTracesParam.getMaxE2eDuration().toString())
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




    /**
     * 按容器分组统计信息 -- traces
     * @param queryTracesParam 筛选条件
     * @return 容器分组统计结果
     */
    public List<NodeStatsResult> getNodesStatsByTrace(QueryTracesParam queryTracesParam) {
        try {
            // 1. 构建查询条件
            Query query = buildQueryToTrace(queryTracesParam);

            Long timeWindow = (System.currentTimeMillis() - queryTracesParam.getStartTime()) / 1000;
            // 2. 构建聚合查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .size(0) // 不返回具体文档
                            .query(query)
                            .aggregations("group_by_container", a -> a
                                    .nested(n -> n
                                            .path("spans")
                                    )
                                    .aggregations("filter_by_container_name", aa -> aa
                                            .filter(f -> f
                                                    .exists(e -> e
                                                            .field("spans.tag.docker_tag.tgid")
                                                    )
                                            )
                                            .aggregations("container_groups", aaa -> aaa
                                                    .terms(t -> t
                                                            .field("spans.tag.docker_tag.tgid")
                                                            .size(100)
                                                    )
                                                    .aggregations("sample_doc", aaaa -> aaaa
                                                            .topHits(th -> th
                                                                    .size(1)
                                                                    .source(src -> src
                                                                            .filter(f -> f
                                                                                    .includes("spans.tag.docker_tag.container_name")
                                                                            )
                                                                    )
                                                            )
                                                    )
                                                    .aggregations("avg_duration", aaaa -> aaaa
                                                            .avg(avg -> avg
                                                                    .field("spans.metric.duration")
                                                            )
                                                    )
                                                    .aggregations("error_count", aaaa -> aaaa
                                                            .filter(f -> f
                                                                    .bool(b -> b
                                                                            .mustNot(mn -> mn
                                                                                    .term(t -> t
                                                                                            .field("spans.metric.status_code")
                                                                                            .value(200)
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                                    .aggregations("total_count", aaaa -> aaaa
                                                            .valueCount(vc -> vc
                                                                    .field("spans.metric.duration")
                                                            )
                                                    )
                                                    .aggregations("error_rate", aaaa -> aaaa
                                                            .bucketScript(bs -> bs
                                                                    .bucketsPath(bp -> bp
                                                                            .dict(Map.of(
                                                                                    "errors", "error_count._count",
                                                                                    "total", "total_count"
                                                                            ))
                                                                    )
                                                                    .script(sc -> sc
                                                                            .source("params.errors / params.total * 100")
                                                                    )
                                                            )
                                                    )
                                                    .aggregations("qps", aaaa -> aaaa
                                                            .bucketScript(bs -> bs
                                                                                                                                    .bucketsPath(bp -> bp
                                                                            .dict(Map.of(
                                                                                    "count", "total_count"
                                                                            ))
                                                                    )
                                                                    .script(sc -> sc
                                                                            .source("params.count / " + timeWindow)
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            ),
                    Traces.class
            );

            // 3. 解析聚合结果
            List<NodeStatsResult> result = new ArrayList<>();

            if (response.aggregations() != null &&
                    response.aggregations().containsKey("group_by_container")) {

                NestedAggregate nestedAgg = response.aggregations().get("group_by_container").nested();
                if (nestedAgg.aggregations() != null &&
                        nestedAgg.aggregations().containsKey("filter_by_container_name")) {

                    FilterAggregate filterAgg = nestedAgg.aggregations().get("filter_by_container_name").filter();
                    if (filterAgg.aggregations() != null &&
                            filterAgg.aggregations().containsKey("container_groups")) {

                        LongTermsAggregate termsAgg = filterAgg.aggregations().get("container_groups").lterms();

                        for (LongTermsBucket bucket : termsAgg.buckets().array()) {
                            String tgid =  String.valueOf(bucket.key());
                            String containerName = "";
                            Double avgDuration = 0.0;
                            Long errorCount = 0L;
                            Double totalCount = 0.0;
                            Double errorRate = 0.0;
                            Double qps = 0.0;

                            // 获取容器名称
                            if (bucket.aggregations().containsKey("sample_doc")) {
                                Aggregate aggregate = bucket.aggregations().get("sample_doc");
                                if (aggregate.isTopHits()) {
                                    TopHitsAggregate topHitsAgg = aggregate.topHits();
                                    HitsMetadata<JsonData> hitsMetadata = topHitsAgg.hits();

                                    if (hitsMetadata.hits() != null && !hitsMetadata.hits().isEmpty()) {
                                        Hit<JsonData> hit = hitsMetadata.hits().get(0); // 获取第一个 hit
                                        JsonData source = hit.source();

                                        if (source != null) {
                                            try {
                                                // 方法1: 使用 ObjectMapper 转换为 Map 进行解析
                                                Map<String, Object> sourceMap = objectMapper.readValue(source.toJson().toString(), Map.class);
                                                containerName = extractContainerNameFromMap(sourceMap);

                                            } catch (Exception e) {
                                                // 方法2: 如果上面失败，尝试其他方式
                                                containerName = parseContainerNameFromJsonData(source);
                                            }
                                        }
                                    }
                                }
                            }

                            // 获取平均响应时延
                            if (bucket.aggregations().containsKey("avg_duration")) {
                                avgDuration = bucket.aggregations().get("avg_duration").avg().value();
                            }

                            // 获取异常请求数
                            if (bucket.aggregations().containsKey("error_count")) {
                                errorCount = bucket.aggregations().get("error_count").filter().docCount();
                            }

                            // 获取总请求数
                            if (bucket.aggregations().containsKey("total_count")) {
                                totalCount = bucket.aggregations().get("total_count").valueCount().value();
                            }

                            // 获取异常比例
                            if (bucket.aggregations().containsKey("error_rate")) {
                                Aggregate aggregate = bucket.aggregations().get("error_rate");
                                if (aggregate.isSimpleValue()) {
                                    errorRate = aggregate.simpleValue().value();
                                }
                            }

                        // 获取 QPS
                            if (bucket.aggregations().containsKey("qps")) {
                                Aggregate aggregate = bucket.aggregations().get("qps");
                                if (aggregate.isSimpleValue()) {
                                    qps = aggregate.simpleValue().value();
                                }
                            }

                            result.add(new NodeStatsResult(
                                    tgid, containerName, avgDuration, errorCount,
                                    totalCount, errorRate, qps));
                        }
                    }
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch container stats aggregation query", e);
        }
    }






    private Query buildQueryToTrace(QueryTracesParam queryTracesParam) {
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


    private String extractContainerNameFromMap(Map<String, Object> sourceMap) {
        try {
            // 根据数据结构逐层解析: _source.tag.docker_tag.container_name
            if (sourceMap.containsKey("tag")) {
                Map<String, Object> tag = (Map<String, Object>) sourceMap.get("tag");
                if (tag.containsKey("docker_tag")) {
                    Map<String, Object> dockerTag = (Map<String, Object>) tag.get("docker_tag");
                    if (dockerTag.containsKey("container_name")) {
                        Object containerNameObj = dockerTag.get("container_name");
                        if (containerNameObj instanceof List) {
                            List<String> containerNames = (List<String>) containerNameObj;
                            if (!containerNames.isEmpty()) {
                                return containerNames.get(0);
                            }
                        } else if (containerNameObj instanceof String) {
                            return (String) containerNameObj;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private String parseContainerNameFromJsonData(JsonData jsonData) {
        try {
            // 直接解析 JsonData
            String jsonString = jsonData.toString();
            // 使用 Jackson ObjectMapper 解析 JSON 字符串
            Map<String, Object> jsonMap = objectMapper.readValue(jsonString, Map.class);
            return extractContainerNameFromMap(jsonMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }



}
