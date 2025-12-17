package com.qcl.constants;


/**
 * 采集器管理类型枚举
 */
public enum AgentManageTypeEnum  {
    REGISTER("register", "注册"),
    ENABLE("enable", "启用"),
    DISABLE("disable", "禁用"),
    DELETE("delete", "删除");

    private final String code;
    private final String description;

    AgentManageTypeEnum(String code, String description) {
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
     * @param code 管理类型code
     * @return 对应的枚举值
     */
    public static AgentManageTypeEnum fromCode(String code) {
        for (AgentManageTypeEnum type : AgentManageTypeEnum.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown agent manage type code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}
