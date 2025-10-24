package com.qcl.service;

import com.qcl.entity.Edges;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.param.QueryEdgeParam;
import com.qcl.entity.param.QueryNodeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsEdgeService {

    PageResult<Edges> queryByPage(QueryEdgeParam queryEdgeParam);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryEdgeParam queryEdgeParam);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryEdgeParam queryEdgeParam);

    List<TimeBucketResult> qpsByMinute(QueryEdgeParam queryEdgeParam);

    List<TimeBucketResult> errorRateByMinute(QueryEdgeParam queryEdgeParam);

    List<TimeBucketResult> latencyStatsByMinute(QueryEdgeParam queryEdgeParam);
}
