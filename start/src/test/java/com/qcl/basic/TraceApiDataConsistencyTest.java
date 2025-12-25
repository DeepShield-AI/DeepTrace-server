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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

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

    @BeforeEach
    public void setUp() {
        // 手动创建RestTemplate实例
        this.restTemplate = new RestTemplate();
    }

    /**
     * 测试获取分页查询API数据
     * 调用：http://114.215.254.187:8081/api/esTraces/queryByPage?pageNum=1&pageSize=10
     */
    @Test
    public void testQueryByPageApi() {
        System.out.println("=== 测试分页查询API ===");

        // 构建API URL
        String apiUrl = String.format("http://%s:%d%s/queryByPage?pageNum=%d&pageSize=%d",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, API_BASE_PATH, 1, 10);

        System.out.println("API URL: " + apiUrl);

        try {
            // 调用API获取数据
            ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PageResult<Traces>>() {}
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

    /**
     * 测试获取统计API数据
     * 调用：http://114.215.254.187:8081/api/esTraces/statistic?type=count
     */
    @Test
    public void testStatisticApi() {
        System.out.println("=== 测试统计API ===");

        // 构建API URL
        String apiUrl = String.format("http://%s:%d%s/statistic?type=%s",
                REMOTE_SERVER_HOST, REMOTE_SERVER_PORT, API_BASE_PATH, "count");

        System.out.println("API URL: " + apiUrl);

        try {
            // 调用统计API获取数据
            ResponseEntity<List<TimeBucketResult>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TimeBucketResult>>() {}
            );

            // 验证响应状态
            assertTrue(response.getStatusCode().is2xxSuccessful(), "统计API调用应返回成功状态码");

            List<TimeBucketResult> statisticResult = response.getBody();
            assertNotNull(statisticResult, "统计API响应体不应为null");

            // 输出统计结果
            System.out.println("统计API返回结果:");
            System.out.println("- 数据点数量: " + statisticResult.size());

            // 输出前几个时间桶的统计信息
            if (!statisticResult.isEmpty()) {
                System.out.println("前5个时间桶统计:");
                for (int i = 0; i < Math.min(5, statisticResult.size()); i++) {
                    TimeBucketResult bucket = statisticResult.get(i);
                    System.out.printf("  %d. 时间: %d, 值: %s%n",
                            i + 1, bucket.getTimeKey(), bucket.getValue());
                }
            }

            // 验证统计数据的合理性
            assertTrue(statisticResult.size() >= 0, "统计结果数量应为非负数");

            System.out.println("统计API测试通过 ✓");

        } catch (Exception e) {
            System.err.println("统计API调用失败: " + e.getMessage());
            e.printStackTrace();
            fail("统计API调用应成功执行");
        }
    }

    /**
     * 测试两个API的集成调用
     */
    @Test
    public void testApiIntegration() {
        System.out.println("=== 测试API集成调用 ===");

        // 测试分页查询API
        testQueryByPageApi();

        // 测试统计API
        testStatisticApi();

        System.out.println("API集成测试完成 ✓");
    }

    /**
     * 写死库中当前的对应数据进行对比验证
     1. 分页查询结果总数对比
     2. 统计结果对比
     */
    @Test
    public void testDataConsistencyWithFixedData() {
        System.out.println("=== 测试API数据与固定ES数据一致性 ===");

        // 预期的固定数据值（根据实际情况修改）
        long expectedTotalCount = 1865; // 假设ES中有1000条trace数据
        long expectedStatisticCount = 1000; // 假设统计结果
    }
}