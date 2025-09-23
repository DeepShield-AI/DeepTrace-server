package com.qcl.service;

import com.qcl.entity.graph.EdgeStatsResult;
import com.qcl.entity.graph.NodeStatsResult;
import com.qcl.entity.param.QueryTracesParam;

import java.util.List;

public interface EsTraceGraphService {

    List<NodeStatsResult> getNodesStats(QueryTracesParam queryTracesParam);
    List<EdgeStatsResult> getEdgesStats(QueryTracesParam queryTracesParam);
}
