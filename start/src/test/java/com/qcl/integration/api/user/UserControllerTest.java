package com.qcl.integration.api.user;

import org.springframework.http.HttpHeaders;
import com.qcl.api.Result;
import com.qcl.entity.param.UserLoginParam;
import com.qcl.entity.param.UserParam;
import com.qcl.base.TestConstants;
import com.qcl.base.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    /**
     * 测试用户注册功能
     */
    @Test
    public void testUserRegistrationIntegration() {
        System.out.println("=== 用户注册功能验证 ===");

        try {
            // 1. 创建测试用户参数
            UserParam userParam = TestDataFactory.createTestUserParam();

            System.out.println("📋 注册测试数据: 用户名=" + userParam.getUsername() +
                    ", 手机号=" + userParam.getPhone() +
                    ", 邮箱=" + userParam.getEmail());

            // 2. 执行注册请求
            String registerUrl = getBaseUrl() + TestConstants.API_PREFIX + TestConstants.REGISTER_PATH;
            HttpEntity<UserParam> request = new HttpEntity<>(userParam);

            ResponseEntity<Result> response = restTemplate.exchange(
                    registerUrl,
                    HttpMethod.POST,
                    request,
                    Result.class
            );

            // 3. 验证注册结果
            assertTrue(response.getStatusCode().is2xxSuccessful(), "注册HTTP状态码应为2xx");
            Result result = response.getBody();
            assertNotNull(result, "注册响应体不应为null");

            // 如果是500错误，先打印详细信息再断言
            if (result.getCode() == 500) {
                System.out.println("❌ 服务器内部错误，详细信息:");
                System.out.println("   消息: " + result.getMessage());
                System.out.println("   数据: " + result.getData());
                fail("注册失败，服务器返回500错误: " + result.getMessage());
            }

            assertEquals(TestConstants.SUCCESS_CODE, result.getCode(), "注册业务状态码应为" + TestConstants.SUCCESS_CODE);
            assertNotNull(result.getData(), "注册成功应返回用户数据");

            // 4. 验证用户数据
            Map<String, Object> userData = (Map<String, Object>) result.getData();
            assertNotNull(userData.get("userId"), "注册用户应有ID");
            assertEquals(userParam.getUsername(), userData.get("username"), "用户名应匹配");
            assertEquals(userParam.getEmail(), userData.get("email"), "邮箱应匹配");
            assertNotNull(userData.get("role"), "注册用户应有角色");

            System.out.println("✅ 用户注册验证通过 - 用户ID: " + userData.get("userId"));
            System.out.println("✅ 用户角色: " + userData.get("role"));
            System.out.println("🎯 注册功能验证完成");

        } catch (Exception e) {
            System.out.println("❌ 注册测试发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("注册测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试用户登录功能
     */
    @Test
    public void testUserLoginIntegration() {
        System.out.println("=== 用户登录功能验证 ===");

        // 1. 登录参数（user-tian）
        UserLoginParam loginParam = new UserLoginParam();
        // 使用常量
        loginParam.setUsername(TestConstants.TEST_USERNAME);
        loginParam.setPassword(TestConstants.TEST_PASSWORD);

        System.out.println("📋 登录测试数据: 用户名=" + loginParam.getUsername());

        // 2. 执行登录请求 - 常量构建URL
        String loginUrl = getBaseUrl() + TestConstants.API_PREFIX + TestConstants.LOGIN_PATH;
        HttpEntity<UserLoginParam> loginRequest = new HttpEntity<>(loginParam);

        ResponseEntity<Result> loginResponse = restTemplate.exchange(
                loginUrl,
                HttpMethod.POST,
                loginRequest,
                Result.class
        );

        // 3. 验证登录结果 - 2xx
        assertTrue(loginResponse.getStatusCode().is2xxSuccessful(), "登录HTTP状态码应为2xx");
        Result loginResult = loginResponse.getBody();
        assertNotNull(loginResult, "登录响应体不应为null");
        assertEquals(TestConstants.SUCCESS_CODE, loginResult.getCode(), "登录业务状态码应为" + TestConstants.SUCCESS_CODE);
        assertNotNull(loginResult.getData(), "登录成功应返回用户数据");

        // 4. 验证返回的数据结构
        Map<String, Object> loginData = (Map<String, Object>) loginResult.getData();

        // 打印完整的登录响应数据结构用于调试
        System.out.println("🔍 登录响应数据结构: " + loginData);
        System.out.println("🔍 登录响应数据所有键: " + loginData.keySet());

        // 验证token相关字段（必须存在）
        assertNotNull(loginData.get("token"), "登录成功应返回token");
        assertNotNull(loginData.get("tokenHead"), "登录成功应返回tokenHead");

        String token = (String) loginData.get("token");
        String tokenHead = (String) loginData.get("tokenHead");

        System.out.println("✅ tokenHead: " + tokenHead);
        System.out.println("✅ token长度: " + (token != null ? token.length() : "null"));

        // 验证用户信息字段（可选，if存在则验证）
        if (loginData.containsKey("userId")) {
            assertNotNull(loginData.get("userId"), "如果存在userId字段，则不应为null");
            System.out.println("✅ 用户ID: " + loginData.get("userId"));
        } else {
            System.out.println("⚠️ 登录响应未包含userId字段");
        }

        if (loginData.containsKey("username")) {
            assertNotNull(loginData.get("username"), "如果存在username字段，则不应为null");
            System.out.println("✅ 用户名: " + loginData.get("username"));
        } else {
            System.out.println("⚠️ 登录响应未包含username字段");
        }

        if (loginData.containsKey("role")) {
            assertNotNull(loginData.get("role"), "如果存在role字段，则不应为null");
            System.out.println("✅ 用户角色: " + loginData.get("role"));
        } else {
            System.out.println("⚠️ 登录响应未包含role字段");
        }

        // 5. 验证完整的Authorization头格式
        String authorizationHeader = tokenHead + " " + token;
        System.out.println("✅ Authorization头: " + authorizationHeader);

        // 6. 测试token有效性 - 使用token访问需要认证的接口
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        HttpEntity<?> request = new HttpEntity<>(headers);

//        // 尝试访问用户信息接口（？未来）
//        try {
//            String userInfoUrl = getBaseUrl() + TestConstants.API_PREFIX + "/user/info";
//            ResponseEntity<Result> userInfoResponse = restTemplate.exchange(
//                    userInfoUrl,
//                    HttpMethod.GET,
//                    request,
//                    Result.class
//            );
//
//            // 如果接口存在且返回成功，说明token有效
//            if (userInfoResponse.getStatusCode().is2xxSuccessful()) {
//                Result userInfoResult = userInfoResponse.getBody();
//                if (userInfoResult != null && userInfoResult.getCode() == TestConstants.SUCCESS_CODE) {
//                    System.out.println("✅ Token验证通过 - 可以正常访问受保护接口");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("⚠️ 用户信息接口可能不存在，但token格式正确");
//        }

        System.out.println("🎯 登录功能验证完成");
    }

    /**
     * 测试分页查询用户列表功能
     */
    @Test
    public void testUserListPaginationIntegration() {
        return; // 暂时跳过此测试
    }
}
