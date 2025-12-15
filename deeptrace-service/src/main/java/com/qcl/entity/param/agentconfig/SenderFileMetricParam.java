package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 文件配置
 */
@Data
public class SenderFileMetricParam {
    /**
     * 文件路径
     */
    private String path = "metrics.csv";
    /**
     * 是否进行文件翻转
     */
    private Boolean rotate = true;
    /**
     * 文件最大大小
     */
    @Min(value = 256, message = "maxSize must be between 256 and 1024")
    @Max(value = 1024, message = "maxSize must be between 256 and 1024")
    @JsonProperty("max_size")
    private Integer maxSize;
    /**
     * 文件最大保存天数
     */
    @Min(value = 1, message = "maxAge must be between 1 and 7")
    @Max(value = 7, message = "maxAge must be between 1 and 7")
    @JsonProperty("max_age")
    private Integer maxAge;
    /**
     * 文件翻转时间间隔
     */
    @JsonProperty("rotate_time")
    private Integer rotateTime = 1;
    /**
     * 文件数据格式
     */
    @JsonProperty("data_format")
    private String dataFormat;

    // Constructors
    public SenderFileMetricParam() {}

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getRotateTime() {
        return rotateTime;
    }

    public void setRotateTime(Integer rotateTime) {
        this.rotateTime = rotateTime;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
}
