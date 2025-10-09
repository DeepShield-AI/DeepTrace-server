package com.qcl.service;

import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketCountResult;

import java.util.List;



public interface EsTraceService {
    List<Traces> queryByPage(QueryTracesParam queryTracesParam);
    List<TimeBucketCountResult> getTraceCountByMinute(QueryTracesParam queryTracesParam);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam queryTracesParam);

    List<LatencyTimeBucketResult> getLatencyStatsByMinute(QueryTracesParam queryTracesParam);
}
