-- 数据库迁移脚本：为 Home 表添加 createTime 字段
-- 执行前请备份数据库

USE manager;

-- 检查 createTime 字段是否已存在，如果不存在则添加
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'manager' 
     AND TABLE_NAME = 'Home' 
     AND COLUMN_NAME = 'createTime') = 0,
    'ALTER TABLE Home ADD COLUMN createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''家庭创建时间'';',
    'SELECT ''createTime字段已存在'' as message;'
));

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有数据设置默认创建时间（设置为当前时间）
UPDATE Home SET createTime = NOW() WHERE createTime IS NULL;

-- 验证字段是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'manager' 
AND TABLE_NAME = 'Home' 
AND COLUMN_NAME = 'createTime';
