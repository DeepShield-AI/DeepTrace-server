package com.encryption.middle.service.impl;

import com.alibaba.fastjson.JSON;
import com.encryption.middle.pojo.dto.DistributTableResponseDTO;
import com.encryption.middle.pojo.dto.DistributeTableDataDTO;
import com.encryption.middle.pojo.dto.DistributeTableQueryDTO;
import com.encryption.middle.result.PageResult;
import com.encryption.middle.service.DistributeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.lucene.search.spans.Spans;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DistributeServiceImpl implements DistributeService {

    @Override
    public PageResult DistributeTableDataQuery(DistributeTableQueryDTO distributeTableDataDTO) throws IOException {
        // 连接elastic进行查询，封装接口数据
        PageResult pageResult = new PageResult();
        // 创建连接
        // 处理请求参数
//        log.info("{}=======", distributeTableDataDTO);
        // 创建请求凭证
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "netsys204")
                );
        // 设置连接IP端口和密码
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("114.215.254.187", 9200, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
        /**
         * spans_agent1
         * test-rps-100-mappings
         * test-rps-100-traces
         */
        SearchRequest searchRequest = new SearchRequest("test-rps-100-traces");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 匹配查询
//        sourceBuilder.query(QueryBuilders.matchQuery("component", "PostStorageServ"));
        // 查全部数据
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置分页，从第 0 页开始，每页返回 10 条记录
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        // 设置查询源
        searchRequest.source(sourceBuilder);

        // 执行查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        log.info("searchResponse:{}", searchResponse);
        SearchHits hits = searchResponse.getHits();

        // 输出总命中数
//        System.out.println("Total hits: " + hits.getTotalHits().value);

        // 遍历文档
//        for (SearchHit hit : hits.getHits()) {
//            // 封装遍历后的对象进行封装List
//            System.out.println("---");
//            System.out.println("ID: " + hit.getId());
//            System.out.println("Source: " + hit.getSourceAsString());
//
//            // 从 Map 获取字段
//            Map<String, Object> source = hit.getSourceAsMap();
//            System.out.println("Name: " + source.get("name"));
//            System.out.println("Age: " + source.get("age"));
//        }
        List<DistributTableResponseDTO> distributTableResponseDTOList = new ArrayList<>();
        for(SearchHit hit : hits.getHits()) {
            // 塞数据
//            log.info("===={}", hit);
            Map<String, Object> source = hit.getSourceAsMap();
            Object spans = source.get("spans");
//            log.info("spans:{}", spans);
//            log.info("duration-----{}", source.get("src_ip").getClass());
            DistributTableResponseDTO distributeTableResponseDTO = new DistributTableResponseDTO();
            distributeTableResponseDTO.setTraceId((String) source.get("trace_id"));
            distributeTableResponseDTO.setSpanNum((Integer) source.get("span_num"));
            distributeTableResponseDTO.setE2eDuration((Double) source.get("e2e_duration"));
            distributeTableResponseDTO.setEndpoint((String) source.get("endpoint"));
            distributeTableResponseDTO.setComponentName((String) source.get("component_name"));
            distributeTableResponseDTO.setServerIp((String) source.get("server_ip"));
            distributeTableResponseDTO.setServerPort((Integer) source.get("server_port"));
            distributeTableResponseDTO.setClientIp((String) source.get("client_ip"));
            distributeTableResponseDTO.setClientPort((Integer) source.get("client_port"));
            distributeTableResponseDTO.setProtocol((String) source.get("protocol"));
            distributeTableResponseDTO.setStatusCode((String) source.get("status_code"));

//            distributeTableResponseDTO.setDataType("Unknown");
//            distributeTableResponseDTO.setStartTime((Long) source.get("start_time"));
//            distributeTableResponseDTO.setEndTime((Long) source.get("end_time"));
//            distributeTableResponseDTO.setDstIp((Long) source.get("dst_ip"));
//            distributeTableResponseDTO.setSrcIp((Long) source.get("src_ip"));
//            distributeTableResponseDTO.setDuration((Double) source.get("duration"));
//            distributeTableResponseDTO.setSrcPort((Integer) source.get("src_port"));
//            distributeTableResponseDTO.setDstPort((Integer) source.get("dst_port"));
//            distributeTableResponseDTO.setDirection((String) source.get("direction"));

            distributTableResponseDTOList.add(distributeTableResponseDTO);
        }


        pageResult.setTotal(10);
        pageResult.setRecords(distributTableResponseDTOList);

        return pageResult;
    }
}
