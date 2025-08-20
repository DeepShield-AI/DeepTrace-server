package com.qcl.service;

import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface EsTraceService {
    public  List<Traces> queryByPage(QueryTracesParam queryTracesParam);
}
