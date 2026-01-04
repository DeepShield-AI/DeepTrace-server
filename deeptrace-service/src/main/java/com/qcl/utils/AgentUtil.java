package com.qcl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcl.constants.AgentManageTypeEnum;
import com.qcl.entity.AgentManageConfig;
import com.qcl.entity.AgentManageConfigDTO;
import com.qcl.service.AgentManageConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AgentUtil {

    /**
     * 查询当前用户的所有采集器配置
     * @param agentManageConfigService 采集器配置服务
     * @param userId 用户ID
     * @return 采集器配置DTO列表
     */
    public static List<AgentManageConfigDTO> queryAllAgent(AgentManageConfigService agentManageConfigService, Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        AgentManageConfig agentManageConfig = new AgentManageConfig();
        agentManageConfig.setUserId(userId.toString());
        agentManageConfig.setType(AgentManageTypeEnum.REGISTER.getCode());
        List<AgentManageConfig> result = agentManageConfigService.queryAll(agentManageConfig);

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        }

        return result.stream()
                .map(config -> {
                    AgentManageConfigDTO dto = new AgentManageConfigDTO();
                    BeanUtils.copyProperties(config, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 同步当前用户的采集器列表的traces nodes edges
     * @param agents 当前用户的采集器列表
     */
    public static void syncData(List<AgentManageConfigDTO> agents) {
        String url = Constants.AGENT_PROCESS_ADDR + "/assemble_trace";

        if (CollectionUtils.isEmpty(agents)) {
            return;
        }

        AgentManageConfigDTO agent = agents.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(agent);
        } catch (JsonProcessingException e) {
            log.error("同步数据失败，请求参数转换阶段 agentInfo = {}", jsonStr, e);
        }

        try {
            String result = OkHttpUtil.postJson(url, jsonStr);
            if (result == null) {
                log.error("同步数据失败，请求响应阶段 agentInfo = {}", jsonStr);
            }

            // 在解析响应前添加验证
            if (!JsonUtil.isValidJson(result)) {
                log.error("同步数据失败，解析响应阶段 agentInfo = {}", jsonStr);
            }

            // 解析响应
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(result);
            int code = jsonObject.getInteger("code");

            if (code != 200) {
                log.error("同步数据失败，响应结果错误，response={}， agentInfo = {}", result, jsonStr);
            }

        } catch (IOException e) {
            log.error("同步数据异常 agentInfo = {}", jsonStr, e);
        }
    }
}
