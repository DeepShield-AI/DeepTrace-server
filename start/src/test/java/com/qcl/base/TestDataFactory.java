package com.qcl.base;

import com.qcl.entity.param.UserLoginParam;
import com.qcl.entity.param.UserParam;

/**
 * 测试数据工厂类
 * 集中管理测试数据的创建和生成逻辑
 */
public final class TestDataFactory {

    /**
     * 创建有效登录参数（使用现有用户）
     */
    public static UserLoginParam createValidLoginParam() {
        UserLoginParam param = new UserLoginParam();
        param.setUsername(TestConstants.TEST_USERNAME);
        param.setPassword(TestConstants.TEST_PASSWORD);
        return param;
    }

    /**
     * 创建测试用户参数（手机号11位）
     */
    public static UserParam createTestUserParam() {
//        String timestamp = String.valueOf(System.currentTimeMillis());
        UserParam param = new UserParam();
        param.setUsername(generateUniqueUsername(TestConstants.TEST_USER_USERNAME));
        param.setPassword(TestConstants.TEST_USER_PASSWORD);
        param.setPhone(generateValidPhoneNumber());
        param.setEmail(generateUniqueEmail(TestConstants.TEST_USER_EMAIL));
        return param;
    }

    /**
     * 生成有效的11位手机号
     */
    private static String generateValidPhoneNumber() {
        String[] prefixes = {"13", "14", "15", "16", "17", "18", "19"};
        String prefix = prefixes[(int) (Math.random() * prefixes.length)];

        StringBuilder phoneNumber = new StringBuilder(prefix);
        for (int i = 0; i < 9; i++) {
            phoneNumber.append((int) (Math.random() * 10));
        }
        return phoneNumber.toString();
    }

    /**
     * 生成唯一的用户名
     */
    private static String generateUniqueUsername(String baseUsername) {
        return baseUsername + "_" + System.currentTimeMillis();
    }

    /**
     * 生成唯一的邮箱地址
     */
    private static String generateUniqueEmail(String baseEmail) {
        return baseEmail.replace("@", "_" + System.currentTimeMillis() + "@");
    }

    // 私有构造方法，防止实例化
    private TestDataFactory() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
