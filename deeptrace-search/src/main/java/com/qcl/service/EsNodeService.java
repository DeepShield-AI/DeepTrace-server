package com.qcl.service;

import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.param.QueryNodeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsNodeService {

    PageResult<Nodes> queryByPage(QueryNodeParam queryNodeParam);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryNodeParam queryNodeParam);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryNodeParam queryNodeParam);

    List<TimeBucketResult> qpsByMinute(QueryNodeParam queryNodeParam);

    List<TimeBucketResult> errorRateByMinute(QueryNodeParam queryNodeParam);

    List<TimeBucketResult> latencyStatsByMinute(QueryNodeParam queryNodeParam);
}
