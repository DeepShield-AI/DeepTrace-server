package com.qcl.entity.statistic;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TimeBucketResult {
    private Long timeKey;
    private Object value;

    public TimeBucketResult(Long timeKey, Object docCount) {
        this.timeKey = timeKey;
        this.value = docCount;
    }
}
