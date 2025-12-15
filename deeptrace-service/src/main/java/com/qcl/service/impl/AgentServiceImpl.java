package com.qcl.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.entity.param.AgentRegisterParam;
import com.qcl.entity.param.agentconfig.AgentParam;
import com.qcl.service.AgentService;
import com.qcl.service.UserService;
import com.qcl.utils.Constants;
import com.qcl.utils.OkHttpUtil;
import com.qcl.api.Result;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {


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

    public String registerAgent(AgentRegisterParam param) {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String delete(AgentRegisterParam param) {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String disable(AgentRegisterParam param) {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String enable(AgentRegisterParam param) {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String editAgentConfig(AgentParam param) {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
