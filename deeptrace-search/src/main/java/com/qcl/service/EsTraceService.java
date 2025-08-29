package com.qcl.service;

import com.qcl.entity.Traces;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.LatencyTimeBucketResult;
import com.qcl.entity.statistic.StatusTimeBucketResult;
import com.qcl.entity.statistic.TimeBucketResult;
import com.qcl.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Trace 查询服务接口
 */
public interface EsTraceService {
    /**
     * 深分页查询 Trace 列表
     */
//    List<Traces> queryByPage(QueryTracesParam param);
    /**
     * 返回分页结果对象
     */
    PageResult<Traces> queryByPageResult(QueryTracesParam param);

    /**
     * 滚动查询 Trace 列表
     */
    Map<String, Object> scrollQuery(QueryTracesParam param, String scrollId, Integer pageSize);

    /**
     * 按分钟统计 Trace 数量
     */
    List<TimeBucketResult> getTraceCountByMinute(QueryTracesParam param);

    /**
     * 按分钟统计状态码分组数量
     */
    List<StatusTimeBucketResult> getStatusCountByMinute(QueryTracesParam param);

    /**
     * 按分钟统计延迟分布
     */
    List<LatencyTimeBucketResult> getLatencyStatsByMinute(QueryTracesParam param);
}