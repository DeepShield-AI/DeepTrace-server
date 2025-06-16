package com.encryption.middle.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Setter
@Getter
public class DistributeTableQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    private String[] filters;
}
