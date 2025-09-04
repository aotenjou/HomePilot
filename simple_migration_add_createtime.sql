-- 简化版数据库迁移脚本：为 Home 表添加 createTime 字段
-- 如果字段已存在会报错，可以忽略

USE manager;

-- 添加 createTime 字段
ALTER TABLE Home ADD COLUMN createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '家庭创建时间';

-- 为现有数据设置默认创建时间
UPDATE Home SET createTime = NOW() WHERE createTime IS NULL;
