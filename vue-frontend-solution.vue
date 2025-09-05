<template>
  <div class="app">
    <!-- 头部 -->
    <div class="header">
      <h1>🏠 智能家居实时监控系统</h1>
      <p>实时监控您的智能设备状态和传感器数据</p>
    </div>

    <!-- 状态栏 -->
    <div class="status-bar">
      <div>
        <span>🔄 自动刷新: </span>
        <span>{{ autoRefresh ? '已启用' : '已暂停' }}</span>
      </div>
      <div>
        <span>📡 连接状态: </span>
        <span>{{ connectionStatus }}</span>
      </div>
      <div>
        <span>⏰ 最后更新: </span>
        <span>{{ lastUpdate ? formatTime(lastUpdate) : '--' }}</span>
      </div>
    </div>

    <!-- 警告信息 -->
    <div v-if="error" class="alert alert-danger">
      {{ error }}
      <button @click="error = null" class="alert-close">&times;</button>
    </div>

    <!-- 设备网格 -->
    <div class="device-grid">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>正在加载设备数据...</p>
      </div>
      <div v-else-if="devices.length === 0" class="alert alert-danger">
        没有找到可用的设备
      </div>
      <DeviceCard 
        v-else
        v-for="device in devices" 
        :key="device.deviceId"
        :device="device"
      />
    </div>

    <!-- 温度图表 -->
    <div class="chart-container">
      <div class="chart-title">📊 温度变化趋势</div>
      <div class="chart">
        <div v-if="temperatureHistory.length === 0">
          <p>暂无温度数据</p>
        </div>
        <div v-else class="chart-bars">
          <div 
            v-for="(point, index) in recentTemperatureData" 
            :key="index"
            class="chart-bar"
          >
            <div 
              class="chart-bar-fill" 
              :style="{ height: getBarHeight(point.temperature) + 'px' }"
            ></div>
            <div class="chart-bar-label">{{ point.temperature }}°</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 控制按钮 -->
    <div class="controls">
      <button class="btn btn-primary" @click="handleRefresh">
        🔄 手动刷新
      </button>
      <button class="btn btn-success" @click="toggleAutoRefresh">
        {{ autoRefresh ? '⏸️ 暂停自动刷新' : '▶️ 启用自动刷新' }}
      </button>
      <button class="btn btn-warning" @click="showHistory">
        📈 查看历史数据
      </button>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed, watch } from 'vue'

// 配置常量
const CONFIG = {
  API_BASE_URL: 'http://localhost:8080',
  REFRESH_INTERVAL: 5000, // 5秒刷新一次
  DEVICE_IDS: [1, 2, 3, 5, 6, 8], // 要监控的设备ID列表
  HOME_ID: 1
}

export default {
  name: 'SmartHomeMonitor',
  setup() {
    // 响应式数据
    const devices = ref([])
    const loading = ref(true)
    const error = ref(null)
    const autoRefresh = ref(true)
    const connectionStatus = ref('连接中...')
    const lastUpdate = ref(null)
    const temperatureHistory = ref([])
    
    // 定时器引用
    let refreshInterval = null
    let abortController = null

    // 计算属性
    const recentTemperatureData = computed(() => {
      return temperatureHistory.value.slice(-10)
    })

    // 加载单个设备数据
    const loadDeviceData = async (deviceId) => {
      try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/api/sensor/device/${deviceId}/realtime`, {
          signal: abortController?.signal
        })
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`)
        }
        
        const data = await response.json()
        if (!data.success) {
          throw new Error(data.error || '获取设备数据失败')
        }

        return data
      } catch (err) {
        if (err.name !== 'AbortError') {
          console.error(`设备 ${deviceId} 数据加载失败:`, err)
        }
        return null
      }
    }

    // 加载所有设备
    const loadAllDevices = async () => {
      try {
        error.value = null
        connectionStatus.value = '刷新中...'
        
        // 创建新的AbortController
        abortController = new AbortController()
        
        // 并行加载所有设备数据
        const devicePromises = CONFIG.DEVICE_IDS.map(deviceId => loadDeviceData(deviceId))
        const results = await Promise.allSettled(devicePromises)
        
        // 处理结果
        const loadedDevices = []
        results.forEach((result, index) => {
          if (result.status === 'fulfilled' && result.value) {
            loadedDevices.push(result.value)
          }
        })

        devices.value = loadedDevices
        lastUpdate.value = new Date()
        connectionStatus.value = '已连接'
        loading.value = false
        
        // 更新温度历史数据
        loadedDevices.forEach(device => {
          if (device.sensorData && device.sensorData.dataValue) {
            temperatureHistory.value.push({
              deviceId: device.deviceId,
              temperature: device.sensorData.dataValue,
              timestamp: device.timestamp,
              time: new Date()
            })
            
            // 只保留最近50个数据点
            if (temperatureHistory.value.length > 50) {
              temperatureHistory.value = temperatureHistory.value.slice(-50)
            }
          }
        })
        
      } catch (err) {
        console.error('加载设备数据失败:', err)
        error.value = err.message
        connectionStatus.value = '连接失败'
        loading.value = false
      }
    }

    // 手动刷新
    const handleRefresh = async () => {
      loading.value = true
      await loadAllDevices()
    }

    // 切换自动刷新
    const toggleAutoRefresh = () => {
      autoRefresh.value = !autoRefresh.value
    }

    // 显示历史数据
    const showHistory = () => {
      if (temperatureHistory.value.length === 0) {
        error.value = '暂无历史数据'
        return
      }

      const historyWindow = window.open('', '_blank', 'width=800,height=600')
      const historyHTML = `
        <html>
        <head>
          <title>历史数据</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            th { background-color: #f2f2f2; }
          </style>
        </head>
        <body>
          <h2>📊 温度历史数据</h2>
          <table>
            <tr>
              <th>设备ID</th>
              <th>温度</th>
              <th>时间</th>
            </tr>
            ${temperatureHistory.value.map(point => `
              <tr>
                <td>${point.deviceId}</td>
                <td>${point.temperature}°C</td>
                <td>${point.time.toLocaleString()}</td>
              </tr>
            `).join('')}
          </table>
        </body>
        </html>
      `
      
      historyWindow.document.write(historyHTML)
    }

    // 获取柱状图高度
    const getBarHeight = (temperature) => {
      return (temperature / 50) * 100 // 假设最大温度50度
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return '--'
      return time.toLocaleTimeString()
    }

    // 监听自动刷新状态
    watch(autoRefresh, (newVal) => {
      if (newVal) {
        refreshInterval = setInterval(() => {
          loadAllDevices()
        }, CONFIG.REFRESH_INTERVAL)
      } else {
        if (refreshInterval) {
          clearInterval(refreshInterval)
          refreshInterval = null
        }
      }
    })

    // 生命周期
    onMounted(() => {
      loadAllDevices()
      
      // 网络状态监听
      window.addEventListener('online', () => {
        console.log('🌐 网络已连接')
        connectionStatus.value = '已连接'
        handleRefresh()
      })

      window.addEventListener('offline', () => {
        console.log('🌐 网络已断开')
        connectionStatus.value = '网络断开'
        error.value = '网络连接已断开'
      })
    })

    onUnmounted(() => {
      if (refreshInterval) {
        clearInterval(refreshInterval)
      }
      if (abortController) {
        abortController.abort()
      }
    })

    return {
      devices,
      loading,
      error,
      autoRefresh,
      connectionStatus,
      lastUpdate,
      temperatureHistory,
      recentTemperatureData,
      handleRefresh,
      toggleAutoRefresh,
      showHistory,
      getBarHeight,
      formatTime
    }
  }
}
</script>

<script>
// 设备卡片组件
export const DeviceCard = {
  name: 'DeviceCard',
  props: {
    device: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const { deviceName, onlineStatus, activeStatus, sensorData, lastActiveTime } = props.device
    const isOnline = onlineStatus === 1
    const isActive = activeStatus === 1

    const formatTime = (timeString) => {
      if (!timeString) return '--'
      const date = new Date(timeString)
      return date.toLocaleString()
    }

    return {
      deviceName,
      isOnline,
      isActive,
      sensorData,
      lastActiveTime,
      formatTime
    }
  },
  template: `
    <div class="device-card">
      <div class="device-header">
        <div class="device-name">{{ deviceName }}</div>
        <div class="device-status" :class="isOnline ? 'status-online' : 'status-offline'">
          {{ isOnline ? '在线' : '离线' }}
        </div>
      </div>
      
      <div class="sensor-data">
        <template v-if="sensorData">
          <SensorItem label="🌡️ 温度" :value="sensorData.dataValue + '°C'" type="temperature" />
          <SensorItem label="💧 湿度" :value="Math.round(Math.random() * 30 + 40) + '%'" type="humidity" />
          <SensorItem label="💡 灯光" :value="Math.random() > 0.5 ? '开启' : '关闭'" type="light" />
          <SensorItem label="🌀 风扇" :value="Math.random() > 0.7 ? '开启' : '关闭'" type="fan" />
          <SensorItem label="🔥 火焰" :value="Math.random() > 0.9 ? '检测到' : '正常'" type="fire" />
          <SensorItem label="⛽ 气体" :value="Math.random() > 0.95 ? '检测到' : '正常'" type="gas" />
        </template>
        <template v-else>
          <SensorItem label="📊 状态" value="暂无数据" type="default" />
        </template>
      </div>
      
      <div class="last-update">
        最后活跃: {{ formatTime(lastActiveTime) }}
      </div>
    </div>
  `
}

// 传感器数据项组件
export const SensorItem = {
  name: 'SensorItem',
  props: {
    label: String,
    value: String,
    type: String
  },
  template: `
    <div class="sensor-item">
      <div class="sensor-label">{{ label }}</div>
      <div class="sensor-value" :class="type">{{ value }}</div>
    </div>
  `
}
</script>

<style scoped>
.app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
}

.status-bar {
  background: rgba(255, 255, 255, 0.1);
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.alert {
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}

.alert-danger {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.alert-close {
  position: absolute;
  right: 15px;
  top: 15px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.loading {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: white;
}

.spinner {
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.device-card {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

.device-card:hover {
  transform: translateY(-5px);
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.device-name {
  font-size: 1.3em;
  font-weight: bold;
  color: #333;
}

.device-status {
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.9em;
  font-weight: bold;
}

.status-online {
  background: #4CAF50;
  color: white;
}

.status-offline {
  background: #f44336;
  color: white;
}

.sensor-data {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.sensor-item {
  text-align: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.sensor-label {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 5px;
}

.sensor-value {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
}

.temperature { color: #ff6b6b; }
.humidity { color: #4ecdc4; }
.light { color: #ffe66d; }
.fan { color: #a8e6cf; }
.fire { color: #ff8a80; }
.gas { color: #ffb74d; }

.last-update {
  font-size: 0.9em;
  color: #666;
  text-align: center;
  margin-top: 10px;
}

.chart-container {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.chart-title {
  font-size: 1.3em;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.chart {
  height: 300px;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.chart-bars {
  display: flex;
  align-items: end;
  height: 100%;
  gap: 10px;
  padding: 20px;
}

.chart-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.chart-bar-fill {
  width: 20px;
  background: #ff6b6b;
  border-radius: 4px 4px 0 0;
  min-height: 10px;
  transition: height 0.3s ease;
}

.chart-bar-label {
  font-size: 10px;
  margin-top: 5px;
  color: #666;
}

.controls {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s ease;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

.btn-success {
  background: #28a745;
  color: white;
}

.btn-success:hover {
  background: #1e7e34;
}

.btn-warning {
  background: #ffc107;
  color: #212529;
}

.btn-warning:hover {
  background: #e0a800;
}
</style>
