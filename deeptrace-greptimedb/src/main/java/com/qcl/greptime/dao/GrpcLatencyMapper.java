package com.qcl.greptime.dao;


import com.qcl.greptime.entity.GrpcLatency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface GrpcLatencyMapper {

    @Select("SELECT COUNT(*) FROM grpc_latencies")
    int countAll();
    
    @Select("SELECT * FROM grpc_latencies ORDER BY ts DESC LIMIT 5")
    List<GrpcLatency> selectLatestFive();

    @Select("SELECT * FROM grpc_latencies WHERE host = #{host} ORDER BY ts DESC LIMIT 5")
    List<GrpcLatency> selectLatestFiveByHostAndMethod(@Param("host") String host);


    List<Map<String, Object>> selectPercentileLatencyByTimeRange(
            @Param("percentile") Double percentile,
            @Param("valueColumn") String valueColumn,
            @Param("groupByColumn") String groupByColumn,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);

    List<Map<String, Object>> queryTQL();

}
