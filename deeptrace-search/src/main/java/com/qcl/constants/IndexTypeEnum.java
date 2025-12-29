package com.qcl.constants;

/**
 * 索引类型枚举
 */
public enum IndexTypeEnum {
    /**
     * 节点索引类型
     */
    NODE("nodes", "节点"),

    /**
     * 边索引类型
     */
    EDGE("edges", "边"),

    /**
     * 追踪索引类型
     */
    TRACE("traces", "调用链");

    private final String code;
    private final String description;

    IndexTypeEnum(String code, String description) {
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
     * 根据编码获取枚举值
     * @param code 索引类型编码
     * @return 对应的枚举值，找不到返回null
     */
    public static IndexTypeEnum fromCode(String code) {
        for (IndexTypeEnum type : IndexTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
