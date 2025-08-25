package com.qcl.controller;

import com.qcl.constants.TraceSearchTypeEnum;
import com.qcl.entity.AgentStat;
import com.qcl.entity.Traces;
import com.qcl.entity.graph.ContainerStatsResult;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsTraceGraphService;
import com.qcl.service.EsTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/esTraces")
public class EsTraceController {

    @Autowired
    private EsTraceService esTraceService;
    @Autowired
    private EsTraceGraphService esTraceGraphService;

    @RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
    public ResponseEntity< List<Traces>> search(QueryTracesParam queryTracesParam) {
        List<Traces> traces = esTraceService.queryByPage(queryTracesParam);
        return ResponseEntity.ok(traces);
    }
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



    @RequestMapping(value = "/getContainerStats", method = RequestMethod.GET)
    public ResponseEntity<List<ContainerStatsResult> > getContainerStats(QueryTracesParam queryTracesParam) {
        List<ContainerStatsResult> traces = esTraceGraphService.getContainerStats(queryTracesParam);
        return ResponseEntity.ok(traces);
    }
}
