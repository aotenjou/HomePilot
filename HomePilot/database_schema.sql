-- 智能家居管理系统数据库表结构
-- 创建时间: 2024年
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `manager`;

-- 1. 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(255) NOT NULL COMMENT '密码(加密)',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 家庭表
CREATE TABLE `home` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '家庭ID',
  `name` varchar(100) NOT NULL COMMENT '家庭名称',
  `address` varchar(255) DEFAULT NULL COMMENT '家庭地址',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭表';

-- 3. 房间表
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `home_id` bigint NOT NULL COMMENT '所属家庭ID',
  `name` varchar(100) NOT NULL COMMENT '房间名称',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_name` (`name`),
  CONSTRAINT `fk_room_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 4. 设备类型表
CREATE TABLE `device_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备类型ID',
  `name` varchar(100) NOT NULL COMMENT '设备类型名称',
  `description` varchar(500) DEFAULT NULL COMMENT '设备类型描述',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备类型表';

-- 5. 设备表
CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `ip_address` varchar(45) NOT NULL COMMENT '设备IP地址',
  `home_id` bigint NOT NULL COMMENT '所属家庭ID',
  `room_id` bigint DEFAULT NULL COMMENT '所属房间ID',
  `type_id` bigint NOT NULL COMMENT '设备类型ID',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `online_status` int DEFAULT 0 COMMENT '在线状态(0:离线,1:在线)',
  `active_status` int DEFAULT 0 COMMENT '激活状态(0:未激活,1:已激活)',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_online_status` (`online_status`),
  CONSTRAINT `fk_device_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`),
  CONSTRAINT `fk_device_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `fk_device_type` FOREIGN KEY (`type_id`) REFERENCES `device_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- 6. 操作表
CREATE TABLE `operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `name` varchar(100) NOT NULL COMMENT '操作名称',
  `description` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作表';

-- 7. 设备操作关联表
CREATE TABLE `device_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `device_type_id` bigint NOT NULL COMMENT '设备类型ID',
  `operation_id` bigint NOT NULL COMMENT '操作ID',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_type_operation` (`device_type_id`, `operation_id`),
  KEY `idx_device_type_id` (`device_type_id`),
  KEY `idx_operation_id` (`operation_id`),
  CONSTRAINT `fk_device_operation_type` FOREIGN KEY (`device_type_id`) REFERENCES `device_type` (`id`),
  CONSTRAINT `fk_device_operation_operation` FOREIGN KEY (`operation_id`) REFERENCES `operation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备操作关联表';

-- 8. 用户家庭关联表
CREATE TABLE `user_home` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `home_id` bigint NOT NULL COMMENT '家庭ID',
  `role` int NOT NULL DEFAULT 1 COMMENT '角色(0:房主,1:家庭成员,2:访客)',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_home` (`user_id`, `home_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_role` (`role`),
  CONSTRAINT `fk_user_home_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_home_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户家庭关联表';

-- 9. 场景表
CREATE TABLE `scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '场景ID',
  `home_id` bigint NOT NULL COMMENT '所属家庭ID',
  `user_id` bigint NOT NULL COMMENT '创建用户ID',
  `name` varchar(100) NOT NULL COMMENT '场景名称',
  `description` varchar(500) DEFAULT NULL COMMENT '场景描述',
  `status` int DEFAULT 0 COMMENT '状态(0:未激活,1:激活)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_scene_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`),
  CONSTRAINT `fk_scene_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景表';

-- 10. 场景设备关联表
CREATE TABLE `device_in_scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `device_operation_id` bigint NOT NULL COMMENT '设备操作ID',
  `scene_id` bigint NOT NULL COMMENT '场景ID',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_device_operation_id` (`device_operation_id`),
  KEY `idx_scene_id` (`scene_id`),
  CONSTRAINT `fk_device_in_scene_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `fk_device_in_scene_operation` FOREIGN KEY (`device_operation_id`) REFERENCES `device_operation` (`id`),
  CONSTRAINT `fk_device_in_scene_scene` FOREIGN KEY (`scene_id`) REFERENCES `scene` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景设备关联表';

-- 11. 访客记录表
CREATE TABLE `guest_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `home_id` bigint NOT NULL COMMENT '家庭ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `record_type` int NOT NULL COMMENT '记录类型(0:进入,1:离开)',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_record_time` (`record_time`),
  CONSTRAINT `fk_guest_record_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`),
  CONSTRAINT `fk_guest_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客记录表';

-- 12. 进入请求记录表
CREATE TABLE `enter_request_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请求ID',
  `home_id` bigint NOT NULL COMMENT '家庭ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` int DEFAULT 0 COMMENT '状态(0:等待处理,1:已通过,2:被拒绝)',
  `record_time` datetime NOT NULL COMMENT '请求时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_record_time` (`record_time`),
  CONSTRAINT `fk_enter_request_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`),
  CONSTRAINT `fk_enter_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='进入请求记录表';

-- 13. 角色默认权限表
CREATE TABLE `role_default_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role` int NOT NULL COMMENT '角色(0:房主,1:家庭成员,2:访客)',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `operation_id` bigint NOT NULL COMMENT '操作ID',
  `has_permission` tinyint(1) DEFAULT 1 COMMENT '是否有权限',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_device_operation` (`role`, `device_id`, `operation_id`),
  KEY `idx_role` (`role`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_operation_id` (`operation_id`),
  CONSTRAINT `fk_role_permission_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `fk_role_permission_operation` FOREIGN KEY (`operation_id`) REFERENCES `operation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色默认权限表';

-- 14. 用户自定义权限表
CREATE TABLE `user_custom_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `home_id` bigint NOT NULL COMMENT '家庭ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `operation_id` bigint NOT NULL COMMENT '操作ID',
  `has_permission` tinyint(1) DEFAULT 1 COMMENT '是否有权限',
  `end_time` datetime DEFAULT NULL COMMENT '权限结束时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_home_device_operation` (`user_id`, `home_id`, `device_id`, `operation_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_home_id` (`home_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_operation_id` (`operation_id`),
  KEY `idx_end_time` (`end_time`),
  CONSTRAINT `fk_user_permission_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_permission_home` FOREIGN KEY (`home_id`) REFERENCES `home` (`id`),
  CONSTRAINT `fk_user_permission_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`),
  CONSTRAINT `fk_user_permission_operation` FOREIGN KEY (`operation_id`) REFERENCES `operation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户自定义权限表';

-- 15. MQTT数据表
CREATE TABLE `mqtt_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `datatime` datetime NOT NULL COMMENT '数据时间',
  `datatopic` varchar(255) NOT NULL COMMENT '数据主题',
  `datavalue` int DEFAULT NULL COMMENT '数据值',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_datatime` (`datatime`),
  KEY `idx_datatopic` (`datatopic`),
  CONSTRAINT `fk_mqtt_data_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQTT数据表';

-- 插入基础数据

-- 插入默认设备类型
INSERT INTO `device_type` (`name`, `description`) VALUES 
('智能灯泡', '可调节亮度和颜色的智能灯泡'),
('智能插座', '可远程控制的智能插座'),
('智能门锁', '支持指纹、密码、NFC等多种开锁方式'),
('智能摄像头', '支持移动侦测和远程监控'),
('智能温控器', '可调节室内温度的智能温控器'),
('智能窗帘', '可远程控制的智能窗帘'),
('智能音箱', '支持语音控制的智能音箱'),
('智能传感器', '温湿度、光照等环境传感器');

-- 插入基础操作
INSERT INTO `operation` (`name`, `description`) VALUES 
('开关', '设备的开关操作'),
('调节亮度', '调节设备亮度'),
('调节颜色', '调节设备颜色'),
('调节温度', '调节设备温度'),
('播放音乐', '播放音乐操作'),
('拍照录像', '拍照或录像操作'),
('锁定解锁', '锁定或解锁操作'),
('调节音量', '调节设备音量');

-- 插入设备类型与操作关联
INSERT INTO `device_operation` (`device_type_id`, `operation_id`, `description`) VALUES 
(1, 1, '智能灯泡开关'),
(1, 2, '智能灯泡亮度调节'),
(1, 3, '智能灯泡颜色调节'),
(2, 1, '智能插座开关'),
(3, 7, '智能门锁锁定解锁'),
(4, 6, '智能摄像头拍照录像'),
(5, 4, '智能温控器温度调节'),
(6, 1, '智能窗帘开关'),
(7, 5, '智能音箱播放音乐'),
(7, 8, '智能音箱音量调节');

-- 创建视图

-- 家庭用户视图
CREATE VIEW `v_home_user` AS
SELECT 
    h.id as home_id,
    h.name as home_name,
    h.address as home_address,
    u.id as user_id,
    u.username as user_name,
    u.phone as user_phone,
    uh.role as user_role,
    CASE uh.role 
        WHEN 0 THEN '房主'
        WHEN 1 THEN '家庭成员'
        WHEN 2 THEN '访客'
        ELSE '未知'
    END as role_name
FROM home h
JOIN user_home uh ON h.id = uh.home_id
JOIN user u ON uh.user_id = u.id
WHERE h.is_deleted = 0 AND uh.is_deleted = 0 AND u.is_deleted = 0;

-- 家庭房间视图
CREATE VIEW `v_home_room` AS
SELECT 
    h.id as home_id,
    h.name as home_name,
    r.id as room_id,
    r.name as room_name
FROM home h
JOIN room r ON h.id = r.home_id
WHERE h.is_deleted = 0 AND r.is_deleted = 0;

-- 家庭设备视图
CREATE VIEW `v_home_device` AS
SELECT 
    h.id as home_id,
    h.name as home_name,
    r.id as room_id,
    r.name as room_name,
    d.id as device_id,
    d.name as device_name,
    d.ip_address,
    dt.name as device_type_name,
    d.online_status,
    d.active_status,
    d.last_active_time,
    CASE d.online_status 
        WHEN 0 THEN '离线'
        WHEN 1 THEN '在线'
        ELSE '未知'
    END as online_status_name,
    CASE d.active_status 
        WHEN 0 THEN '未激活'
        WHEN 1 THEN '已激活'
        ELSE '未知'
    END as active_status_name
FROM home h
JOIN device d ON h.id = d.home_id
LEFT JOIN room r ON d.room_id = r.id
JOIN device_type dt ON d.type_id = dt.id
WHERE h.is_deleted = 0 AND d.is_deleted = 0 AND dt.is_deleted = 0;

-- 场景设备视图
CREATE VIEW `v_scene_device` AS
SELECT 
    s.id as scene_id,
    s.name as scene_name,
    s.description as scene_description,
    s.status as scene_status,
    h.id as home_id,
    h.name as home_name,
    d.id as device_id,
    d.name as device_name,
    dt.name as device_type_name,
    o.name as operation_name,
    o.description as operation_description,
    dis.id as device_in_scene_id
FROM scene s
JOIN home h ON s.home_id = h.id
JOIN device_in_scene dis ON s.id = dis.scene_id
JOIN device d ON dis.device_id = d.id
JOIN device_type dt ON d.type_id = dt.id
JOIN device_operation do ON dis.device_operation_id = do.id
JOIN operation o ON do.operation_id = o.id
WHERE s.is_deleted = 0 AND dis.is_deleted = 0 AND d.is_deleted = 0;

-- 创建索引优化查询性能
CREATE INDEX `idx_device_home_room` ON `device` (`home_id`, `room_id`);
CREATE INDEX `idx_user_home_role` ON `user_home` (`user_id`, `role`);
CREATE INDEX `idx_scene_home_status` ON `scene` (`home_id`, `status`);
CREATE INDEX `idx_guest_record_home_time` ON `guest_record` (`home_id`, `record_time`);
CREATE INDEX `idx_enter_request_home_status` ON `enter_request_record` (`home_id`, `status`);
CREATE INDEX `idx_mqtt_data_device_time` ON `mqtt_data` (`device_id`, `datatime`);

-- 添加表注释
ALTER TABLE `user` COMMENT = '用户表 - 存储系统用户信息';
ALTER TABLE `home` COMMENT = '家庭表 - 存储家庭基本信息';
ALTER TABLE `room` COMMENT = '房间表 - 存储房间信息，关联家庭';
ALTER TABLE `device_type` COMMENT = '设备类型表 - 存储设备类型分类';
ALTER TABLE `device` COMMENT = '设备表 - 存储智能设备信息';
ALTER TABLE `operation` COMMENT = '操作表 - 存储设备可执行的操作';
ALTER TABLE `device_operation` COMMENT = '设备操作关联表 - 定义设备类型支持的操作';
ALTER TABLE `user_home` COMMENT = '用户家庭关联表 - 定义用户与家庭的关系和角色';
ALTER TABLE `scene` COMMENT = '场景表 - 存储自动化场景配置';
ALTER TABLE `device_in_scene` COMMENT = '场景设备关联表 - 定义场景中包含的设备操作';
ALTER TABLE `guest_record` COMMENT = '访客记录表 - 记录访客进出记录';
ALTER TABLE `enter_request_record` COMMENT = '进入请求记录表 - 记录进入家庭请求';
ALTER TABLE `role_default_permission` COMMENT = '角色默认权限表 - 定义角色对设备的默认权限';
ALTER TABLE `user_custom_permission` COMMENT = '用户自定义权限表 - 存储用户自定义的设备权限';
ALTER TABLE `mqtt_data` COMMENT = 'MQTT数据表 - 存储设备上报的数据';

-- 完成提示
SELECT '数据库表结构创建完成！' as message;



