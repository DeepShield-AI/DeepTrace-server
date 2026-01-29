package com.qcl.controller;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.User;
import com.qcl.service.EsMetricService;
import com.qcl.service.UserService;
import com.qcl.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/api/metric")
public class EsMetricController {

    @Autowired
    private EsMetricService esMetricService;
    @Autowired
    private UserService userService;

    // 获取 agent 列表（从 agent_basic 中获取）
    @RequestMapping(value = "/agentList", method = RequestMethod.GET)
    public ResponseEntity<?> agentList(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                       Principal principal) {
        // 获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return ResponseEntity.badRequest().body(userName+"该用户不存在");
        }

        // 调用服务层从 ES 查询 agent_basic 数据
        Page<AgentBasic> page = esMetricService.getAgentList(user, keyword, pageNum, pageSize);
        PageResult<AgentBasic> result = new PageResult<AgentBasic>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ResponseEntity.ok(result);
    }

    // 获取 metric tags 数据
    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity<?> getMetricTags() {
        // 调用服务层从 ES 查询 metric tags 数据
        Map<String, Object> tags = esMetricService.getMetricTags();
        return ResponseEntity.ok(tags);
    }

    // 获取 metric chart 数据
    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    public ResponseEntity<?> getMetricChartData(
            @RequestParam(required = false) String agentName,
            @RequestParam(required = false) String namespace,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) String networkInterface,
            @RequestParam(required = false) Integer dataSize,
            @RequestParam(required = false) String name,
            Principal principal) {
        // 获取当前登录用户
        String userName = principal.getName();
        if (userName == null){
            return ResponseEntity.badRequest().body("暂未登录或token已经过期");
        }
        User user = this.userService.queryByUsername(userName);
        if (user == null ){
            return ResponseEntity.badRequest().body(userName+"该用户不存在");
        }
        
        // 调用服务层从 ES 查询 metric chart 数据
        Map<String, Object> chartData = esMetricService.getMetricChartData(
                user, agentName, namespace, startTime, endTime, cpu, device, networkInterface, dataSize, name);
        return ResponseEntity.ok(chartData);
    }
}
