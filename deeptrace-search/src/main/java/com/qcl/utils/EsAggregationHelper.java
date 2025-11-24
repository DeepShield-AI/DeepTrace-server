package com.qcl.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.qcl.entity.Traces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch 聚合查询辅助类
 *
 * 作用：
 * - 对指定索引与字段执行 terms 聚合，返回去重后的字符串列表（bucket key）
 * - 将 Elasticsearch 调用封装为可注入的 Spring 组件，便于复用和单元测试替换
 *
 * 注意：
 * - 使用 terms 聚合时存在桶大小限制（本类默认返回最多 {@link #DEFAULT_SIZE} 个不同值）
 *   若需要全部值且数量可能超过该阈值，应改用 composite 聚合并分页拉取
 */
@Component
public class EsAggregationHelper {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(EsAggregationHelper.class);
    // 注入的 Elasticsearch Java 客户端（ELC）
    private final ElasticsearchClient elasticsearchClient;
    // 默认 terms 聚合桶大小（最多返回多少 distinct 值）
    private static final int DEFAULT_SIZE = 1000;

    /**
     * 构造函数，Spring 会注入 ElasticsearchClient
     * @param elasticsearchClient Elasticsearch Java client 实例
     */
    public EsAggregationHelper(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * 获取指定索引中某字段的去重字符串值，使用默认桶大小 {@link #DEFAULT_SIZE}
     * @param index 索引名，例如 "traces"
     * @param field 要聚合的字段名，例如 "endpoint.keyword"
     * @return 去重值列表（按聚合返回的 bucket 顺序），发生错误时返回空列表（不会抛异常）
     */
    public List<String> getDistinctTerms(String index, String field) {
        return getDistinctTerms(index, field, DEFAULT_SIZE);
    }

    /**
     * 获取指定索引中某字段的去重字符串值
     * @param index 索引名
     * @param field 字段名（通常使用 `.keyword` 类型字段以获得精确值）
     * @param size  terms 聚合的桶上限（最大返回多少个不同值）
     * @return 去重字符串列表；若出错或无聚合结果，返回空列表
     */
    public List<String> getDistinctTerms(String index, String field, int size) {
        try {
            // 发起聚合查询，size(0) 表示不返回文档，仅返回聚合
            SearchResponse<Traces> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .size(0)
                            .aggregations("distinct", a -> a.terms(t -> t.field(field).size(size))),
                    Traces.class
            );

            List<String> result = new ArrayList<>();

            // 如果聚合存在，解析 string terms bucket 的 key
            if (response.aggregations() != null && response.aggregations().containsKey("distinct")) {
                StringTermsAggregate agg = response.aggregations().get("distinct").sterms();
                if (agg != null && agg.buckets() != null && agg.buckets().array() != null) {
                    for (StringTermsBucket bucket : agg.buckets().array()) {
                        // bucket.key() 可能为 null，使用 stringValue 安全获取
                        if (bucket != null && bucket.key() != null) {
                            result.add(bucket.key().stringValue());
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            // 判断无数据或查询失败
            logger.error("Failed to fetch distinct terms for field {} in index {}", field, index, e);
            return new ArrayList<>();
        }
    }
}

/**
 * 查询ES某字段所有去重值
 */
//private List<String> getAllDistinctValues(String field) {
//    try {
//        SearchResponse<Traces> response = elasticsearchClient.search(s -> s
//                        .index("traces")
//                        .size(0)
//                        .aggregations("distinct", a -> a.terms(t -> t.field(field).size(1000))),
//                Traces.class
//        );
//        List<String> result = new ArrayList<>();
//        if (response.aggregations() != null && response.aggregations().containsKey("distinct")) {
//            StringTermsAggregate agg = response.aggregations().get("distinct").sterms();
//            for (StringTermsBucket bucket : agg.buckets().array()) {
//                result.add(bucket.key().stringValue());
//            }
//        }
//        return result;
//    } catch (Exception e) {
//        return new ArrayList<>();
//    }
//}