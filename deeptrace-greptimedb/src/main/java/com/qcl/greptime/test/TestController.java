package com.qcl.greptime.test;


import com.qcl.greptime.entity.GrpcLatency;
import com.qcl.greptime.service.MetricWithUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/greptimedb")
public class TestController {

    @Autowired
    private MetricWithUserService metricWithUserService;

    @RequestMapping(value = "/getMetric", method = RequestMethod.GET)
    List<Object> getMetric(Long userId, String metric, Long start, Timestamp end){

        return metricWithUserService.getMetricsWithUser(userId,metric,start,end);

    }


    @RequestMapping(value = "/getGrpcLatency", method = RequestMethod.GET)
    Object getGrpcLatency(String host, String method,
                                     Double percentile,
                                  String valueColumn,
                                  String groupByColumn,
                                     Long start,
                                     Long end){

        return metricWithUserService.getGrpcLatency(host, method, percentile, valueColumn,groupByColumn,start, end);

    }

}
