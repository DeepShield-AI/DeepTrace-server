package com.qcl.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PageResult implements Serializable {
    private long total; //总记录数
    private List records; //当前数据集合
    private Object data; //单一数据
}
