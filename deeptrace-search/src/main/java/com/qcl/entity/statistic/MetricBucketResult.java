package com.qcl.entity.statistic;
import lombok.Data;
import lombok.NoArgsConstructor;

@Deprecated
@Data
@NoArgsConstructor
public class MetricBucketResult {
    public MetricBucketResult(int i, int j) {
        //TODO Auto-generated constructor stub
    }
    private Long timeKey;
    private Long value;
}
