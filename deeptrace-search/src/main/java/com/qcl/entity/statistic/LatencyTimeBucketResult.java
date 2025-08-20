package com.qcl.entity.statistic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatencyTimeBucketResult {
    private Long timeKey;
    private Object avgDuration;
    private Object p75Duration;
    private Object p90Duration;
    private Object p99Duration;
}
