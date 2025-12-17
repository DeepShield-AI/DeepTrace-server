/**
   * 采集器管理信息表（注册、启用、禁用、删除等）
 */
CREATE TABLE agent_manage_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type varchar(64)  NOT NULL COMMENT '管理类型 register：注册  enable：启用  disable：禁用  delete：删除',
    host_ip VARCHAR(255) NOT NULL COMMENT '采集器IP地址',
    host_password VARCHAR(255) NOT NULL COMMENT '采集器密码',
    ssh_port INT NOT NULL COMMENT 'SSH端口',
    agent_name VARCHAR(255) NOT NULL COMMENT '采集器名称',
    user_name VARCHAR(255) COMMENT '用户名',
    user_id VARCHAR(255) COMMENT '用户ID',
    create_time TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_ip_type`(`host_ip`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采集器管理信息表（注册、启用、禁用、删除等）';
