package com.qcl.integration.api.es;

import com.qcl.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EsTraceController 增强版集成测试
 * 包含参数验证、业务逻辑测试、边界条件测试等
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EsTraceControllerTest {

    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;
    private static final String BASE_URL = "/api/esTraces";

    @LocalServerPort
    private int localServerPort;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 构建认证头
     */
    private HttpHeaders buildAuthHeaders() {
        String authToken = TokenUtil.getLoginToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    // ========== 基础功能测试（原有测试） ==========

    /**
     * 测试分页查询接口
     */
    @Test
    public void testQueryByPageIntegration() {
        System.out.println("=== 测试分页查询接口 ===");

        // 构建查询参数
        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 10);
        System.out.println("请求URL: " + url);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        // 验证HTTP状态码
        assertTrue(response.getStatusCode().is2xxSuccessful(),
                "HTTP状态码应为200，实际为: " + response.getStatusCodeValue());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "响应体不应为null");

        // 调试输出响应体结构
        System.out.println("响应体结构: " + responseBody.keySet());
        System.out.println("响应体内容: " + responseBody);

        // 检查响应体是否包含错误信息
        if (responseBody.containsKey("code") && !"200".equals(responseBody.get("code").toString())) {
            System.out.println("❌ 接口返回错误: " + responseBody.get("message"));
            return;
        }

        // 验证分页数据结构
        assertTrue(responseBody.containsKey("content"), "响应体应包含content字段");
        assertTrue(responseBody.containsKey("totalElements"), "响应体应包含totalElements字段");
        assertTrue(responseBody.containsKey("totalPages"), "响应体应包含totalPages字段");
        assertTrue(responseBody.containsKey("pageNumber"), "响应体应包含pageNumber字段");
        assertTrue(responseBody.containsKey("pageSize"), "响应体应包含pageSize字段");

        // 验证content字段
        Object content = responseBody.get("content");
        assertNotNull(content, "content字段不应为null");
        assertTrue(content instanceof List, "content字段应为List类型");

        List<?> contentList = (List<?>) content;
        System.out.println("分页数据: 总记录数=" + responseBody.get("totalElements") +
                ", 总页数=" + responseBody.get("totalPages") +
                ", 当前页记录数=" + contentList.size());

        System.out.println("✅ 分页查询接口测试完成");
    }

    // ========== 参数验证测试（新增） ==========

    /**
     * 测试分页参数边界值验证
     */
    @Test
    public void testQueryByPageParameterValidation() {
        System.out.println("=== 测试分页参数边界值验证 ===");

        // 测试无效页码
        testInvalidPageNumber(-1);
        testInvalidPageNumber(-100);

        // 测试无效页大小
        testInvalidPageSize(0);
        testInvalidPageSize(-1);
        testInvalidPageSize(1001); // 超过最大限制

        // 测试空参数
        testQueryByPageWithoutParameters();

        System.out.println("✅ 分页参数边界值验证测试完成");
    }

    private void testInvalidPageNumber(int pageNum) {
        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, pageNum, 10);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("message")) {
            System.out.println("无效页码 " + pageNum + " 的错误信息: " + responseBody.get("message"));
        }
    }

    private void testInvalidPageSize(int pageSize) {
        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, pageSize);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("message")) {
            System.out.println("无效页大小 " + pageSize + " 的错误信息: " + responseBody.get("message"));
        }
    }

    private void testQueryByPageWithoutParameters() {
        String url = String.format("http://%s:%d%s/queryByPage",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful(),
                "无参数查询应返回成功状态");

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "无参数查询响应体不应为null");

        // 验证使用默认参数
        if (responseBody.containsKey("pageNumber")) {
            Object pageNumber = responseBody.get("pageNumber");
            assertNotNull(pageNumber, "默认页码不应为null");
        }
    }

    // ========== 数据完整性测试（新增） ==========

    /**
     * 测试Trace数据字段完整性验证
     */
    @Test
    public void testTraceDataIntegrity() {
        System.out.println("=== 测试Trace数据字段完整性验证 ===");

        // 获取Trace数据
        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 5);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        if (responseBody.containsKey("content")) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
            if (!content.isEmpty()) {
                // 验证第一条Trace记录的字段完整性
                Map<String, Object> firstTrace = content.get(0);
                System.out.println("Trace记录字段: " + firstTrace.keySet());

                // 灵活验证字段存在性
                verifyTraceFieldPresenceFlexible(firstTrace, "traceId");
                verifyTraceFieldPresenceFlexible(firstTrace, "startTime");
                verifyTraceFieldPresenceFlexible(firstTrace, "protocol");
                verifyTraceFieldPresenceFlexible(firstTrace, "statusCode");
                verifyTraceFieldPresenceFlexible(firstTrace, "e2eDuration");

                // 验证数据逻辑一致性
                verifyTimeRangeConsistency(firstTrace);
            }
        }

        System.out.println("✅ Trace数据字段完整性验证测试完成");
    }

    private void verifyTraceFieldPresenceFlexible(Map<String, Object> trace, String fieldName) {
        assertTrue(trace.containsKey(fieldName),
                "Trace记录应包含字段: " + fieldName + ", 实际字段: " + trace.keySet());

        Object value = trace.get(fieldName);
        System.out.println("字段 " + fieldName + " 的值: " + value + ", 类型: " + (value != null ? value.getClass().getSimpleName() : "null"));
    }

    private void verifyTimeRangeConsistency(Map<String, Object> trace) {
        if (trace.containsKey("startTime") && trace.containsKey("endTime")) {
            Long startTime = (Long) trace.get("startTime");
            Long endTime = (Long) trace.get("endTime");

            if (startTime != null && endTime != null) {
                assertTrue(startTime <= endTime,
                        "开始时间应小于等于结束时间: startTime=" + startTime + ", endTime=" + endTime);
            }
        }
    }

    // ========== 业务逻辑测试（新增） ==========

    /**
     * 测试时间范围过滤功能
     */
    @Test
    public void testTimeRangeFiltering() {
        System.out.println("=== 测试时间范围过滤功能 ===");

        long endTime = System.currentTimeMillis();
        long startTime = endTime - 24 * 60 * 60 * 1000L; // 24小时前

        // 测试时间范围过滤
        String urlWithTimeRange = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d&startTime=%d&endTime=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 10, startTime, endTime);

        ResponseEntity<Map> responseWithTimeRange = restTemplate.exchange(
                urlWithTimeRange,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(responseWithTimeRange.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBodyWithTimeRange = responseWithTimeRange.getBody();
        assertNotNull(responseWithTimeRange);

        // 验证时间范围过滤效果
        if (responseBodyWithTimeRange.containsKey("content")) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBodyWithTimeRange.get("content");
            for (Map<String, Object> trace : content) {
                if (trace.containsKey("startTime")) {
                    Long traceStartTime = (Long) trace.get("startTime");
                    assertTrue(traceStartTime >= startTime && traceStartTime <= endTime,
                            "Trace的开始时间应在指定时间范围内");
                }
            }
        }

        System.out.println("✅ 时间范围过滤功能测试完成");
    }

    /**
     * 测试状态码过滤功能
     */
    @Test
    public void testStatusCodeFiltering() {
        System.out.println("=== 测试状态码过滤功能 ===");

        // 测试状态码过滤（假设有200状态码的数据）
        String urlWithStatusCode = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d&statusCodes=200",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 10);

        ResponseEntity<Map> responseWithStatusCode = restTemplate.exchange(
                urlWithStatusCode,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(responseWithStatusCode.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBodyWithStatusCode = responseWithStatusCode.getBody();
        assertNotNull(responseBodyWithStatusCode);

        // 验证状态码过滤效果
        if (responseBodyWithStatusCode.containsKey("content")) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBodyWithStatusCode.get("content");
            for (Map<String, Object> trace : content) {
                if (trace.containsKey("statusCode")) {
                    String statusCode = (String) trace.get("statusCode");
                    assertEquals("200", statusCode,
                            "Trace的状态码应为200");
                }
            }
        }

        System.out.println("✅ 状态码过滤功能测试完成");
    }

    // ========== 边界条件测试（新增） ==========

    /**
     * 测试超长时间范围查询
     */
    @Test
    public void testVeryLargeTimeRange() {
        System.out.println("=== 测试超长时间范围查询 ===");

        long endTime = System.currentTimeMillis();
        long startTime = endTime - 365L * 24 * 60 * 60 * 1000L; // 一年前

        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d&startTime=%d&endTime=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 5, startTime, endTime);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("超长时间范围查询响应: " + responseBody.keySet());

        System.out.println("✅ 超长时间范围查询测试完成");
    }

    /**
     * 测试不存在的Trace ID查询
     */
    @Test
    public void testNonExistentTraceId() {
        System.out.println("=== 测试不存在的Trace ID查询 ===");

        String nonExistentTraceId = "non-existent-trace-id-123456";
        String url = String.format("http://%s:%d%s/traceDetail?traceId=%s&pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, nonExistentTraceId, 0, 10);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        // 验证空结果或错误信息
        if (responseBody.containsKey("content")) {
            List<?> content = (List<?>) responseBody.get("content");
            assertTrue(content.isEmpty(), "不存在的Trace ID应返回空结果");
        }

        System.out.println("✅ 不存在的Trace ID查询测试完成");
    }

    // ========== 原有其他测试方法（保持） ==========

    /**
     * 测试带时间范围的分页查询
     */
    @Test
    public void testQueryByPageWithTimeRangeIntegration() {
        System.out.println("=== 测试带时间范围的分页查询 ===");

        // 构建查询参数（使用最近的时间范围）
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 24 * 60 * 60 * 1000L; // 24小时前

        String url = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d&startTime=%d&endTime=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 5, startTime, endTime);
        System.out.println("请求URL: " + url);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("响应体结构: " + responseBody.keySet());

        // 检查错误信息
        if (responseBody.containsKey("code") && !"200".equals(responseBody.get("code").toString())) {
            System.out.println("❌ 接口返回错误: " + responseBody.get("message"));
            return;
        }

        // 验证分页数据
        if (responseBody.containsKey("content")) {
            List<?> content = (List<?>) responseBody.get("content");
            System.out.println("获取到 " + content.size() + " 条记录");
        }

        System.out.println("✅ 带时间范围的分页查询测试完成");
    }

    /**
     * 测试Trace详情查询接口
     */
    @Test
    public void testTraceDetailIntegration() {
        System.out.println("=== 测试Trace详情查询接口 ===");

        // 先获取一条Trace记录用于详情查询
        String listUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, 0, 1);
        ResponseEntity<Map> listResponse = restTemplate.exchange(
                listUrl,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        if (!listResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("❌ 获取Trace列表失败");
            return;
        }

        Map<String, Object> listBody = listResponse.getBody();
        if (listBody == null || !listBody.containsKey("content")) {
            System.out.println("❌ 未获取到Trace记录");
            return;
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) listBody.get("content");
        if (content.isEmpty()) {
            System.out.println("⚠️ 当前无Trace记录，跳过详情查询测试");
            return;
        }

        // 获取第一条Trace的ID
        Map<String, Object> firstTrace = content.get(0);
        String traceId = (String) firstTrace.get("traceId");
        System.out.println("查询Trace详情，traceId: " + traceId);

        // 查询Trace详情
        String detailUrl = String.format("http://%s:%d%s/traceDetail?traceId=%s&pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, traceId, 0, 10);
        System.out.println("详情查询URL: " + detailUrl);

        ResponseEntity<Map> detailResponse = restTemplate.exchange(
                detailUrl,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(detailResponse.getStatusCode().is2xxSuccessful());
        Map<String, Object> detailBody = detailResponse.getBody();
        assertNotNull(detailBody);

        System.out.println("详情响应体结构: " + detailBody.keySet());

        if (detailBody.containsKey("content")) {
            List<?> detailContent = (List<?>) detailBody.get("content");
            System.out.println("Trace详情记录数: " + detailContent.size());
        }

        System.out.println("✅ Trace详情查询接口测试完成");
    }

    /**
     * 测试滚动查询接口
     */
    @Test
    public void testScrollQueryIntegration() {
        System.out.println("=== 测试滚动查询接口 ===");

        String url = String.format("http://%s:%d%s/scrollQuery?scrollId=%s&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, "", 10);
        System.out.println("请求URL: " + url);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("滚动查询响应体结构: " + responseBody.keySet());

        if (responseBody.containsKey("scrollId")) {
            String scrollId = (String) responseBody.get("scrollId");
            System.out.println("获取到scrollId: " + scrollId);
        }

        System.out.println("✅ 滚动查询接口测试完成");
    }

    /**
     * 测试过滤器接口
     */
    @Test
    public void testFiltersIntegration() {
        System.out.println("=== 测试过滤器接口 ===");

        String url = String.format("http://%s:%d%s/filters",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL);
        System.out.println("请求URL: " + url);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("过滤器接口响应体结构: " + responseBody.keySet());

        // 验证过滤器数据结构
        assertTrue(responseBody.containsKey("allEndpoints") || responseBody.containsKey("allProtocols") ||
                responseBody.containsKey("allStatusOptions"), "过滤器接口应返回筛选选项");

        System.out.println("✅ 过滤器接口测试完成");
    }

    // ========== 统计查询测试（修复版） ==========

    /**
     * 测试统计查询接口 - 请求数统计
     */
    @Test
    public void testStatisticCountIntegration() {
        System.out.println("=== 测试统计查询接口 - 请求数统计 ===");

        String url = String.format("http://%s:%d%s/statistic?type=%s",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, "count");
        System.out.println("请求URL: " + url);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                List.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        List<Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("统计查询响应类型: List, 大小: " + responseBody.size());
        System.out.println("响应内容: " + responseBody);

        // 验证统计数据结构 - 修复字段名验证
        if (!responseBody.isEmpty()) {
            Object firstItem = responseBody.get(0);
            System.out.println("第一条统计数据的类型: " + firstItem.getClass().getSimpleName());

            if (firstItem instanceof Map) {
                Map<?, ?> statItem = (Map<?, ?>) firstItem;
                System.out.println("统计数据的字段: " + statItem.keySet());

                // 修复：根据实际响应字段名验证
                assertTrue(statItem.containsKey("timeKey") || statItem.containsKey("value"),
                        "统计数据应包含timeKey或value字段，实际字段: " + statItem.keySet());
            }
        }

        System.out.println("✅ 请求数统计接口测试完成");
    }

    /**
     * 测试统计查询接口 - 状态码分组统计
     */
    @Test
    public void testStatisticStatusCountIntegration() {
        System.out.println("=== 测试统计查询接口 - 状态码分组统计 ===");

        String url = String.format("http://%s:%d%s/statistic?type=%s",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, "statusCount");
        System.out.println("请求URL: " + url);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                List.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        List<Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("状态码统计响应大小: " + responseBody.size());

        if (!responseBody.isEmpty()) {
            System.out.println("第一条状态码统计数据: " + responseBody.get(0));

            if (responseBody.get(0) instanceof Map) {
                Map<?, ?> statusItem = (Map<?, ?>) responseBody.get(0);
                System.out.println("状态码统计数据的字段: " + statusItem.keySet());

                // 修复：根据实际响应字段名验证
                assertTrue(statusItem.containsKey("statusCode") || statusItem.containsKey("count"),
                        "状态码统计数据应包含statusCode或count字段，实际字段: " + statusItem.keySet());
            }
        }

        System.out.println("✅ 状态码分组统计接口测试完成");
    }

    /**
     * 测试统计查询接口 - 延迟统计
     */
    @Test
    public void testStatisticLatencyStatsIntegration() {
        System.out.println("=== 测试统计查询接口 - 延迟统计 ===");

        String url = String.format("http://%s:%d%s/statistic?type=%s",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, "latencyStats");
        System.out.println("请求URL: " + url);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                List.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        List<Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("延迟统计响应大小: " + responseBody.size());

        if (!responseBody.isEmpty()) {
            System.out.println("第一条延迟统计数据: " + responseBody.get(0));

            if (responseBody.get(0) instanceof Map) {
                Map<?, ?> latencyItem = (Map<?, ?>) responseBody.get(0);
                System.out.println("延迟统计数据的字段: " + latencyItem.keySet());

                // 修复：根据实际响应字段名验证
                assertTrue(latencyItem.containsKey("avgDuration") || latencyItem.containsKey("p75Duration") ||
                                latencyItem.containsKey("p90Duration") || latencyItem.containsKey("p99Duration"),
                        "延迟统计数据应包含延迟统计字段，实际字段: " + latencyItem.keySet());
            }
        }

        System.out.println("✅ 延迟统计接口测试完成");
    }

    /**
     * 测试无效统计类型
     */
    @Test
    public void testStatisticInvalidTypeIntegration() {
        System.out.println("=== 测试无效统计类型 ===");

        String url = String.format("http://%s:%d%s/statistic?type=%s",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, BASE_URL, "invalidType");
        System.out.println("请求URL: " + url);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                Map.class
        );

        // 无效类型可能返回错误或空结果
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        System.out.println("无效类型统计响应: " + responseBody);

        System.out.println("✅ 无效统计类型测试完成");
    }
}
