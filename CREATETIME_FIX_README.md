# CreateTime 字段添加说明

## 问题描述
在添加 `createTime` 字段后，创建家庭接口 `/home/create` 出现 500 错误。

## 问题原因
数据库表中还没有 `createTime` 字段，但代码已经尝试插入该字段。

## 解决方案

### 方案一：执行数据库迁移脚本（推荐）

1. 执行以下 SQL 脚本添加 `createTime` 字段：

```sql
-- 简化版迁移脚本
USE manager;
ALTER TABLE Home ADD COLUMN createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '家庭创建时间';
UPDATE Home SET createTime = NOW() WHERE createTime IS NULL;
```

2. 执行完成后，恢复 HomeMapper.xml 中的完整 SQL 语句：

```xml
<insert id="createHome">
    INSERT INTO Home (name, address, createTime) VALUES (#{name}, #{address}, NOW())
</insert>
```

### 方案二：使用当前兼容版本（临时方案）

当前代码已经修改为兼容版本，即使数据库中没有 `createTime` 字段也能正常工作：

- `createHome` 方法只插入 `name` 和 `address` 字段
- 查询方法使用 `SELECT *` 来兼容有无 `createTime` 字段的情况

## 文件修改说明

### 已修改的文件：
1. `src/main/java/com/example/manager/entity/Home.java` - 添加了 createTime 字段
2. `src/main/resources/mapper/HomeMapper.xml` - 修改为兼容版本
3. `src/main/resources/db/schema.sql` - 更新了表结构定义

### 数据库迁移脚本：
- `database_migration_add_createtime.sql` - 完整版迁移脚本
- `simple_migration_add_createtime.sql` - 简化版迁移脚本

## 测试建议

1. 先使用当前兼容版本测试创建家庭功能是否正常
2. 如果正常，再执行数据库迁移脚本
3. 迁移完成后，可以恢复完整的 SQL 语句以获得更好的性能

## 注意事项

- 执行数据库迁移前请备份数据库
- 如果 `createTime` 字段已存在，迁移脚本会报错，可以忽略
- 建议在测试环境先验证迁移脚本的正确性
