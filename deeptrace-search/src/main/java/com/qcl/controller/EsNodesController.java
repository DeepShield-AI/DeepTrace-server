package com.qcl.controller;

import com.qcl.entity.param.QueryNodeParam;
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


    /**
     * 调用日志查询
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/log/queryByPage", method = RequestMethod.GET)
    public ResponseEntity<PageResult<Nodes>> search(QueryNodeParam queryNodeParam) {
        PageResult<Nodes> result = esNodeServices.queryByPage(queryNodeParam);

        return ResponseEntity.ok(result);
    }


    /**
     * 调用日志分组
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryNodeParam queryNodeParam) {
        List<StatusTimeBucketResult> statusResult = esNodeServices.getStatusCountByMinute(queryNodeParam);
        return ResponseEntity.ok(statusResult);
    }


    /**
     * 端点列表
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/queryEndpoint", method = RequestMethod.GET)
    public ResponseEntity<List<EndpointProtocolStatsResult>> getEndpointProtocolStats(QueryNodeParam queryNodeParam) {
        List<EndpointProtocolStatsResult> results = esNodeServices.getEndpointProtocolStats(queryNodeParam);
        return ResponseEntity.ok(results);
    }




    /**
     * 按分钟统计请求个数，每秒XX个
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/qps", method = RequestMethod.GET)
    public ResponseEntity<?> qpsByMinute(@ModelAttribute QueryNodeParam queryNodeParam) {
        List<TimeBucketResult> statusResult = esNodeServices.qpsByMinute(queryNodeParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计异常比例
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/errorRate", method = RequestMethod.GET)
    public ResponseEntity<?> errorRateByMinute(@ModelAttribute QueryNodeParam queryNodeParam) {
        List<TimeBucketResult> statusResult = esNodeServices.errorRateByMinute(queryNodeParam);
        return ResponseEntity.ok(statusResult);
    }

    /**
     * 按分钟统计响应时延
     * @param queryNodeParam
     * @return
     */
    @RequestMapping(value = "/kpi/latencyStats", method = RequestMethod.GET)
    public ResponseEntity<?> latencyStatsByMinute(@ModelAttribute QueryNodeParam queryNodeParam) {
        List<TimeBucketResult> statusResult = esNodeServices.latencyStatsByMinute(queryNodeParam);
        return ResponseEntity.ok(statusResult);
    }


}
