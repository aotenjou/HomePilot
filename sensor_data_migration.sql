-- 创建传感器数据表，用于存储硬件发送的详细传感器数据
CREATE TABLE IF NOT EXISTS sensor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据记录ID',
    device_id BIGINT NOT NULL COMMENT '设备ID，关联Device表',
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

-- 为Device表添加mac_address字段（如果不存在的话）
ALTER TABLE Device ADD COLUMN IF NOT EXISTS mac_address VARCHAR(20) COMMENT '设备MAC地址';
ALTER TABLE Device ADD INDEX IF NOT EXISTS idx_mac_address (mac_address);

