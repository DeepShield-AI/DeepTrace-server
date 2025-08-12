package com.qcl.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 采集器配置表（用户添加）(CollectorUserConfig)实体类
 */
@Setter
@Getter
public class CollectorUserConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 827792852025052156L;

    /** 主键ID */
    private Long id;
    /** 配置ID */
    private String configId;
    /** 采集器组ID */
    private String groupId;
    /** 采集器组名称 */
    @NotBlank(message = "采集器组名称不能为空")
    private String groupName;
    /** 所属团队 */
    @NotBlank(message = "所属团队不能为空")
    private String teamName;
    /** CPU限制(Core) */
    @NotNull(message = "CPU限制不能为空")
    @Positive(message = "CPU限制必须为正数")
    private Integer maxCpus;
    /** 内存限制(MB) */
    @NotNull(message = "内存限制不能为空")
    @Positive(message = "内存限制必须为正数")
    private Integer maxMemory;
    /** 采集网口 */
    @NotBlank(message = "采集网口不能为空")
    private String collectionPort;
    /** 创建时间 */
    private Date createdAt;
    /** 修改时间 */
    private Date updatedAt;
    /** 所属用户 */
    private Long userId;
}