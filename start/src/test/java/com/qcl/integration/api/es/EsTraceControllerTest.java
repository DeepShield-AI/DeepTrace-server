package com.qcl.integration.api.es;

import com.qcl.base.TestConstants;
import com.qcl.integration.api.common.EsTraceTestHelper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EsTraceController 集成测试
 * 验证分页查询功能
 * EsTraceTestHelper 辅助类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("EsTraceController 集成测试套件")
public class EsTraceControllerTest {

    private RestTemplate restTemplate;

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    @Order(1)
    @DisplayName("TC001 - 分页查询功能验证（包含无参和有参验证）")
    public void testBasicPaginationFunctionality() {
        System.out.println("=== 开始基础分页查询功能验证 ===");

        // 无参（使用默认值）和有参（指定页大小）
        Object[][] testScenarios = {
                {"无参测试（使用默认页大小）", null},
                {"页大小5查询", 5},
                {"页大小1查询", 1},
                {"页大小0查询", 0},

        };

        for (Object[] scenario : testScenarios) {
            String testCase = (String) scenario[0];
            Integer pageSize = (Integer) scenario[1];
            String url;

            if (pageSize == null) {
                // 无参 则默认值
                url = EsTraceTestHelper.buildQueryUrlWithoutPageSize(TestConstants.DEFAULT_PAGE_NUMBER);
            } else {
                // 有参 指定页大小
                url = EsTraceTestHelper.buildQueryUrl(TestConstants.DEFAULT_PAGE_NUMBER, pageSize);
            }

            System.out.println("\n开始测试: " + testCase);
            System.out.println("请求URL: " + url);

            ResponseEntity<Map> response = EsTraceTestHelper.executeQueryWithErrorHandling(restTemplate, url, testCase);
            assertNotNull(response);
            assertTrue(response.getStatusCode().is2xxSuccessful());

            // 验证分页响应结构
            if (response.getBody() != null) {
                EsTraceTestHelper.validatePaginationResponse(response.getBody(),
                        pageSize != null ? pageSize : TestConstants.DEFAULT_PAGE_SIZE, testCase);
            }

            // 使用EsTraceTestHelper的详细打印功能
            EsTraceTestHelper.printDetailedResponse(response, testCase);
        }

        System.out.println("=== 基础分页查询功能验证完成 ===");
    }

    @Test
    @Order(2)
    @DisplayName("TC002 - Trace详情查询功能验证")
    public void testTraceDetailFunctionality() {
        System.out.println("=== 开始Trace详情查询功能验证 ===");

        // 获取有效的traceId（第一个trace的traceId）
        String baseUrl = EsTraceTestHelper.buildQueryUrl(0, 1);
        ResponseEntity<Map> baseResponse = EsTraceTestHelper.executeQueryWithErrorHandling(restTemplate, baseUrl, "获取测试traceId");

        if (baseResponse != null && baseResponse.getBody() != null) {
            Map<String, Object> responseBody = baseResponse.getBody();
            if (responseBody.containsKey("content")) {
                List<?> content = (List<?>) responseBody.get("content");
                if (!content.isEmpty() && content.get(0) instanceof Map) {
                    Map<String, Object> firstTrace = (Map<String, Object>) content.get(0);

                    // 字段名检查
                    String traceId = null;
                    if (firstTrace.containsKey("traceId")) {
                        traceId = (String) firstTrace.get("traceId");
                    } else if (firstTrace.containsKey("trace_id")) {
                        traceId = (String) firstTrace.get("trace_id");
                    } else if (firstTrace.containsKey("id")) {
                        traceId = (String) firstTrace.get("id");
                    }

                    // 空串检查
                    if (traceId != null && !traceId.isEmpty()) {
                        String testCase = "Trace详情查询 - traceId: " + traceId;
                        String url = EsTraceTestHelper.buildTraceDetailUrl(traceId);

                        System.out.println("\n开始测试: " + testCase);
                        System.out.println("请求URL: " + url);

                        ResponseEntity<Map> response = EsTraceTestHelper.executeQueryWithErrorHandling(restTemplate, url, testCase);

                        // 确保API调用成功
                        assertNotNull(response);
                        assertTrue(response.getStatusCode().is2xxSuccessful());

                        // 使用EsTraceTestHelper的详细打印功能
                        EsTraceTestHelper.printDetailedResponse(response, testCase);
                    } else {
                        System.out.println("无法获取有效的traceId，跳过Trace详情查询测试");
                        // 打印第一个trace的内容以便调试
                        System.out.println("第一个Trace的内容: " + firstTrace);
                    }
                } else {
                    System.out.println("content为空或第一个元素不是Map类型，跳过Trace详情查询测试");
                }
            } else {
                System.out.println("响应中不包含content字段，跳过Trace详情查询测试");
            }
        } else {
            System.out.println("获取traceId请求失败，跳过Trace详情查询测试");
        }

        System.out.println("=== Trace详情查询功能验证完成 ===");
    }

    @Test
    @Order(3)
    @DisplayName("TC003 - 统计信息查询功能验证")
    public void testTraceStatisticsFunctionality() {
        System.out.println("=== 开始统计信息查询功能验证 ===");

        // 定义所有支持的统计类型
        String[] statisticTypes = {"count", "statusCount", "latencyStats"};
        String[] typeDescriptions = {"请求数时序统计", "状态码分组统计", "延迟统计"};

        for (int i = 0; i < statisticTypes.length; i++) {
            String type = statisticTypes[i];
            String description = typeDescriptions[i];

            System.out.println("\n=== 开始测试: " + description + " (type=" + type + ") ===");

            // 构建统计信息查询URL
            String url = EsTraceTestHelper.buildStatisticUrl(type);

            System.out.println("请求URL: " + url);

            // 使用RestTemplate直接调用，处理List类型的响应
            ResponseEntity<Object> response;
            try {
                HttpHeaders headers = EsTraceTestHelper.buildAuthHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);

                response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

                // 确保API调用成功
                assertNotNull(response);
                assertTrue(response.getStatusCode().is2xxSuccessful());

                // 验证响应类型为List
                Object responseBody = response.getBody();
                assertNotNull(responseBody, "响应体不应为null");

                // 检查是否为List类型
                // 简要打印
//                if (responseBody instanceof List) {
//                    List<?> resultList = (List<?>) responseBody;
//                    System.out.println(description + "返回List类型数据，大小: " + resultList.size());
//
//                    // 打印List中的前几个元素
//                    if (!resultList.isEmpty()) {
//                        System.out.println("List中的第一个元素: " + resultList.get(0));
//                        if (resultList.size() > 1) {
//                            System.out.println("List中的第二个元素: " + resultList.get(1));
//                        }
//                    }
//                } else {
//                    System.out.println("响应类型不是List，实际类型: " + responseBody.getClass().getSimpleName());
//                    System.out.println("响应内容: " + responseBody);
//                }

                // 全部打印
                if (responseBody instanceof List) {
                    List<?> resultList = (List<?>) responseBody;
                    System.out.println(description + "返回List类型数据，大小: " + resultList.size());

                    // 打印List中的所有元素
                    if (!resultList.isEmpty()) {
                        System.out.println("完整的统计数据:");
                        for (int j = 0; j < resultList.size(); j++) {
                            System.out.println("第" + (j+1) + "条数据: " + resultList.get(j));
                        }
                    }
                } else {
                    System.out.println("响应类型不是List，实际类型: " + responseBody.getClass().getSimpleName());
                    System.out.println("响应内容: " + responseBody);
                }

                // 打印详细响应信息
                System.out.println("响应状态: " + response.getStatusCode());
                System.out.println("响应头: " + response.getHeaders());
                System.out.println("响应体类型: " + (responseBody != null ? responseBody.getClass().getSimpleName() : "null"));

            } catch (Exception e) {
                System.err.println(description + "查询失败: " + e.getMessage());
                e.printStackTrace();
                fail(description + "查询失败: " + e.getMessage());
            }

            System.out.println("=== " + description + "测试完成 ===");
        }

        System.out.println("=== 统计信息查询功能验证完成 ===");
    }

//    @Test
//    @Order(4)
//    @DisplayName("TC004 - 滚动查询功能验证")
//    public void testScrollQueryFunctionality() {
//        System.out.println("=== 开始滚动查询功能验证 ===");
//
//        // 构建滚动查询URL
//        String url = EsTraceTestHelper.buildScrollQueryUrl();
//
//        System.out.println("\n开始测试: 滚动查询");
//        System.out.println("请求URL: " + url);
//
//        ResponseEntity<Map> response = EsTraceTestHelper.executeQueryWithErrorHandling(restTemplate, url, "滚动查询");
//
//        // 确保API调用成功
//        assertNotNull(response);
//        assertTrue(response.getStatusCode().is2xxSuccessful());
//
//        // 验证滚动查询响应结构
//        if (response.getBody() != null) {
//            Map<String, Object> responseBody = response.getBody();
//
//            // 滚动查询应该返回数据列表
//            assertTrue(responseBody.containsKey("content") || responseBody.containsKey("data"),
//                    "滚动查询响应应包含content或data字段");
//
//            // 验证响应包含必要字段
//            if (responseBody.containsKey("content")) {
//                List<?> content = (List<?>) responseBody.get("content");
//                assertNotNull(content, "content字段不应为null");
//            } else if (responseBody.containsKey("data")) {
//                List<?> data = (List<?>) responseBody.get("data");
//                assertNotNull(data, "data字段不应为null");
//            }
//        }
//
//        // 使用EsTraceTestHelper的详细打印功能
//        EsTraceTestHelper.printDetailedResponse(response, "滚动查询");
//
//        System.out.println("=== 滚动查询功能验证完成 ===");
//    }

//    @Test
//    @Order(5)
//    @DisplayName("TC005 - 筛选项查询功能验证")
//    public void testFiltersFunctionality() {
//        System.out.println("=== 开始筛选项查询功能验证 ===");
//
//        // 构建筛选项查询URL
//        String url = EsTraceTestHelper.buildFiltersUrl();
//
//        System.out.println("\n开始测试: 筛选项查询");
//        System.out.println("请求URL: " + url);
//
//        ResponseEntity<Map> response = EsTraceTestHelper.executeQueryWithErrorHandling(restTemplate, url, "筛选项查询");
//
//        // 确保API调用成功
//        assertNotNull(response);
//        assertTrue(response.getStatusCode().is2xxSuccessful());
//
//        // 验证筛选项响应结构
//        if (response.getBody() != null) {
//            Map<String, Object> responseBody = response.getBody();
//
//            // 筛选项查询应该返回可用的筛选条件
//            assertTrue(responseBody.containsKey("filters") || responseBody.containsKey("data"),
//                    "筛选项查询响应应包含filters或data字段");
//
//            // 验证响应包含必要的筛选条件
//            if (responseBody.containsKey("filters")) {
//                Map<?, ?> filters = (Map<?, ?>) responseBody.get("filters");
//                assertNotNull(filters, "filters字段不应为null");
//                assertFalse(filters.isEmpty(), "filters字段不应为空");
//            } else if (responseBody.containsKey("data")) {
//                Map<?, ?> data = (Map<?, ?>) responseBody.get("data");
//                assertNotNull(data, "data字段不应为null");
//            }
//        }
//
//        // 使用EsTraceTestHelper的详细打印功能
//        EsTraceTestHelper.printDetailedResponse(response, "筛选项查询");
//
//        System.out.println("=== 筛选项查询功能验证完成 ===");
//    }
}
