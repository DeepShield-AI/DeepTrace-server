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
 * 查询功能测试类 - 测试各种查询条件和组合
 * 测试目标：验证复杂查询场景的正确性
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class QueryIntegrationTest {

    @Autowired
    private EsTraceService esTraceService;

    private static final int TEST_PAGE_SIZE = 5;

    @BeforeEach
    void setUp() {
        assertNotNull(esTraceService, "Service应该被正确注入");
    }

    /**
     * 测试1: 多条件组合查询 - 时间范围 + 协议过滤
     */
    @Test
    void testMultiConditionQuery1() {
        long currentTime = System.currentTimeMillis() / 1000;

        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setStartTime(currentTime - 3600);
        param.setEndTime(currentTime);
        param.setProtocols(List.of("HTTP", "gRPC"));

        PageResult<Traces> result = esTraceService.queryByPageResult(param);
        assertNotNull(result, "多条件组合查询结果不应该为null");

        System.out.println("✅ 时间范围+协议过滤组合查询测试通过");
    }

    /**
     * 测试2: 多条件组合查询 - 协议 + 状态码过滤
     */
    @Test
    void testMultiConditionQuery2() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setProtocols(List.of("HTTP"));
        param.setStatusCodes(List.of("200", "404"));

        PageResult<Traces> result = esTraceService.queryByPageResult(param);
        assertNotNull(result, "协议+状态码组合查询结果不应该为null");

        System.out.println("✅ 协议+状态码组合查询测试通过");
    }

    /**
     * 测试3: 响应时间范围查询
     */
    @Test
    void testDurationRangeQuery() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);
        param.setMinE2eDuration(100L); // 100ms以上
        param.setMaxE2eDuration(5000L); // 5s以下

        PageResult<Traces> result = esTraceService.queryByPageResult(param);
        assertNotNull(result, "响应时间范围查询结果不应该为null");

        System.out.println("✅ 响应时间范围查询测试通过");
    }

    /**
     * 测试4: 空条件查询（返回所有数据）
     */
    @Test
    void testEmptyConditionQuery() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);

        PageResult<Traces> result = esTraceService.queryByPageResult(param);
        assertNotNull(result, "空条件查询结果不应该为null");

        System.out.println("✅ 空条件查询测试通过");
    }

    /**
     * 测试5: 查询结果一致性验证
     */
    @Test
    void testQueryConsistency() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(TEST_PAGE_SIZE);

        // 第一次查询
        PageResult<Traces> result1 = esTraceService.queryByPageResult(param);
        // 第二次相同条件查询
        PageResult<Traces> result2 = esTraceService.queryByPageResult(param);

        // 验证一致性
        assertEquals(result1.getTotalElements(), result2.getTotalElements(),
                "相同条件的两次查询应该返回相同数量的记录");

        System.out.println("✅ 查询结果一致性测试通过");
    }
}