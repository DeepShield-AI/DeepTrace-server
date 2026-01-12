package com.qcl.base;

/**
 * 测试常量类 - 测试工程师角度：完整、可维护的测试配置
 */
public final class TestConstants {

    // ========== 测试环境配置 ==========
    public static final String REMOTE_SERVER_HOST = "114.215.254.187";
    public static final int REMOTE_SERVER_PORT = 8081;
    //  esTraces 基础URL
    public static final String ES_TRACE_BASE_URL = "http://" + REMOTE_SERVER_HOST + ":" + REMOTE_SERVER_PORT;

    // ========== 用户认证相关 ==========
    public static final String TEST_USERNAME = "tian";
    public static final String TEST_PASSWORD = "t4139567";
    public static final String TEST_EMAIL = "2425958593@qq.com";
    public static final String TEST_USER_USERNAME = "testuser";
    public static final String TEST_USER_PASSWORD = "Test123456";
    public static final String TEST_USER_EMAIL = "test@example.com";

    // ========== API路径常量 ==========
    public static final String API_PREFIX = "/api";
    public static final String LOGIN_PATH = "/user/login";
    public static final String REGISTER_PATH = "/user/register";
    public static final String ES_TRACES_BASE_PATH = "/esTraces"; // 只保留基础路径
    // 完整路径
    public static final String ES_TRACES_QUERY_PATH = ES_TRACES_BASE_PATH + "/queryByPage";
    public static final String ES_TRACES_DETAIL_PATH = ES_TRACES_BASE_PATH + "/traceDetail";

    // ========== 测试数据常量 ==========
    public static final String NON_EXISTENT_TRACE_ID = "non-existent-trace-id-123456";
    public static final String SAMPLE_TRACE_ID_PREFIX = "trace-";
    public static final String[] SUPPORTED_PROTOCOLS = {"HTTP", "HTTPS", "TCP", "UDP"};
    public static final String[] HTTP_METHODS = {"GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"};

    // ========== 分页配置常量 ==========
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int[] VALID_PAGE_SIZES = {5, 10, 20, 50, 100};

    // ========== HTTP状态码常量 ==========
    public static final int SUCCESS_CODE = 200;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int NOT_FOUND_CODE = 404;
    public static final int INTERNAL_ERROR_CODE = 500;

    // Trace数据状态码（字符串类型，用于URL参数）
    public static final String VALID_STATUS_CODE = "200";
    public static final String REDIRECT_STATUS_CODE = "302";
    public static final String CLIENT_ERROR_CODE = "400";
    public static final String SERVER_ERROR_CODE = "500";
    public static final String[] COMMON_STATUS_CODES = {"200", "302", "400", "401", "403", "404", "500"};

    // ========== 时间相关常量（毫秒） ==========
    public static final long ONE_SECOND_MS = 1000L;
    public static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    public static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    public static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
    public static final long ONE_WEEK_MS = 7 * ONE_DAY_MS;

    public static final long DEFAULT_TIMEOUT = 30000L;
    public static final long SHORT_TIMEOUT = 5000L;
    public static final long LONG_TIMEOUT = 60000L;

    // ========== 测试配置常量 ==========
    public static final int MAX_RETRY_COUNT = 3;
    public static final long RETRY_INTERVAL_MS = 2000L;
    public static final int SAMPLE_DATA_COUNT = 5;

    // 私有构造方法，防止实例化
    private TestConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}