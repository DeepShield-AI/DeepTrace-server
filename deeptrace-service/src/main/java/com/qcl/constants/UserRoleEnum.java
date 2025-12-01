package com.qcl.constants;

public enum UserRoleEnum {
    /**
     * 管理员角色
     */
    ADMIN("admin", "管理员"),

    /**
     * 普通用户角色
     */
    USER("user", "普通用户");

    private final String code;
    private final String description;

    UserRoleEnum(String code, String description) {
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
     * @param code 角色编码
     * @return 对应的枚举值，找不到返回null
     */
    public static UserRoleEnum fromCode(String code) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}