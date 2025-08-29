# 智能家居管理系统 API 文档

## 概述

本文档描述了智能家居管理系统的所有API接口，用于前后端对接开发。

- **基础URL**: `http://localhost:8081`
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
  "rooms": [
    {
      "id": 1,
      "name": "客厅",
      "homeId": 1
    }
  ]
}
```

### 3. 删除房间

**接口地址**: `DELETE /home/{homeId}/room/delete/{roomId}`

**响应示例**:
```json
{
  "message": "删除成功"
}
```

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

### 5. 删除设备

**接口地址**: `DELETE /home/{homeId}/room/device/delete?id={deviceId}`

**响应示例**:
```json
{
  "status": "success",
  "message": "删除成功"
}
```

### 6. 移动设备

**接口地址**: `POST /home/{homeId}/room/device/move`

**请求参数**:
```json
{
  "deviceId": 1,
  "newRoomId": 2
}
```

**响应示例**:
```json
{
  "message": "移动成功"
}
```

## 设备交互接口

### 1. 设备操作

**接口地址**: `POST /home/{homeId}/device/operation`

**请求参数**:
```json
{
  "deviceId": 1,
  "operationId": 1,
  "parameters": {
    "brightness": 80,
    "color": "#FF0000"
  }
}
```

**响应示例**:
```json
{
  "message": "操作成功"
}
```

### 2. 获取设备操作列表

**接口地址**: `GET /home/{homeId}/device/{deviceId}/operations`

**响应示例**:
```json
{
  "operations": [
    {
      "id": 1,
      "name": "开关",
      "description": "设备的开关操作"
    },
    {
      "id": 2,
      "name": "调节亮度",
      "description": "调节设备亮度"
    }
  ]
}
```

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
  "message": "场景启动成功"
}
```

### 4. 获取场景列表

**接口地址**: `GET /home/{homeId}/scene/list`

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

## 成员管理接口

### 1. 添加家庭成员

**接口地址**: `POST /home/{homeId}/member/add`

**请求参数**:
```json
{
  "phone": "13800138001",
  "role": 1
}
```

**响应示例**:
```json
{
  "message": "添加成功"
}
```

### 2. 获取家庭成员列表

**接口地址**: `GET /home/{homeId}/member/list`

**响应示例**:
```json
{
  "members": [
    {
      "userId": 1,
      "username": "张三",
      "phone": "13800138000",
      "role": 0,
      "roleName": "房主"
    },
    {
      "userId": 2,
      "username": "李四",
      "phone": "13800138001",
      "role": 1,
      "roleName": "家庭成员"
    }
  ]
}
```

### 3. 移除家庭成员

**接口地址**: `DELETE /home/{homeId}/member/remove/{userId}`

**响应示例**:
```json
{
  "message": "移除成功"
}
```

### 4. 更新成员角色

**接口地址**: `POST /home/{homeId}/member/role/update`

**请求参数**:
```json
{
  "userId": 2,
  "newRole": 2
}
```

**响应示例**:
```json
{
  "message": "角色更新成功"
}
```

## 权限管理接口

### 1. 获取用户权限

**接口地址**: `GET /home/{homeId}/permission/user/{userId}`

**响应示例**:
```json
{
  "permissions": [
    {
      "deviceId": 1,
      "deviceName": "客厅灯",
      "operationId": 1,
      "operationName": "开关",
      "hasPermission": true
    }
  ]
}
```

### 2. 设置用户权限

**接口地址**: `POST /home/{homeId}/permission/set`

**请求参数**:
```json
{
  "userId": 2,
  "deviceId": 1,
  "operationId": 1,
  "hasPermission": true,
  "endTime": "2024-12-31T23:59:59"
}
```

**响应示例**:
```json
{
  "message": "权限设置成功"
}
```

## 访客管理接口

### 1. 获取访客记录

**接口地址**: `GET /home/{homeId}/guest/records`

**响应示例**:
```json
{
  "records": [
    {
      "id": 1,
      "userId": 3,
      "username": "访客",
      "recordType": 0,
      "recordTypeName": "进入",
      "recordTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 获取进入请求列表

**接口地址**: `GET /home/{homeId}/enter-request/list`

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

### 3. 处理进入请求

**接口地址**: `POST /home/{homeId}/enter-request/handle`

**请求参数**:
```json
{
  "requestId": 1,
  "action": "approve"
}
```

**响应示例**:
```json
{
  "message": "请求已通过"
}
```

## 聊天接口

### 1. 发送聊天消息

**接口地址**: `POST /home/{homeId}/chat`

**请求参数**:
```json
{
  "message": "你好，智能家居"
}
```

**响应示例**:
```json
{
  "response": "你好！我是你的智能家居助手，有什么可以帮助你的吗？"
}
```

## 错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token无效） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

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

## 更新日志

- **v1.0.0** (2024-01-01): 初始版本，包含基础功能接口
- **v1.1.0** (2024-01-15): 新增场景管理和权限控制接口
- **v1.2.0** (2024-02-01): 新增访客管理和聊天功能接口

---

如有疑问，请联系后端开发团队。


