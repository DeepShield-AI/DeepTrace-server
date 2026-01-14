package com.qcl.integration.api.common;

import com.qcl.base.TestConstants;
import com.qcl.unit.util.TokenUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * ES Trace测试辅助工具类 - 增强版本
 * 包含从EsTraceControllerTest_中提取的工具方法
 */
public class EsTraceTestHelper {

    // ========== 基础工具方法 ==========

    /**
     * 构建认证头 - 基于TokenUtil（当前登录用户Token）
     */
    public static HttpHeaders buildAuthHeaders() {
        String authToken = TokenUtil.getLoginToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.set("Content-Type", "application/json");
        headers.set("User-Agent", "EsTraceControllerTest/1.0");
        return headers;
    }

    /**
     * 构建Trace基础查询URL - 基于TestConstants
     */
    public static String buildQueryUrl(int pageNum, int pageSize) {
        return String.format("%s%s%s?pageNum=%d&pageSize=%d",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX,
                TestConstants.ES_TRACES_QUERY_PATH, pageNum, pageSize);
    }

    /**
     * 构建滚动查询URL
     */
    public static String buildScrollQueryUrl(String scrollId, Integer pageSize) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(String.format("%s%s%s/scrollQuery",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX,
                TestConstants.ES_TRACES_QUERY_PATH));

        if (scrollId != null) {
            urlBuilder.append("?scrollId=").append(scrollId);
        }
        if (pageSize != null) {
            urlBuilder.append(scrollId != null ? "&" : "?")
                    .append("pageSize=").append(pageSize);
        }

        return urlBuilder.toString();
    }

    /**
     * 构建Trace详情查询URL
     */
    public static String buildTraceDetailUrl(String traceId) {
        return String.format("%s%s/esTraces/traceDetail?traceId=%s",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX, traceId);
    }

    /**
     * 构建无参查询URL（默认值）
     */
    public static String buildQueryUrlWithoutPageSize(int pageNum) {
        return String.format("%s%s%s?pageNum=%d",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX,
                TestConstants.ES_TRACES_QUERY_PATH, pageNum);
    }

    /**
     * 构建带过滤条件的查询URL
     */
    public static String buildQueryUrlWithFilters(int pageNum, int pageSize, Map<String, Object> filters) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(buildQueryUrl(pageNum, pageSize));

        filters.forEach((key, value) -> {
            if (value != null) {
                urlBuilder.append("&").append(key).append("=").append(value.toString());
            }
        });

        return urlBuilder.toString();
    }

    /**
     * 构建统计查询URL
     */
    public static String buildStatisticUrl(String type) {
        return String.format("%s%s/esTraces/statistic?type=%s",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX, type);
    }

    /**
     * 构建筛选项查询URL
     */
    public static String buildFiltersUrl() {
        return String.format("%s%s/esTraces/filters",
                TestConstants.ES_TRACE_BASE_URL, TestConstants.API_PREFIX);
    }

    /**
     * 执行HTTP请求 - 统一请求执行逻辑
     */
    public static ResponseEntity<Map> executeQuery(RestTemplate restTemplate, String url) {
        return restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(buildAuthHeaders()), Map.class);
    }

    /**
     * 执行HTTP请求并处理异常 - 增强错误处理和调试
     */
    public static ResponseEntity<Map> executeQueryWithErrorHandling(RestTemplate restTemplate, String url, String testCase) {
        try {
            long startTime = System.currentTimeMillis();

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildAuthHeaders()), Map.class);

            long responseTime = System.currentTimeMillis() - startTime;
            System.out.println(testCase + " - 响应时间: " + responseTime + "ms");

            // 详细的响应内容分析
            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // 记录业务状态码和消息
                if (responseBody.containsKey("code")) {
                    Object codeObj = responseBody.get("code");
                    String codeStr = codeObj != null ? codeObj.toString() : "null";
                    String message = responseBody.get("message") != null ?
                            responseBody.get("message").toString() :
                            responseBody.get("error") != null ?
                                    responseBody.get("error").toString() : "无错误信息";

                    System.out.println(testCase + " - 业务响应: code=" + codeStr + ", message=" + message);
                }

                // 记录响应数据结构
                if (responseBody.containsKey("content")) {
                    Object content = responseBody.get("content");
                    if (content instanceof List) {
                        int size = ((List<?>) content).size();
                        System.out.println(testCase + " - 数据内容: " + size + " 条记录");
                    }
                }
            }

            return response;
        } catch (Exception e) {
            System.err.println(testCase + " - 请求异常: " + e.getMessage());
            // 记录详细的异常信息
            if (e.getMessage().contains("500")) {
                System.err.println(testCase + " - 服务器内部错误，请检查API路径和参数是否正确");
            } else if (e.getMessage().contains("404")) {
                System.err.println(testCase + " - 接口不存在，请检查URL路径");
            } else if (e.getMessage().contains("401")) {
                System.err.println(testCase + " - 认证失败，请检查Token有效性");
            }
            return null;
        }
    }

    // ========== 验证方法 ==========

    /**
     * 验证分页响应结构
     */
    public static void validatePaginationResponse(Map<String, Object> responseBody, int expectedPageSize, String testCase) {
        if (responseBody == null) {
            throw new AssertionError(testCase + " - 响应体不应为null");
        }

        // 验证必需字段
        String[] requiredFields = {"content", "totalElements", "totalPages", "pageNumber", "pageSize"};
        for (String field : requiredFields) {
            if (!responseBody.containsKey(field)) {
                throw new AssertionError(testCase + " - 响应体应包含" + field + "字段");
            }
        }

        // 验证content字段
        Object content = responseBody.get("content");
        if (content == null) {
            throw new AssertionError(testCase + " - content字段不应为null");
        }
        if (!(content instanceof List)) {
            throw new AssertionError(testCase + " - content字段应为List类型");
        }

        List<?> contentList = (List<?>) content;
        if (contentList.size() > expectedPageSize) {
            throw new AssertionError(testCase + " - 当前页记录数应小于等于页大小: " + contentList.size() + " > " + expectedPageSize);
        }

        System.out.println(testCase + " - 分页结构验证通过，记录数: " + contentList.size());
    }

    /**
     * 验证Trace记录数据质量
     */
    public static void validateTraceDataQuality(List<Map<String, Object>> traces, String testCase) {
        if (traces.isEmpty()) {
            System.out.println(testCase + " - 无Trace数据可验证");
            return;
        }

        int validCount = 0;
        for (Map<String, Object> trace : traces) {
            if (validateSingleTraceRecord(trace)) {
                validCount++;
            }
        }

        double validityRate = (double) validCount / traces.size() * 100;
        System.out.println(testCase + " - 数据质量验证: " + validCount + "/" + traces.size() +
                " (" + String.format("%.2f", validityRate) + "%) 条记录有效");

        if (validityRate < 80.0) {
            throw new AssertionError(testCase + " - 数据有效性应不低于80%");
        }
    }

    private static boolean validateSingleTraceRecord(Map<String, Object> trace) {
        // 必需字段验证
        String[] requiredFields = {"traceId", "startTime", "protocol"};
        for (String field : requiredFields) {
            if (!trace.containsKey(field) || trace.get(field) == null) {
                return false;
            }
        }

        // 逻辑一致验证
        if (trace.containsKey("startTime") && trace.containsKey("endTime")) {
            Long startTime = safeGetLong(trace, "startTime");
            Long endTime = safeGetLong(trace, "endTime");
            if (startTime != null && endTime != null && startTime > endTime) {
                return false;
            }
        }

        return true;
    }

    private static Long safeGetLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try { return Long.parseLong((String) value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    // ========== 打印方法 ==========

    /**
     * 详细打印响应数据
     */
    public static void printDetailedResponse(ResponseEntity<Map> response, String testCase) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("=== " + testCase + " ===");
        System.out.println("HTTP状态码: " + response.getStatusCodeValue());

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            printDetailedMap(responseBody, "", 0);
        }
        System.out.println("=".repeat(80));
    }

    /**
     * 递归打印Map的所有字段
     * int level: 记录当前是第几层嵌套
     * String indent：缩进字符串，控制输出的格式
     */
    private static void printDetailedMap(Map<String, Object> map, String indent, int level) {
        // 遍历，递归处理嵌套
        // Map.Entry<String, Object> entry - Map中的每个键值对
        // map.entrySet() - 获取Map中所有的键值对集合
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 获取当前键值对
            String key = entry.getKey();
            Object value = entry.getValue();

            // 如果值是嵌套的Map对象
            if (value instanceof Map) {
                System.out.println(indent + key + ": {");
                printDetailedMap((Map<String, Object>) value, indent + "  ", level + 1);
                System.out.println(indent + "}");
            } else if (value instanceof List) {
                // 如果值是List数组
                List<?> list = (List<?>) value;
                System.out.println(indent + key + ": [");

                // 打印完整记录
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        System.out.println(indent + "  [" + i + "]: {");
                        printDetailedMap((Map<String, Object>) item, indent + "    ", level + 1);
                        System.out.println(indent + "  }");
                    } else {
                        System.out.println(indent + "  [" + i + "]: " + item);
                    }
                }
                System.out.println(indent + "]");
            } else {
                // 打印pageNumber, pageSize, totalElements, totalPages
                System.out.println(indent + key + ": " + value);
            }
        }
    }
}