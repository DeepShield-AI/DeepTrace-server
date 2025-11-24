package com.qcl.entity.statistic;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 状态码分组的时间桶结果（嵌套结构）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusTimeBucketResult {
    private String statusCode;
//    private List<TimeBucketCountResult> timeBuckets;
    private List<TimeBucketResult> timeBuckets;
}
