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
 * 性能测试类 - 测试查询性能和并发能力
 * 测试目标：建立性能基准，监控系统性能
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class PerformanceIntegrationTest {

    @Autowired
    private EsTraceService esTraceService;

    private static final int TEST_PAGE_SIZE = 5;
    private static final long PERFORMANCE_THRESHOLD_MS = 5000; // 5秒阈值
    private static final long COMPLEX_QUERY_THRESHOLD_MS = 10000; // 10秒阈值

    @BeforeEach
    void setUp() {
        assertNotNull(esTraceService, "Service应该被正确注入");
    }

    /**
     * 测试1: 基础查询性能基准
     */
    @Test
    void testBasicQueryPerformance() {
        long startTime = System.currentTimeMillis();

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        long queryTime = System.currentTimeMillis() - startTime;

        assertNotNull(result, "性能测试查询结果不应该为null");
        assertTrue(queryTime < PERFORMANCE_THRESHOLD_MS,
                "基础查询应该在" + PERFORMANCE_THRESHOLD_MS + "ms内完成，实际耗时: " + queryTime + "ms");

        System.out.println("✅ 基础查询性能测试通过，耗时: " + queryTime + "ms");
    }

    /**
     * 测试2: 复杂查询性能基准
     */
    @Test
    void testComplexQueryPerformance() {
        long startTime = System.currentTimeMillis();

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setProtocols(List.of("HTTP"));
        param.setStatusCodes(List.of("200"));
        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        long queryTime = System.currentTimeMillis() - startTime;

        assertNotNull(result, "复杂查询结果不应该为null");
        assertTrue(queryTime < COMPLEX_QUERY_THRESHOLD_MS,
                "复杂查询应该在" + COMPLEX_QUERY_THRESHOLD_MS + "ms内完成，实际耗时: " + queryTime + "ms");

        System.out.println("✅ 复杂查询性能测试通过，耗时: " + queryTime + "ms");
    }

    /**
     * 测试3: 时间范围查询性能
     */
    @Test
    void testTimeRangeQueryPerformance() {
        long currentTime = System.currentTimeMillis() / 1000;

        long startTime = System.currentTimeMillis();

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStartTime(currentTime - 3600);
        param.setEndTime(currentTime);
        PageResult<Traces> result = esTraceService.queryByPageResult(param);

        long queryTime = System.currentTimeMillis() - startTime;

        assertNotNull(result, "时间范围查询结果不应该为null");
        assertTrue(queryTime < PERFORMANCE_THRESHOLD_MS,
                "时间范围查询应该在" + PERFORMANCE_THRESHOLD_MS + "ms内完成，实际耗时: " + queryTime + "ms");

        System.out.println("✅ 时间范围查询性能测试通过，耗时: " + queryTime + "ms");
    }

    /**
     * 测试4: 统计功能性能
     */
    @Test
    void testStatisticalFunctionPerformance() {
        QueryTracesParam param = new QueryTracesParam();
        long currentTime = System.currentTimeMillis() / 1000;
        param.setStartTime(currentTime - 3600);
        param.setEndTime(currentTime);

        long startTime = System.currentTimeMillis();

        // 时间统计功能
        var timeStats = esTraceService.getTraceCountByMinute(param);

        long statTime = System.currentTimeMillis() - startTime;

        assertNotNull(timeStats, "时间统计结果不应该为null");
        assertTrue(statTime < COMPLEX_QUERY_THRESHOLD_MS,
                "统计功能应该在" + COMPLEX_QUERY_THRESHOLD_MS + "ms内完成，实际耗时: " + statTime + "ms");

        System.out.println("✅ 统计功能性能测试通过，耗时: " + statTime + "ms");
    }

    /**
     * 测试5: 多次查询性能稳定性
     */
    @Test
    void testQueryStability() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);

        long totalTime = 0;
        int iterations = 3;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            PageResult<Traces> result = esTraceService.queryByPageResult(param);
            long queryTime = System.currentTimeMillis() - startTime;

            assertNotNull(result, "查询结果不应该为null");
            totalTime += queryTime;

            System.out.println("第" + (i+1) + "次查询耗时: " + queryTime + "ms");
        }

        long averageTime = totalTime / iterations;
        assertTrue(averageTime < PERFORMANCE_THRESHOLD_MS,
                "平均查询时间应该在" + PERFORMANCE_THRESHOLD_MS + "ms内，实际平均: " + averageTime + "ms");

        System.out.println("✅ 查询稳定性测试通过，平均耗时: " + averageTime + "ms");
    }
}