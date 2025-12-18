package com.qcl.controller;

import com.qcl.constants.AgentManageTypeEnum;
import com.qcl.entity.AgentManageConfig;
import com.qcl.entity.User;
import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.entity.param.agentconfig.AgentParam;
import com.qcl.service.AgentManageConfigService;
import com.qcl.service.AgentService;
import com.qcl.api.Result;
import com.qcl.service.AgentUserConfigService;
import com.qcl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentManageConfigService agentManageConfigService;


    @RequestMapping(value = "/get", method = RequestMethod.GET)
    Result<String> forwardGet(String param){
        return agentService.forwardGet(param);
    }

    /**
     * 采集器注册
     * @param param
     * @param principal
     * @return
     */
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
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            return agentService.registerAgent(param);

        } catch (Exception e) {
            log.error("采集器注册异常",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 采集器启用
     * @param param
     * @param principal
     * @return
     */
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
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            return agentService.enable(param);

        } catch (Exception e) {
            log.error("采集器启用异常",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 采集器禁用
     * @param param
     * @param principal
     * @return
     */
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
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            return agentService.disable(param);

        } catch (Exception e) {
            log.error("采集器禁用异常",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 采集器删除（同步删除采集器配置）
     * @param param
     * @param principal
     * @return
     */
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
            param.setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            return agentService.delete(param);

        } catch (Exception e) {
            log.error("采集器删除异常",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询采集器管理中相关配置（eg.采集器启用用户填写的信息）
     * @param hostIp
     * @param principal
     * @return
     */
    @RequestMapping(value = "/query_manage_config", method = RequestMethod.GET)
    public Result<List<AgentManageConfig>> queryManageConfig(@Validated @RequestParam String hostIp, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized(null);
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }

            AgentManageConfig agentManageConfig = new AgentManageConfig();
            agentManageConfig.setUserId(user.getUserId().toString());
            agentManageConfig.setHostIp(hostIp);
            List<AgentManageConfig>  result = agentManageConfigService.queryAll(agentManageConfig);

            return Result.success(result);
        } catch (Exception e) {
            log.error("采集器删除异常",e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 采集器配置 用户下发
     * @param param
     * @param principal
     * @return
     */
    @RequestMapping(value = "/edit_agent_config", method = RequestMethod.POST)
    public Result<String> editAgentConfig(@Validated @RequestBody AgentParam param, Principal principal) {
        try {
            String userName = principal.getName();
            if (userName == null){
                return Result.unauthorized("");
            }
            User user = this.userService.queryByUsername(userName);
            if (user == null ){
                return Result.error(userName+"该用户不存在");
            }

            param.getAgentInfo().setUserId(user.getUserId().toString());
            // 调用服务层处理注册逻辑
            return agentService.editAgentConfig(param);

        } catch (Exception e) {
            log.error("配置下发异常",e);
            return Result.error(e.getMessage());
        }
    }


}
