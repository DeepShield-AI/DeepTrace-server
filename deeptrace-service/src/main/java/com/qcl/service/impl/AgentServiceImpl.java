package com.qcl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.constants.AgentManageTypeEnum;
import com.qcl.entity.AgentManageConfig;
import com.qcl.entity.AgentUserConfig;
import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.entity.param.agentconfig.AgentParam;
import com.qcl.service.AgentManageConfigService;
import com.qcl.service.AgentService;
import com.qcl.service.AgentUserConfigService;
import com.qcl.service.UserService;
import com.qcl.utils.Constants;
import com.qcl.utils.OkHttpUtil;
import com.qcl.api.Result;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentManageConfigService agentManageConfigService;
    @Autowired
    private AgentUserConfigService agentUserConfigService;

    public ResponseEntity<String> forwardRequest(String param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/api/native-agent";

        try {
            // 使用我们的工具类发送请求
            Response response = OkHttpUtil.postJsonWithResponse(
                    url,
                    param,
                    null  // 可以在这里添加请求头
            );

            if (response.isSuccessful()) {
                return ResponseEntity.ok(Constants.SUCCESS);
            }else{
                return ResponseEntity.status(response.code()).body(response.message());
            }
        } catch (IOException e) {
            //e.printStackTrace()
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public Result<String> forwardGet(String param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/api/native-agent";
        url="https://www.cnblogs.com/wuyongyin/p/16468864.html";

        try {
            // 使用我们的工具类发送请求
            Response response = OkHttpUtil.getWithResponse(
                    url,
                    null
            );

            if (response.isSuccessful()) {
                return Result.success();
            }else{
                return Result.error(response.code(), response.message());
            }
        } catch (IOException e) {
            //e.printStackTrace()
            log.error(e.getMessage());
            return Result.error("Error: " + e.getMessage());
        }
    }

    public Result<String>  registerAgent(AgentRegisterParam param) {
        //判断host_ip是否已被注册过
        Boolean hasRegister = agentManageConfigService.isHostIpExist(param.getHostIp(), AgentManageTypeEnum.REGISTER.getCode());
        if (hasRegister){
            log.error(param.getHostIp()+ " 注册中，请10分钟后查看" + param);
            return Result.error(param.getHostIp()+ " 注册中，请10分钟后查看");
        }

        //注册配置入库
        AgentManageConfig agentManageConfig = new AgentManageConfig();
        agentManageConfig.setType(AgentManageTypeEnum.REGISTER.getCode());
        agentManageConfig.setCreateTime(new Date());
        BeanUtils.copyProperties(param,agentManageConfig);
        agentManageConfigService.insert(agentManageConfig);

        String url = Constants.AGENT_PROCESS_ADDR + "/register_agent";

        // 将实体参数转换为Map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("host_ip", param.getHostIp());
        paramMap.put("user_name", param.getUserName());
        paramMap.put("host_password", param.getHostPassword());
        paramMap.put("user_id", param.getUserId());
        paramMap.put("ssh_port", String.valueOf(param.getSshPort()));
        paramMap.put("agent_name", param.getAgentName());

        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, paramMap);//todo??? 接口需要返回json格式的结果，有错误码和错误信息
            if (result == null ){
                return Result.error("注册失败");
            }
            // 在解析响应前添加验证
            if (!isValidJson(result)) {
                return Result.error("注册失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }


            return Result.success(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Result<String> delete(AgentRegisterParam param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/delete_agent";

        // 将实体参数转换为Map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("host_ip", param.getHostIp());
        paramMap.put("user_name", param.getUserName());
        paramMap.put("host_password", param.getHostPassword());
        paramMap.put("user_id", param.getUserId());
        paramMap.put("ssh_port", String.valueOf(param.getSshPort()));
        paramMap.put("agent_name", param.getAgentName());

        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, paramMap);//todo??? 接口需要返回json格式的结果，有错误码和错误信息
            if (result == null){
                return Result.error("删除失败");
            }
            // 在解析响应前添加验证
            if (!isValidJson(result)) {
                return Result.error("删除失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }

            //同步删除配置信息
            agentManageConfigService.deleteByHostIp(param.getUserId(), param.getHostIp());
            return Result.success(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<String> disable(AgentRegisterParam param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/stop_agent";

        // 将实体参数转换为Map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("host_ip", param.getHostIp());
        paramMap.put("user_name", param.getUserName());
        paramMap.put("host_password", param.getHostPassword());
        paramMap.put("user_id", param.getUserId());
        paramMap.put("ssh_port", String.valueOf(param.getSshPort()));
        paramMap.put("agent_name", param.getAgentName());

        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, paramMap);//todo??? 接口需要返回json格式的结果，有错误码和错误信息

            if (result == null){
                return Result.error("禁用失败");
            }
            // 在解析响应前添加验证
            if (!isValidJson(result)) {
                return Result.error("禁用失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }


            //禁用配置入库
            AgentManageConfig agentManageConfig = new AgentManageConfig();
            agentManageConfig.setType(AgentManageTypeEnum.DISABLE.getCode());
            BeanUtils.copyProperties(param,agentManageConfig);
            agentManageConfigService.insert(agentManageConfig);

            return Result.success(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public  Result<String>  enable(AgentRegisterParam param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/start_agent";

        // 将实体参数转换为Map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("host_ip", param.getHostIp());
        paramMap.put("user_name", param.getUserName());
        paramMap.put("host_password", param.getHostPassword());
        paramMap.put("user_id", param.getUserId());
        paramMap.put("ssh_port", String.valueOf(param.getSshPort()));
        paramMap.put("agent_name", param.getAgentName());

        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, paramMap);//todo??? 接口需要返回json格式的结果，有错误码和错误信息
            if (result == null){
                return Result.error("启用失败");
            }
            // 在解析响应前添加验证
            if (!isValidJson(result)) {
                return Result.error("启用失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }

            //注册配置入库
            AgentManageConfig agentManageConfig = new AgentManageConfig();
            agentManageConfig.setType(AgentManageTypeEnum.ENABLE.getCode());
            BeanUtils.copyProperties(param,agentManageConfig);
            agentManageConfigService.insert(agentManageConfig);

            return Result.success(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<String> editAgentConfig(AgentParam param) {
        String url = Constants.AGENT_PROCESS_ADDR + "/sync_agent_config";

        //将本次新增/修改的配置入库
        AgentUserConfig agentUserConfig = new AgentUserConfig();
        agentUserConfig.setUserId(param.getAgentInfo().getUserId());
        agentUserConfig.setHostIp(param.getAgentInfo().getHostIp());
        agentUserConfig.setAgentName(param.getAgentInfo().getAgentName());
        agentUserConfig.setCreateTime(new Date());

        // 将实体参数转换为Map
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        agentUserConfig.setConfig(jsonStr);
        agentUserConfigService.insert(agentUserConfig);


        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, jsonStr);
            if (result == null){
                return Result.error("配置下发失败");
            }
            // 在解析响应前添加验证
            if (!isValidJson(result)) {
                return Result.error("配置下发失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }


            return Result.success(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 判断字符串是否为有效的JSON格式
     * @param str 待验证的字符串
     * @return 是否为JSON格式
     */
    private boolean isValidJson(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            JSON.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
