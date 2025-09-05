# 智能家居管理系统 API 文档

## 概述

本文档描述了智能家居管理系统的所有API接口，用于前后端对接开发。

- **基础URL**: `http://localhost:8080`
- **公网地址**：`http://8.130.88.98:8080`
- **认证方式**: JWT Token (在请求头中携带)
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

### 成功响应
```json
{
  "status": "success",
  "message": "操作成功",
  "data": {}
}
```

### 错误响应
```json
{
  "status": "error",
  "message": "错误描述"
}
```

## 认证相关接口

### 1. 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "登录成功"
}
```

**状态码**:
- `200`: 登录成功
- `404`: 用户不存在
- `401`: 密码错误

### 2. 用户注册

**接口地址**: `POST /auth/register`

**请求参数**:
```json
{
  "username": "张三",
  "phone": "13800138000",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "message": "注册成功"
}
```

**状态码**:
- `201`: 注册成功
- `400`: 手机号已注册
- `500`: 注册失败

### 3. 搜索用户

**接口地址**: `GET /auth/search-user-by-phone?phone={phone}`

**响应示例**:
```json
{
  "status": "success",
  "name": "张三",
  "userId": 1
}
```

## 家庭管理接口

### 1. 获取用户家庭列表

**接口地址**: `GET /home/get`

**请求头**: 需要JWT Token

**响应示例**:
```json
{
  "homes": [
    {
      "id": 1,
      "name": "我的家",
      "address": "北京市朝阳区xxx街道",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 创建家庭

**接口地址**: `POST /home/create`

**请求参数**:
```json
{
  "name": "新家庭",
  "address": "家庭地址"
}
```

**响应示例**:
```json
{
  "message": "创建成功"
}
```

### 3. 删除家庭

**接口地址**: `DELETE /home/delete/{homeId}`

**响应示例**:
```json
{
  "message": "删除成功"
}
```

### 4. 查看家庭详情

**接口地址**: `GET /home/view/{homeId}`

**响应示例**:
```json
{
  "home": {
    "id": 1,
    "name": "我的家",
    "address": "北京市朝阳区xxx街道"
  },
  "rooms": [
    {
      "id": 1,
      "name": "客厅",
      "homeId": 1
    }
  ],
  "members": [
    {
      "userId": 1,
      "username": "张三",
      "role": 0,
      "roleName": "房主"
    }
  ],
  "devices": [
    {
      "id": 1,
      "name": "客厅灯",
      "typeName": "智能灯泡",
      "onlineStatus": 1,
      "activeStatus": 1
    }
  ]
}
```

### 5. 获取用户所在家庭

**接口地址**: `GET /home/myHome`

**请求头**: 需要JWT Token

**响应示例**:
```json
{
  "home": [
    {
      "homeId": 1,
      "homeName": "我的家",
      "role": 0,
      "roleName": "房主"
    }
  ]
}
```

**状态码**:
- `200`: 查询成功

### 6. 更新家庭名称

**接口地址**: `POST /home/{homeId}/updateName`

**请求参数**:
```json
{
  "name": "新家庭名称"
}
```

**响应示例**:
```json
{
  "message": "更新成功"
}
```

**状态码**:
- `200`: 更新成功
- `400`: 家庭名称不能为空
- `404`: 家庭不存在
- `500`: 更新失败

### 7. 更新家庭地址

**接口地址**: `POST /home/{homeId}/updateAddress`

**请求参数**:
```json
{
  "address": "新家庭地址"
}
```

**响应示例**:
```json
{
  "message": "更新成功"
}
```

**状态码**:
- `200`: 更新成功
- `400`: 地址不能为空
- `404`: 家庭不存在
- `500`: 更新失败

### 8. 搜索家庭

**接口地址**: `GET /home/search?keyword={keyword}`

**请求参数**:
- `keyword` (query): 搜索关键词

**响应示例**:
```json
{
  "homes": [
    {
      "id": 1,
      "name": "我的家",
      "address": "北京市朝阳区xxx街道"
    }
  ]
}
```

**状态码**:
- `200`: 搜索成功
- `404`: 没有找到符合条件的家庭

## 房间管理接口

### 1. 创建房间

**接口地址**: `POST /home/{homeId}/room/create`

**请求参数**:
```json
{
  "homeId": 1,
  "name": "卧室"
}
```

**响应示例**:
```json
{
  "message": "创建成功"
}
```

### 2. 获取房间列表

**接口地址**: `GET /home/{homeId}/room/list`

**响应示例**:
```json
{
  "message": "查看成功",
  "Rooms": [
    {
      "id": 1,
      "homeId": 2,
      "name": "bedroom",
      "isDeleted": false
    },
    {
      "id": 3,
      "homeId": 2,
      "name": "bedroom",
      "isDeleted": false
    }
  ]
}
```

### 3. 删除房间

**接口地址**: `DELETE /home/{homeId}/room/delete`

**请求参数**:
```json
{
  "id": 1
}
```

**响应示例**:
```json
{
  "message": "删除成功"
}
```

**状态码**:
- `200`: 删除成功
- `404`: 家庭不存在该房间
- `500`: 删除失败

### 4. 获取房间设备

**接口地址**: `POST /home/{homeId}/room/device`

**请求参数**:
```json
{
  "id": 1
}
```

**响应示例**:
```json
{
  "devices": [
    {
      "id": 1,
      "name": "客厅灯",
      "ipAddress": "192.168.1.100",
      "homeId": 1,
      "roomId": 1,
      "typeId": 1,
      "onlineStatus": 1,
      "activeStatus": 1
    }
  ],
  "message": "查看成功"
}
```

**状态码**:
- `200`: 设备查看成功
- `404`: 房间或设备未找到

## 设备管理接口

### 1. 获取设备类型列表

**接口地址**: `GET /home/{homeId}/room/device/type/list`

**响应示例**:
```json
[
  {
    "id": 1,
    "name": "智能灯泡",
    "description": "可调节亮度和颜色的智能灯泡"
  },
  {
    "id": 2,
    "name": "智能插座",
    "description": "可远程控制的智能插座"
  }
]
```

### 2. 添加设备

**接口地址**: `POST /home/{homeId}/room/device/add`

**请求参数**:
```json
{
  "ipAddress": "192.168.1.100",
  "homeId": 1,
  "roomId": 1,
  "typeId": 1,
  "name": "客厅灯",
  "onlineStatus": 0,
  "activeStatus": 0
}
```

**响应示例**:
```json
{
  "status": "success",
  "message": "添加设备成功",
  "deviceId": 1
}
```

### 3. 获取设备列表

**接口地址**: `GET /home/{homeId}/room/device/list`

**响应示例**:
```json
{
  "devices": [
    {
      "id": 1,
      "name": "客厅灯",
      "ipAddress": "192.168.1.100",
      "homeId": 1,
      "roomId": 1,
      "typeId": 1,
      "onlineStatus": 1,
      "activeStatus": 1,
      "lastActiveTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 4. 更新设备

**接口地址**: `POST /home/{homeId}/room/device/update`

**请求参数**:
```json
{
  "id": 1,
  "name": "客厅主灯",
  "roomId": 1
}
```

**响应示例**:
```json
{
  "status": "success",
  "message": "更新成功"
}
```

**状态码**:
- `200`: 更新成功
- `400`: 更新失败

### 5. 删除设备

**接口地址**: `DELETE /home/{homeId}/room/device/delete?id={deviceId}`

**请求参数**:
- `id` (query): 设备ID

**响应示例**:
```json
{
  "status": "success",
  "message": "删除成功"
}
```

**状态码**:
- `200`: 删除成功
- `400`: 删除失败

### 6. 更新激活设备

**接口地址**: `POST /home/{homeId}/room/device/active`

**请求参数**:
- `id` (query): 设备ID

**请求示例**:
```bash
POST /home/1/room/device/active?id=1
```

**响应示例**:
```json
{
  "status": "success",
  "message": "更新时间成功"
}
```

### 7. 获取用户可访问设备

**接口地址**: `GET /home/{homeId}/room/device/accessibleDevices`

**请求头**: 需要JWT Token

**请求参数**:
- `homeId` (path): 家庭ID (在URL路径中)

**响应示例**:
```json
{
  "devices": [
    {
      "id": 1,
      "name": "客厅灯",
      "ipAddress": "192.168.1.100",
      "homeId": 1,
      "roomId": 1,
      "typeId": 1,
      "onlineStatus": 1,
      "activeStatus": 1,
      "lastActiveTime": "2024-01-01T10:00:00"
    }
  ],
  "userRole": "HOST",
  "isGuest": false
}
```

**状态码**:
- `200`: 查询成功
- `404`: 没有可访问的设备

**说明**: 
- 该接口会根据当前登录用户的角色返回不同的设备列表
- 用户ID自动从JWT Token中获取，无需手动传递
- 房主可以访问所有设备，家庭成员访问大部分设备，访客只能访问特定设备
- 响应中包含用户角色信息，便于前端进行权限控制



## 传感器数据接口

### 1. 获取设备最新传感器数据

**接口地址**: `GET /api/sensor/device/{deviceId}/latest`

**请求参数**:
- `deviceId` (path): 设备ID

**响应示例**:
```json
{
  "success": true,
  "deviceId": 1,
  "deviceName": "客厅主灯",
  "deviceType": 1,
  "roomId": 1,
  "homeId": 1,
  "lastActiveTime": "2025-09-04T16:56:14",
  "onlineStatus": 0,
  "activeStatus": 0,
  "sensorData": {
    "id": 23,
    "deviceId": 1,
    "dataTime": "2025-09-04T16:56:14",
    "topic": null,
    "dataValue": 30
  },
  "dataCount": 2
}
```

**状态码**:
- `200`: 获取成功
- `404`: 设备不存在
- `500`: 服务器内部错误

### 2. 获取实时传感器数据（轮询接口）

**接口地址**: `GET /api/sensor/device/{deviceId}/realtime`

**请求参数**:
- `deviceId` (path): 设备ID

**响应示例**:
```json
{
  "success": true,
  "timestamp": "2025-09-05T21:04:59.107394",
  "deviceId": 1,
  "deviceName": "客厅主灯",
  "onlineStatus": 0,
  "activeStatus": 0,
  "lastActiveTime": "2025-09-04T16:56:14",
  "sensorData": {
    "id": 23,
    "deviceId": 1,
    "dataTime": "2025-09-04T16:56:14",
    "topic": null,
    "dataValue": 30
  },
  "hasData": true
}
```

**状态码**:
- `200`: 获取成功
- `404`: 设备不存在
- `500`: 服务器内部错误

**说明**:
- 该接口适用于前端轮询获取实时数据
- 建议轮询间隔为5-10秒
- 返回的数据包含时间戳，便于前端判断数据新鲜度

### 3. 获取设备传感器数据历史记录

**接口地址**: `GET /api/sensor/device/{deviceId}/history`

**请求参数**:
- `deviceId` (path): 设备ID
- `limit` (query): 返回数据条数限制，默认10条

**响应示例**:
```json
{
  "success": true,
  "deviceId": 1,
  "deviceName": "客厅主灯",
  "historyData": [
    {
      "id": 22,
      "deviceId": 1,
      "dataTime": "2025-09-04T16:55:02",
      "topic": "security_sensors",
      "dataValue": 30
    },
    {
      "id": 23,
      "deviceId": 1,
      "dataTime": "2025-09-04T16:56:14",
      "topic": "security_sensors",
      "dataValue": 30
    }
  ],
  "dataCount": 2
}
```

**状态码**:
- `200`: 获取成功
- `404`: 设备不存在
- `500`: 服务器内部错误

### 4. 获取家庭所有设备传感器数据

**接口地址**: `GET /api/sensor/home/{homeId}/all`

**请求参数**:
- `homeId` (path): 家庭ID

**响应示例**:
```json
{
  "success": true,
  "homeId": 1,
  "deviceCount": 9,
  "devices": [
    {
      "id": 1,
      "name": "客厅主灯",
      "ipAddress": "0",
      "homeId": 1,
      "roomId": 1,
      "typeId": 1,
      "onlineStatus": 0,
      "activeStatus": 0,
      "lastActiveTime": "2025-09-04T16:56:14"
    }
  ]
}
```

**状态码**:
- `200`: 获取成功
- `500`: 服务器内部错误

**说明**:
- 返回家庭中所有设备的基本信息和最新活跃时间
- 适用于设备概览页面显示
- 数据已按设备最后活跃时间排序

## 设备交互接口

### 1. 设备操作

**接口地址**: `POST /home/{homeId}/device/{deviceId}/operation/{operationId}`

**请求参数**:
- `deviceId` (path): 设备ID
- `operationId` (path): 操作ID
- `homeId` (path): 家庭ID

**请求头**: 需要JWT Token

**响应示例**:
```json
{
  "message": "命令已发送"
}
```

**状态码**:
- `200`: 命令已发送
- `403`: 没有权限进行此操作
- `404`: 设备未在线

**说明**:
- 该接口会自动进行权限检查
- 访客用户只能对特定设备执行查看类操作
- 房主和家庭成员拥有更广泛的设备操作权限

### 2. 移动设备

**接口地址**: `POST /home/{homeId}/device/move`

**请求参数**:
```json
{
  "deviceId": 1,
  "roomId": 2
}
```

**响应示例**:
```json
{
  "message": "设备移动成功"
}
```

**状态码**:
- `200`: 设备移动成功
- `400`: 请求参数错误或运行时错误
- `500`: 设备移动失败或服务器错误

### 3. 连接设备

**接口地址**: `POST /home/{homeId}/device/connect`

**请求参数**: 无

**响应示例**:
```json
{
  "message": "设备连接成功"
}
```

**状态码**:
- `200`: 设备连接成功

### 4. 断开设备连接

**接口地址**: `POST /home/{homeId}/device/disconnect`

**请求参数**: 无

**响应示例**:
```json
{
  "message": "设备断开连接成功"
}
```

**状态码**:
- `200`: 设备断开连接成功

### 5. 获取设备数据

**接口地址**: `GET /home/{homeId}/device/{deviceId}/getData`

**请求参数**:
- `deviceId` (path): 设备ID

**响应示例**:
```json
{
  "data": [
    {
      "id": 1,
      "deviceId": 1,
      "data": "设备数据内容",
      "timestamp": "2024-01-01T10:00:00"
    }
  ]
}
```

**状态码**:
- `200`: 获取数据成功
- `404`: 没有任何数据信息



## 场景管理接口

### 1. 创建场景

**接口地址**: `POST /home/{homeId}/scene/add`

**请求参数**:
```json
{
  "name": "回家模式",
  "description": "回家后自动开启灯光和空调",
  "status": 1,
  "startTime": "2024-01-01T18:00:00",
  "endTime": "2024-01-01T22:00:00",
  "deviceOperation": [
    {
      "deviceId": 1,
      "deviceOperationId": 1,
      "parameters": {
        "brightness": 100
      }
    }
  ]
}
```

**响应示例**:
```json
{
  "message": "创建场景成功"
}
```

### 2. 获取场景设备视图

**接口地址**: `GET /home/{homeId}/scene/view/{sceneId}/device`

**响应示例**:
```json
{
  "devices": [
    {
      "deviceId": 1,
      "deviceName": "客厅灯",
      "deviceTypeName": "智能灯泡",
      "operationName": "开关",
      "operationDescription": "设备的开关操作"
    }
  ],
  "message": "查看成功"
}
```



### 4. 获取场景列表

**接口地址**: `GET /home/{homeId}/scene/view`

**响应示例**:
```json
{
  "scenes": [
    {
      "id": 1,
      "name": "回家模式",
      "description": "回家后自动开启灯光和空调",
      "status": 1,
      "startTime": "2024-01-01T18:00:00",
      "endTime": "2024-01-01T22:00:00"
    }
  ]
}
```

### 5. 删除场景

**接口地址**: `DELETE /home/{homeId}/scene/delete/{sceneId}`

**响应示例**:
```json
{
  "message": "删除成功"
}
```

**状态码**:
- `200`: 删除成功
- `404`: 场景不存在
- `500`: 删除场景失败

### 3. 启动场景

**接口地址**: `POST /home/{homeId}/scene/start`

**请求参数**:
```json
{
  "sceneId": 1
}
```

**响应示例**:
```json
{
  "message": "场景已开启",
  "设备开启情况": {
    "1": "已执行",
    "2": "未在线"
  }
}
```

**状态码**:
- `200`: 启动成功
- `404`: 场景不存在
- `500`: 启动失败

### 4. 停止场景

**接口地址**: `POST /home/{homeId}/scene/stop`

**请求参数**:
```json
{
  "sceneId": 1
}
```

**响应示例**:
```json
{
  "message": "场景已关闭"
}
```

**状态码**:
- `200`: 停止成功
- `404`: 场景不存在
- `500`: 停止失败

### 5. 更新场景

**接口地址**: `POST /home/{homeId}/scene/update/{sceneId}`

**请求参数**:
```json
{
  "name": "回家模式",
  "description": "回家后自动开启灯光和空调",
  "status": 1,
  "startTime": "2024-01-01T18:00:00",
  "endTime": "2024-01-01T22:00:00",
  "deviceOperation": [
    {
      "deviceId": 1,
      "deviceOperationId": 1,
      "parameters": {
        "brightness": 100
      }
    }
  ]
}
```

**响应示例**:
```json
{
  "message": "更新场景成功"
}
```

**状态码**:
- `201`: 修改成功
- `404`: 场景不存在
- `500`: 修改失败

## 成员管理接口

### 1. 添加家庭成员

**接口地址**: `POST /home/member/add`

**请求参数**:
```json
{
  "userId": 1,
  "homeId": 1,
  "role": 1
}
```

**响应示例**:
```json
{
  "message": "添加成功"
}
```

**说明**: 家庭成员信息通过查看家庭详情接口获取，该接口返回家庭的所有成员信息。

## 权限管理接口

### 1. 添加用户权限

**接口地址**: `POST /permission/{homeId}/add`

**请求头**: 需要JWT Token

**请求参数**:
```json
{
  "id": 1,
  "userId": 1,
  "homeId": 1,
  "deviceId": 1,
  "operationId": 1,
  "hasPermission": true,
  "endTime": "2024-12-31T23:59:59"
}
```

**响应示例**:
```json
{
  "message": "添加权限成功"
}
```

**状态码**:
- `201`: 添加权限成功
- `409`: 该用户已拥有此权限
- `500`: 添加权限失败

**说明**:
- 只有房主可以添加用户权限
- 权限ID必须唯一
- 可以设置权限的有效期

### 2. 取消用户权限

**接口地址**: `DELETE /permission/cancel`

**请求头**: 需要JWT Token

**请求参数**:
```json
{
  "id": 1
}
```

**响应示例**:
```json
{
  "message": "取消权限成功"
}
```

**状态码**:
- `200`: 取消权限成功
- `409`: 该用户未拥有此权限
- `500`: 取消权限失败

**说明**:
- 只有房主可以取消用户权限
- 通过权限ID来取消特定权限

## 访客权限管理接口

### 1. 获取访客可访问设备列表

**接口地址**: `GET /guest/{userId}/home/{homeId}/accessible-devices`

**请求参数**:
- `userId` (path): 用户ID
- `homeId` (path): 家庭ID

**响应示例**:
```json
{
  "devices": [
    {
      "id": 5,
      "name": "客厅窗户",
      "ipAddress": "192.168.1.105",
      "homeId": 1,
      "roomId": 1,
      "typeId": 5,
      "onlineStatus": 1,
      "activeStatus": 1,
      "lastActiveTime": "2024-01-01T10:00:00"
    },
    {
      "id": 6,
      "name": "门厅监控",
      "ipAddress": "192.168.1.106",
      "homeId": 1,
      "roomId": 2,
      "typeId": 6,
      "onlineStatus": 1,
      "activeStatus": 1,
      "lastActiveTime": "2024-01-01T10:00:00"
    }
  ],
  "userRole": "GUEST",
  "accessibleDeviceTypes": [5, 6],
  "message": "访客可访问设备列表获取成功"
}
```

**状态码**:
- `200`: 查询成功
- `403`: 用户不是访客
- `404`: 没有可访问的设备

**说明**:
- 该接口专门为访客用户设计
- 访客只能访问特定类型的设备（如窗户、监控摄像头等）
- 返回的设备列表已根据访客权限进行过滤
- 需要验证用户确实为访客身份

### 2. 检查访客设备操作权限

**接口地址**: `GET /guest/{userId}/home/{homeId}/device/{deviceId}/operation/{operationId}/check`

**请求参数**:
- `userId` (path): 用户ID
- `homeId` (path): 家庭ID
- `deviceId` (path): 设备ID
- `operationId` (path): 操作ID

**响应示例**:
```json
{
  "hasPermission": false,
  "message": "访客没有权限执行此操作"
}
```

**状态码**:
- `200`: 权限检查完成
- `403`: 用户不是访客

**说明**:
- 用于检查访客用户是否可以对指定设备执行指定操作
- 访客通常只能执行查看类操作（操作ID 1, 2）
- 不能执行控制类操作（操作ID > 2）
- 会验证用户是否为访客身份

### 3. 获取访客权限说明

**接口地址**: `GET /guest/permission-info`

**请求参数**: 无

**响应示例**:
```json
{
  "role": "GUEST",
  "description": "访客用户",
  "restrictions": [
    "只能访问特定类型的设备（如传感器、监控摄像头等）",
    "只能执行查看类操作，不能进行控制操作",
    "不能添加、删除或修改设备",
    "不能访问敏感设备（如门锁、保险柜等）"
  ],
  "accessibleDeviceTypes": [5, 6],
  "allowedOperations": [1, 2]
}
```

**状态码**:
- `200`: 获取成功

**说明**:
- 提供访客用户的权限说明和限制信息
- 帮助前端了解访客的权限范围
- 可用于权限提示和用户引导
- 无需认证，可直接访问

## 权限系统说明

### 权限层次结构

系统采用三层权限控制机制：

1. **角色默认权限**: 基于用户角色（房主/家庭成员/访客）的默认权限
2. **用户自定义权限**: 针对特定用户的个性化权限设置
3. **访客特殊限制**: 对访客用户的额外权限限制

### 用户角色权限对比

| 操作类型 | 房主(HOST) | 家庭成员(MEMBER) | 访客(GUEST) |
|---------|-----------|------------------|-------------|
| 创建/删除房间 | ✅ | ❌ | ❌ |
| 添加/删除设备 | ✅ | ✅ | ❌ |
| 设备控制操作 | ✅ | ✅ | 仅查看类操作 |
| 场景管理 | ✅ | ✅ | ❌ |
| 权限管理 | ✅ | ❌ | ❌ |
| 访客申请处理 | ✅ | ❌ | ❌ |

### 权限接口使用建议

1. **普通用户**: 使用 `/home/{homeId}/room/device/accessibleDevices` 获取可访问设备
2. **访客用户**: 使用 `/guest/{userId}/home/{homeId}/accessible-devices` 获取访客专用设备列表
3. **权限管理**: 房主可使用 `/permission/add` 和 `/permission/cancel` 管理用户权限
4. **权限检查**: 使用 `/guest/{userId}/home/{homeId}/device/{deviceId}/operation/{operationId}/check` 检查访客操作权限

## 访客管理接口

### 1. 发起加入家庭申请

**接口地址**: `POST /home/{homeId}/request/put`

**请求参数**: 无

**响应示例**:
```json
{
  "message": "申请成功"
}
```

**状态码**:
- `200`: 申请成功/已发送过申请
- `404`: 该家庭不存在
- `409`: 已加入该家庭
- `500`: 申请失败

### 2. 获取进入请求列表

**接口地址**: `GET /home/{homeId}/request/receive`

**响应示例**:
```json
{
  "requests": [
    {
      "id": 1,
      "userId": 3,
      "username": "访客",
      "status": 0,
      "statusName": "等待处理",
      "recordTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**状态码**:
- `200`: 获取成功
- `404`: 该家庭不存在/没有加入家庭的申请

### 3. 处理进入请求

**接口地址**: `POST /home/{homeId}/request/receive/handle`

**请求参数**:
```json
{
  "requestId": 1,
  "userId": 3,
  "status": 1
}
```

**响应示例**:
```json
{
  "message": "处理成功"
}
```

**状态码**:
- `200`: 处理成功
- `404`: 申请不存在或已取消
- `500`: 处理失败

## 聊天接口

**说明**: 系统提供AI聊天功能，通过AI聊天接口与用户进行智能对话。

## AI 聊天接口

### 1. AI 聊天流式接口

**接口地址**: `POST /home/{homeId}/ai/chat`

**请求参数**:
```json
{
  "input": "你好，智能家居"
}
```

**请求头**: 需要JWT Token

**响应格式**: Server-Sent Events (SSE)

**响应示例**:
```
data: 你好！我是你的智能家居AI助手
data: 我可以帮你控制设备、设置场景等
data: 有什么可以帮助你的吗？
```

**状态码**:
- `200`: 聊天响应流式返回成功
- `500`: 服务异常

**说明**:
- 使用火山引擎 Ark API
- 支持流式响应，实时返回 AI 回复
- 超时时间设置为 5 分钟
- 输入长度限制为 2000 字符
- 需要用户认证

## AI 指导服务接口

### 1. 流式聊天指导

**接口地址**: `GET /guidance/stream`

**请求参数**:
- `message` (query): 用户输入的消息内容

**请求头**: 无需认证

**响应格式**: Server-Sent Events (SSE)

**响应示例**:
```
data: 你好！我是你的AI助手，有什么可以帮助你的吗？
data: 我可以帮你解答关于智能家居的问题
data: 或者提供其他方面的帮助
```

**状态码**:
- `200`: 连接成功，开始流式响应
- `500`: 服务器内部错误

**说明**:
- 使用阿里云百炼 API (qwen-max 模型)
- 支持流式响应，实时返回 AI 回复
- 超时时间设置为 60 秒
- 无需用户认证，可直接访问

## 错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token无效） |
| 403 | 权限不足（如访客尝试控制设备、用户角色权限不足等） |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

### 403权限不足详细说明

- **设备操作权限不足**: 用户尝试操作没有权限的设备
- **访客权限限制**: 访客用户尝试执行超出权限范围的操作
- **角色权限不足**: 用户角色不具备执行该操作的权限
- **自定义权限限制**: 用户被明确禁止执行某项操作

## 认证说明

### JWT Token 使用方式

在需要认证的接口中，请在请求头中添加：

```
Authorization: Bearer {token}
```

### Token 过期处理

当Token过期时，服务器会返回401状态码，前端需要：

1. 清除本地存储的Token
2. 跳转到登录页面
3. 重新获取Token

## 注意事项

1. **时间格式**: 所有时间字段使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`
2. **分页**: 列表接口暂不支持分页，返回所有数据
3. **文件上传**: 当前版本不支持文件上传功能
4. **WebSocket**: 实时通知功能通过WebSocket实现，端口为8081
5. **MQTT**: 设备通信使用MQTT协议，支持设备状态实时同步
6. **访客权限**: 访客用户只能访问特定类型的设备（如窗户、监控摄像头等），且只能执行查看类操作，不能进行设备控制
7. **权限检查**: 所有设备操作接口都会自动进行权限验证，无权限时会返回403错误

## 更新日志

- **v1.0.0** (2024-01-01): 初始版本，包含基础功能接口
- **v1.1.0** (2024-01-15): 新增场景管理和权限控制接口
- **v1.2.0** (2024-02-01): 新增访客管理和聊天功能接口
- **v1.3.0** (2024-12-19): 新增AI指导服务接口，完善设备交互接口
- **v1.4.0** (2024-12-19): 重大更新，全面修正API接口路径和参数，新增多个缺失接口，包括AI聊天、场景管理、权限管理等
- **v1.4.1** (2024-12-19): 修正场景管理接口编号，删除重复和不存在的接口，完善接口参数和状态码说明
- **v1.5.0** (2024-12-19): 新增访客权限管理系统，实现基于角色的设备访问权限控制，访客只能访问部分设备且只能进行查看操作
- **v1.5.1** (2024-12-19): 修正权限管理接口文档，更新接口路径和参数说明，新增权限系统详细说明
- **v1.6.0** (2025-09-05): 新增传感器数据接口，支持JSON格式硬件消息解析，提供设备传感器数据获取、实时数据轮询、历史数据查询等功能

---

如有疑问，请联系后端开发团队。


