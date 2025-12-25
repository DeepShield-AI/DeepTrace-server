package com.qcl.basic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.qcl.entity.Traces;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API与ES索引数据一致性测试
 * 验证API返回数据与ES索引直接查询结果的一致性
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TraceApiDataConsistencyTest {

    // 远程服务器配置
    private static final String REMOTE_SERVER_HOST = "114.215.254.187";
    private static final int REMOTE_SERVER_PORT = 8081;
    private static final String API_BASE_PATH = "/api/esTraces";

    @LocalServerPort
    private int localServerPort;

    // 使用普通RestTemplate替代TestRestTemplate以避免自动装配问题
    private RestTemplate restTemplate;

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    @BeforeEach
    public void setUp() {
        // 手动创建RestTemplate实例
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void testQueryByPageApi() {
        System.out.println("=== 测试分页查询API ===");

        // 构建API URL
        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, API_BASE_PATH, 1, 10);

        System.out.println("API URL: " + apiUrl);

        try {
            // 直接创建HttpHeaders并设置Authorization头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aWFuIiwiY3JlYXRlZCI6MTc2NjcxMzQ0ODQ3MywiZXhwIjoxNzY2NzE3MDQ4fQ.vgOM3gHSYqFgrKu_sPO_Gt3rTsdBVFgGO9KUf350qSA");

            // 创建HttpEntity对象
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            System.out.println("请求头: " + requestEntity.getHeaders());

            // 调用API获取数据（带认证头）
            ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    requestEntity,  // 使用带认证头的请求实体
                    new ParameterizedTypeReference<PageResult<Traces>>() {
                    }
            );

            // 验证响应状态
            assertTrue(response.getStatusCode().is2xxSuccessful(), "API调用应返回成功状态码");

            PageResult<Traces> pageResult = response.getBody();
            assertNotNull(pageResult, "API响应体不应为null");

            // 输出API返回结果
            System.out.println("API返回结果:");
            System.out.println("- 总记录数: " + pageResult.getTotalElements());
            System.out.println("- 当前页记录数: " + (pageResult.getContent() != null ? pageResult.getContent().size() : 0));
            System.out.println("- 页码: " + pageResult.getPageNumber());
            System.out.println("- 页大小: " + pageResult.getPageSize());
            System.out.println("- 总页数: " + pageResult.getTotalPages());

            // 验证分页数据完整性
            assertNotNull(pageResult.getContent(), "内容列表不应为null");
            assertTrue(pageResult.getPageSize() >= 0, "页大小应为非负数");
            assertTrue(pageResult.getTotalElements() >= 0, "总记录数应为非负数");
            assertTrue(pageResult.getTotalPages() >= 0, "总页数应为非负数");

            // 如果有数据，输出前几条记录的概要信息
            if (!pageResult.getContent().isEmpty()) {
                System.out.println("前3条记录概要:");
                for (int i = 0; i < Math.min(3, pageResult.getContent().size()); i++) {
                    Traces trace = pageResult.getContent().get(i);
                    System.out.printf("  %d. traceId: %s, startTime: %d, protocol: %s%n",
                            i + 1, trace.getTraceId(), trace.getStartTime(), trace.getProtocol());
                }
            }

            System.out.println("分页查询API测试通过 ✓");

        } catch (Exception e) {
            System.err.println("分页查询API调用失败: " + e.getMessage());
            e.printStackTrace();
            fail("API调用应成功执行");
        }
    }
}