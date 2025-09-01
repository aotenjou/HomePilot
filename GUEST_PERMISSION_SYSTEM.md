# 访客权限系统实现说明

## 概述

本系统实现了基于角色的设备访问权限控制，特别针对访客用户进行了权限限制。访客只能访问部分设备，且只能进行查看类操作，不能进行控制操作。

## 系统架构

### 1. 角色定义

系统定义了三种用户角色：

- **房主 (HOST, role=0)**: 拥有所有设备的完整权限
- **家庭成员 (MEMBER, role=1)**: 拥有大部分设备权限，某些敏感设备受限
- **访客 (GUEST, role=2)**: 只能访问特定类型的设备，且只能进行查看操作

### 2. 权限控制层次

权限检查按以下优先级进行：

1. **用户自定义权限**: 检查 `User_Custom_Permission` 表中的个人权限设置
2. **角色默认权限**: 检查 `Role_Default_Permission` 表中的角色默认权限
3. **访客特殊限制**: 对访客用户进行额外的设备类型和操作类型限制

### 3. 访客权限限制

#### 可访问的设备类型
- 窗户设备 (type_id=5): 只能查看状态
- 监控摄像头 (type_id=6): 只能查看，不能控制

#### 可执行的操作
- 操作ID 1, 2: 查看类操作
- 禁止所有控制类操作 (操作ID > 2)

## 核心组件

### 1. DevicePermissionService

主要的权限服务接口，提供以下功能：

```java
// 检查设备操作权限
boolean checkDevicePermission(Long userId, Long homeId, Long deviceId, Long operationId);

// 获取用户可访问设备列表
List<Device> getAccessibleDevices(Long userId, Long homeId);

// 获取用户角色
UserHome.Role getUserRole(Long userId, Long homeId);

// 检查是否为访客
boolean isGuestUser(Long userId, Long homeId);
```

### 2. DevicePermissionChecker

权限检查中间件，在设备操作前进行权限验证：

```java
@Component
public class DevicePermissionChecker {
    public boolean checkPermission(HttpServletRequest request);
}
```

### 3. DeviceOperationInterceptor

设备操作拦截器，集成到Spring MVC拦截器链中：

```java
@Component
public class DeviceOperationInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);
}
```

## API接口

### 1. 设备管理接口

#### 获取用户可访问设备
```
GET /home/{homeId}/room/device/accessible-devices?userId={userId}
```

响应示例：
```json
{
  "devices": [...],
  "userRole": "GUEST",
  "isGuest": true
}
```

### 2. 访客权限专用接口

#### 获取访客可访问设备
```
GET /guest/{userId}/home/{homeId}/accessible-devices
```

#### 检查访客设备操作权限
```
GET /guest/{userId}/home/{homeId}/device/{deviceId}/operation/{operationId}/check
```

#### 获取访客权限说明
```
GET /guest/permission-info
```

### 3. 设备操作接口

#### 发送设备操作命令
```
POST /home/{homeId}/device/{deviceId}/operation/{operationId}
```

该接口会自动进行权限检查，访客无权限时会返回403错误。

## 数据库配置

### 1. 权限表结构

#### Role_Default_Permission (角色默认权限表)
```sql
CREATE TABLE Role_Default_Permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role INTEGER NOT NULL,           -- 角色代码 (0:房主, 1:成员, 2:访客)
    device_id BIGINT NOT NULL,       -- 设备ID
    operation_id BIGINT NOT NULL,    -- 操作ID
    has_permission BOOLEAN NOT NULL, -- 是否有权限
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
```

#### User_Custom_Permission (用户自定义权限表)
```sql
CREATE TABLE User_Custom_Permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,         -- 用户ID
    home_id BIGINT NOT NULL,         -- 家庭ID
    device_id BIGINT NOT NULL,       -- 设备ID
    operation_id BIGINT NOT NULL,    -- 操作ID
    has_permission BOOLEAN NOT NULL, -- 是否有权限
    end_time DATETIME,               -- 权限有效期
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
```

### 2. 权限数据初始化

执行 `src/main/resources/db/guest_permissions.sql` 脚本来初始化权限数据：

```bash
mysql -u username -p database_name < src/main/resources/db/guest_permissions.sql
```

## 配置说明

### 1. Web配置

在 `WebConfig.java` 中配置拦截器：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 设备操作权限拦截
    registry.addInterceptor(authInterceptor)
        .addPathPatterns("/home/{homeId}/device/{deviceId}/operation/*").order(1);
    registry.addInterceptor(deviceOperationInterceptor)
        .addPathPatterns("/home/{homeId}/device/{deviceId}/operation/*").order(2);
}
```

### 2. 访客权限配置

在 `DevicePermissionServiceImpl.java` 中配置访客可访问的设备类型：

```java
// 访客可访问的设备类型（只读设备）
private static final List<Long> GUEST_ACCESSIBLE_DEVICE_TYPES = Arrays.asList(
    5L,  // 窗户（只能查看状态）
    6L   // 监控摄像头（只能查看，不能控制）
);
```

## 测试

### 1. 单元测试

运行访客权限测试：

```bash
mvn test -Dtest=GuestPermissionTest
```

### 2. API测试

使用以下curl命令测试API：

```bash
# 获取访客可访问设备
curl -X GET "http://localhost:8080/guest/3/home/1/accessible-devices"

# 检查访客权限
curl -X GET "http://localhost:8080/guest/3/home/1/device/1/operation/1/check"

# 尝试访客无权限的操作
curl -X POST "http://localhost:8080/home/1/device/1/operation/1" \
  -H "Authorization: Bearer guest_token"
```

## 安全考虑

1. **权限验证**: 所有设备操作都经过权限验证
2. **角色隔离**: 访客用户被严格限制在特定设备类型
3. **操作限制**: 访客只能进行查看类操作
4. **日志记录**: 所有权限检查都有详细的日志记录

## 扩展说明

### 1. 添加新的访客可访问设备类型

修改 `DevicePermissionServiceImpl.java` 中的 `GUEST_ACCESSIBLE_DEVICE_TYPES` 列表。

### 2. 自定义访客权限

通过 `User_Custom_Permission` 表为特定访客用户设置自定义权限。

### 3. 添加新的操作类型

在 `Operation` 表中添加新操作，并在权限表中配置相应的权限。

## 故障排除

### 1. 权限检查失败

检查以下项目：
- 用户是否在 `User_Home` 表中有正确的角色
- `Role_Default_Permission` 表中是否有对应的权限配置
- 设备ID和操作ID是否正确

### 2. 访客无法访问设备

检查以下项目：
- 设备类型是否在 `GUEST_ACCESSIBLE_DEVICE_TYPES` 列表中
- 操作ID是否在允许的范围内（≤2）
- 数据库中是否有正确的权限配置

### 3. 拦截器不生效

检查以下项目：
- `WebConfig.java` 中的拦截器配置
- 请求路径是否匹配拦截器模式
- 拦截器顺序是否正确
