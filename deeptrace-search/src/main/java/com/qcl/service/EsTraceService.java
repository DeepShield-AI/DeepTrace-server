package com.qcl.service;

import com.qcl.entity.Traces;
import com.qcl.entity.UserDTO;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;
import java.util.Map;


public interface EsTraceService {
    /**
     * 返回分页结果对象
     */
    PageResult<Traces> queryByPageResult(QueryTracesParam param, UserDTO user);
    /**
     * 根据 Trace ID 查询单个 Trace 详情，返回分页结果对象（方便前端统一处理）
     */
    PageResult<Traces> traceDetailResult(QueryTracesParam param);
    /**
     * 获取所有可用的筛选项
     */
    Map<String, List<String>> getAllFilterOptions();
    /**
     * 滚动查询 Trace 列表
     */
    Map<String, Object> scrollQuery(QueryTracesParam param, String scrollId, Integer pageSize);
    /**
     * 按分钟统计 Trace 数量
     */
//    List<TimeBucketCountResult> getTraceCountByMinute(QueryTracesParam queryTracesParam);
    List<TimeBucketResult> getTraceCountByMinute(QueryTracesParam queryTracesParam, UserDTO user);
    /**
     * 按分钟统计状态码分组数量
     */
    List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam queryTracesParam, UserDTO user);
    /**
     * 按分钟统计延迟分布
     */
    List<LatencyTimeBucketResult> getLatencyStatsByMinute(QueryTracesParam queryTracesParam, UserDTO user);
}


