package com.qcl.entity.statistic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusTimeBucketResult {
    private String statusCode;
    private List<TimeBucketResult> timeBuckets;
}
