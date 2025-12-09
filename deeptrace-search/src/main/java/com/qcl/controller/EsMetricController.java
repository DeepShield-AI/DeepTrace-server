package com.qcl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.qcl.entity.param.QueryMetricParam;
import com.qcl.entity.statistic.MetricBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsMetricService;
import com.qcl.service.EsTraceService;


@RestController
@RequestMapping("/api/esMetrics")
public class EsMetricController {
    @Autowired
    private EsMetricService esMetricService;

    @RequestMapping(value = "/metricTypes", method = RequestMethod.GET)
    // @GetMapping("/metricTypes")
    /**
     * 获取所有指标类型
     *
     * @param param
     * @return
     */
    public ResponseEntity getMetricTypes(@RequestParam(required = false) String param) {
        System.out.println("=======Metric111启动========");
        return ResponseEntity.ok("success");  
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResponseEntity getMetricDetail(QueryMetricParam param) {  
        List<MetricBucketResult> result = esMetricService.getDetailByMetricType(param); // 添加了缺失的分号
        return ResponseEntity.ok(result);
    }
}