package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 发送配置
 */
@Data
public class SenderParam {
    /**
     * elastic配置
     */
    @JsonProperty("elastic")
    private SenderElasticParam elastic;
    @JsonProperty("file")
    private SenderFileParam file;

    // Constructors
    public SenderParam() {}


}
