package com.qcl.controller;

import com.alibaba.fastjson.JSON;
import com.qcl.entity.DistributeTableQueryDTO;
import com.qcl.entity.FlamegraphQueryDTO;
import com.qcl.entity.ServiceListDTO;
import com.qcl.result.PageResult;
import com.qcl.result.Result;
import com.qcl.service.DistributeService;
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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin
public class ViewController {
//    private static final Logger log = (Logger) LoggerFactory.getLogger(ViewController.class);

    @Autowired
    private DistributeService distributeService;

    @RequestMapping("/eventDetail")
    public Result<String> eventDetail() {
        return Result.success("TTTTTT");
    }
    @RequestMapping("/test")
    public String test()throws IOException {
        // 创建连接
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "netsys204")
                );
        // 设置连接IP端口以及密码
        RestHighLevelClient client=new RestHighLevelClient(
                RestClient.builder(new HttpHost("114.215.254.187",9200, "http"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );

        SearchRequest searchRequest = new SearchRequest("spans_agent1");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("component", "PostStorageServ"));
        // 设置分页，从第 0 页开始，每页返回 10 条记录
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        // 设置查询源
        searchRequest.source(sourceBuilder);

        // 执行查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.print(searchResponse);
        return JSON.toJSONString(searchResponse);
//        try {
//            SearchRequest searchRequest = new SearchRequest("PostStorageServ");
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            sourceBuilder.query(QueryBuilders.matchQuery("component", "PostStorageServ"));
//            // 设置分页，从第 0 页开始，每页返回 10 条记录
//            sourceBuilder.from(0);
//            sourceBuilder.size(10);
//            // 设置查询源
//            searchRequest.source(sourceBuilder);
//
//            // 执行查询
//            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//            return JSON.toJSONString(searchResponse);
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
//        client.close();
//        return "开启成功";
    }
//    @RequestMapping("/test2")
//    public PageResult test2() {
//        LocalDateTime local = LocalDateTime.now();
//        DistributeTableDataDTO dataDTO = new DistributeTableDataDTO();
//        dataDTO.setStartTime(local);
//        dataDTO.setPage(10);
//        dataDTO.setPageSize(10);
//        PageResult pageResult = distributeService.DistributeTableDataQuery(dataDTO);
//        System.out.print(pageResult);
//        return pageResult;
//    }

    /**
     * 查分布式服务表格
     * @param distributeTableQueryDTO
     * @return
     * @throws IOException
     */
    @RequestMapping("/distributeList")
    public Result distributeList(@RequestBody DistributeTableQueryDTO distributeTableQueryDTO) throws IOException {
        log.info("distributeTableQueryDTO{}", distributeTableQueryDTO);
        PageResult pageResult = distributeService.DistributeTableDataQuery(distributeTableQueryDTO);
        Result result = new Result();
        result.setData(pageResult);
        return result;
    }

    /**
     * 火焰图数据查询
     * @param flamegraphQueryDTO
     * @return
     * @throws IOException
     */
    @RequestMapping("/flamegraphList")
    public Result distributeFlamegraphList(FlamegraphQueryDTO flamegraphQueryDTO) throws IOException {
        PageResult pageResult = distributeService.FlamegraphDataQuery(flamegraphQueryDTO);
        Result result = new Result();
        result.setData(pageResult);
        return result;
    }

    @RequestMapping("serviceList")
    public Result serviceMetricList(ServiceListDTO serviceListDTO) throws IOException {
        PageResult pageResult = distributeService.ServiceTableDataQuery(serviceListDTO);
        Result result = new Result();
        result.setData(pageResult);
        return result;
    }

}
