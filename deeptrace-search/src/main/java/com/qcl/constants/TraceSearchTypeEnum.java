package com.qcl.constants;

/**
 * Trace搜索类型枚举
 */
public enum TraceSearchTypeEnum {
    /**
     * 请求数统计
     */
    COUNT("count", "请求数统计"),

    /**
     * 状态码分组统计
     */
    STATUSCOUNT("statusCount", "状态码分组统计"),

    /**
     * 延迟统计
     */
    LATENCYSTATS("latencyStats", "延迟统计");

    private final String code;
    private final String description;

    TraceSearchTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举值
     * @param code 枚举code
     * @return 对应的枚举值，找不到返回null
     */
    public static TraceSearchTypeEnum fromCode(String code) {
        for (TraceSearchTypeEnum type : TraceSearchTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
