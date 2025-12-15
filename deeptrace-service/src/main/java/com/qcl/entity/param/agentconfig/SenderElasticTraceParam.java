package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * elastic配置
 */
@Data
public class SenderElasticTraceParam {
    /**
     * elastic url --无需用户填写
     */
    private String nodeUrl;
    /**
     * elastic用户名 --无需用户填写
     */
    private String username;
    /**
     * elastic密码 --无需用户填写
     */
    private String password;
    /***
     * elastic index名称 --无需用户填写
     */
    private String indexName;
    /**
     * elastic请求超时时长，秒
     */
    @Min(value = 1, message = "requestTimeout must be between 1 and 20")
    @Max(value = 20, message = "requestTimeout must be between 1 and 20")
    @JsonProperty("request_timeout")
    private Integer requestTimeout;
    /**
     * elastic单次请求缓冲的条数
     */
    @Min(value = 16, message = "bulkSize must be between 16 and 1024")
    @Max(value = 1024, message = "bulkSize must be between 16 and 1024")
    @JsonProperty("bulk_size")
    private Integer bulkSize;

    // Constructors
    public SenderElasticTraceParam() {}

    // Getters and Setters
    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Integer getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(Integer bulkSize) {
        this.bulkSize = bulkSize;
    }
}