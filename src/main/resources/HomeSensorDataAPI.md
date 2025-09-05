# 家庭传感器数据API接口文档

## 概述
本文档描述了家庭传感器数据相关的API接口，用于获取和管理家庭中所有传感器设备的数据信息。

## 基础信息
- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Token (在请求头中添加 `Authorization: Bearer <token>`)
- **数据格式**: JSON
- **字符编码**: UTF-8

## API接口列表

### 1. 获取家庭所有传感器数据

**接口地址**: `GET /api/home/{homeId}/sensor/data`

**接口描述**: 获取指定家庭中所有传感器设备的当前数据和历史数据

**请求参数**:
- `homeId` (路径参数): 家庭ID，必填
- `limit` (查询参数): 每个设备返回的历史数据条数，可选，默认10条

**请求示例**:
```
GET /api/home/1/sensor/data?limit=5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**:
```json
{
  "homeId": 1,
  "homeName": "我的家",
  "dataTime": "2024-01-15T10:30:00",
  "sensorDevices": [
    {
      "deviceId": 101,
      "deviceName": "客厅温度传感器",
      "deviceType": "温度传感器",
      "roomId": 1,
      "roomName": "客厅",
      "onlineStatus": 1,
      "lastActiveTime": "2024-01-15T10:25:00",
      "latestData": {
        "id": 1001,
        "value": "25.6",
        "topic": "home/1/room/1/temperature",
        "recordTime": "2024-01-15T10:25:00",
        "statusDescription": "温度正常"
      },
      "historyData": [
        {
          "id": 1000,
          "value": "25.2",
          "topic": "home/1/room/1/temperature",
          "recordTime": "2024-01-15T10:20:00",
          "statusDescription": "温度正常"
        }
      ]
    }
  ]
}
```

### 2. 获取指定设备传感器历史数据

**接口地址**: `GET /api/home/{homeId}/sensor/device/{deviceId}/history`

**接口描述**: 获取指定传感器设备的历史数据记录

**请求参数**:
- `homeId` (路径参数): 家庭ID，必填
- `deviceId` (路径参数): 设备ID，必填
- `limit` (查询参数): 返回的历史数据条数，可选，默认50条

**请求示例**:
```
GET /api/home/1/sensor/device/101/history?limit=20
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**:
```json
{
  "deviceId": 101,
  "deviceName": "客厅温度传感器",
  "deviceType": "温度传感器",
  "historyData": [
    {
      "id": 1001,
      "value": "25.6",
      "topic": "home/1/room/1/temperature",
      "recordTime": "2024-01-15T10:25:00",
      "statusDescription": "温度正常"
    }
  ]
}
```

### 3. 获取家庭传感器数据概览

**接口地址**: `GET /api/home/{homeId}/sensor/overview`

**接口描述**: 获取家庭传感器设备的概览信息，包括设备数量、在线状态等统计信息

**请求参数**:
- `homeId` (路径参数): 家庭ID，必填

**请求示例**:
```
GET /api/home/1/sensor/overview
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**:
```json
{
  "homeId": 1,
  "homeName": "我的家",
  "totalSensorDevices": 5,
  "onlineDevices": 4,
  "offlineDevices": 1,
  "lastUpdateTime": "2024-01-15T10:30:00",
  "deviceSummary": [
    {
      "deviceType": "温度传感器",
      "count": 2,
      "onlineCount": 2
    },
    {
      "deviceType": "湿度传感器",
      "count": 1,
      "onlineCount": 1
    },
    {
      "deviceType": "烟雾传感器",
      "count": 2,
      "onlineCount": 1
    }
  ]
}
```

## 响应状态码

- `200 OK`: 请求成功
- `400 Bad Request`: 请求参数错误
- `401 Unauthorized`: 未授权，需要登录
- `403 Forbidden`: 权限不足
- `404 Not Found`: 资源不存在
- `500 Internal Server Error`: 服务器内部错误

## 错误响应格式

```json
{
  "error": "错误类型",
  "message": "错误描述信息",
  "timestamp": "2024-01-15T10:30:00"
}
```

## 数据结构说明

### HomeAllSensorDataResponse
家庭传感器数据响应对象，包含家庭基本信息和所有传感器设备数据。

### SensorDeviceData
传感器设备数据对象，包含设备基本信息、在线状态、最新数据和历史数据。

### SensorData
传感器数据对象，包含数据ID、数值、主题、记录时间和状态描述。

## 注意事项

1. 所有时间字段均使用ISO 8601格式 (yyyy-MM-ddTHH:mm:ss)
2. 传感器设备类型通过设备类型名称中的关键词识别（如：传感器、检测、监测等）
3. 状态描述根据设备类型和数据值自动生成
4. 历史数据按时间倒序排列（最新的在前）
5. 建议合理设置limit参数以避免数据量过大影响性能