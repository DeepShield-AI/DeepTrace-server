package com.qcl.entity.statistic;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 按分钟/小时等 统计（统一返回给前端的结构）
 */
@Data
@NoArgsConstructor
public class TimeBucketResult extends Object{
    private Long timeKey;
    private Object value;

    public TimeBucketResult(Long timeKey, Object docCount) {
        this.timeKey = timeKey;
        this.value = docCount;
    }
}
