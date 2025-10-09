package com.qcl.entity.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按分钟/小时等 统计请求个数
 */
@Deprecated
@Data
@NoArgsConstructor
public class TimeBucketCountResult extends Object{
    private Long timeKey;
    private Long docCount;

    public TimeBucketCountResult(Long timeKey, Long docCount) {
        this.timeKey = timeKey;
        this.docCount = docCount;
    }
}
