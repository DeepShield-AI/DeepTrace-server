package com.qcl.entity.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户注册
 */
@Getter
@Setter
public class UserParam {
    /**
     * 用户名
     */
    @NotEmpty
    private String username;
    /**
     * 密码
     */
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "密码必须由数字和字母组成，至少8位")
    private String password;
    /**
     * 电话号码
     */
    @NotEmpty
    @Pattern(regexp = "^\\d{11}$", message = "电话号码必须为11位数字")
    private String phone;
    /**
     * 邮箱
     */
    @NotEmpty
    private String email;
    /**
     * 帐号启用状态：0->禁用；1->启用
     */
//    private Integer status;
    /**
     * 备注信息
     */
    private String note;

}
