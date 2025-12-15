package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * 发送配置
 */
@Data
public class SenderParam {
    /**
     * elastic配置
     */
    @Valid
    @JsonProperty("elastic")
    private SenderElasticParam elastic;
    @Valid
    @JsonProperty("file")
    private SenderFileParam file;

    // Constructors
    public SenderParam() {}


}
