package com.qcl.service;

import com.qcl.entity.Edges;
import com.qcl.entity.EndpointProtocolStatsResult;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.vo.PageResult;

import java.util.List;

public interface EsEdgesServices {

    PageResult<Edges> queryByPage(QueryTracesParam queryTracesParam);

    List<EndpointProtocolStatsResult> getEndpointProtocolStats(QueryTracesParam queryTracesParam);
}
