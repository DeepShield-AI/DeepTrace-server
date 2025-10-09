package com.qcl.controller;

import com.qcl.service.EsNodesServices;
import com.qcl.constants.TraceSearchTypeEnum;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsTraceService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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



    //todo
    @RequestMapping(value = "/statistic/status", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryTracesParam queryTracesParam) {
        List<StatusTimeBucketResult> statusResult = esNodeServices.getStatusCountByMinute(queryTracesParam);
        return ResponseEntity.ok(statusResult);
    }




}
