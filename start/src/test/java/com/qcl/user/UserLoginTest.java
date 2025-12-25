package com.qcl.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户登录功能验证测试类
 * 使用现有的用户名密码验证登录功能并获取token
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserLoginTest {

    // 远程服务器配置
    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;
    private static final String USER_API_BASE_PATH = "/api/user";
    private static final String TRACE_API_BASE_PATH = "/api/esTraces";

    // 现有的用户名和密码
    private static final String EXISTING_USERNAME = "tian";
    private static final String EXISTING_PASSWORD = "t4139567";

    // 存储获取的token
    private String authToken;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 创建HTTP请求头
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 创建带认证头的HTTP实体
     */
    private HttpEntity<?> createAuthHttpEntity() {
        HttpHeaders headers = createHeaders();
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }
        return new HttpEntity<>(headers);
    }

    /**
     * 测试用户登录功能
     */
    @Test
    public void testUserLogin() {
        System.out.println("=== 测试用户登录功能 ===");
        System.out.println("用户名: " + EXISTING_USERNAME);
        System.out.println("密码: " + EXISTING_PASSWORD);

        // 构建登录请求体
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", EXISTING_USERNAME);
        loginBody.put("password", EXISTING_PASSWORD);

        String loginUrl = String.format("http://%s:%d%s/login",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, USER_API_BASE_PATH);

        System.out.println("登录URL: " + loginUrl);

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginBody, createHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // 输出响应状态信息
            System.out.println("响应状态码: " + response.getStatusCodeValue());
            System.out.println("响应头: " + response.getHeaders());

            // 验证响应状态
            assertTrue(response.getStatusCode().is2xxSuccessful(),
                    "登录API调用应返回成功状态码，实际状态码: " + response.getStatusCodeValue());

            assertNotNull(response.getBody(), "登录响应体不应为null");

            Map<String, Object> responseBody = response.getBody();
            System.out.println("登录响应体: " + responseBody);

            // 验证登录结果
            assertNotNull(responseBody.get("code"), "响应应包含code字段");
            assertEquals(200, responseBody.get("code"),
                    "登录应返回成功状态码200，实际code: " + responseBody.get("code"));

            assertNotNull(responseBody.get("data"), "登录返回数据不应为null");

            Map<String, Object> tokenData = (Map<String, Object>) responseBody.get("data");
            assertNotNull(tokenData.get("token"), "token不应为null");
            assertNotNull(tokenData.get("tokenHead"), "tokenHead不应为null");

            // 保存token供后续测试使用
            authToken = (String) tokenData.get("token");
            System.out.println("✅ Token获取成功!");
            System.out.println("Token Head: " + tokenData.get("tokenHead"));
            System.out.println("Token (前50字符): " + authToken.substring(0, Math.min(50, authToken.length())) + "...");
            System.out.println("Token 长度: " + authToken.length() + " 字符");

        } catch (Exception e) {
            System.err.println("❌ 用户登录失败: " + e.getMessage());
            e.printStackTrace();

            // 提供详细的错误分析
            if (e.getMessage().contains("401")) {
                System.err.println("❌ 认证失败 - 用户名或密码错误");
            } else if (e.getMessage().contains("404")) {
                System.err.println("❌ API接口不存在 - 请检查URL是否正确");
            } else if (e.getMessage().contains("500")) {
                System.err.println("❌ 服务器内部错误 - 请检查服务器状态");
            } else if (e.getMessage().contains("Connection refused")) {
                System.err.println("❌ 连接被拒绝 - 请检查服务器是否运行在 " + REMOTE_SERVER_HOST + ":" + REMOTE_SERVER_PORT);
            }

            fail("用户登录应成功执行，错误信息: " + e.getMessage());
        }
    }

    /**
     * 测试使用token访问受保护API
     */
    @Test
    public void testProtectedApiWithToken() {
        System.out.println("=== 测试使用token访问受保护API ===");

        // 先执行登录测试获取token
        testUserLogin();

        if (authToken == null || authToken.isEmpty()) {
            fail("❌ 无法获取有效token，无法进行API测试");
        }

        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, TRACE_API_BASE_PATH, 1, 10);

        System.out.println("API URL: " + apiUrl);
        System.out.println("使用的Token: Bearer " + authToken.substring(0, Math.min(30, authToken.length())) + "...");

        try {
            HttpEntity<?> requestEntity = createAuthHttpEntity();
            System.out.println("请求头: " + requestEntity.getHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            // 输出响应状态信息
            System.out.println("响应状态码: " + response.getStatusCodeValue());
            System.out.println("响应体: " + response.getBody());

            // 验证响应状态
            assertTrue(response.getStatusCode().is2xxSuccessful(),
                    "带token的API调用应返回成功状态码，实际状态码: " + response.getStatusCodeValue());

            Map<String, Object> responseBody = response.getBody();
            assertNotNull(responseBody, "API响应体不应为null");

            // 验证API返回的数据结构
            assertNotNull(responseBody.get("code"), "响应应包含code字段");
            assertEquals(200, responseBody.get("code"),
                    "API调用应返回成功状态码200，实际code: " + responseBody.get("code"));

            System.out.println("✅ 带token的API调用成功!");

        } catch (Exception e) {
            System.err.println("❌ 带token的API调用失败: " + e.getMessage());
            e.printStackTrace();

            // 提供详细的错误分析
            if (e.getMessage().contains("401")) {
                System.err.println("❌ Token无效或已过期 - 需要重新登录获取新token");
            } else if (e.getMessage().contains("403")) {
                System.err.println("❌ 权限不足 - 用户没有访问该API的权限");
            }

            fail("带token的API调用应成功执行，错误信息: " + e.getMessage());
        }
    }

    /**
     * 测试无token访问受保护API（应失败）
     */
    @Test
    public void testProtectedApiWithoutToken() {
        System.out.println("=== 测试无token访问受保护API（预期失败） ===");

        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, TRACE_API_BASE_PATH, 1, 10);

        System.out.println("API URL: " + apiUrl);

        try {
            // 创建不带认证头的请求
            HttpEntity<?> requestEntity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            // 输出响应状态信息
            System.out.println("响应状态码: " + response.getStatusCodeValue());
            System.out.println("响应体: " + response.getBody());

            // 验证响应状态应为401（未授权）
            assertEquals(401, response.getStatusCodeValue(),
                    "无token的API调用应返回401状态码，实际状态码: " + response.getStatusCodeValue());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                assertNotNull(responseBody.get("code"), "响应应包含code字段");
                assertEquals(401, responseBody.get("code"),
                        "无token的API调用应返回401状态码，实际code: " + responseBody.get("code"));
            }

            System.out.println("✅ 无token的API调用正确返回401错误!");

        } catch (Exception e) {
            System.err.println("❌ 无token的API调用异常: " + e.getMessage());
            // 对于无token的情况，异常也是预期的结果
            System.out.println("✅ 无token的API调用正确抛出异常!");
        }
    }

    /**
     * 完整的登录验证流程
     */
    @Test
    public void testCompleteLoginVerification() {
        System.out.println("=== 开始完整登录验证流程 ===");

        // 1. 测试无token访问（应失败）
        testProtectedApiWithoutToken();

        // 2. 测试登录获取token
        testUserLogin();

        // 3. 测试带token访问（应成功）
        testProtectedApiWithToken();

        System.out.println("✅ 完整登录验证流程完成!");
    }
}
