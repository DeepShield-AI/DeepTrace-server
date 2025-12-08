package com.qcl.entity.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UserLoginParam {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
