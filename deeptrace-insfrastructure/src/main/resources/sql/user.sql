CCREATE TABLE `user` (
                        `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `username` varchar(64)  NOT NULL COMMENT '用户名',
                        `password` varchar(64)  NOT NULL COMMENT '密码',
                        `phone` varchar(64)  NOT NULL COMMENT '电话号码',
                        `email` varchar(64) NOT NULL COMMENT '邮箱',
                        `status` int(1) NOT NULL DEFAULT 1 COMMENT '帐号启用状态：0->禁用；1->启用',
                        `role` varchar(64)  NOT NULL COMMENT '用户角色 admin：管理员  user：普通用户',
                        `note` varchar(512) DEFAULT NULL COMMENT '备注信息',
                        `create_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                        `login_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `uk_username`(`username`)
)
    ENGINE = InnoDB
CHARACTER SET = utf8
COLLATE = utf8_general_ci
COMMENT = '用户个人表';