package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
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


    public List<Traces> queryByPage2(QueryTracesParam queryTracesParam) {
        try {
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

            // traceIds 条件
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
                                .field("protocol")
                                .terms(t2 -> t2.value(
                                        queryTracesParam.getProtocol().stream()
                                                .map(FieldValue::of)
                                                .collect(Collectors.toList())
                                ))
                        )
                ));
            }



            // 3. 构建最终查询
            Query finalQuery = Query.of(q -> q
                    .bool(b -> b.must(mainMustConditions))
            );



            // 5. 执行查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .query(finalQuery)
                            .source(src -> src
                                    .filter(f -> f
                                            .excludes("topology", "components")
                                    )
                            ),
                    Traces.class
            );

            // 6. 提取结果
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }


    public  List<Traces> queryByPage(QueryTracesParam queryTracesParam) {
        List<String> traceIds = List.of(
                "12248920362114992407","30977720884229318799"
        );

        try {
            // 1. 构建嵌套查询
            Query nestedQuery = Query.of(q -> q
                    .nested(n -> n
                            .path("spans")
                            .query(nq -> nq
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("spans.tag.docker_tag.container_name.keyword")
                                                            .value("/service1")
                                                    )
                                            )
                                            .must(m -> m
                                                    .terms(t -> t
                                                            .field("spans.context.trace_id")
                                                            .terms(t2 -> t2.value(
                                                                    traceIds.stream()
                                                                            .map(FieldValue::of)
                                                                            .collect(Collectors.toList())
                                                            ))
                                                    )
                                            )
                                    )
                            )
                    )
            );


            // 3. 执行查询
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index("traces")
                            .query(nestedQuery)
                            .source(src -> src
                                    .filter(f -> f
                                            .excludes("topology", "components")
                                    )
                            ),
                    Traces.class
            );

            // 4. 提取结果
            List<Traces> traces =  response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            return traces;

        } catch (Exception e) {
            throw new RuntimeException("Error executing Elasticsearch query", e);
        }
    }

    /*public SearchHits<Traces> queryByPage() {
        List<String> traceIds = List.of("12248920362114992407","30977720884229318799");
            // 1. 构建嵌套查询
            Query nestedQuery = Query.of(q -> q
                    .nested(n -> n
                            .path("spans")
                            .query(nq -> nq
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("spans.tag.docker_tag.container_name.keyword")
                                                            .value("/service1")
                                                    )
                                            )
                                            .must(m -> m
                                                    .terms(t -> t
                                                            .field("spans.context.trace_id")
                                                            .terms(t2 -> t2.value(traceIds.stream().map(v ->
                                                                    FieldValue.of(v)).toList()))
                                                    )
                                            )
                                    )
                            )
                    )
            );

            // 2. 构建完整查询
            NativeQuery searchQuery = new NativeQueryBuilder()
                    .withQuery(nestedQuery)
                    .build();

        // 3. 打印查询语句
        try {
            String queryString = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(nestedQuery.term());
            System.out.println("Elasticsearch Query: " + queryString);
        } catch (Exception e) {
            System.err.println("Failed to serialize query: " + e.getMessage());
        }


        // 3. 执行查询并返回结果
        Object result =  elasticsearchOperations.search(searchQuery, Traces.class);
            return elasticsearchOperations.search(searchQuery, Traces.class);
    }*/
}
