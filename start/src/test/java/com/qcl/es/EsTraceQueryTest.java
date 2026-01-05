package com.qcl.es;

import com.qcl.base.BaseApiTest;
import com.qcl.config.TestConfig;
import com.qcl.entity.Traces;
import com.qcl.vo.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


/**
 * ES Trace查询测试 - - 检查ES索引与API返回数据是否一致
 */
public class EsTraceQueryTest extends BaseApiTest {

    /**
     * 测试基础分页查询
     */
    @Test
    public void testBasicPaginationQuery() {
        String apiUrl = buildPaginationUrl(
                TestConfig.ES_TRACE_API_BASE_PATH + "/queryByPage",
                TestConfig.DEFAULT_PAGE_NUM,
                TestConfig.DEFAULT_PAGE_SIZE);

        ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                new ParameterizedTypeReference<PageResult<Traces>>() {}
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        PageResult<Traces> result = response.getBody();
        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    /**
     * 测试带过滤条件查询
     */
    @Test
    public void testQueryWithFilters() {
        System.out.println("=== 测试带协议过滤的查询 ===");

        String apiUrl = buildPaginationUrl(
                TestConfig.ES_TRACE_API_BASE_PATH + "/queryByPage",
                TestConfig.DEFAULT_PAGE_NUM,
                TestConfig.DEFAULT_PAGE_SIZE) + "&protocol=HTTP";

        System.out.println("请求URL: " + apiUrl);

        ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                new ParameterizedTypeReference<PageResult<Traces>>() {}
        );

        // 打印响应状态
        System.out.println("响应状态码: " + response.getStatusCodeValue());

        PageResult<Traces> result = response.getBody();

        if (result != null) {
            System.out.println("总记录数: " + result.getTotalElements());
            System.out.println("当前页记录数: " + (result.getContent() != null ? result.getContent().size() : 0));
            System.out.println("页大小: " + result.getPageSize());
            System.out.println("当前页码: " + result.getPageNumber());

            // 打印协议类型分布
            if (result.getContent() != null && !result.getContent().isEmpty()) {
                System.out.println("协议类型分布:");
                result.getContent().stream()
                        .map(trace -> trace.getProtocol() != null ? trace.getProtocol() : "null")
                        .distinct()
                        .forEach(protocol -> {
                            long count = result.getContent().stream()
                                    .filter(trace -> protocol.equals(trace.getProtocol()))
                                    .count();
                            System.out.println("  " + protocol + ": " + count + " 条记录");
                        });

                // 打印前3条记录的详细信息
                System.out.println("前3条记录详情:");
                result.getContent().stream()
                        .limit(3)
                        .forEach(trace -> {
                            System.out.println("  traceId: " + trace.getTraceId());
                            System.out.println("  协议: " + trace.getProtocol());
                            System.out.println("  开始时间: " + trace.getStartTime());
//                            System.out.println("  服务名: " + trace.getServiceName());
                            System.out.println("  ---");
                        });
            } else {
                System.out.println("⚠️ 未返回任何记录");
            }
        } else {
            System.out.println("❌ 响应体为空");
        }

        System.out.println("✅ 带协议过滤的查询测试完成");
    }

    /**
     * 测试时间范围查询
     * 通过分页遍历的方式获取所有符合时间范围的记录（查询时有pageSize限制）
     */

    /**
     * 小时间区间（15ms）
     * 1761887638911 - 1761887638926
    */
    @Test
    public void testSmallTime() {
        System.out.println("=== 测试指定小时间区间范围查询 ===");

        // 选取的时间区间
        long startTime = 1761887638911L;
        long endTime = 1761887638926L;

        // 打印时间范围
        printTimeRange(startTime, endTime);

        // 构建基础URL
        String baseUrl = buildBaseUrl();

        // 执行分页遍历查询
        List<Traces> allTraces = paginatedQuery(baseUrl, startTime, endTime);

        // 分析查询结果
        analyzeResults(allTraces, startTime, endTime);
    }

    /**
     * 空时间区间
     * 0 - 0
     */
    @Test
    public void testZeroTime() {
        System.out.println("=== 测试空时间区间查询 ===");

        // 选取的时间区间
        long startTime = 0L;
        long endTime = 0L;

        // 打印时间范围
        printTimeRange(startTime, endTime);

        // 构建基础URL
        String baseUrl = buildBaseUrl();

        // 执行分页遍历查询
        List<Traces> allTraces = paginatedQuery(baseUrl, startTime, endTime);

        // 分析查询结果
        analyzeResults(allTraces, startTime, endTime);
    }

    /**
     * 大时间区间（24h）
     * 0 - 0
     */

    @Test
    public void testLargeTime() {
        System.out.println("=== 测试大时间区间（24小时）查询 ===");

        // 选取的时间区间
        long startTime = 1761887638911L;
        long endTime = startTime + 24 * 60 * 60 * 1000L; // 24小时

        // 打印时间范围
        printTimeRange(startTime, endTime);

        // 构建基础URL
        String baseUrl = buildBaseUrl();

        // 执行分页遍历查询
        List<Traces> allTraces = paginatedQuery(baseUrl, startTime, endTime);

        // 分析查询结果
        analyzeResults(allTraces, startTime, endTime);
    }

    /**
     * 打印时间范围信息
     */
    private void printTimeRange(long startTime, long endTime) {
        System.out.println("查询时间范围:");
        System.out.println("  开始时间: " + startTime + " (" + new java.util.Date(startTime) + ")");
        System.out.println("  结束时间: " + endTime + " (" + new java.util.Date(endTime) + ")");
        System.out.println("  时间跨度: " + (endTime - startTime) + " 毫秒");
    }

    /**
     * 构建基础URL
     */
    private String buildBaseUrl() {
        return "http://" + TestConfig.REMOTE_SERVER_HOST + ":" + TestConfig.REMOTE_SERVER_PORT;
    }

    /**
     * 执行分页查询
     */
    private List<Traces> paginatedQuery(String baseUrl, long startTime, long endTime) {
        int pageSize = 1000;
        int currentPage = 0;    // 从第0页开始
        List<Traces> allTraces = new ArrayList<>();

        System.out.println("\n=== 开始分页遍历所有记录 ===");

        while (true) {
            String apiUrl = buildApiUrl(baseUrl, currentPage, pageSize, startTime, endTime);
            System.out.println("请求第 " + currentPage + " 页，URL: " + apiUrl);

            try {
                PageResult<Traces> result = executePageQuery(apiUrl);

                if (result == null || result.getContent() == null) {
                    System.out.println("❌ 第 " + currentPage + " 页返回结果为空");
                    break;
                }

                int currentPageRecords = result.getContent().size();

                if (currentPageRecords == 0) {
                    System.out.println("✅ 第 " + currentPage + " 页为空，已遍历完所有记录");
                    break;
                }

                allTraces.addAll(result.getContent());
                System.out.println("  第 " + currentPage + " 页获取到 " + currentPageRecords + " 条记录");
                System.out.println("  累计已获取 " + allTraces.size() + " 条记录");

                if (currentPageRecords < pageSize) {
                    System.out.println("✅ 当前页记录数(" + currentPageRecords + ")小于页大小(" + pageSize + ")，已遍历完所有记录");
                    break;
                }

                currentPage++;

            } catch (Exception e) {
                System.out.println("❌ 第 " + currentPage + " 页请求异常: " + e.getMessage());
                break;
            }
        }

        return allTraces;
    }

    /**
     * 构建API URL
     */
    private String buildApiUrl(String baseUrl, int pageNum, int pageSize, long startTime, long endTime) {
        return baseUrl + TestConfig.ES_TRACE_API_BASE_PATH +
                "/queryByPage?pageNum=" + pageNum + "&pageSize=" + pageSize +
                "&startTime=" + startTime + "&endTime=" + endTime;
    }

    /**
     * 执行单页查询
     */
    private PageResult<Traces> executePageQuery(String apiUrl) {
        ResponseEntity<PageResult<Traces>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(buildAuthHeaders()),
                new ParameterizedTypeReference<PageResult<Traces>>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("❌ 请求失败，状态码: " + response.getStatusCodeValue());
            return null;
        }

        return response.getBody();
    }

    /**
     * 分析查询结果
     */
    private void analyzeResults(List<Traces> allTraces, long startTime, long endTime) {
        System.out.println("\n=== 分析结果 ===");
        System.out.println("总共获取到 " + allTraces.size() + " 条记录");

        if (allTraces.isEmpty()) {
            System.out.println("⚠️ 未获取到任何记录");
            return;
        }

        // 统计时间范围内的记录
        List<Traces> inRangeRecords = allTraces.stream()
                .filter(trace -> trace.getStartTime() >= startTime && trace.getStartTime() <= endTime)
                .collect(Collectors.toList());

        System.out.println("在严格时间范围内的记录数: " + inRangeRecords.size() + "/" + allTraces.size());

        // 打印时间分布
        printTimeDistribution(allTraces);

        // 打印在时间范围内的记录详情
        if (!inRangeRecords.isEmpty()) {
            printInRangeRecords(inRangeRecords);
        } else {
            printClosestRecords(allTraces, startTime);
        }
    }

    /**
     * 打印时间分布
     */
    private void printTimeDistribution(List<Traces> traces) {
        long minTime = traces.stream().mapToLong(Traces::getStartTime).min().orElse(0);
        long maxTime = traces.stream().mapToLong(Traces::getStartTime).max().orElse(0);

        System.out.println("所有记录的时间范围:");
        System.out.println("  最早时间: " + minTime + " (" + new java.util.Date(minTime) + ")");
        System.out.println("  最晚时间: " + maxTime + " (" + new java.util.Date(maxTime) + ")");
    }

    /**
     * 打印在时间范围内的记录
     */
    private void printInRangeRecords(List<Traces> inRangeRecords) {
        System.out.println("\n=== 在指定时间范围内的记录详情 ===");
        inRangeRecords.forEach(trace -> {
            System.out.println("  traceId: " + trace.getTraceId());
            System.out.println("  开始时间: " + trace.getStartTime() + " (" + new java.util.Date(trace.getStartTime()) + ")");
            System.out.println("  结束时间: " + trace.getEndTime() + " (" + new java.util.Date(trace.getEndTime()) + ")");
            System.out.println("  协议: " + trace.getProtocol());
            System.out.println("  ---");
        });
    }

    /**
     * 打印最接近时间范围的记录
     */
    private void printClosestRecords(List<Traces> allTraces, long targetTime) {
        System.out.println("⚠️ 所有" + allTraces.size() + "条记录中，没有一条在指定时间范围内");

        System.out.println("\n=== 最接近时间范围的记录 ===");
        allTraces.stream()
                .sorted((t1, t2) -> Long.compare(
                        Math.abs(t1.getStartTime() - targetTime),
                        Math.abs(t2.getStartTime() - targetTime)
                ))
                .limit(5)
                .forEach(trace -> {
                    System.out.println("  traceId: " + trace.getTraceId());
                    System.out.println("  开始时间: " + trace.getStartTime() + " (" + new java.util.Date(trace.getStartTime()) + ")");
                    System.out.println("  协议: " + trace.getProtocol());
                    System.out.println("  与目标时间差: " + Math.abs(trace.getStartTime() - targetTime) + " 毫秒");
                    System.out.println("  ---");
                });
    }
}


