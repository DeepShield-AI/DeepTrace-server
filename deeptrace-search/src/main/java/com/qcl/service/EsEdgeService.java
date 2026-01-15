package com.qcl.service;

import com.qcl.entity.Edges;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryEdgeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsEdgeService {

    PageResult<Edges> queryByPage(QueryEdgeParam queryEdgeParam, UserDTO user);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryEdgeParam queryEdgeParam, UserDTO user);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryEdgeParam queryEdgeParam, UserDTO user);

    List<TimeBucketResult> qpsByMinute(QueryEdgeParam queryEdgeParam, UserDTO user);

    List<TimeBucketResult> errorRateByMinute(QueryEdgeParam queryEdgeParam, UserDTO user);

    List<TimeBucketResult> latencyStatsByMinute(QueryEdgeParam queryEdgeParam, UserDTO user);
}
