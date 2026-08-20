package com.qcl.greptime.service;

import com.qcl.greptime.dao.GreptimeMetricDao;
import com.qcl.greptime.dao.GrpcLatencyMapper;
import com.qcl.greptime.entity.GrpcLatency;
import com.qcl.greptime.entity.MetricPoint;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricWithUserService {

    private final GreptimeMetricDao greptimeMetricDao;
    private final GrpcLatencyMapper grpcLatencyMapper;


    public Object getGrpcLatency(String host, String method,
                                 Double percentile,
                                 String valueColumn,
                                 String groupByColumn,
                                 Long start,
                                 Long end) {
        Object res = null;
        switch ( method) {
            case "1":
                res =  grpcLatencyMapper.selectLatestFiveByHostAndMethod(host);
                break;
            case "2":
                res =  grpcLatencyMapper.selectPercentileLatencyByTimeRange(percentile, valueColumn, groupByColumn,start, end);
                break;
            case "3":
                res =  grpcLatencyMapper.queryTQL();
                break;
            default:
                res =  grpcLatencyMapper.selectLatestFiveByHostAndMethod(host);
                break;
        }
        return res;
    }

    public List<Object> getMetricsWithUser(Long userId, String metric, Long start, Timestamp end) {
        List<MetricPoint> points = greptimeMetricDao.queryByMetricAndRange(metric, start, end);

        // 示例合并 DTO（这里只返回简单 Map-like 对象示例）
        List<Object> result = points.stream()
                .map(p -> {
                    return new java.util.HashMap<String, Object>() {{
                        put("ts",convertUtcToBeijing(p.getTs()));
                        put("value", p.getValue1());
                        put("metric", p.getMetric1());
                        put("userName", "lmm");
                    }};
                })
                .collect(Collectors.toList());
        return result;
    }

    // 将UTC时间戳转换为北京时间 
    public static String convertUtcToBeijing(Timestamp utcTimestamp) {
        if (utcTimestamp == null) {
            return null;
        }

        // 获取北京时间时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");

        // 转换过程：UTC Instant → Beijing ZonedDateTime → Beijing Instant → Timestamp
        Instant utcInstant = utcTimestamp.toInstant();
        ZonedDateTime beijingDateTime = utcInstant.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(beijingZone);
//        return Timestamp.from(beijingDateTime.toInstant());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return beijingDateTime.format(formatter);
    }
}
