package com.qcl.controller;

import com.qcl.config.TestConfig;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.service.EsTraceService;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 现代化的EsTraceController测试类 - 不使用@MockBean
 * Spring Boot 3.4.0+ 推荐使用此方式
 */
@WebMvcTest(EsTraceController.class)  // 测试Spring MVC控制器
@Import(TestConfig.class)  // 导入测试配置类，提供模拟的EsTraceService
@ExtendWith(MockitoExtension.class)  // 使用JUnit Jupiter的Mockito扩展
public class EsTraceControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 用于模拟HTTP请求

    @Autowired
    private EsTraceService esTraceService;  // 从TestConfig中注入模拟的EsTraceService

    private List<Traces> mockTraces;
    private PageResult<Traces> mockPageResult;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        mockTraces = new ArrayList<>();

        // 创建分页结果
        mockPageResult = new PageResult<>();
        mockPageResult.setContent(mockTraces);
        mockPageResult.setTotalElements(0);
        mockPageResult.setPageNumber(1);
        mockPageResult.setPageSize(10);
    }

    /**
     * 测试分页查询功能
     */
    @Test
    public void testSearch() throws Exception {
        // 配置模拟服务的行为
        Mockito.when(esTraceService.queryByPageResult(any(QueryTracesParam.class))).thenReturn(mockPageResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/queryByPage")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试单个Trace详情查询功能
     */
    @Test
    public void testTraceDetail() throws Exception {
        // 配置模拟服务的行为
        Mockito.when(esTraceService.traceDetailResult(any(QueryTracesParam.class))).thenReturn(mockPageResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/traceDetail")
                        .param("traceId", "test-trace-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试滚动查询功能
     */
    @Test
    public void testScrollQuery() throws Exception {
        // 模拟滚动查询结果
        Map<String, Object> scrollResult = new HashMap<>();
        scrollResult.put("items", mockTraces);
        scrollResult.put("scrollId", "test-scroll-id");

        // 配置模拟服务的行为
        Mockito.when(esTraceService.scrollQuery(any(QueryTracesParam.class), anyString(), any()))
                .thenReturn(scrollResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/scrollQuery")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取筛选项功能
     */
    @Test
    public void testFilters() throws Exception {
        // 模拟筛选项结果
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("serviceNames", Arrays.asList("service-a", "service-b"));
        filters.put("statusCodes", Arrays.asList("200", "404", "500"));

        // 配置模拟服务的行为
        Mockito.when(esTraceService.getAllFilterOptions()).thenReturn(filters);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/filters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试按分钟统计Trace数量
     */
    @Test
    public void testStatisticCount() throws Exception {
        // 模拟统计结果
        List<TimeBucketResult> countResult = new ArrayList<>();

        // 配置模拟服务的行为
        Mockito.when(esTraceService.getTraceCountByMinute(any(QueryTracesParam.class)))
                .thenReturn(countResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/statistic")
                        .param("type", "COUNT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试按分钟统计状态码分组数量
     */
    @Test
    public void testStatisticStatusCount() throws Exception {
        // 模拟统计结果
        List<StatusTimeBucketResult> statusResult = new ArrayList<>();

        // 配置模拟服务的行为
        Mockito.when(esTraceService.getStatusCountByMinute(any(QueryTracesParam.class)))
                .thenReturn(statusResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/statistic")
                        .param("type", "STATUSCOUNT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试按分钟统计延迟分布
     */
    @Test
    public void testStatisticLatencyStats() throws Exception {
        // 模拟统计结果
        List<LatencyTimeBucketResult> latencyResult = new ArrayList<>();

        // 配置模拟服务的行为
        Mockito.when(esTraceService.getLatencyStatsByMinute(any(QueryTracesParam.class)))
                .thenReturn(latencyResult);

        // 执行测试
        mockMvc.perform(get("/api/esTraces/statistic")
                        .param("type", "LATENCYSTATS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试无效统计类型参数处理
     */
    @Test
    public void testStatisticInvalidType() throws Exception {
        // 执行测试，使用无效的统计类型
        mockMvc.perform(get("/api/esTraces/statistic")
                        .param("type", "INVALID_TYPE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试缺少统计类型参数处理
     */
    @Test
    public void testStatisticMissingType() throws Exception {
        // 执行测试，不提供统计类型参数
        mockMvc.perform(get("/api/esTraces/statistic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
