package com.qcl.base;

import com.qcl.entity.Traces;
import com.qcl.entity.User;
import com.qcl.entity.param.QueryTracesParam;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 测试数据工厂 - 统一管理测试数据创建
 */
public class TestDataFactory {

    // User相关测试数据
    public static User createValidUser() {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setUsername("testuser_" + System.currentTimeMillis());
        user.setEmail("test" + System.currentTimeMillis() + "@example.com");
        user.setPassword("encryptedPassword123");
        user.setRole("USER");
        return user;
    }

    public static User createAdminUser() {
        User user = createValidUser();
        user.setRole("ADMIN");
        return user;
    }

    public static User createUserWithInvalidData() {
        User user = new User();
        user.setUsername(""); // 空用户名
        user.setEmail("invalid-email"); // 无效邮箱
        return user;
    }

    // Trace相关测试数据
    public static QueryTracesParam createValidTraceQueryParam() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(0);
        param.setPageSize(10);
        param.setStartTime(Instant.now().minusSeconds(3600).getEpochSecond());
        param.setEndTime(Instant.now().getEpochSecond());
        return param;
    }

    public static QueryTracesParam createTraceQueryWithStatusCodes() {
        QueryTracesParam param = createValidTraceQueryParam();
        param.setStatusCodes(Arrays.asList("200", "404", "500"));
        return param;
    }

    public static QueryTracesParam createInvalidTraceQueryParam() {
        QueryTracesParam param = new QueryTracesParam();
        param.setPageNum(-1); // 无效页码
        param.setPageSize(0); // 无效页大小
        param.setStartTime(Instant.now().getEpochSecond());
        param.setEndTime(Instant.now().minusSeconds(3600).getEpochSecond()); // 无效时间范围
        return param;
    }

    public static Traces createSampleTrace() {
        Traces trace = new Traces();
        trace.setTraceId("test-trace-" + System.currentTimeMillis());  
        trace.setStartTime(Instant.now().minusSeconds(300).getEpochSecond());  
        trace.setE2eDuration(150L);  
        trace.setEndpoint("/api/test");
        trace.setProtocol("HTTP");
        trace.setStatusCode("200");  
        return trace;
    }

    public static List<Traces> createTraceList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createSampleTrace())
                .collect(Collectors.toList());
    }

    public static Traces createTraceWithErrorStatus() {
        Traces trace = createSampleTrace();
        trace.setStatusCode("500");  
        trace.setE2eDuration(5000L); // 长响应时间
        return trace;
    }
}