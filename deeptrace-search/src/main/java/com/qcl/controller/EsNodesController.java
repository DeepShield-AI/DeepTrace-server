package com.qcl.controller;

import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsNodesServices;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/api/esNodes")
public class EsNodesController {

    @Autowired
    private EsNodesServices esNodeServices;



    @RequestMapping(value = "/log/queryByPage", method = RequestMethod.GET)
    public ResponseEntity<PageResult<Nodes>> search(QueryTracesParam queryTracesParam) {
        PageResult<Nodes> result = esNodeServices.queryByPage(queryTracesParam);

        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/queryEndpoint", method = RequestMethod.GET)
    public ResponseEntity<List<EndpointProtocolStatsResult>> getEndpointProtocolStats(QueryTracesParam queryTracesParam) {
        List<EndpointProtocolStatsResult> results = esNodeServices.getEndpointProtocolStats(queryTracesParam);
        return ResponseEntity.ok(results);
    }



    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryTracesParam queryTracesParam) {
        List<StatusTimeBucketResult> statusResult = esNodeServices.getStatusCountByMinute(queryTracesParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计请求个数，每秒XX个
     * @param queryTracesParam
     * @return
     */
    @RequestMapping(value = "/kpi/qps", method = RequestMethod.GET)
    public ResponseEntity<?> qpsByMinute(@ModelAttribute QueryTracesParam queryTracesParam) {
        List<TimeBucketResult> statusResult = esNodeServices.qpsByMinute(queryTracesParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计异常比例
     * @param queryTracesParam
     * @return
     */
    @RequestMapping(value = "/kpi/errorRate", method = RequestMethod.GET)
    public ResponseEntity<?> errorRateByMinute(@ModelAttribute QueryTracesParam queryTracesParam) {
        List<TimeBucketResult> statusResult = esNodeServices.errorRateByMinute(queryTracesParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计响应时延
     * @param queryTracesParam
     * @return
     */
    @RequestMapping(value = "/kpi/latencyStats", method = RequestMethod.GET)
    public ResponseEntity<?> latencyStatsByMinute(@ModelAttribute QueryTracesParam queryTracesParam) {
        List<TimeBucketResult> statusResult = esNodeServices.latencyStatsByMinute(queryTracesParam);
        return ResponseEntity.ok(statusResult);
    }


}
