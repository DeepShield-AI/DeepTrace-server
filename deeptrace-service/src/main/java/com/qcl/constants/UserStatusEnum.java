package com.qcl.constants;

/**
 * 帐号启用状态枚举
 */
public enum UserStatusEnum {
    /**
     * 禁用状态
     */
    DISABLED(0, "禁用"),

    /**
     * 启用状态
     */
    ENABLED(1, "启用");

    private final Integer code;
    private final String description;

    UserStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值
     * @param code 状态编码
     * @return 对应的枚举值，找不到返回null
     */
    public static UserStatusEnum fromCode(Integer code) {
        for (UserStatusEnum status : UserStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
