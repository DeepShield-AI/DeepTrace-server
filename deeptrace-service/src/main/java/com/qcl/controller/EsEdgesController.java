package com.qcl.controller;

import com.qcl.entity.Edges;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.param.QueryEdgeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsEdgeService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 路径分析对外API
 */
@Controller
@RequestMapping("/api/esEdges")
public class EsEdgesController {

    @Autowired
    private EsEdgeService esEdgeService;


    /**
     * 路径分析--调用日志查询
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/log/queryByPage", method = RequestMethod.GET)
    public ResponseEntity<PageResult<Edges>> search(QueryEdgeParam queryEdgeParam) {
        PageResult<Edges> result = esEdgeService.queryByPage(queryEdgeParam);

        return ResponseEntity.ok(result);
    }

    /**
     * 路径分析-调用日志分组
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryEdgeParam queryEdgeParam) {
        List<StatusTimeBucketResult> statusResult = esEdgeService.getStatusCountByMinute(queryEdgeParam);
        return ResponseEntity.ok(statusResult);
    }


    /**
     * 路径分析端点列表
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/queryEndpoint", method = RequestMethod.GET)
    public ResponseEntity<List<EndpointProtocolStatsResult>> getEndpointProtocolStats(QueryEdgeParam queryEdgeParam) {
        List<EndpointProtocolStatsResult> results = esEdgeService.getEndpointProtocolStats(queryEdgeParam);
        return ResponseEntity.ok(results);
    }





    /**
     * 按分钟统计请求个数，每秒XX个
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/qps", method = RequestMethod.GET)
    public ResponseEntity<?> qpsByMinute(@ModelAttribute QueryEdgeParam queryEdgeParam ) {
        List<TimeBucketResult> statusResult = esEdgeService.qpsByMinute(queryEdgeParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计异常比例
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/errorRate", method = RequestMethod.GET)
    public ResponseEntity<?> errorRateByMinute(@ModelAttribute  QueryEdgeParam queryEdgeParam) {
        List<TimeBucketResult> statusResult = esEdgeService.errorRateByMinute(queryEdgeParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计响应时延
     * @param queryEdgeParam
     * @return
     */
    @RequestMapping(value = "/kpi/latencyStats", method = RequestMethod.GET)
    public ResponseEntity<?> latencyStatsByMinute(@ModelAttribute  QueryEdgeParam queryEdgeParam) {
        List<TimeBucketResult> statusResult = esEdgeService.latencyStatsByMinute(queryEdgeParam);
        return ResponseEntity.ok(statusResult);
    }



}
