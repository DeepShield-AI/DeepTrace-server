/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.qcl.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qcl.entity.param.QueryMetricParam;
import com.qcl.entity.param.QueryTracesParam;
import com.qcl.entity.statistic.MetricBucketResult;
import com.qcl.entity.statistic.TimeBucketCountResult;
import com.qcl.entity.statistic.TimeBucketResult;

/**
 *
 * @author 10264
 */
public interface EsMetricService {    
    List<MetricBucketResult> getDetailByMetricType(QueryMetricParam QueryMetricParam);

}
