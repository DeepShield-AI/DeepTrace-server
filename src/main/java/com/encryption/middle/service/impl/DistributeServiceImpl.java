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
         */
        SearchRequest searchRequest = new SearchRequest("test-rps-100-mappings");
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
//        log.info("{}", searchResponse);
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
            log.info("===={}", hit);
            Map<String, Object> source = hit.getSourceAsMap();
            log.info("duration-----{}", source.get("src_ip").getClass());
            DistributTableResponseDTO distributTableResponseDTO = new DistributTableResponseDTO();
            distributTableResponseDTO.setDataType("Unknown");
            distributTableResponseDTO.setDelay((double)source.get("duration"));
            distributTableResponseDTO.setClient((Long) source.get("src_ip"));

            distributTableResponseDTOList.add(distributTableResponseDTO);
        }


        pageResult.setTotal(10);
        pageResult.setRecords(distributTableResponseDTOList);

        return pageResult;
    }
}
