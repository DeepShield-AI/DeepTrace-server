package com.qcl.entity;

import lombok.Data;

@Data
public class UserDTO {
    /**
    * 用户ID，主键，自增
     */
    private Long userId;
    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    private Integer status;
    /**
     * 用户角色 admin：管理员  user：普通用户
     */
    private String role;
}
