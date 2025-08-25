package com.qcl.service;

import com.qcl.entity.graph.ContainerStatsResult;
import com.qcl.entity.param.QueryTracesParam;

import java.util.List;

public interface EsTraceGraphService {

    List<ContainerStatsResult> getContainerStats(QueryTracesParam queryTracesParam);
}
