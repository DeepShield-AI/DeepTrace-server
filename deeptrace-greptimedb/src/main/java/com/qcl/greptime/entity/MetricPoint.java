package com.qcl.greptime.entity;

import java.sql.Timestamp;

public class MetricPoint {
    private Timestamp ts;
    private String metric1;
    private Double value1;
    private String tag1;

    // getters / setters
    public Timestamp getTs() { return ts; }
    public void setTs(Timestamp ts) { this.ts = ts; }
    public String getMetric1() { return metric1; }
    public void setMetric1(String metric1) { this.metric1 = metric1; }
    public Double getValue1() { return value1; }
    public void setValue1(Double value1) { this.value1 = value1; }
    public String getTag1() { return tag1; }
    public void setTag1(String tag1) { this.tag1 = tag1; }
}