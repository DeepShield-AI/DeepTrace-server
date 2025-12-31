package com.qcl.user;

import com.qcl.util.TokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户登录功能验证测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserLoginTest {

    /**
     * 测试用户登录功能
     */
    @Test
    public void testUserLogin() {
        System.out.println("=== 测试用户登录功能 ===");

        String token = TokenUtil.getLoginToken();
        assertNotNull(token, "登录应返回有效的token");
        assertFalse(token.isEmpty(), "token不应为空");
        assertTrue(TokenUtil.validateToken(token), "token格式应有效");

        System.out.println("✅ 用户登录测试通过!");
    }
}
