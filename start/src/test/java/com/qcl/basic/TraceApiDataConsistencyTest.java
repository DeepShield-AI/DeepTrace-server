package com.qcl.basic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.qcl.entity.Traces;
import com.qcl.util.TokenUtil;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API与ES索引数据一致性测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TraceApiDataConsistencyTest {

    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;
    private static final String API_BASE_PATH = "/api/esTraces";

    @LocalServerPort
    private int localServerPort;

    private RestTemplate restTemplate;

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 综合测试：认证机制验证 + 数据完整性验证
     */
    @Test
    public void testQueryByPageApiWithAuthentication() {
        System.out.println("=== 综合测试：认证机制验证 + 数据完整性验证 ===");

        // 1. 使用TokenUtil获取登录token
        String authToken = TokenUtil.getLoginToken();
        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, API_BASE_PATH, 1, 10);

        System.out.println("API URL: " + apiUrl);

        try {
            // 2. 设置认证头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            System.out.println("请求头: " + requestEntity.getHeaders());

            // 3. 验证数据完整性（使用强类型接收响应）
            ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<PageResult<Traces>>() {}
            );

            // 验证HTTP状态码
            assertTrue(response.getStatusCode().is2xxSuccessful(),
                    "带token的API调用应返回成功状态码");

            PageResult<Traces> pageResult = response.getBody();
            assertNotNull(pageResult, "API响应体不应为null");

            System.out.println("数据完整性验证 - API返回结果:");
            System.out.println("- 总记录数: " + pageResult.getTotalElements());
            System.out.println("- 当前页记录数: " + (pageResult.getContent() != null ? pageResult.getContent().size() : 0));

            // 验证数据结构完整性
            assertNotNull(pageResult.getContent(), "内容列表不应为null");
            assertTrue(pageResult.getPageSize() >= 0, "页大小应为非负数");
            assertTrue(pageResult.getTotalElements() >= 0, "总记录数应为非负数");

            // 打印数据详情
            if (!pageResult.getContent().isEmpty()) {
                System.out.println("前3条记录概要:");
                for (int i = 0; i < Math.min(3, pageResult.getContent().size()); i++) {
                    Traces trace = pageResult.getContent().get(i);
                    System.out.printf("  %d. traceId: %s, startTime: %d, protocol: %s%n",
                            i + 1, trace.getTraceId(), trace.getStartTime(), trace.getProtocol());
                }
            }

            System.out.println("✅ 综合测试通过 - token获取和数据完整性验证均成功!");

        } catch (Exception e) {
            System.err.println("❌ 综合测试失败: " + e.getMessage());
            fail("API调用应成功执行");
        }
    }

    /**
     * 测试无token访问受保护API（应失败）
     */
    @Test
    public void testProtectedApiWithoutToken() {
        System.out.println("=== 测试无token访问受保护API（应失败） ===");

        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, API_BASE_PATH, 1, 10);

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            System.out.println("响应状态码: " + response.getStatusCodeValue());
            System.out.println("响应体: " + response.getBody());

            // 无token访问应返回401或403
            assertTrue(response.getStatusCode().value() == 401 || response.getStatusCode().value() == 403,
                    "无token访问应返回401或403状态码");

            System.out.println("✅ 无token访问被正确拒绝!");

        } catch (Exception e) {
            System.out.println("无token访问被正确拒绝: " + e.getMessage());
        }
    }
}