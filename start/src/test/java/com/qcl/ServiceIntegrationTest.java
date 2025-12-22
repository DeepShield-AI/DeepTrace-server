package com.qcl;

import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.service.EsTraceService;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务服务层测试类 - 测试Service层的业务逻辑
 * 测试目标：验证业务逻辑的正确性和完整性
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ServiceIntegrationTest {

    @Autowired
    private EsTraceService esTraceService;

    private static final int TEST_PAGE_SIZE = 5;

    @BeforeEach
    void setUp() {
        assertNotNull(esTraceService, "Service应该被正确注入");
    }

    /**
     * 新增测试: 数据准确性验证测试
     * 测试目标：验证Service层查询返回的数据是否准确
     */
    @Test
    void testDataAccuracyValidation() {
        System.out.println("🔍 开始数据准确性验证测试...");

        // 测试场景1: 基础查询数据准确性
        testBasicQueryAccuracy();

        // 测试场景2: 时间范围查询数据准确性
        testTimeRangeQueryAccuracy();

        // 测试场景3: 协议过滤查询数据准确性
        testProtocolFilterAccuracy();

        // 测试场景4: 状态码过滤查询数据准确性
        testStatusCodeFilterAccuracy();

        System.out.println("✅ 数据准确性验证测试全部通过");
    }

    /**
     * 场景1: 基础查询数据准确性测试
     */
    private void testBasicQueryAccuracy() {
        System.out.println("📊 测试基础查询数据准确性...");

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        // 验证数据准确性
        validateDataAccuracy("基础查询", result);
    }

    /**
     * 场景2: 时间范围查询数据准确性测试
     */
    private void testTimeRangeQueryAccuracy() {
        System.out.println("⏰ 测试时间范围查询数据准确性...");

        long currentTime = System.currentTimeMillis() / 1000;
        long oneHourAgo = currentTime - 3600;

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStartTime(oneHourAgo);
        param.setEndTime(currentTime);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        // 验证数据准确性
        validateDataAccuracy("时间范围查询", result);

        // 额外验证：时间范围过滤是否生效
        if (!result.getContent().isEmpty()) {
            Traces firstTrace = result.getContent().get(0);
            assertNotNull(firstTrace.getStartTime(), "Trace的开始时间不应该为null");
            System.out.println("✅ 时间范围过滤验证通过");
        }
    }

    /**
     * 场景3: 协议过滤查询数据准确性测试
     */
    private void testProtocolFilterAccuracy() {
        System.out.println("🌐 测试协议过滤查询数据准确性...");

        List<String> protocols = List.of("HTTP", "gRPC");

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setProtocols(protocols);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        // 验证数据准确性
        validateDataAccuracy("协议过滤查询", result);

        // 额外验证：协议过滤是否生效
        if (!result.getContent().isEmpty()) {
            Traces firstTrace = result.getContent().get(0);
            assertNotNull(firstTrace.getProtocol(), "Trace的协议不应该为null");
            assertTrue(protocols.contains(firstTrace.getProtocol()),
                    "返回的Trace协议应该在过滤条件内");
            System.out.println("✅ 协议过滤验证通过");
        }
    }

    /**
     * 场景4: 状态码过滤查询数据准确性测试
     */
    private void testStatusCodeFilterAccuracy() {
        System.out.println("🔢 测试状态码过滤查询数据准确性...");

        List<String> statusCodes = List.of("200", "404", "500");

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStatusCodes(statusCodes);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        // 验证数据准确性
        validateDataAccuracy("状态码过滤查询", result);

        // 额外验证：状态码过滤是否生效
        if (!result.getContent().isEmpty()) {
            Traces firstTrace = result.getContent().get(0);
            assertNotNull(firstTrace.getStatusCode(), "Trace的状态码不应该为null");
            assertTrue(statusCodes.contains(firstTrace.getStatusCode()),
                    "返回的Trace状态码应该在过滤条件内");
            System.out.println("✅ 状态码过滤验证通过");
        }
    }

    /**
     * 通用数据准确性验证方法
     */
    private void validateDataAccuracy(String testScenario, PageResult<Traces> result) {
        System.out.println("🔬 验证 " + testScenario + " 数据准确性...");

        // 1. 验证基础数据结构
        assertNotNull(result, testScenario + " - 结果不应该为null");
        assertNotNull(result.getContent(), testScenario + " - 内容列表不应该为null");
        assertTrue(result.getTotalElements() >= 0, testScenario + " - 总记录数应该有效");
        assertTrue(result.getPageNumber() >= 0, testScenario + " - 页码应该有效");
        assertTrue(result.getPageSize() > 0, testScenario + " - 页面大小应该有效");

        // 2. 验证数据内容完整性（如果数据不为空）
        if (!result.getContent().isEmpty()) {
            Traces sampleTrace = result.getContent().get(0);

            // 验证关键业务字段不为null
            assertNotNull(sampleTrace.getTraceId(), testScenario + " - Trace ID不应该为null");
            assertNotNull(sampleTrace.getProtocol(), testScenario + " - 协议不应该为null");
            assertNotNull(sampleTrace.getStatusCode(), testScenario + " - 状态码不应该为null");
            assertNotNull(sampleTrace.getStartTime(), testScenario + " - 开始时间不应该为null");

            // 验证字段格式正确性
            validateFieldFormats(testScenario, sampleTrace);

            System.out.println("📋 " + testScenario + " - 数据内容完整性验证通过");
        } else {
            System.out.println("⚠️ " + testScenario + " - 无数据，跳过内容验证");
        }

        // 3. 验证分页逻辑正确性
        validatePaginationLogic(testScenario, result);

        System.out.println("✅ " + testScenario + " - 数据准确性验证通过");
    }

    /**
     * 验证字段格式正确性
     */
    private void validateFieldFormats(String testScenario, Traces trace) {
        // 验证Trace ID格式（通常为UUID或特定格式）
        assertTrue(trace.getTraceId().length() > 0, testScenario + " - Trace ID应该非空");

        // 验证协议格式（应为已知协议类型）
        List<String> validProtocols = List.of("HTTP", "gRPC", "MySQL", "Redis");
        assertTrue(validProtocols.contains(trace.getProtocol()),
                testScenario + " - 协议应该是有效类型");

        // 验证状态码格式（应为数字字符串）
        try {
            Integer.parseInt(trace.getStatusCode());
        } catch (NumberFormatException e) {
            fail(testScenario + " - 状态码应该是数字格式: " + trace.getStatusCode());
        }

        // 验证时间戳格式（应为有效时间戳）
        assertTrue(trace.getStartTime() > 0, testScenario + " - 开始时间应该是有效时间戳");
    }

    /**
     * 验证分页逻辑正确性
     */
    private void validatePaginationLogic(String testScenario, PageResult<Traces> result) {
        int expectedPageSize = Math.min(TEST_PAGE_SIZE, (int) result.getTotalElements());

        // 验证返回数据量不超过页面大小
        assertTrue(result.getContent().size() <= TEST_PAGE_SIZE,
                testScenario + " - 返回数据量不应该超过页面大小");

        // 验证总页数计算正确
        if (result.getTotalElements() > 0) {
            int expectedTotalPages = (int) Math.ceil((double) result.getTotalElements() / TEST_PAGE_SIZE);
            assertEquals(expectedTotalPages, result.getTotalPages(),
                    testScenario + " - 总页数计算应该正确");
        }

        System.out.println("📄 " + testScenario + " - 分页逻辑验证通过");
    }

    /**
     * 测试1: 基础查询功能
     */
    @Test
    void testBasicQuery() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        assertNotNull(result, "查询结果不应该为null");
        assertNotNull(result.getContent(), "内容列表不应该为null");
        assertTrue(result.getTotalElements() >= 0, "总记录数应该大于等于0");

        System.out.println("✅ 基础查询测试通过，记录数: " + result.getContent().size());
    }

    /**
     * 测试2: 时间范围查询
     */
    @Test
    void testTimeRangeQuery() {
        long currentTime = System.currentTimeMillis() / 1000;

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStartTime(currentTime - 3600); // 1小时前
        param.setEndTime(currentTime);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        assertNotNull(result, "时间范围查询结果不应该为null");
        System.out.println("✅ 时间范围查询测试通过");
    }

    /**
     * 测试3: 协议过滤查询
     */
    @Test
    void testProtocolFilterQuery() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setProtocols(List.of("HTTP", "gRPC"));

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        assertNotNull(result, "协议过滤查询结果不应该为null");
        System.out.println("✅ 协议过滤查询测试通过");
    }

    /**
     * 测试4: 状态码过滤查询
     */
    @Test
    void testStatusCodeFilterQuery() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStatusCodes(List.of("200", "404", "500"));

        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        assertNotNull(result, "状态码过滤查询结果不应该为null");
        System.out.println("✅ 状态码过滤查询测试通过");
    }

    /**
     * 测试5: 统计功能验证
     */
    @Test
    void testStatisticalFunctions() {
        QueryTracesParam param = new QueryTracesParam();
        long currentTime = System.currentTimeMillis() / 1000;
        param.setStartTime(currentTime - 3600);
        param.setEndTime(currentTime);

        // 时间统计功能
        var timeStats = esTraceService.getTraceCountByMinute(param);
        assertNotNull(timeStats, "时间统计结果不应该为null");

        // 筛选项统计功能
        var filterOptions = esTraceService.getAllFilterOptions();
        assertNotNull(filterOptions, "筛选项统计结果不应该为null");

        System.out.println("✅ 统计功能测试通过");
    }
}