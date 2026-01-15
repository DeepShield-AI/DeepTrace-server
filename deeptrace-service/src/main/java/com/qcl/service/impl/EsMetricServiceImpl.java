package com.qcl.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.qcl.constants.UserRoleEnum;
import com.qcl.entity.AgentBasic;
import com.qcl.entity.User;
import com.qcl.repository.ElasticsearchClientWrapper;
import com.qcl.repository.EsAgentBasicRepository;
import com.qcl.service.EsMetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class EsMetricServiceImpl implements EsMetricService {

    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;
    
    @Autowired
    private ElasticsearchClientWrapper elasticsearchClientWrapper;

    /**
     * 从 ES 查询 agent_basic 数据
     * @param user 当前登录用户
     * @param keyword 搜索关键词（agent 名称）
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 分页查询结果
     */
    @Override
    public Page<AgentBasic> getAgentList(User user, String keyword, Integer pageNum, Integer pageSize) {
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        
        // 获取用户 ID
        Long userId = user.getUserId();
        
        // 根据用户角色查询
        if (StringUtils.equalsIgnoreCase(user.getRole(), UserRoleEnum.ADMIN.getCode())) {
            // 管理员用户：查询所有 agent
            if (keyword == null || keyword.isEmpty()) {
                return esAgentBasicRepository.findAll(pageRequest);
            } else {
                return esAgentBasicRepository.findByNameContaining(keyword, pageRequest);
            }
        } else {
            // 普通用户：查询自己的 agent
            if (keyword == null || keyword.isEmpty()) {
                return esAgentBasicRepository.findByUserId(userId.toString(), pageRequest);
            } else {
                return esAgentBasicRepository.findByNameContainingAndUserIdEquals(keyword, userId.toString(), pageRequest);
            }
        }
    }

    /**
     * 从 ES 查询 metric tags 数据
     * @return 封装后的 tags 数据
     */
    @Override
    public Map<String, Object> getMetricTags() {
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从 Elasticsearch 查询 metric tags 数据
            // 假设 ES 中存在 metric 索引，并且包含 metrics.tags 字段
            
            // 检查 metric 索引是否存在
            boolean metricIndexExists = elasticsearchClientWrapper.indexExists("metrics");
            
            if (metricIndexExists) {
                // 实现真实的 Elasticsearch 聚合查询逻辑
                // 查询 metrics.tags.cpu、metrics.tags.device、metrics.tags.interface 的唯一值
                
                // CPU 核心数据 - 从 metrics.tags.cpu 字段查询
                Map<String, Object> cpuMap = new HashMap<>();
                List<String> cpuCoreStrings = elasticsearchClientWrapper.getDistinctValues("metrics", "tags.cpu");
                List<Integer> cpuCores = new ArrayList<>();
                for (String coreStr : cpuCoreStrings) {
                    try {
                        cpuCores.add(Integer.parseInt(coreStr));
                    } catch (NumberFormatException e) {
                        log.warn("无法将 CPU 核心值转换为整数: {}", coreStr);
                    }
                }
                cpuMap.put("core", cpuCores);
                result.put("cpu", cpuMap);
                
                // 网络接口数据 - 从 metrics.tags.interface 字段查询
                Map<String, Object> networkMap = new HashMap<>();
                List<String> interfaces = elasticsearchClientWrapper.getDistinctValues("metrics", "tags.interface");
                networkMap.put("interface", interfaces);
                result.put("network", networkMap);
                
                // 磁盘设备数据 - 从 metrics.tags.device 字段查询
                Map<String, Object> diskMap = new HashMap<>();
                List<String> devices = elasticsearchClientWrapper.getDistinctValues("metrics", "tags.device");
                diskMap.put("device", devices);
                result.put("disk", diskMap);
                
                log.info("从 ES 的 metric 索引中查询到 tags 数据: CPU核心数={}, 网络接口数={}, 磁盘设备数={}", 
                         cpuCores.size(), interfaces.size(), devices.size());
            } else {
                // metric 索引不存在，使用默认数据
                log.info("ES 中不存在 metric 索引，使用默认数据");
                
                // 默认 CPU 核心数据
                Map<String, Object> cpuMap = new HashMap<>();
                List<Integer> cpuCores = Arrays.asList(0, 1, 2, 3, 4, 5);
                cpuMap.put("core", cpuCores);
                result.put("cpu", cpuMap);
                
                // 默认网络接口数据
                Map<String, Object> networkMap = new HashMap<>();
                List<String> interfaces = Arrays.asList("eno1", "eno2", "eno3");
                networkMap.put("interface", interfaces);
                result.put("network", networkMap);
                
                // 默认磁盘设备数据
                Map<String, Object> diskMap = new HashMap<>();
                List<String> devices = Arrays.asList("loop1", "loop2");
                diskMap.put("device", devices);
                result.put("disk", diskMap);
            }
            
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            System.out.println("查询 ES metric tags 数据时发生异常，使用默认数据");
            
            // 异常时返回空数据结构
            result.put("cpu", Collections.singletonMap("core", Collections.emptyList()));
            result.put("network", Collections.singletonMap("interface", Collections.emptyList()));
            result.put("disk", Collections.singletonMap("device", Collections.emptyList()));
        }
        
        return result;
    }
    
    /**
     * 从 ES 查询曲线图指标数据
     * @param namespace 命名空间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param cpu CPU 核心
     * @param device 磁盘设备
     * @param networkInterface 网络接口
     * @param dataSize 数据大小
     * @param name 指标名称
     * @return 曲线图指标数据
     */
    @Override
    public Map<String, Object> getMetricChartData(String namespace, String startTime, String endTime, String cpu, String device, String networkInterface, Integer dataSize, String name) {
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从 Elasticsearch 查询曲线图指标数据
            List<Map<String, Object>> chartData = elasticsearchClientWrapper.getMetricChartData(
                    "test_1s_metric", namespace, startTime, endTime, cpu, device, networkInterface, dataSize, name);
            
            // 构建返回结果
            result.put("data", chartData);
            result.put("total", chartData.size());
            result.put("success", true);
            
            log.info("从 ES 的 test_1s_metric 索引中查询到曲线图指标数据: 共 {} 个数据点", chartData.size());
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            log.error("查询 ES metric 曲线图指标数据时发生异常", e);
            
            // 异常时返回错误信息
            result.put("data", Collections.emptyList());
            result.put("total", 0);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
}