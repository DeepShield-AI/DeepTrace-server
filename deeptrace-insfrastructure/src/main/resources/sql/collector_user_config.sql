CREATE TABLE `collector_user_config` (
                                         `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `config_id` varchar(128) DEFAULT NULL COMMENT '配置ID',
                                         `group_id` varchar(128) DEFAULT NULL COMMENT '采集器组ID',
                                         `group_name` varchar(128) DEFAULT NULL COMMENT '采集器组名称',
                                         `team_name` varchar(128) DEFAULT NULL COMMENT '所属团队',
                                         `max_cpus` int DEFAULT NULL COMMENT ' CPU限制(Core)',
                                         `max_memory` int DEFAULT NULL COMMENT '内存限制(MB)',
                                         `collection_port` varchar(256) DEFAULT NULL COMMENT '采集网口',
                                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                         `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         `user_id` int DEFAULT NULL COMMENT '所属用户',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采集器配置表（用户添加）';



