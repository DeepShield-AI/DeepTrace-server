package com.qcl.base;

/**
 * 测试常量类
 * 包含测试环境使用的常量定义
 */
public final class TestConstants {

    // 测试环境远程服务器相关常量
    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;

    // 静态用户登录相关常量
    public static final String TEST_USERNAME = "tian";
    public static final String TEST_PASSWORD = "t4139567";
    public static final String TEST_EMAIL = "2425958593@qq.com";

    // 注册用户常量
    public static final String TEST_USER_USERNAME = "testuser";
    public static final String TEST_USER_PASSWORD = "Test123456";
    public static final String TEST_USER_EMAIL = "test@example.com";

    // API路径常量
    public static final String API_PREFIX = "/api";
    public static final String LOGIN_PATH = "/user/login";
    public static final String REGISTER_PATH = "/user/register";
    public static final String ES_TRACES_QUERY_PATH = "/esTraces/queryByPage";
    // 不存在的 Trace ID
    public static final String NON_EXISTENT_TRACE_ID = "non-existent-trace-id-123456";

    // 测试配置常量
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int MAX_PAGE_SIZE = 100;

    // 响应状态码常量
    public static final int SUCCESS_CODE = 200;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int NOT_FOUND_CODE = 404;

    // 时间相关常量（毫秒）
    public static final long DEFAULT_TIMEOUT = 30000L;
    public static final long SHORT_TIMEOUT = 5000L;

    // 私有构造方法，防止实例化
    private TestConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}