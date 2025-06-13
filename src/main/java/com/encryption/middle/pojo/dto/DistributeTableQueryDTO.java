package com.encryption.middle.pojo.dto;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class DistributeTableQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    private String[] filters;
}
