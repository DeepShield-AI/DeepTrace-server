package com.qcl.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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
import org.springframework.data.domain.PageImpl;
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
    
    @Autowired
    private ElasticsearchClient elasticsearchClient;

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
        
        try {
            // 直接从 ES 数据库的 agent_basic 索引中查询所有数据，按分页参数返回
            // 构建搜索请求
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();
            searchRequestBuilder.index("agent_basic"); // 指定索引为 agent_basic
            searchRequestBuilder.size(pageSize); // 设置每页大小
            searchRequestBuilder.from(pageNum * pageSize); // 设置偏移量
            searchRequestBuilder.timeout("30s"); // 设置超时时间
            
            // 构建查询条件 - 查询所有数据，不添加额外过滤条件
            searchRequestBuilder.query(q -> q.matchAll(m -> m));
            
            // 执行搜索请求
            SearchRequest searchRequest = searchRequestBuilder.build();
            SearchResponse<Map> searchResponse = elasticsearchClient.search(searchRequest, Map.class);
            
            // 提取并转换结果
            List<AgentBasic> agentBasics = new ArrayList<>();
            // 使用Jackson将Map转换为对象，自动处理字段名转换（下划线转驼峰）
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
            
            for (var hit : searchResponse.hits().hits()) {
                var source = hit.source();
                if (source != null) {
                    AgentBasic agentBasic = new AgentBasic();
                    
                    // 从state字段中提取数据，state可能是嵌套对象或字符串
                    if (source.containsKey("state")) {
                        Object stateObj = source.get("state");
                        System.out.println("stateeee: " + stateObj + ", type: " + stateObj.getClass());
                        
                        if (stateObj instanceof Map) {
                            // 如果state是Map类型，直接转换
                            Map<?, ?> state = (Map<?, ?>) stateObj;
                            agentBasic = objectMapper.convertValue(state, AgentBasic.class);
                            
                            // 从source中获取id作为lcuuid的备选，确保lcuuid不为空
                            if (agentBasic.getLcuuid() == null || agentBasic.getLcuuid().isEmpty()) {
                                if (source.containsKey("id")) {
                                    agentBasic.setLcuuid(source.get("id").toString());
                                }
                            }
                        } else {
                            // 如果state不是Map类型（可能是String或其他类型），直接从source中获取数据
                            agentBasic = objectMapper.convertValue(source, AgentBasic.class);
                        }
                    } else {
                        // 兼容旧格式，直接从source中获取数据
                        agentBasic = objectMapper.convertValue(source, AgentBasic.class);
                    }
                    
                    agentBasics.add(agentBasic);
                }
            }
            
            // 获取总命中数
            long totalHits = searchResponse.hits().total().value();
            
            // 创建分页结果
            return new PageImpl<>(agentBasics, pageRequest, totalHits);
            
        } catch (Exception e) {
            log.error("查询 agent 列表失败", e);
            // 如果查询失败，返回空的 Page 对象
            return Page.empty(pageRequest);
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
            // 使用与 getMetricChartData 相同的索引名称
            String indexName = "test_1s_metric";
            
            // 检查索引是否存在
            boolean indexExists = elasticsearchClientWrapper.indexExists(indexName);
            
            if (indexExists) {
                // 实现真实的 Elasticsearch 聚合查询逻辑
                // 查询 tags.cpu、tags.device、tags.interface 的唯一值
                
                // CPU 核心数据 - 从 tags.cpu 字段查询
                Map<String, Object> cpuMap = new HashMap<>();
                List<String> cpuCoreStrings = elasticsearchClientWrapper.getDistinctValues(indexName, "tags.cpu");
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
                
                // 网络接口数据 - 从 tags.interface 字段查询
                Map<String, Object> networkMap = new HashMap<>();
                List<String> interfaces = elasticsearchClientWrapper.getDistinctValues(indexName, "tags.interface");
                networkMap.put("interface", interfaces);
                result.put("network", networkMap);
                
                // 磁盘设备数据 - 从 tags.device 字段查询
                Map<String, Object> diskMap = new HashMap<>();
                List<String> devices = elasticsearchClientWrapper.getDistinctValues(indexName, "tags.device");
                diskMap.put("device", devices);
                result.put("disk", diskMap);
                
                log.info("从 ES 的 {} 索引中查询到 tags 数据: CPU核心数={}, 网络接口数={}, 磁盘设备数={}", 
                         indexName, cpuCores.size(), interfaces.size(), devices.size());
            } else {
                // 索引不存在，使用默认数据
                log.info("ES 中不存在 {} 索引，使用默认数据", indexName);
                
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
            log.error("查询 ES metric tags 数据时发生异常", e);
            
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