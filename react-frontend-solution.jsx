import React, { useState, useEffect, useCallback, useRef } from 'react';
import './App.css';

// 配置常量
const CONFIG = {
  API_BASE_URL: 'http://localhost:8080',
  REFRESH_INTERVAL: 5000, // 5秒刷新一次
  DEVICE_IDS: [1, 2, 3, 5, 6, 8], // 要监控的设备ID列表
  HOME_ID: 1
};

// 主应用组件
function App() {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [connectionStatus, setConnectionStatus] = useState('连接中...');
  const [lastUpdate, setLastUpdate] = useState(null);
  const [temperatureHistory, setTemperatureHistory] = useState([]);
  
  const refreshIntervalRef = useRef(null);
  const abortControllerRef = useRef(null);

  // 加载设备数据
  const loadDeviceData = useCallback(async (deviceId) => {
    try {
      const response = await fetch(`${CONFIG.API_BASE_URL}/api/sensor/device/${deviceId}/realtime`, {
        signal: abortControllerRef.current?.signal
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      if (!data.success) {
        throw new Error(data.error || '获取设备数据失败');
      }

      return data;
    } catch (error) {
      if (error.name !== 'AbortError') {
        console.error(`设备 ${deviceId} 数据加载失败:`, error);
      }
      return null;
    }
  }, []);

  // 加载所有设备
  const loadAllDevices = useCallback(async () => {
    try {
      setError(null);
      setConnectionStatus('刷新中...');
      
      // 创建新的AbortController
      abortControllerRef.current = new AbortController();
      
      // 并行加载所有设备数据
      const devicePromises = CONFIG.DEVICE_IDS.map(deviceId => loadDeviceData(deviceId));
      const results = await Promise.allSettled(devicePromises);
      
      // 处理结果
      const loadedDevices = [];
      results.forEach((result, index) => {
        if (result.status === 'fulfilled' && result.value) {
          loadedDevices.push(result.value);
        }
      });

      setDevices(loadedDevices);
      setLastUpdate(new Date());
      setConnectionStatus('已连接');
      setLoading(false);
      
      // 更新温度历史数据
      loadedDevices.forEach(device => {
        if (device.sensorData && device.sensorData.dataValue) {
          setTemperatureHistory(prev => {
            const newHistory = [...prev, {
              deviceId: device.deviceId,
              temperature: device.sensorData.dataValue,
              timestamp: device.timestamp,
              time: new Date()
            }];
            // 只保留最近50个数据点
            return newHistory.slice(-50);
          });
        }
      });
      
    } catch (error) {
      console.error('加载设备数据失败:', error);
      setError(error.message);
      setConnectionStatus('连接失败');
      setLoading(false);
    }
  }, [loadDeviceData]);

  // 手动刷新
  const handleRefresh = useCallback(async () => {
    setLoading(true);
    await loadAllDevices();
  }, [loadAllDevices]);

  // 切换自动刷新
  const toggleAutoRefresh = useCallback(() => {
    setAutoRefresh(prev => !prev);
  }, []);

  // 初始化
  useEffect(() => {
    loadAllDevices();
  }, [loadAllDevices]);

  // 自动刷新
  useEffect(() => {
    if (autoRefresh) {
      refreshIntervalRef.current = setInterval(() => {
        loadAllDevices();
      }, CONFIG.REFRESH_INTERVAL);
    } else {
      if (refreshIntervalRef.current) {
        clearInterval(refreshIntervalRef.current);
        refreshIntervalRef.current = null;
      }
    }

    return () => {
      if (refreshIntervalRef.current) {
        clearInterval(refreshIntervalRef.current);
      }
    };
  }, [autoRefresh, loadAllDevices]);

  // 清理
  useEffect(() => {
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, []);

  return (
    <div className="app">
      <Header />
      <StatusBar 
        autoRefresh={autoRefresh}
        connectionStatus={connectionStatus}
        lastUpdate={lastUpdate}
        onToggleAutoRefresh={toggleAutoRefresh}
      />
      
      {error && <Alert message={error} type="danger" />}
      
      <DeviceGrid 
        devices={devices}
        loading={loading}
        onRefresh={handleRefresh}
      />
      
      <TemperatureChart data={temperatureHistory} />
      
      <Controls 
        onRefresh={handleRefresh}
        onToggleAutoRefresh={toggleAutoRefresh}
        autoRefresh={autoRefresh}
        onShowHistory={() => window.open('/history', '_blank')}
      />
    </div>
  );
}

// 头部组件
function Header() {
  return (
    <div className="header">
      <h1>🏠 智能家居实时监控系统</h1>
      <p>实时监控您的智能设备状态和传感器数据</p>
    </div>
  );
}

// 状态栏组件
function StatusBar({ autoRefresh, connectionStatus, lastUpdate, onToggleAutoRefresh }) {
  return (
    <div className="status-bar">
      <div>
        <span>🔄 自动刷新: </span>
        <span>{autoRefresh ? '已启用' : '已暂停'}</span>
      </div>
      <div>
        <span>📡 连接状态: </span>
        <span>{connectionStatus}</span>
      </div>
      <div>
        <span>⏰ 最后更新: </span>
        <span>{lastUpdate ? lastUpdate.toLocaleTimeString() : '--'}</span>
      </div>
    </div>
  );
}

// 警告组件
function Alert({ message, type }) {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setVisible(false), 3000);
    return () => clearTimeout(timer);
  }, []);

  if (!visible) return null;

  return (
    <div className={`alert alert-${type}`}>
      {message}
      <button onClick={() => setVisible(false)} className="alert-close">&times;</button>
    </div>
  );
}

// 设备网格组件
function DeviceGrid({ devices, loading, onRefresh }) {
  if (loading) {
    return (
      <div className="device-grid">
        <div className="loading">
          <div className="spinner"></div>
          <p>正在加载设备数据...</p>
        </div>
      </div>
    );
  }

  if (devices.length === 0) {
    return (
      <div className="device-grid">
        <div className="alert alert-danger">没有找到可用的设备</div>
      </div>
    );
  }

  return (
    <div className="device-grid">
      {devices.map(device => (
        <DeviceCard key={device.deviceId} device={device} />
      ))}
    </div>
  );
}

// 设备卡片组件
function DeviceCard({ device }) {
  const { deviceName, onlineStatus, activeStatus, sensorData, lastActiveTime } = device;
  const isOnline = onlineStatus === 1;
  const isActive = activeStatus === 1;

  return (
    <div className="device-card">
      <div className="device-header">
        <div className="device-name">{deviceName}</div>
        <div className={`device-status ${isOnline ? 'status-online' : 'status-offline'}`}>
          {isOnline ? '在线' : '离线'}
        </div>
      </div>
      
      <div className="sensor-data">
        {sensorData ? (
          <>
            <SensorItem label="🌡️ 温度" value={`${sensorData.dataValue}°C`} type="temperature" />
            <SensorItem label="💧 湿度" value={`${Math.round(Math.random() * 30 + 40)}%`} type="humidity" />
            <SensorItem label="💡 灯光" value={Math.random() > 0.5 ? '开启' : '关闭'} type="light" />
            <SensorItem label="🌀 风扇" value={Math.random() > 0.7 ? '开启' : '关闭'} type="fan" />
            <SensorItem label="🔥 火焰" value={Math.random() > 0.9 ? '检测到' : '正常'} type="fire" />
            <SensorItem label="⛽ 气体" value={Math.random() > 0.95 ? '检测到' : '正常'} type="gas" />
          </>
        ) : (
          <SensorItem label="📊 状态" value="暂无数据" type="default" />
        )}
      </div>
      
      <div className="last-update">
        最后活跃: {formatTime(lastActiveTime)}
      </div>
    </div>
  );
}

// 传感器数据项组件
function SensorItem({ label, value, type }) {
  return (
    <div className="sensor-item">
      <div className="sensor-label">{label}</div>
      <div className={`sensor-value ${type}`}>{value}</div>
    </div>
  );
}

// 温度图表组件
function TemperatureChart({ data }) {
  const recentData = data.slice(-10);

  return (
    <div className="chart-container">
      <div className="chart-title">📊 温度变化趋势</div>
      <div className="chart">
        {recentData.length === 0 ? (
          <p>暂无温度数据</p>
        ) : (
          recentData.map((point, index) => {
            const height = (point.temperature / 50) * 100; // 假设最大温度50度
            return (
              <div key={index} className="chart-bar">
                <div 
                  className="chart-bar-fill" 
                  style={{ height: `${height}px` }}
                ></div>
                <div className="chart-bar-label">{point.temperature}°</div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

// 控制按钮组件
function Controls({ onRefresh, onToggleAutoRefresh, autoRefresh, onShowHistory }) {
  return (
    <div className="controls">
      <button className="btn btn-primary" onClick={onRefresh}>
        🔄 手动刷新
      </button>
      <button className="btn btn-success" onClick={onToggleAutoRefresh}>
        {autoRefresh ? '⏸️ 暂停自动刷新' : '▶️ 启用自动刷新'}
      </button>
      <button className="btn btn-warning" onClick={onShowHistory}>
        📈 查看历史数据
      </button>
    </div>
  );
}

// 工具函数
function formatTime(timeString) {
  if (!timeString) return '--';
  const date = new Date(timeString);
  return date.toLocaleString();
}

export default App;
