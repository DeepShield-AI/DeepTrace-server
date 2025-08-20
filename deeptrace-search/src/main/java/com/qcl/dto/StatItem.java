package com.qcl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatItem {
    private String date;
    private String type;
    private Number value;
}