package com.qcl.entity.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeBucketResult {
    private Long timeKey;
    private Long docCount;
    private String statusCode;

    public TimeBucketResult(Long timeKey, Long docCount) {
        this.timeKey = timeKey;
        this.docCount = docCount;
    }
}
