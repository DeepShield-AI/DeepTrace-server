package com.qcl.config;

/**
 * 测试配置常量类
 */
public class TestConfig {

    // 服务器配置
    public static final String REMOTE_SERVER_HOST = "114.215.254.187";
    public static final int REMOTE_SERVER_PORT = 8081;

    // API路径配置
    public static final String USER_API_BASE_PATH = "/api/user";
    public static final String ES_TRACE_API_BASE_PATH = "/api/esTraces";
    public static final String FILE_API_BASE_PATH = "/api/file";

    // 测试用户配置
    public static final String TEST_USERNAME = "tian";
    public static final String TEST_PASSWORD = "t4139567";
    public static final String TEST_EMAIL = "test@example.com";

    // 测试文件配置
    public static final String TEST_FILE_PATH = "src/test/resources/test-file.txt";
    public static final String TEST_UPLOAD_DIR = "/uploads/";

    // 测试数据配置
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int MAX_RETRY_COUNT = 3;

    // 超时配置（毫秒）
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 30000;
}