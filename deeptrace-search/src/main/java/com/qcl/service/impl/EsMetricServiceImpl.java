/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.qcl.service.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.qcl.entity.param.QueryMetricParam;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.MetricBucketResult;
import com.qcl.entity.statistic.TimeBucketCountResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsMetricService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author 10264
 */
@Service
@RequiredArgsConstructor
public class EsMetricServiceImpl implements EsMetricService {
    
        private final ElasticsearchClient elasticsearchClient;
        @Retryable(
                retryFor = {SocketException.class, ConnectException.class},
                maxAttempts = 5,
                backoff = @Backoff(delay = 100, multiplier = 2)
        )
        @Override
        public List<MetricBucketResult> getDetailByMetricType(QueryMetricParam queryMetricParam) {
            List<MetricBucketResult> results = new ArrayList<>();
            System.out.println(queryMetricParam.toString());
            MetricBucketResult result = new MetricBucketResult(11111123,11111);
            try {
                SearchResponse<Object> response = elasticsearchClient.search(
                        s -> s
                                .index("metric*")
                                .query(q -> q
                                    .bool(b -> b
                                        .must(m -> m
                                            .term(t -> t
                                                .field("name")
                                                .value(queryMetricParam.getMetricType())
                                            )
                                        )
                                        .filter(f -> f
                                        
                                            // 使用不同的方式构建范围查询
                                            .range(r -> {
                                                // 先设置字段
                                                r = r.field("timestamp");
                                                // 然后设置范围条件
                                                r = r.gte(g -> g.value(queryMetricParam.getStartTime().toString()));
                                                r = r.lte(l -> l.value(queryMetricParam.getEndTime().toString()));
                                                return r;
                                            })
                                        )
                                    )
                                )
                                .size(100) //默认传10条
                                ,
                                Object.class
                );
                System.out.println(response.toString());
                System.out.println("查询结果命中数: " + response.hits().hits().size());
                System.out.println("查询总命中数: " + response.hits().total().value());
            } catch (ElasticsearchException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            results.add(result);
            return results;
        }

}
