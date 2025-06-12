package com.encryption.middle.pojo.dto;

import java.io.Serializable;

public class DistributeTableQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    private String[] filters;
}
