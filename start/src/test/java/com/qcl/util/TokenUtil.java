package com.qcl.util;

import com.qcl.base.TestConstants;

/**
 * 测试用Token工具类
 */
public class TokenUtil {

    /**
     * 获取测试用登录Token
     */
    public static String getLoginToken() {
        return TestConstants.TEST_TOKEN;
    }

    /**
     * 生成带时间戳的测试Token（避免重复）
     */
    public static String generateTimestampToken() {
        return TestConstants.TEST_TOKEN + "-" + System.currentTimeMillis();
    }

    /**
     * 生成指定用户的测试Token
     */
    public static String generateUserToken(String username) {
        return TestConstants.TEST_TOKEN + "-" + username + "-" + System.currentTimeMillis();
    }

    /**
     * 生成管理员Token
     */
    public static String generateAdminToken() {
        return TestConstants.TEST_TOKEN + "-ADMIN-" + System.currentTimeMillis();
    }
}