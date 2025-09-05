-- 在manager数据库中创建传感器数据表
USE manager;

-- 创建传感器数据表
CREATE TABLE IF NOT EXISTS sensor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据记录ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    device_mac VARCHAR(20) COMMENT '设备MAC地址',
    topic VARCHAR(255) NOT NULL COMMENT 'MQTT主题',
    temperature DOUBLE COMMENT '温度（摄氏度）',
    humidity DOUBLE COMMENT '湿度（百分比）',
    light_a INT COMMENT '灯光A状态',
    light_b INT COMMENT '灯光B状态', 
    light_c INT COMMENT '灯光C状态',
    fan_state INT COMMENT '风扇状态',
    fire_state INT COMMENT '火焰传感器状态',
    gas_state INT COMMENT '气体传感器状态',
    auto_temp INT COMMENT '自动温控状态',
    fire_open INT COMMENT '消防开关状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
    message_raw TEXT COMMENT '原始消息内容',
    INDEX idx_device_id (device_id),
    INDEX idx_device_mac (device_mac),
    INDEX idx_create_time (create_time),
    INDEX idx_device_time (device_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器数据表';

-- 为Device表添加mac_address字段（如果需要的话）
-- ALTER TABLE Device ADD COLUMN mac_address VARCHAR(20) COMMENT '设备MAC地址';

-- 插入一些测试数据 
INSERT INTO sensor_data (device_id, device_mac, topic, temperature, humidity, light_a, light_b, light_c, fan_state, fire_state, gas_state, auto_temp, fire_open, message_raw) VALUES
(1, 'DC:11:31:EA:21:4E', 'Hardware_OutPut', 25.5, 60.2, 0, 0, 0, 0, 0, 0, 0, 1, '{"title":"Room","device":"DC:11:31:EA:21:4E","temperature":25.5}'),
(1, '70:11:31:E2:0E:0E', 'Hardware_OutPut', 29.6, 47.1, 0, 0, 0, 0, 0, 0, 0, 1, '{"title":"Room","device":"70:11:31:E2:0E:0E","temperature":29.6}');

SELECT 'sensor_data 表已创建并插入测试数据' AS message;

