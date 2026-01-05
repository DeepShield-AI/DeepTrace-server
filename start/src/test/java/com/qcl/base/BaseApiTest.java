package com.qcl.base;

import com.qcl.config.TestConfig;
import com.qcl.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * API测试基类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseApiTest {

    protected RestTemplate restTemplate;
    protected String authToken;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
        this.authToken = TokenUtil.getLoginToken();
    }

    /**
     * 构建带认证头的HttpHeaders
     */
    protected HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    /**
     * 构建完整的API URL
     */
    protected String buildApiUrl(String apiPath) {
        return String.format("http://%s:%d%s",
                TestConfig.REMOTE_SERVER_HOST,
                TestConfig.REMOTE_SERVER_PORT,
                apiPath);
    }

    /**
     * 构建分页查询URL
     */
    protected String buildPaginationUrl(String apiPath, int pageNum, int pageSize) {
        return String.format("%s?pageNum=%d&pageSize=%d",
                buildApiUrl(apiPath), pageNum, pageSize);
    }
}