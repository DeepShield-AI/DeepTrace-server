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
import com.qcl.utils.JsonUtil;
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
            log.error(param.getHostIp()+ " 注册中或已完成注册" + param);
            return Result.error(param.getHostIp()+ " 注册中，请10分钟后启用");
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
            String result = OkHttpUtil.postJson(url, paramMap);
            if (result == null ){
                return Result.error("注册失败");
            }
            // 在解析响应前添加验证
            if (!JsonUtil.isValidJson(result)) {
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
            if (!JsonUtil.isValidJson(result)) {
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
            if (!JsonUtil.isValidJson(result)) {
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

        // 将实体参数转换为Map
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("host_ip", param.getHostIp());
        paramMap.put("user_name", param.getUserName());
        paramMap.put("host_password", param.getHostPassword());
        paramMap.put("user_id", param.getUserId());
        paramMap.put("ssh_port", String.valueOf(param.getSshPort()));
        paramMap.put("agent_name", param.getAgentName());


        //采集器注册状态查询。未完成给提示用户10分钟后再试；注册失败给用户注册失败的原因（将注册的配置从数据表中同步删除）；注册成功支持用户进行下一步启用操作
        try {
            String url = Constants.AGENT_PROCESS_ADDR + "/check_agent_registration";
            String registerResult = OkHttpUtil.postJson(url, paramMap);
            if (registerResult == null){
                return Result.error("查询注册状态失败");
            }
            // 在解析响应前添加验证
            if (!JsonUtil.isValidJson(registerResult)) {
                return Result.error("查询注册状态失败：" + registerResult);
            }

            // 解析响应
            JSONObject jsonObject = JSON.parseObject(registerResult);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");

            //注册失败给用户注册失败的原因;
            if (code == 404) {
                log.error("注册失败: " + registerResult);
                //删除该采集的注册配置
                AgentManageConfig agentManageConfig = new AgentManageConfig();
                agentManageConfig.setType(AgentManageTypeEnum.REGISTER.getCode());
                agentManageConfig.setHostIp(param.getHostIp());
                agentManageConfig.setUserId(param.getUserId());
                agentManageConfigService.deleteByParam(agentManageConfig);
                return Result.error("注册失败: " + JSONObject.parseObject(data).getString("details"));
            }else if (code != 200) {
                log.error("查询注册状态失败: " + registerResult);
                return Result.error("查询注册状态失败: " + message);
            }

            //未完成给提示用户10分钟后再试;
            if (!JSONObject.parseObject(data).getBoolean("is_registered")){
                return Result.error("注册还未完成，请用户10分钟后再试");
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        //注册成功支持用户进行下一步启用操作
        try {
            String url = Constants.AGENT_PROCESS_ADDR + "/start_agent";
            String result = OkHttpUtil.postJson(url, paramMap);//todo??? 接口需要返回json格式的结果，有错误码和错误信息
            if (result == null){
                return Result.error("启用失败");
            }
            // 在解析响应前添加验证
            if (!JsonUtil.isValidJson(result)) {
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


        // 将实体参数转换为Map
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        // 使用HttpClientUtil发送POST请求
        try {
            String result = OkHttpUtil.postJson(url, jsonStr);
            if (result == null){
                return Result.error("配置下发失败");
            }
            // 在解析响应前添加验证
            if (!JsonUtil.isValidJson(result)) {
                return Result.error("配置下发失败：" + result);
            }
            // 解析响应
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");

            if (code != 200) {
                return Result.error("操作失败: " + message);
            }

            //将本次新增/修改的配置入库
            AgentUserConfig agentUserConfig = new AgentUserConfig();
            agentUserConfig.setUserId(param.getAgentInfo().getUserId());
            agentUserConfig.setHostIp(param.getAgentInfo().getHostIp());
            agentUserConfig.setAgentName(param.getAgentInfo().getAgentName());
            agentUserConfig.setCreateTime(new Date());
            agentUserConfig.setConfig(jsonStr);
            //根据hostIp配置，有则更新，无则新增
            agentUserConfigService.insert(agentUserConfig);

            return Result.success(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
