package com.qcl.controller;

import com.qcl.entity.User;
import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.service.AgentService;
import com.qcl.api.Result;
import com.qcl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    Result<String> forwardGet(String param){
        return agentService.forwardGet(param);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<String> registerAgent(@Validated  @RequestBody AgentRegisterParam param, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized("");
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }
            param.setUserName(userName);
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            String result = agentService.registerAgent(param);
            if (result == null || !result.contains("successfully")){
                return Result.error("注册失败");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public Result<String> enable(@Validated @RequestBody AgentRegisterParam param, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized("");
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }
            param.setUserName(userName);
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            String result = agentService.enable(param);
            if (result == null || !result.contains("successfully")){
                return Result.error("启用失败");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    public Result<String> disable(@Validated @RequestBody AgentRegisterParam param, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized("");
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }
            param.setUserName(userName);
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            String result = agentService.disable(param);
            if (result == null || !result.contains("successfully")){
                return Result.error("禁用失败");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<String> delete( @Validated @RequestBody AgentRegisterParam param, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized("");
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }
            param.setUserName(userName);
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            String result = agentService.delete(param);
            if (result == null || !result.contains("successfully")){
                return Result.error("删除失败");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

}
