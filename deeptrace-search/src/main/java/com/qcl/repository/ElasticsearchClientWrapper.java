package com.qcl.repository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchClientWrapper {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 执行搜索查询
     */
    public SearchResponse<Map> search(String index, SearchRequest searchRequest) {
        try {
            searchRequest.index().add(index);
            return elasticsearchClient.search(searchRequest, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("搜索请求执行失败", e);
        }
    }

    /**
     * 批量索引文档
     */
    public void bulkIndex(String index, List<Map<String, Object>> documents) {
        try {
            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

            for (Map<String, Object> doc : documents) {
                bulkBuilder.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .document(doc)
                        )
                );
            }

            BulkResponse result = elasticsearchClient.bulk(bulkBuilder.build());

            if (result.errors()) {
                log.error("批量索引部分失败: {}", result.items());
            } else {
                log.info("批量索引成功: {} 条文档", documents.size());
            }

        } catch (IOException e) {
            throw new RuntimeException("批量索引失败", e);
        }
    }

    /**
     * 检查索引是否存在
     */
    public boolean indexExists(String indexName) {
        try {
            ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(indexName));
            return elasticsearchClient.indices().exists(existsRequest).value();
        } catch (IOException e) {
            log.error("检查索引是否存在失败: {}", indexName, e);
            return false;
        }
    }

    /**
     * 转换SearchResponse到Map列表
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> extractHits(SearchResponse<Map> response) {
        return response.hits().hits().stream()
                .map(hit -> (Map<String, Object>) hit.source())
                .collect(Collectors.toList());
    }

    /**
     * 获取总命中数
     */
    public long getTotalHits(SearchResponse<Map> response) {
        return response.hits().total().value();
    }

    /**
     * 列出所有索引
     */
    public List<String> listAllIndices() {
        try {
            log.info("开始列出所有 ES 索引");
            GetIndexRequest getIndexRequest = GetIndexRequest.of(e -> e.index("*"));
            GetIndexResponse getIndexResponse = elasticsearchClient.indices().get(getIndexRequest);
            List<String> indices = getIndexResponse.result().keySet().stream().collect(Collectors.toList());
            log.info("成功获取到 {} 个索引", indices.size());
            for (String index : indices) {
                log.info("- {}", index);
            }
            return indices;
        } catch (IOException e) {
            log.error("列出所有索引失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 执行聚合查询，将指定字段的所有值列出来然后去重，返回去重后的列表
     */
    public List<String> getDistinctValues(String index, String field) {
        try {
            log.info("开始执行查询: 索引={}, 字段={}", index, field);
            
            // 检查索引是否存在
            if (!indexExists(index)) {
                log.error("索引不存在: {}", index);
                return new ArrayList<>();
            }
            
            // 尝试查询ES库中的数据，使用合理的size值
            try {
                log.info("尝试查询ES库中的数据");
                // 查询文档，设置一个合理的size值
                SearchRequest searchRequest = SearchRequest.of(s -> s
                        .index(index)
                        .size(100000000) // 设置一个合理的size值
                        .timeout("30s") // 设置合理的超时时间
                        .query(q -> q.matchAll(m -> m)) // 使用matchAll查询
                );
                SearchResponse<Map> searchResponse = elasticsearchClient.search(searchRequest, Map.class);
                log.info("查询完成，共获取到 {} 条文档", searchResponse.hits().hits().size());
                
                // 从文档中提取字段值
                Set<String> valueSet = new HashSet<>();
                for (var hit : searchResponse.hits().hits()) {
                    var source = hit.source();
                    if (source != null) {
                        // 尝试从source中提取字段值
                        Object value = extractFieldValue(source, field);
                        if (value != null) {
                            valueSet.add(value.toString());
                        }
                    }
                }
                List<String> distinctValues = new ArrayList<>(valueSet);
                log.info("成功从数据中提取字段值，共 {} 个去重后的值", distinctValues.size());
                
                // 打印获取到的值
                if (!distinctValues.isEmpty()) {
                    log.info("获取到的值示例: {}", distinctValues.subList(0, Math.min(10, distinctValues.size())));
                }
                return distinctValues;
            } catch (Exception e) {
                log.warn("查询数据失败，返回空列表: {}", e.getMessage());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("执行查询失败: 索引={}, 字段={}", index, field, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 从Map中提取指定字段的值，支持嵌套字段路径
     * @param source 源Map
     * @param fieldPath 字段路径，例如 "metrics.tags.cpu"
     * @return 字段值
     */
    private Object extractFieldValue(Map<?, ?> source, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Object current = source;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
                if (current == null) {
                    break;
                }
            } else {
                break;
            }
        }
        
        return current;
    }
    
    /**
     * 执行查询，枚举去重出tags下所有的key和值，返回类似[{cpu: 0}]这样的格式
     */
    public List<Map<String, Object>> getDistinctTags(String index) {
        try {
            log.info("开始执行查询: 索引={}", index);
            
            // 检查索引是否存在
            if (!indexExists(index)) {
                log.error("索引不存在: {}", index);
                return new ArrayList<>();
            }
            
            // 测试性查询：先查询该索引的前5条文档，验证是否能从ES中查到数据
            log.info("执行测试性查询，验证ES连接和索引数据");
            try {
                SearchRequest testRequest = SearchRequest.of(s -> s
                        .index(index)
                        .size(5) // 只查询前5条
                        .timeout("10s") // 设置较短的超时时间
                        .query(q -> q.matchAll(m -> m)) // 使用matchAll查询
                );
                SearchResponse<Map> testResponse = elasticsearchClient.search(testRequest, Map.class);
                log.info("测试性查询完成，共获取到 {} 条文档", testResponse.hits().hits().size());
                // 打印前几条文档的内容
                for (int i = 0; i < Math.min(3, testResponse.hits().hits().size()); i++) {
                    var hit = testResponse.hits().hits().get(i);
                    log.info("测试文档 {}: {}", i+1, hit.source());
                }
            } catch (Exception e) {
                log.warn("测试性查询失败: {}", e.getMessage());
                // 测试性查询失败，继续执行主查询
            }
            
            // 查询包含tags字段的文档，获取tags数据
            log.info("执行tags字段查询，获取tags数据");
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(index)
                    .size(100000000) // 设置合理的size值
                    .timeout("30s") // 设置合理的超时时间
                    .query(q -> q.matchAll(m -> m)) // 使用matchAll查询
            );
            
            // 执行搜索请求
            log.info("执行搜索请求");
            SearchResponse<Map> searchResponse = elasticsearchClient.search(searchRequest, Map.class);
            log.info("tags字段查询执行完成，共获取到 {} 条文档", searchResponse.hits().hits().size());
            
            // 提取tags字段下的所有key和值
            List<Map<String, Object>> distinctTags = new ArrayList<>();
            Set<String> seenTags = new HashSet<>(); // 用于去重
            
            // 处理搜索结果
            for (var hit : searchResponse.hits().hits()) {
                var source = hit.source();
                if (source != null) {
                    // 检查是否包含tags字段
                    if (source.containsKey("tags")) {
                        var tags = source.get("tags");
                        if (tags instanceof Map) {
                            // 处理tags是Map的情况，比如 {"tags": {"cpu": 0}} 或 {"tags": {"cpu=0"}}
                            Map<?, ?> tagsMap = (Map<?, ?>) tags;
                            for (Map.Entry<?, ?> entry : tagsMap.entrySet()) {
                                String key = entry.getKey().toString();
                                Object value = entry.getValue();
                                
                                // 检查value是否是字符串，且格式为"key=value"
                                if (value instanceof String && ((String) value).contains("=")) {
                                    String[] parts = ((String) value).split("=", 2);
                                    if (parts.length == 2) {
                                        String tagKey = parts[0];
                                        String tagValue = parts[1];
                                        
                                        // 创建一个包含key和value的Map
                                        Map<String, Object> tagMap = new HashMap<>();
                                        tagMap.put(tagKey, tagValue);
                                        
                                        // 生成一个唯一标识，用于去重
                                        String tagIdentifier = tagKey + ":" + tagValue;
                                        if (!seenTags.contains(tagIdentifier)) {
                                            seenTags.add(tagIdentifier);
                                            distinctTags.add(tagMap);
                                        }
                                    }
                                } else {
                                    // 常规的键值对处理
                                    String tagKey = key;
                                    Object tagValue = value;
                                    
                                    // 创建一个包含key和value的Map
                                    Map<String, Object> tagMap = new HashMap<>();
                                    tagMap.put(tagKey, tagValue);
                                    
                                    // 生成一个唯一标识，用于去重
                                    String tagIdentifier = tagKey + ":" + tagValue.toString();
                                    if (!seenTags.contains(tagIdentifier)) {
                                        seenTags.add(tagIdentifier);
                                        distinctTags.add(tagMap);
                                    }
                                }
                            }
                        } else if (tags instanceof List) {
                            // 处理tags是List的情况，比如 {"tags": [{"cpu": 0}]}
                            List<?> tagsList = (List<?>) tags;
                            for (Object tag : tagsList) {
                                if (tag instanceof Map) {
                                    Map<?, ?> tagMap = (Map<?, ?>) tag;
                                    for (Map.Entry<?, ?> entry : tagMap.entrySet()) {
                                        String key = entry.getKey().toString();
                                        Object value = entry.getValue();
                                        String tagKey = key;
                                        Object tagValue = value;
                                        
                                        // 创建一个包含key和value的Map
                                        Map<String, Object> tagMapObj = new HashMap<>();
                                        tagMapObj.put(tagKey, tagValue);
                                        
                                        // 生成一个唯一标识，用于去重
                                        String tagIdentifier = tagKey + ":" + tagValue.toString();
                                        if (!seenTags.contains(tagIdentifier)) {
                                            seenTags.add(tagIdentifier);
                                            distinctTags.add(tagMapObj);
                                        }
                                    }
                                } else if (tag instanceof String && ((String) tag).contains("=")) {
                                    // 处理tags列表中包含"key=value"格式字符串的情况
                                    String[] parts = ((String) tag).split("=", 2);
                                    if (parts.length == 2) {
                                        String tagKey = parts[0];
                                        String tagValue = parts[1];
                                        
                                        // 创建一个包含key和value的Map
                                        Map<String, Object> tagMap = new HashMap<>();
                                        tagMap.put(tagKey, tagValue);
                                        
                                        // 生成一个唯一标识，用于去重
                                        String tagIdentifier = tagKey + ":" + tagValue;
                                        if (!seenTags.contains(tagIdentifier)) {
                                            seenTags.add(tagIdentifier);
                                            distinctTags.add(tagMap);
                                        }
                                    }
                                }
                            }
                        } else if (tags instanceof String) {
                            // 处理tags是字符串的情况，比如 {"tags": "cpu=0"}
                            String tagsStr = (String) tags;
                            if (tagsStr.contains("=")) {
                                String[] parts = tagsStr.split("=", 2);
                                if (parts.length == 2) {
                                    String tagKey = parts[0];
                                    String tagValue = parts[1];
                                    
                                    // 创建一个包含key和value的Map
                                    Map<String, Object> tagMap = new HashMap<>();
                                    tagMap.put(tagKey, tagValue);
                                    
                                    // 生成一个唯一标识，用于去重
                                    String tagIdentifier = tagKey + ":" + tagValue;
                                    if (!seenTags.contains(tagIdentifier)) {
                                        seenTags.add(tagIdentifier);
                                        distinctTags.add(tagMap);
                                    }
                                }
                            }
                        } else {
                            // 处理tags是其他类型的情况
                            log.warn("tags字段类型未知: {}", tags.getClass());
                        }
                    }
                }
            }
            
            log.info("tags字段处理完成: 共获取到 {} 个去重后的tags", distinctTags.size());
            // 打印获取到的tags
            if (!distinctTags.isEmpty()) {
                log.info("获取到的tags示例: {}", distinctTags.subList(0, Math.min(10, distinctTags.size())));
            }
            return distinctTags;
        } catch (Exception e) {
            log.error("执行查询失败: 索引={}", index, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 执行查询，获取曲线图指标数据
     */
    public List<Map<String, Object>> getMetricChartData(String index, String namespace, String startTime, String endTime, String cpu, String device, String networkInterface, Integer dataSize, String name) {
        try {
            log.info("开始执行曲线图指标查询: 索引={}, namespace={}, startTime={}, endTime={}, cpu={}, device={}, networkInterface={}, dataSize={}, name={}", 
                    index, namespace, startTime, endTime, cpu, device, networkInterface, dataSize, name);
            
            // 检查索引是否存在
            if (!indexExists(index)) {
                log.error("索引不存在: {}", index);
                return new ArrayList<>();
            }
            
            // 构建搜索请求，添加查询条件
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();
            searchRequestBuilder.index(index);
            searchRequestBuilder.size(10000); // 减小size值，避免内存问题
            searchRequestBuilder.timeout("30s"); // 设置合理的超时时间
            
            // 构建查询条件 - 使用Elasticsearch的filter方法来过滤信息
            try {
                // 构建bool查询，使用filter子句进行过滤
                searchRequestBuilder.query(q -> q
                        .bool(b -> {
                            // 添加filter子句
                            b.filter(f -> f
                                    .bool(fb -> {
                                        // 添加namespace条件
                                        if (namespace != null && !namespace.isEmpty()) {
                                            fb.must(m -> m.term(t -> t.field("namespace").value(namespace)));
                                        }
                                        
                                        // 添加name条件
                                        if (name != null && !name.isEmpty()) {
                                            fb.must(m -> m.match(t -> t.field("name").query(name)));
                                        }
                                        
                                        // 添加tags.cpu条件，支持嵌套对象和字符串格式
                                        if (cpu != null && !cpu.isEmpty()) {
                                            fb.must(m -> m
                                                .bool(cpuBool -> cpuBool
                                                    .should(s -> s.term(t -> t.field("tags.cpu").value(cpu)))
                                                    .should(s -> s.term(t -> t.field("tags").value("cpu=" + cpu)))
                                                )
                                            );
                                        }
                                        
                                        // 添加tags.device条件，支持嵌套对象和字符串格式
                                        if (device != null && !device.isEmpty()) {
                                            fb.must(m -> m
                                                .bool(deviceBool -> deviceBool
                                                    .should(s -> s.term(t -> t.field("tags.device").value(device)))
                                                    .should(s -> s.term(t -> t.field("tags").value("device=" + device)))
                                                )
                                            );
                                        }
                                        
                                        // 添加tags.interface条件，支持嵌套对象和字符串格式
                                        if (networkInterface != null && !networkInterface.isEmpty()) {
                                            fb.must(m -> m
                                                .bool(interfaceBool -> interfaceBool
                                                    .should(s -> s.term(t -> t.field("tags.interface").value(networkInterface)))
                                                    .should(s -> s.term(t -> t.field("tags").value("interface=" + networkInterface)))
                                                )
                                            );
                                        }
                                        
                                        return fb;
                                    })
                            );
                            
                            return b;
                        })
                );
            } catch (Exception e) {
                log.warn("构建查询条件失败，使用matchAll查询: {}", e.getMessage());
                // 使用matchAll查询作为 fallback
                searchRequestBuilder.query(q -> q.matchAll(m -> m));
            }
            
            // 添加排序，使用更安全的方式
            try {
                // 只使用基本的排序，不检查字段是否存在，让Elasticsearch自动处理缺失的字段
                searchRequestBuilder.sort(sort -> sort
                        .field(f -> f
                                .field("timestamp")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                        )
                );
            } catch (Exception e) {
                log.warn("添加排序失败，跳过排序: {}", e.getMessage());
            }
            
            SearchRequest searchRequest = searchRequestBuilder.build();
            log.info("构建的搜索请求: {}", searchRequest);
            
            // 执行搜索请求
            log.info("执行搜索请求");
            SearchResponse<Map> searchResponse = elasticsearchClient.search(searchRequest, Map.class);
            log.info("曲线图指标查询完成，共获取到 {} 条文档", searchResponse.hits().hits().size());
            
            // 提取并过滤数据
            List<Map<String, Object>> chartData = new ArrayList<>();
            // 添加调试日志，查看前5条原始数据
            int debugCount = 0;
            for (var hit : searchResponse.hits().hits()) {
                var source = hit.source();
                if (source != null && debugCount < 5) {
                    log.debug("原始数据 {}: {}", debugCount + 1, source);
                    debugCount++;
                }
            }
            
            for (var hit : searchResponse.hits().hits()) {
                var source = hit.source();
                if (source != null) {
                    // 在内存中进行时间范围过滤
                    boolean match = true;
                    
                    // 过滤时间范围
                    if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                        Object timestampObj = source.get("timestamp");
                        if (timestampObj == null) {
                            match = false;
                        } else {
                            try {
                                long docTimestamp = 0;
                                if (timestampObj instanceof Long) {
                                    docTimestamp = (Long) timestampObj;
                                } else {
                                    docTimestamp = Long.parseLong(timestampObj.toString());
                                }
                                long startTimestamp = Long.parseLong(startTime);
                                long endTimestamp = Long.parseLong(endTime);
                                
                                if (docTimestamp < startTimestamp || docTimestamp > endTimestamp) {
                                    match = false;
                                }
                            } catch (Exception e) {
                                log.warn("时间戳解析失败: {}", timestampObj, e);
                                match = false;
                            }
                        }
                    }
                    
                    if (match) {
                        Map<String, Object> dataPoint = new HashMap<>();
                        
                        // 提取时间戳
                        if (source.containsKey("timestamp")) {
                            Object timestampObj = source.get("timestamp");
                            if (timestampObj != null) {
                                try {
                                    long timestampMs = 0;
                                    if (timestampObj instanceof Long) {
                                        timestampMs = (Long) timestampObj;
                                    } else {
                                        timestampMs = Long.parseLong(timestampObj.toString());
                                    }
                                    dataPoint.put("timestamp", timestampMs);
                                } catch (Exception e) {
                                    log.warn("时间戳转换失败: {}", timestampObj, e);
                                    dataPoint.put("timestamp", timestampObj.toString());
                                }
                            }
                        }
                        
                        // 提取值
                        if (source.containsKey("value")) {
                            dataPoint.put("value", source.get("value"));
                        }
                        
                        // 提取tags
                        if (source.containsKey("tags")) {
                            dataPoint.put("tags", source.get("tags"));
                        }
                        
                        // 提取namespace
                        if (source.containsKey("namespace")) {
                            dataPoint.put("namespace", source.get("namespace"));
                        }
                        
                        // 提取name
                        if (source.containsKey("name")) {
                            dataPoint.put("name", source.get("name"));
                        }
                        
                        chartData.add(dataPoint);
                    }
                }
            }
            
            // 如果指定了dataSize，并且数据量大于dataSize，则进行平均抽样
            if (dataSize != null && dataSize > 0 && chartData.size() > dataSize) {
                log.info("原始数据量为 {}，需要抽样到 {} 个数据点", chartData.size(), dataSize);
                // 解析startTime和endTime为毫秒时间戳
                long startTimestamp = 0;
                long endTimestamp = 0;
                try {
                    if (startTime != null && !startTime.isEmpty()) {
                        startTimestamp = Long.parseLong(startTime);
                    }
                    if (endTime != null && !endTime.isEmpty()) {
                        endTimestamp = Long.parseLong(endTime);
                    }
                } catch (Exception e) {
                    log.warn("时间戳解析失败: {}", e.getMessage());
                }
                chartData = sampleData(chartData, dataSize, startTimestamp, endTimestamp);
                log.info("抽样完成，共 {} 个数据点", chartData.size());
            }
            
            log.info("曲线图指标数据提取完成，共 {} 个数据点", chartData.size());
            return chartData;
        } catch (Exception e) {
            log.error("执行曲线图指标查询失败: 索引={}", index, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 对数据进行平均抽样，确保第一个元素是最接近startTime的时间，最后一个元素是最接近endTime的时间
     * @param data 原始数据列表
     * @param sampleSize 抽样大小
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 抽样后的数据列表
     */
    private List<Map<String, Object>> sampleData(List<Map<String, Object>> data, int sampleSize, long startTime, long endTime) {
        List<Map<String, Object>> sampledData = new ArrayList<>();
        int dataSize = data.size();
        
        // 如果数据量小于等于抽样大小，直接返回
        if (dataSize <= sampleSize) {
            return data;
        }
        
        // 找到最接近startTime的数据点
        Map<String, Object> firstPoint = data.get(0);
        long firstTimestamp = getTimestamp(firstPoint);
        long minStartTimeDiff = Math.abs(firstTimestamp - startTime);
        
        for (Map<String, Object> point : data) {
            long timestamp = getTimestamp(point);
            long diff = Math.abs(timestamp - startTime);
            if (diff < minStartTimeDiff) {
                minStartTimeDiff = diff;
                firstPoint = point;
            }
        }
        
        // 找到最接近endTime的数据点
        Map<String, Object> lastPoint = data.get(dataSize - 1);
        long lastTimestamp = getTimestamp(lastPoint);
        long minEndTimeDiff = Math.abs(lastTimestamp - endTime);
        
        for (Map<String, Object> point : data) {
            long timestamp = getTimestamp(point);
            long diff = Math.abs(timestamp - endTime);
            if (diff < minEndTimeDiff) {
                minEndTimeDiff = diff;
                lastPoint = point;
            }
        }
        
        // 如果抽样大小为1，直接返回最接近startTime的数据点
        if (sampleSize == 1) {
            sampledData.add(firstPoint);
            return sampledData;
        }
        
        // 计算中间需要抽样的数量
        int middleSampleSize = sampleSize - 2;
        List<Map<String, Object>> middleData = new ArrayList<>();
        
        // 过滤掉已经选中的第一个和最后一个数据点
        for (Map<String, Object> point : data) {
            if (!point.equals(firstPoint) && !point.equals(lastPoint)) {
                middleData.add(point);
            }
        }
        
        // 对中间数据进行平均抽样
        if (middleSampleSize > 0 && !middleData.isEmpty()) {
            double interval = (double) middleData.size() / middleSampleSize;
            for (int i = 0; i < middleSampleSize; i++) {
                int index = (int) Math.round(i * interval);
                index = Math.min(index, middleData.size() - 1);
                sampledData.add(middleData.get(index));
            }
        }
        
        // 添加第一个和最后一个数据点
        sampledData.add(0, firstPoint);
        sampledData.add(lastPoint);
        
        return sampledData;
    }
    
    /**
     * 从数据点中获取时间戳
     * @param dataPoint 数据点
     * @return 时间戳（毫秒）
     */
    private long getTimestamp(Map<String, Object> dataPoint) {
        Object timestampObj = dataPoint.get("timestamp");
        if (timestampObj instanceof Long) {
            return (Long) timestampObj;
        } else if (timestampObj instanceof String) {
            try {
                return Long.parseLong((String) timestampObj);
            } catch (Exception e) {
                log.warn("时间戳解析失败: {}", timestampObj, e);
                return 0;
            }
        }
        return 0;
    }
}
