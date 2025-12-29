package com.qcl.service;

import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.Nodes;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryNodeParam;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsNodeService {

    PageResult<Nodes> queryByPage(QueryNodeParam queryNodeParam, UserDTO user);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryNodeParam queryNodeParam, UserDTO user);

    List<StatusTimeBucketResult> getStatusCountByMinute(QueryNodeParam queryNodeParam, UserDTO user);

    List<TimeBucketResult> qpsByMinute(QueryNodeParam queryNodeParam, UserDTO user);

    List<TimeBucketResult> errorRateByMinute(QueryNodeParam queryNodeParam, UserDTO user);

    List<TimeBucketResult> latencyStatsByMinute(QueryNodeParam queryNodeParam, UserDTO user);
}
