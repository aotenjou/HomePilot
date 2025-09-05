/*
  建立smart_home的数据库
*/
DROP DATABASE IF EXISTS manager;

CREATE DATABASE IF NOT EXISTS manager
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE manager;

/*
  建表
*/
-- 1.用户表
CREATE TABLE User (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一标识',
                       username VARCHAR(255) NOT NULL COMMENT '用户名',
                       -- sex INTEGER COMMENT '性别',
                       -- email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱（登录用）',
                       phone VARCHAR(255) COMMENT '手机号',
                       password varchar(255) COMMENT '密码',
                       -- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间'
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2.家庭表
CREATE TABLE Home (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '家庭唯一标识',
                       name VARCHAR(255) NOT NULL COMMENT '家庭名称',
                       address VARCHAR(255) COMMENT '家庭地址',
                       createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '家庭创建时间',
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭表';


--  3.用户家庭关联表
CREATE TABLE User_Home (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户与家庭角色关联ID',
                            user_id BIGINT NOT NULL COMMENT '用户ID，关联Users表',
                            home_id BIGINT NOT NULL COMMENT '家庭ID，关联Homes表',
                            role INTEGER NOT NULL COMMENT '角色类型（房主/成员/访客）',
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与家庭角色关联表';

CREATE TABLE ROOM (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间唯一标识',
                       name VARCHAR(255) NOT NULL COMMENT '房间名称',
                       home_id BIGINT NOT NULL COMMENT '所属家庭ID',
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';
#
-- 4.设备类型表
CREATE TABLE Device_Type (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备类型ID',
                              name VARCHAR(255) NOT NULL COMMENT '设备类型名称',
                              description VARCHAR(255) COMMENT '设备类型描述',
                              is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类型表';

-- 5.设备操作表
CREATE TABLE Operation (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '操作唯一标识',
                            name VARCHAR(255) NOT NULL COMMENT '操作名称，如TurnOn',
                            description VARCHAR(255) COMMENT '操作描述',
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备操作表';
-- 6.设备表
CREATE TABLE Device (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备唯一标识',
                         ip_address VARCHAR(20) NOT NULL COMMENT '设备IP地址',
                         home_id BIGINT NOT NULL COMMENT '所属家庭ID',
                         type_id BIGINT NOT NULL COMMENT '设备类型ID',
                         name VARCHAR(255) NOT NULL COMMENT '设备名称',
                         online_status INTEGER COMMENT '设备当前状态',
                         active_status INTEGER COMMENT '设备当前状态',
                         last_active_time DATETIME COMMENT '最后活跃时间',
                         room_id varchar(255) NOT NULL COMMENT '所在位置',
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';
#
-- 7. 角色默认权限配置表
CREATE TABLE Role_Default_Permission (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '占位符',
                                          role INTEGER NOT NULL COMMENT '角色类型',
                                          device_id BIGINT NOT NULL COMMENT '设备ID，关联设备表',
                                          operation_id BIGINT NOT NULL COMMENT '操作ID，关联Operations表',
                                          has_permission BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否拥有该权限',
                                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色默认权限配置表';
#
#
# -- 8.用户自定义权限表
CREATE TABLE User_Custom_Permission (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自定义权限ID',
                                         user_id BIGINT NOT NULL COMMENT '用户ID，关联Users表',
                                         home_id BIGINT NOT NULL COMMENT '家庭ID，关联Homes表',
                                         device_id BIGINT NOT NULL COMMENT '具体设备ID',
                                         operation_id BIGINT NOT NULL COMMENT '具体操作ID',
                                         has_permission BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否授权该操作',
                                         end_time DATETIME COMMENT '授权有效期',
                                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户自定义设备权限表';
#
-- 9.设备支持操作表
CREATE TABLE Device_Operation (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '占位符',
                                   device_type_id BIGINT NOT NULL COMMENT '设备类型ID',
                                   operation_id BIGINT NOT NULL COMMENT '操作ID',
                                   description VARCHAR(255) COMMENT '操作描述',
                                   is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类型支持的操作表';
#
# -- 10.访客记录表
CREATE TABLE Guest_Record (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '访客记录ID',
                              home_id BIGINT NOT NULL COMMENT '家庭ID，关联Homes表',
                              user_id BIGINT NOT NULL  COMMENT '用户ID，关联Users表',
                              type INTEGER COMMENT '访客记录类型',
                              record_time DATETIME NOT NULL COMMENT '访客记录时间',
                              is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客记录表';

CREATE TABLE Enter_Request_Record (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '访客记录ID',
                              home_id BIGINT NOT NULL COMMENT '家庭ID，关联Homes表',
                              user_id BIGINT NOT NULL  COMMENT '用户ID，关联Users表',
                              status INTEGER COMMENT '申请状态',
                              record_time DATETIME NOT NULL COMMENT '访客记录时间',
                              is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客申请记录表';
# 12.植物养护表
CREATE TABLE DeviceData_Plant (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '植物养护数据ID',
                                      device_id BIGINT NOT NULL COMMENT '设备ID',
                                      data_time BIGINT NOT NULL COMMENT '用户ID',
                                      humidity INTEGER COMMENT '湿度',
                                      illumination INTEGER NOT NULL COMMENT '光照'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='植物养护表';

-- 12.房间表
CREATE TABLE Rooms(
                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
                      home_id BIGINT NOT NULL COMMENT '家庭ID',
                      name varchar(255) NOT NULL COMMENT '房间名称',
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '删除状态，默认未删除'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客申请记录表';
-- 13.场景表
CREATE TABLE Scene(
                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场景ID',
                      home_id BIGINT NOT NULL COMMENT '家庭ID',
                      user_id BIGINT NOT NULL COMMENT '用户ID',
                      name varchar(255) NOT NULL COMMENT '场景名称',
                      description varchar(255) NOT NULL COMMENT '场景描述',
                      status INTEGER COMMENT '场景当前状态',
                      start_time DATETIME COMMENT '场景有效时间',
                      end_time DATETIME COMMENT '场景有效时间',
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景表';

-- 14.场景设备表
CREATE TABLE Device_In_Scene(
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场景设备ID',
                                 device_id BIGINT NOT NULL COMMENT '设备ID',
                                 operation_id BIGINT NOT NULL COMMENT '操作类型ID',
                                 scene_id BIGINT NOT NULL COMMENT '场景ID',
                                 is_deleted BOOLEAN NOT NULL DEFAULT FALSE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景设备表';


-- 插入的测试数据
INSERT INTO Device_Type (name, description) VALUES
('灯', '照到哪里哪里亮'),
('风扇', '感觉不如空调'),
('恒温器', '可以进去住两天再说'),
('门', '我要进来咯'),
('窗户', '不会有人从这里进来的对吧'),
('监控摄像头', '我会一直，一直视奸你……'),
('空调', '主不在乎'),
('加热器', '不如多盖几床被子'),
('电视', '抱歉，我和手机已经约好了'),
('冰箱', '不要想太多，我吃饱了'),
('洗衣机', '真的会有人手洗衣服吗'),
('洗碗机','就算有了这个还是得自己洗');

INSERT INTO Device (home_id, ip_address,type_id, name, online_status, active_status, last_active_time, room_id) VALUES
(1, 0,1, 'LED灯', 0, 0, NOW(), 1),
(1, 0,4, '撤硕大门', 0, 0, NOW(), 1),
(1, 0,5, '土壤湿度检测', 0, 0, NOW(), 1)
;

CREATE TABLE Device_Data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据ID',
  device_id BIGINT NOT NULL COMMENT '设备ID，关联Device表',
  illumination INTEGER NOT NULL COMMENT '数据类型',
  humidity INTEGER NOT NULL COMMENT '数据值',
  data_time DATETIME NOT NULL COMMENT '数据记录时间',
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备数据表';

CREATE TABLE device_test(
   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
   topic varchar(255) NOT NULL COMMENT '设备主题'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备订阅表';

INSERT INTO device_test (id, topic) VALUES
(1,'group3_led'),
(2,'group3_door'),
(3,'group3_soil');

CREATE TABLE Mqtt_data (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '占位符',
                                    device_id BIGINT NOT NULL COMMENT '设备ID',
                                    datatime DATETIME NOT NULL COMMENT '数据发送时间',
                                    datatopic VARCHAR(255) NOT NULL COMMENT '数据发送主题',
                                    datavalue INT COMMENT '数据数值（向后兼容）',
                                    sensor_data JSON COMMENT '完整的传感器数据（JSON格式）',
                                    temperature DECIMAL(5,2) COMMENT '温度',
                                    humidity DECIMAL(5,2) COMMENT '湿度',
                                    light_a INT COMMENT '光照传感器A',
                                    light_b INT COMMENT '光照传感器B',
                                    light_c INT COMMENT '光照传感器C',
                                    fan_state INT COMMENT '风扇状态',
                                    fire_state INT COMMENT '火焰状态',
                                    gas_state INT COMMENT '气体状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备传感器数据表';


INSERT INTO Device_Operation (device_type_id, operation_id, description) VALUES
(1, 1, '开关'),
(1, 2, '调光'),
(1, 3, '调色'),
(2, 1, '开关'),
(2, 2, '调速'),
(3, 1, '开关'),
(3, 2, '调光'),
(3, 3, '调色'),
(4, 1, '开关'),
(4, 2, '调速'),
(5, 1, '开关');

INSERT INTO Operation (id, name, description) VALUES
(1, 'TurnOn', '打开'),
(2, 'TurnOff', '关闭'),
(3, 'Dim', '调光'),
(4, 'Color', '调色'),
(5, 'Open', '打开'),
(6, 'Close', '关闭'),
(7, 'SpeedUp', '调速'),
(8, 'SpeedDown', '调速');