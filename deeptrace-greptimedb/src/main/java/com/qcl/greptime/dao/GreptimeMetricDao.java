package com.qcl.greptime.dao;

import com.qcl.greptime.entity.MetricPoint;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

public interface GreptimeMetricDao {
    List<MetricPoint> queryByMetricAndRange(@Param("metric") String metric,
                                            @Param("start") Long start,
                                            @Param("end") Timestamp end);
}