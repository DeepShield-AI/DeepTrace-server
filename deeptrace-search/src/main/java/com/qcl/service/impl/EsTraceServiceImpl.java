package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EsTraceServiceImpl implements EsTraceService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;
    // 添加 ObjectMapper 用于序列化查询
    private final ObjectMapper objectMapper = new ObjectMapper();



    public List<Traces> queryByPage(QueryTracesParam queryTracesParam) {
        try {
            Query finalQuery = buildQuery(queryTracesParam);

            // 4.构建动态排序条件
            List<co.elastic.clients.elasticsearch._types.SortOptions> sortOptions = new ArrayList<>();

            if (queryTracesParam.getSortBy() != null && !queryTracesParam.getSortBy().isEmpty()) {
                co.elastic.clients.elasticsearch._types.SortOrder order =
                        "desc".equalsIgnoreCase(queryTracesParam.getSortOrder()) ?
                                co.elastic.clients.elasticsearch._types.SortOrder.Desc :
                                co.elastic.clients.elasticsearch._types.SortOrder.Asc;

                sortOptions.add(co.elastic.clients.elasticsearch._types.SortOptions.of(so -> so
                        .field(f -> f
                                .field(queryTracesParam.getSortBy())
                                .order(order)
                        )
                ));
            } else {
                // 默认排序
                sortOptions.add(co.elastic.clients.elasticsearch._types.SortOptions.of(so -> so
                        .field(f -> f
                                .field("start_time")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
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
