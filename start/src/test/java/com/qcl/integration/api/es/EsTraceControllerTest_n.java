package com.qcl.integration.api.es;

import com.qcl.base.TestConstants;
import com.qcl.integration.api.common.EsTraceTestHelper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EsTraceController 集成测试 - 简化版本（无数据库依赖）
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
        }
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("EsTraceController 集成测试套件")
public class EsTraceControllerTest_n {

    private RestTemplate restTemplate;

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    @Order(1)
    @DisplayName("TC001 - 基础分页查询功能验证")
    public void testBasicPaginationFunctionality() {
        System.out.println("=== 开始基础分页查询功能验证 ===");

        // 定义测试场景：无参（使用默认值）和有参（指定页大小）
        Object[][] testScenarios = {
                {"无参测试（使用默认页大小）", null},
                {"页大小5查询", 5},
                {"页大小1查询", 1},
                {"页大小2查询", 2},
                {"页大小3查询", 3}
        };

        for (Object[] scenario : testScenarios) {
            String testCase = (String) scenario[0];
            Integer pageSize = (Integer) scenario[1];
            String url;

            if (pageSize == null) {
                // 无参测试 - 使用默认值
                url = EsTraceTestHelper.buildQueryUrlWithoutPageSize(TestConstants.DEFAULT_PAGE_NUMBER);
            } else {
                // 有参测试 - 指定页大小
                url = EsTraceTestHelper.buildQueryUrl(TestConstants.DEFAULT_PAGE_NUMBER, pageSize);
            }

            System.out.println("\n开始测试: " + testCase);
            System.out.println("请求URL: " + url);

            ResponseEntity<Map> response = EsTraceTestHelper.executeQuery(restTemplate, url);
            assertTrue(response.getStatusCode().is2xxSuccessful());

            // 使用EsTraceTestHelper的详细打印功能
            EsTraceTestHelper.printDetailedResponse(response, testCase);
        }

        System.out.println("=== 基础分页查询功能验证完成 ===");
    }

    // 其他测试方法保持不变...
}
