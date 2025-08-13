package com.encryption.middle.service.impl;

import com.alibaba.fastjson.JSON;
import com.encryption.middle.pojo.dto.*;
import com.encryption.middle.result.PageResult;
import com.encryption.middle.service.DistributeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.spans.Spans;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DistributeServiceImpl implements DistributeService {
    final String dbIpStreet = "202.112.237.37"; //47.96.100.108
    //final String dbIpStreet = "114.215.254.187";

    /**
     * 表格数据查询
     * @param distributeTableDataDTO
     * @return
     * @throws IOException
     */
//    @Override
//    public PageResult DistributeTableDataQuery(DistributeTableQueryDTO distributeTableDataDTO) throws IOException {
////        final String dbIpStreet = "114.215.254.187";
//        // 连接elastic进行查询，封装接口数据
//        PageResult pageResult = new PageResult();
////        log.info("测试--{}", distributeTableDataDTO);
//        // 创建连接
//        // 处理请求参数
////        log.info("{}=======", distributeTableDataDTO);
//        // 创建请求凭证
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                    new UsernamePasswordCredentials("elastic", "netsys204")
//                );
//        // 设置连接IP端口和密码
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(dbIpStreet, 9200, "http"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
//        );
//        /**
//         * spans_agent1
//         * test-rps-100-mappings
//         * test-rps-100-traces
//         */
//        SearchRequest searchRequest = new SearchRequest("traces");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        // 匹配查询
////        sourceBuilder.query(QueryBuilders.matchQuery("component", "PostStorageServ"));
//        // 查全部数据
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
//        // 设置分页，从第 0 页开始，每页返回 10 条记录
//        sourceBuilder.from(distributeTableDataDTO.getPage());
//        sourceBuilder.size(distributeTableDataDTO.getPageSize());
//        // 设置查询源
//        searchRequest.source(sourceBuilder);
//
//        // 执行查询
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
////        log.info("searchResponse:{}", searchResponse);
//        SearchHits hits = searchResponse.getHits();
//        log.info("totalHits:{}", hits);
//        log.info("HHHHHHHHH:{}", hits.getHits());
//        TotalHits totalHits = hits.getTotalHits();
//
//        Long totalCount = totalHits.value;
//
//        SearchHit[] hits1 = hits.getHits();
//        List<DistributTableResponseDTO> distributTableResponseDTOList = new ArrayList<>();
//
//        for(SearchHit hit : hits1) {
//            Map<String, Object> source = hit.getSourceAsMap();
//            DistributTableResponseDTO distributeTableResponseDTO = new DistributTableResponseDTO();
//            distributeTableResponseDTO.setTraceId((String) source.get("trace_id"));
//            distributeTableResponseDTO.setSpanNum((Integer) source.get("span_num"));
//            distributeTableResponseDTO.setE2eDuration((Integer) source.get("e2e_duration"));
//            distributeTableResponseDTO.setEndpoint((String) source.get("endpoint"));
//            distributeTableResponseDTO.setComponentName((String) source.get("component_name"));
//            distributeTableResponseDTO.setServerIp((String) source.get("server_ip"));
//            distributeTableResponseDTO.setServerPort((Integer) source.get("server_port"));
//            distributeTableResponseDTO.setClientIp((String) source.get("client_ip"));
//            distributeTableResponseDTO.setClientPort((Integer) source.get("client_port"));
//            distributeTableResponseDTO.setProtocol((String) source.get("protocol"));
//            distributeTableResponseDTO.setStatusCode((String) source.get("status_code"));
//            distributeTableResponseDTO.setSpanNum((Integer) source.get("span_num"));
//
//            distributTableResponseDTOList.add(distributeTableResponseDTO);
//        }
//        pageResult.setTotal(totalCount);
//        pageResult.setRecords(distributTableResponseDTOList);
//
//        return pageResult;
//    }

    public PageResult DistributeTableDataQuery(DistributeTableQueryDTO queryDTO) throws IOException {
        PageResult pageResult = new PageResult();

        // 创建Elasticsearch客户端连接
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "netsys204"));
        new UsernamePasswordCredentials("elastic", "deeptrace123"));


        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(dbIpStreet, 9200, "http"))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
        try {

            SearchRequest searchRequest = new SearchRequest("traces");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            // 构建布尔查询，支持多条件组合
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 添加trace_id精确匹配条件（使用.keyword后缀确保精确匹配）
            if (queryDTO.getTraceId() != null && !queryDTO.getTraceId().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("trace_id.keyword", queryDTO.getTraceId()));
            }

            // 添加endpoint精确匹配条件
            if (queryDTO.getEndpoint() != null && !queryDTO.getEndpoint().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("endpoint.keyword", queryDTO.getEndpoint()));
            }

            // 添加component_name精确匹配条件
            if (queryDTO.getComponentName() != null && !queryDTO.getComponentName().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("component_name.keyword", queryDTO.getComponentName()));
            }

            // 添加status_code精确匹配条件
            if (queryDTO.getStatusCode() != null && !queryDTO.getStatusCode().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("status_code.keyword", queryDTO.getStatusCode()));
            }

            // 添加protocol精确匹配条件
            if (queryDTO.getProtocol() != null && !queryDTO.getProtocol().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("protocol.keyword", queryDTO.getProtocol()));
            }

            // 添加server_ip精确匹配条件
            if (queryDTO.getServerIp() != null && !queryDTO.getServerIp().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("server_ip.keyword", queryDTO.getServerIp()));
            }

            // 添加client_ip精确匹配条件
            if (queryDTO.getClientIp() != null && !queryDTO.getClientIp().isEmpty()) {
                boolQuery.must(QueryBuilders.termQuery("client_ip.keyword", queryDTO.getClientIp()));
            }

            // 添加e2e_duration范围查询条件
            if (queryDTO.getMinDuration() != null || queryDTO.getMaxDuration() != null) {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("e2e_duration");
                if (queryDTO.getMinDuration() != null) {
                    rangeQuery.gte(queryDTO.getMinDuration());
                }
                if (queryDTO.getMaxDuration() != null) {
                    rangeQuery.lte(queryDTO.getMaxDuration());
                }
                boolQuery.must(rangeQuery);
            }

            // 如果有查询条件，则使用布尔查询；否则查询所有数据
            if (boolQuery.hasClauses()) {
                // 有查询条件时不需要进行分页参数设置
                sourceBuilder.query(boolQuery);
            } else {
                sourceBuilder.query(QueryBuilders.matchAllQuery());
                // 设置分页参数
                sourceBuilder.from(queryDTO.getPage());
                sourceBuilder.size(queryDTO.getPageSize());
            }


            // 设置查询源并执行查询
            searchRequest.source(sourceBuilder);

            // 添加日志输出查询DSL，便于调试
            log.info("执行的Elasticsearch查询DSL: {}", sourceBuilder.toString());

            // 执行查询
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            TotalHits totalHits = hits.getTotalHits();

            Long totalCount = totalHits.value;
            SearchHit[] hitsArray = hits.getHits();

            // 处理查询结果
            List<DistributTableResponseDTO> responseList = new ArrayList<>();
            for (SearchHit hit : hitsArray) {
                Map<String, Object> source = hit.getSourceAsMap();
                DistributTableResponseDTO responseDTO = new DistributTableResponseDTO();

                // 从结果中提取字段
                responseDTO.setTraceId((String) source.get("trace_id"));
                responseDTO.setSpanNum((Integer) source.get("span_num"));
                responseDTO.setE2eDuration((Integer) source.get("e2e_duration"));
                responseDTO.setEndpoint((String) source.get("endpoint"));
                responseDTO.setComponentName((String) source.get("component_name"));
                responseDTO.setServerIp((String) source.get("server_ip"));
                responseDTO.setServerPort((Integer) source.get("server_port"));
                responseDTO.setClientIp((String) source.get("client_ip"));
                responseDTO.setClientPort((Integer) source.get("client_port"));
                responseDTO.setProtocol((String) source.get("protocol"));
                responseDTO.setStatusCode((String) source.get("status_code"));
                responseList.add(responseDTO);
            }

            pageResult.setTotal(totalCount);
            pageResult.setRecords(responseList);

        } finally {
            // 确保资源释放
            try {
                client.close();
            } catch (IOException e) {
                log.error("关闭Elasticsearch客户端失败", e);
            }
        }

        return pageResult;
    }
    /**
     * 火焰图相关数据查询
     * @param flamegraphDataQuery
     * @return
     * @throws IOException
     */
    @Override
    public PageResult FlamegraphDataQuery(FlamegraphQueryDTO flamegraphDataQuery) throws IOException {

        // 连接elastic进行查询，封装接口数据
        PageResult pageResult = new PageResult();
//        log.info("测试--{}", flamegraphDataQuery.getTraceId());

        // 创建连接
        // 处理请求参数
//        log.info("{}=======", distributeTableDataDTO);
        // 创建请求凭证
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "deeptrace123")
        );
        // 设置连接IP端口和密码
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(dbIpStreet, 9200, "http"))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
        /**
         * spans_agent1
         *
         * test-rps-100-mappings
         * test-rps-100-traces
         */
        SearchRequest searchRequest = new SearchRequest("traces");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 匹配查询
//        sourceBuilder.query(QueryBuilders.matchQuery("trace_id", flamegraphDataQuery.getTraceId ()));
        sourceBuilder.query(QueryBuilders.termsQuery("trace_id", flamegraphDataQuery.getTraceId()));

        // 查全部数据
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置分页，从第 0 页开始，每页返回 10 条记录
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        // 设置查询源
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] hits = searchResponse.getHits().getHits();
        log.info("ServerInfo=={}", hits);


        for(SearchHit hit : hits) {
            Map<String, Object> source = hit.getSourceAsMap();
            ArrayList spans = (ArrayList) source.get("spans");
            Map<String, Object> data = new HashMap<>();
            Object topology = (Object) source.get("topology");
            Object components = (Object) source.get("components");

            data.put("topology", topology);
            data.put("components", components);


            pageResult.setRecords(spans);
            pageResult.setData(data);
        }
//        pageResult.setData(searchResponse.getHits().getHits());
//        log.info("测试2{}", searchResponse.getHits().getHits());
        return pageResult;
    }

    /**
     *
     * @param serviceListDTO
     * @return
     * @throws IOException
     */
    public PageResult ServiceTableDataQuery(ServiceListDTO serviceListDTO) throws IOException {
        PageResult pageResult = new PageResult();
        // 创建Elasticsearch客户端连接
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "netsys204"));

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(dbIpStreet, 9200, "http"))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );

        try {
            SearchRequest searchRequest = new SearchRequest("traces");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 构建布尔查询，支持多条件组合
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

             //查全部数据
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            // 设置分页，从第 0 页开始，每页返回 10 条记录
            sourceBuilder.from(serviceListDTO.getPage());
            sourceBuilder.size(serviceListDTO.getPageSize());
            // 设置查询源
            searchRequest.source(sourceBuilder);

            // 执行查询
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 设置查询源并执行查询
            searchRequest.source(sourceBuilder);

            // 添加日志输出查询DSL，便于调试
            log.info("执行的Elasticsearch查询DSL: {}", sourceBuilder.toString());

            SearchHits hits = searchResponse.getHits();
            TotalHits totalHits = hits.getTotalHits();

            Long totalCount = totalHits.value;
            SearchHit[] hitsArray = hits.getHits();

            // 处理查询结果
            List<DistributTableResponseDTO> responseList = new ArrayList<>();
            for (SearchHit hit : hitsArray) {
                Map<String, Object> source = hit.getSourceAsMap();
                DistributTableResponseDTO responseDTO = new DistributTableResponseDTO();

                // 从结果中提取字段
                responseDTO.setTraceId((String) source.get("trace_id"));
                responseDTO.setSpanNum((Integer) source.get("span_num"));
                responseDTO.setE2eDuration((Integer) source.get("e2e_duration"));
                responseDTO.setEndpoint((String) source.get("endpoint"));
                responseDTO.setComponentName((String) source.get("component_name"));
                responseDTO.setServerIp((String) source.get("server_ip"));
                responseDTO.setServerPort((Integer) source.get("server_port"));
                responseDTO.setClientIp((String) source.get("client_ip"));
                responseDTO.setClientPort((Integer) source.get("client_port"));
                responseDTO.setProtocol((String) source.get("protocol"));
                responseDTO.setStatusCode((String) source.get("status_code"));

                responseList.add(responseDTO);
            }

            pageResult.setTotal(totalCount);
            pageResult.setRecords(responseList);

        } finally {
            // 确保资源释放
            try {
                client.close();
            } catch (IOException e) {
                log.error("关闭Elasticsearch客户端失败", e);
            }
        }

        return pageResult;
    }
    // 打印查询结果
    private void printSearchResults(SearchResponse response) {
        long totalHits = response.getHits().getTotalHits().value;
        System.out.println("找到 " + totalHits + " 条匹配记录");

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println("\n文档ID: " + hit.getId());
            System.out.println("来源索引: " + hit.getIndex());
            System.out.println("匹配分数: " + hit.getScore());

            // 打印文档原始JSON内容
            System.out.println("文档内容: " + hit.getSourceAsString());
            /*
            Map<String, Object> source = hit.getSourceAsMap();
            System.out.println("trace_id: " + source.get("trace_id"));
            System.out.println("span_id: " + source.get("span_id"));
            System.out.println("timestamp: " + source.get("timestamp"));
            */
        }
    }
}
