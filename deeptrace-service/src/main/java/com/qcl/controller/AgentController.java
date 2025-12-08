package com.qcl.controller;

import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.service.AgentService;
import com.qcl.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    Result<String> forwardGet(String param){
        return agentService.forwardGet(param);
    }

    @PostMapping("/register")
    public Result<String> registerAgent(@RequestBody AgentRegisterParam param) {
        try {
            // 调用服务层处理注册逻辑
            String result = agentService.registerAgent(param);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
