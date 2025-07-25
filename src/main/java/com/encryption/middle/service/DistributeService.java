package com.encryption.middle.service;

import com.encryption.middle.pojo.dto.DistributeTableDataDTO;
import com.encryption.middle.pojo.dto.DistributeTableQueryDTO;
import com.encryption.middle.pojo.dto.FlamegraphQueryDTO;
import com.encryption.middle.pojo.dto.ServiceListDTO;
import com.encryption.middle.result.PageResult;
import org.springframework.stereotype.Service;

import java.io.IOException;


public interface DistributeService {
    PageResult DistributeTableDataQuery(DistributeTableQueryDTO distributeTableDataDTO) throws IOException;
    PageResult FlamegraphDataQuery(FlamegraphQueryDTO flamegraphDataQuery) throws IOException;

    PageResult ServiceTableDataQuery(ServiceListDTO serviceListDTO) throws IOException;
}
