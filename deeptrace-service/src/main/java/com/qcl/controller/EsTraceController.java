package com.qcl.controller;

import com.qcl.constants.TraceSearchTypeEnum;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;
import com.qcl.service.EsTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//import java.util.List;
//import java.util.Map;
import java.util.*;

@Controller
@RequestMapping("/api/esTraces")
public class EsTraceController {

    @Autowired
    private EsTraceService esTraceService;

    /**
     * 分页查询
     */
    @RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
    // 使用 @ModelAttribute 自动绑定查询参数
    public ResponseEntity<PageResult<Traces>> search(@ModelAttribute QueryTracesParam queryTracesParam) {
        PageResult<Traces> result = esTraceService.queryByPageResult(queryTracesParam);
        return ResponseEntity.ok(result);
    }

    /**
     * 单个Trace详情查询
     */
    @RequestMapping(value = "/traceDetail", method = RequestMethod.GET)
    public ResponseEntity<PageResult<Traces>> traceDetail(@ModelAttribute QueryTracesParam queryTracesParam) {
        PageResult<Traces> result = esTraceService.traceDetailResult(queryTracesParam);
        return ResponseEntity.ok(result);
    }

    /**
     * 滚动查询
     */
    @RequestMapping(value = "/scrollQuery", method = RequestMethod.GET)
    public ResponseEntity<?> scrollQuery(QueryTracesParam param,
                                         @RequestParam(required = false) String scrollId,
                                         @RequestParam(required = false) Integer pageSize) {
        Map<String, Object> result = esTraceService.scrollQuery(param, scrollId, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有可用的筛选项
     */
    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<String>>> filters() {
        Map<String, List<String>> filters = esTraceService.getAllFilterOptions();
        return ResponseEntity.ok(filters);
    }

    /**
     * 统计查询
     */
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    // 使用 @ModelAttribute 自动绑定查询参数（内置Apifox不显示？
    public ResponseEntity<?> statistic(@ModelAttribute QueryTracesParam queryTracesParam,
                                       @RequestParam(required = false) String type) {
        // 参数校验
        if (type == null || type.isEmpty()) {
            return ResponseEntity.badRequest().body("查询参数不能为空");
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
