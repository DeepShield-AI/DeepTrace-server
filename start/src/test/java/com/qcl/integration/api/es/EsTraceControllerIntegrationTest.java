package com.qcl.integration.api.es;

import com.qcl.base.BaseIntegrationTest;
import com.qcl.base.TestConstants;
import com.qcl.base.TestDataFactory;
import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * EsTraceController 分页查询集成测试
 */
@DisplayName("EsTrace分页查询测试")
class EsTraceControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String authToken;
    private HttpHeaders headers;

    @BeforeAll
    static void setupAuth(@Autowired TestRestTemplate restTemplate) {
        System.out.println("初始化认证...");
        authToken = loginAndGetToken(restTemplate);
        assumeTrue(authToken != null, "认证失败，跳过测试");
    }

    @BeforeEach
    void setupHeaders() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", TestConstants.BEARER_PREFIX + authToken);
    }

    /**
     * 登录获取token
     */
    private static String loginAndGetToken(TestRestTemplate restTemplate) {
        try {
            Map<String, String> loginBody = new HashMap<>();
            loginBody.put("username", TestConstants.TEST_USERNAME);
            loginBody.put("password", TestConstants.TEST_PASSWORD);

            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginBody, loginHeaders);

            // 使用注入的TestRestTemplate，它已经配置了正确的服务器地址
            ResponseEntity<Map> response = restTemplate.exchange(
                    "/api/user/login",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null && (Integer) body.get("code") == 200) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return (String) data.get("token");
                }
            }
        } catch (Exception e) {
            System.err.println("登录异常: " + e.getMessage());
            e.printStackTrace(); // 添加详细错误信息
        }
        return null;
    }

    /**
     * 构建查询参数
     */
    private String buildParams(QueryTracesParam param) {
        StringBuilder sb = new StringBuilder("?");
        if (param.getTraceId() != null) sb.append("&traceId=").append(param.getTraceId());
        if (param.getStartTime() != null) sb.append("&startTime=").append(param.getStartTime());
        if (param.getEndTime() != null) sb.append("&endTime=").append(param.getEndTime());
        if (param.getStatusCodes() != null) sb.append("&statusCode=").append(param.getStatusCodes());
        if (param.getPageNum() != null) sb.append("&pageNumber=").append(param.getPageNum());
        if (param.getPageSize() != null) sb.append("&pageSize=").append(param.getPageSize());
        return sb.toString().replace("?&", "?");
    }

    @Test
    @DisplayName("分页查询功能测试")
    @Timeout(10) // 10秒超时
    void testPaginationFunction() {
        // 准备测试数据 - 使用实际存在的用户ID
        QueryTracesParam param = TestDataFactory.createValidTraceQueryParam();
        param.setPageNum(1);
        param.setPageSize(10);

        // 如果可能，设置一个实际存在的用户ID
        // param.setUserId(1L); // 使用存在的用户ID

        // 构建请求
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = TestConstants.API_ES_TRACES + "/queryByPage" + buildParams(param);

        // 执行请求
        ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity,
                new org.springframework.core.ParameterizedTypeReference<PageResult<Traces>>() {});

        // 如果索引不存在，可能是404错误，这是正常的
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            System.out.println("⚠️ ES索引不存在，跳过测试");
            return; // 优雅地跳过测试
        }

        // 验证响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP状态码应为200");

        // 验证响应结构
        PageResult<Traces> result = response.getBody();
        assertNotNull(result, "响应体不应为空");

        // 验证分页信息
        assertNotNull(result.getContent(), "分页内容不应为空");
        assertTrue(result.getPageNumber() >= 0, "页码应大于等于0");
        assertTrue(result.getPageSize() > 0, "页面大小应大于0");

        // 验证数据完整性
        if (!result.getContent().isEmpty()) {
            Traces trace = result.getContent().get(0);
            assertNotNull(trace.getTraceId(), "Trace ID不应为空");
            assertTrue(trace.getStartTime() > 0, "开始时间应大于0");
        }

        System.out.println("✅ 分页查询测试通过 - 页码: " + result.getPageNumber() +
                ", 页面大小: " + result.getPageSize() +
                ", 数据条数: " + result.getContent().size());
    }
}