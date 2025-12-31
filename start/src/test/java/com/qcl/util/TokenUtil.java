package com.qcl.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Token工具类 - 专用于获取和管理登录token
 */
public class TokenUtil {

    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;
    private static final String USER_API_BASE_PATH = "/api/user";
    private static final String EXISTING_USERNAME = "tian";
    private static final String EXISTING_PASSWORD = "t4139567";

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取登录token
     */
    public static String getLoginToken() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", EXISTING_USERNAME);
        loginBody.put("password", EXISTING_PASSWORD);

        String loginUrl = String.format("http://%s:%d%s/login",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, USER_API_BASE_PATH);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("登录API调用失败，状态码: " + response.getStatusCodeValue());
            }

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("登录响应体为空");
            }

            Integer code = (Integer) responseBody.get("code");
            if (code != 200) {
                throw new RuntimeException("登录失败，业务状态码: " + code);
            }

            Map<String, Object> tokenData = (Map<String, Object>) responseBody.get("data");
            String authToken = (String) tokenData.get("token");
            if (authToken == null || authToken.isEmpty()) {
                throw new RuntimeException("token为空");
            }

            System.out.println("✅ Token获取成功: " + authToken.substring(0, Math.min(30, authToken.length())) + "...");
            return authToken;

        } catch (Exception e) {
            System.err.println("❌ Token获取失败: " + e.getMessage());
            throw new RuntimeException("Token获取失败: " + e.getMessage());
        }
    }

    /**
     * 验证token有效性
     */
    public static boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 这里可以添加更复杂的token验证逻辑
        // 比如检查token格式、过期时间等
        return token.length() > 10; // 简单的长度检查
    }
}