package com.qcl.service;

import com.qcl.entity.DistributeTableQueryDTO;
import com.qcl.entity.FlamegraphQueryDTO;
import com.qcl.entity.ServiceListDTO;
import com.qcl.result.PageResult;

import java.io.IOException;


public interface DistributeService {
    PageResult DistributeTableDataQuery(DistributeTableQueryDTO distributeTableDataDTO) throws IOException;
    PageResult FlamegraphDataQuery(FlamegraphQueryDTO flamegraphDataQuery) throws IOException;

    PageResult ServiceTableDataQuery(ServiceListDTO serviceListDTO) throws IOException;
}
