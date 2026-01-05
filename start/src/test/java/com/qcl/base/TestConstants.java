package com.qcl.base;

public class TestConstants {

    // 认证相关
    public static final String TEST_USERNAME = "tian";
    public static final String TEST_PASSWORD = "t4139567";
    public static final String TEST_EMAIL = "2425958593@qq.com";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TEST_TOKEN = "test-jwt-token";

    // API路径常量
    public static final String API_ES_TRACES = "/api/esTraces";
    public static final String API_USERS = "/api/users";

    // 测试数据时间范围
    public static final long TEST_START_TIME = System.currentTimeMillis() / 1000 - 3600; // 1小时前
    public static final long TEST_END_TIME = System.currentTimeMillis() / 1000;

    // 分页参数
    public static final int DEFAULT_PAGE_NUM = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // 响应时间阈值（毫秒）
    public static final long MAX_RESPONSE_TIME_UNIT = 5000;     // 单元测试
    public static final long MAX_RESPONSE_TIME_INTEGRATION = 30000; // 集成测试
    public static final long MAX_RESPONSE_TIME_PERFORMANCE = 120000; // 性能测试

    // 错误消息
    public static final String ERROR_INVALID_PARAMETERS = "无效的参数";
    public static final String ERROR_UNAUTHORIZED = "未授权访问";
    public static final String ERROR_NOT_FOUND = "资源未找到";
}