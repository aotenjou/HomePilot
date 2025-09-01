# 安全警报系统使用说明

## 功能概述

本系统实现了通过火焰感应和可燃气体传感器实时监控安全状态的功能，当检测到异常时会自动发送警报并执行相应的安全措施。

## 主要功能

### 1. 火焰传感器监控
- 实时检测火焰传感器数据
- 当检测到火焰时自动触发警报
- 自动关闭可能引起火灾的设备（灯光、电器等）

### 2. 可燃气体传感器监控
- 实时检测可燃气体浓度
- 当浓度超过阈值（50）时触发警报
- 自动关闭燃气设备（炉具、热水器等）

### 3. 实时警报推送
- 通过WebSocket实时推送到前端
- 通过MQTT发送警报消息
- 控制台输出警报信息

## API接口

### 安全状态查询
```
GET /api/security/status/{homeId}
```

### 测试接口
```
POST /api/security/test/fire/{homeId}/{roomId}
POST /api/security/test/gas/{homeId}/{roomId}
```

### 演示接口
```
POST /api/demo/simulate/fire?homeId={homeId}&roomId={roomId}&value={value}
POST /api/demo/simulate/gas?homeId={homeId}&roomId={roomId}&value={value}
GET /api/demo/security/status/{homeId}
```

## WebSocket连接

前端可以通过以下方式连接WebSocket接收实时警报：

```javascript
// 连接WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected to WebSocket');
    
    // 订阅安全警报主题
    stompClient.subscribe('/topic/security/' + homeId, function (message) {
        const alert = JSON.parse(message.body);
        console.log('收到安全警报:', alert);
        // 处理警报信息
        showAlert(alert);
    });
});

function showAlert(alert) {
    if (alert.type === 'fire') {
        alert('🔥 火焰警报！' + alert.message);
    } else if (alert.type === 'gas') {
        alert('⚠️ 可燃气体警报！' + alert.message);
    }
}
```

## 硬件集成

### MQTT消息格式
硬件设备需要按照以下格式发送MQTT消息：

```
主题: security_sensors
消息格式: {设备ID}:{传感器值}
示例: 1001:1 (火焰传感器检测到火焰)
示例: 1002:100 (可燃气体传感器检测到高浓度)
```

### 设备类型识别
系统通过设备类型名称识别传感器：
- 火焰传感器：包含"火焰"或"火警"
- 可燃气体传感器：包含"可燃气体"、"燃气"或"气体"

## 自动安全措施

### 火焰警报时
- 自动关闭房间内的灯光
- 自动关闭电器设备
- 自动关闭插座

### 可燃气体警报时
- 自动关闭燃气设备
- 自动关闭炉具
- 自动关闭热水器

## 配置说明

### MQTT配置
在`application.yml`中配置MQTT连接信息：
```yaml
spring:
  mqtt:
    url: tcp://localhost:1883
    username: your_username
    password: your_password
    client:
      consumerid: security_consumer
    default:
      topic: security_sensors
```

### 阈值配置
可以在`SecurityAlertServiceImpl`中修改警报阈值：
- 火焰传感器：`value > 0`
- 可燃气体传感器：`value > 50`

## 使用示例

1. 启动应用后，系统会自动订阅`security_sensors`主题
2. 硬件设备发送传感器数据到MQTT主题
3. 系统自动处理传感器数据并触发相应警报
4. 前端通过WebSocket接收实时警报信息
5. 系统自动执行安全措施（关闭相关设备）

## 注意事项

1. 确保MQTT服务器正常运行
2. 确保设备类型名称正确配置
3. 确保房间和设备信息在数据库中正确配置
4. 建议在生产环境中添加更多的安全验证和日志记录
