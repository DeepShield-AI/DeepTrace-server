/**
   * 采集器用户配置表（用户添加）
 */
CREATE TABLE agent_user_config (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                             host_ip VARCHAR(255) NOT NULL COMMENT '采集器IP地址',
                             agent_name VARCHAR(255) NOT NULL COMMENT '采集器名称',
                             user_id VARCHAR(255) COMMENT '用户ID',
                             config VARCHAR(2048) COMMENT '用户完整配置',
                             create_time TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             UNIQUE KEY `uk_ip`(`host_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采集器用户配置表（用户添加）';
