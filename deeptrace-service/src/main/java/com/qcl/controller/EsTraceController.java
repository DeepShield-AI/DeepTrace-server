package com.qcl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.constants.AgentManageTypeEnum;
import com.qcl.constants.TraceSearchTypeEnum;
import com.qcl.entity.*;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.AgentManageConfigService;
import com.qcl.service.UserService;
import com.qcl.utils.AgentUtil;
import com.qcl.utils.JsonUtil;
import com.qcl.utils.OkHttpUtil;
import com.qcl.vo.PageResult;
import com.qcl.service.EsTraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/api/esTraces")
public class EsTraceController {

    @Autowired
    private EsTraceService esTraceService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentManageConfigService agentManageConfigService;

    /**
     * 分页查询
     * v2:支持用户筛选
     */
    @RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
    // 使用 @ModelAttribute 自动绑定查询参数
    public ResponseEntity<?> search(@ModelAttribute QueryTracesParam queryTracesParam, Principal principal) {
        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  ResponseEntity.badRequest().body(userName+"该用户不存在");
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);

        //同步当前用户的采集器列表的traces nodes edges
        List<AgentManageConfigDTO> agents = AgentUtil.queryAllAgent(agentManageConfigService,user.getUserId());
        AgentUtil.syncData(agents);

        PageResult<Traces> result = esTraceService.queryByPageResult(queryTracesParam, userDTO);
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
     *  v2:支持用户筛选
     */
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    // 使用 @ModelAttribute 自动绑定查询参数（内置Apifox不显示？
    public ResponseEntity<?> statistic(@ModelAttribute QueryTracesParam queryTracesParam,
                                       @RequestParam(required = false) String type,
                                       Principal principal) {

        //获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return  ResponseEntity.badRequest().body(userName+"该用户不存在");
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);

        //同步当前用户的采集器列表的traces nodes edges
        List<AgentManageConfigDTO> agents = AgentUtil.queryAllAgent(agentManageConfigService,user.getUserId());
        AgentUtil.syncData(agents);

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
                List<TimeBucketResult> countResult = esTraceService.getTraceCountByMinute(queryTracesParam, userDTO);
                return ResponseEntity.ok(countResult);

            case STATUSCOUNT:
                // 状态码分组统计
                List<StatusTimeBucketResult> statusResult = esTraceService.getStatusCountByMinute(queryTracesParam, userDTO);
                return ResponseEntity.ok(statusResult);

            case LATENCYSTATS:
                // 延迟统计
                List<LatencyTimeBucketResult> latencyResult = esTraceService.getLatencyStatsByMinute(queryTracesParam, userDTO);
                return ResponseEntity.ok(latencyResult);

            default:
                return ResponseEntity.badRequest().body("无效的搜索类型: " + type);
        }
    }
}
