package com.qcl.service;

import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsNodesServices {

    PageResult<Nodes> queryByPage(QueryTracesParam queryTracesParam);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryTracesParam queryTracesParam);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam queryTracesParam);

    List<TimeBucketResult> qpsByMinute(QueryTracesParam queryTracesParam);

    List<TimeBucketResult> errorRateByMinute(QueryTracesParam queryTracesParam);

    List<TimeBucketResult> latencyStatsByMinute(QueryTracesParam queryTracesParam);
}
