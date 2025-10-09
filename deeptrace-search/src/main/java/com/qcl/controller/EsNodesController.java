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

    @Autowired
    private EsTraceService esTraceService;


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
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    public ResponseEntity<?> statistic(@ModelAttribute QueryTracesParam queryTracesParam,
                                    @RequestParam(required = false) String type) {
        // 参数校验
        if (type == null || type.isEmpty()) {
            return ResponseEntity.badRequest().body( "查询参数不能为空");
        }

        // 根据type参数调用不同的方法
        TraceSearchTypeEnum searchType = TraceSearchTypeEnum.fromCode(type);
        if (searchType == null) {
            return ResponseEntity.badRequest().body("无效的搜索类型: " + type);
        }

        switch (searchType) {
            case COUNT:
                // 请求数时序统计
                List<TimeBucketResult> countResult = esTraceService.getTraceCountByMinute(queryTracesParam);
                return ResponseEntity.ok(countResult);

            case STATUSCOUNT:
                // 状态码分组统计
                List<StatusTimeBucketResult> statusResult = esTraceService.getStatusCountByMinute(queryTracesParam);
                return ResponseEntity.ok(statusResult);

            case LATENCYSTATS:
                // 延迟统计
                List<LatencyTimeBucketResult> latencyResult = esTraceService.getLatencyStatsByMinute(queryTracesParam);
                return ResponseEntity.ok(latencyResult);
            default:
                return ResponseEntity.badRequest().body("无效的搜索类型: " + type);
        }
    }




}
