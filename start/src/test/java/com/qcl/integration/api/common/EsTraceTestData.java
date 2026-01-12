package com.qcl.integration.api.common;

import com.qcl.base.TestConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * ES Trace测试数据工厂 - 基于现有TestConstants扩展
 * 职责：提供测试用的标准数据
 */
public class EsTraceTestData {

    /**
     * 获取基础分页测试参数 - 用户指定的参数
     */
    public static int[] getBasicPaginationSizes() {
        return new int[]{0, 5, 1, 2, 3}; // 0表示无参，使用默认值
    }

    /**
     * 获取边界值测试参数 - 基于现有VALID_PAGE_SIZES扩展
     */
    public static int[] getBoundaryPaginationSizes() {
        return new int[]{-1, 0, 1, 100, 101};
    }

    /**
     * 获取时间范围过滤参数 - 基于现有时间常量
     */
    public static Map<String, Long> getTimeRangeFilters() {
        long endTime = System.currentTimeMillis();
        Map<String, Long> filters = new HashMap<>();
        filters.put("1小时", TestConstants.ONE_HOUR_MS);
        filters.put("1天", TestConstants.ONE_DAY_MS);
        filters.put("1周", TestConstants.ONE_WEEK_MS);
        return filters;
    }

    /**
     * 获取状态码过滤参数 - 基于现有状态码常量
     */
    public static Map<String, String> getStatusCodeFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("成功状态码", TestConstants.VALID_STATUS_CODE);
        filters.put("客户端错误", TestConstants.CLIENT_ERROR_CODE);
        filters.put("服务器错误", TestConstants.SERVER_ERROR_CODE);
        filters.put("不存在状态码", "999");
        return filters;
    }

    /**
     * 获取协议类型过滤参数 - 基于现有协议常量
     */
    public static String[] getProtocolFilters() {
        return TestConstants.SUPPORTED_PROTOCOLS;
    }
}