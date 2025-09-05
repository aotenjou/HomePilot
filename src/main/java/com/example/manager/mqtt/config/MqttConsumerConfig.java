package com.example.manager.mqtt.config;

import com.example.manager.mqtt.Callback.MqttConsumerCallBack;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MqttConsumerConfig {
    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.mqtt.client.consumerid}")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    /**
     * 客户端对象
     */
    private MqttClient client;

    /**
     * 在bean初始化后连接到服务器
     */
    @PostConstruct
    public void init(){
        System.out.println("=== MQTT Consumer 初始化开始 ===");
        connect();
    }

    /**
     * 客户端连接服务端
     */
    public void connect(){
        try {
            System.out.println("=== MQTT连接开始 ===");
            System.out.println("MQTT服务器地址: " + hostUrl);
            System.out.println("MQTT客户端ID: " + clientId);
            System.out.println("MQTT用户名: " + username);
            
            //创建MQTT客户端对象
            client = new MqttClient(hostUrl,clientId,new MemoryPersistence());
            System.out.println("MQTT客户端对象创建成功");
            
            //连接设置
            MqttConnectOptions options = new MqttConnectOptions();
            //是否清空session，设置为false表示服务器会保留客户端的连接记录，客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
            //设置为true表示每次连接到服务端都是以新的身份
            options.setCleanSession(true);
            //设置连接用户名
            options.setUserName(username);
            //设置连接密码
            options.setPassword(password.toCharArray());
            //设置超时时间，单位为秒
            options.setConnectionTimeout(100);
            //设置心跳时间 单位为秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            options.setKeepAliveInterval(20);
            //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
            options.setWill("willTopic",(clientId + "与服务器断开连接").getBytes(),0,false);
            
            System.out.println("MQTT连接选项配置完成，开始连接...");
            
            //设置回调
            client.setCallback(new MqttConsumerCallBack());
            client.connect(options);
            
            System.out.println("MQTT连接成功！");
            
            //订阅主题
            //消息等级，和主题数组一一对应，服务端将按照指定等级给订阅了主题的客户端推送消息
            int[] qos = {0,0,0};
            //主题
            String[] topics = {"Hardware_OutPut","Hardware_InPut_Room","Hardware_InPut_Plant"};
            //订阅主题
            client.subscribe(topics,qos);
            
            System.out.println("MQTT主题订阅成功: " + String.join(", ", topics));
            System.out.println("=== MQTT连接配置完成 ===");
            
        } catch (MqttException e) {
            System.err.println("=== MQTT连接失败 ===");
            System.err.println("错误代码: " + e.getReasonCode());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disConnect(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 订阅主题
     */
    public void subscribe(String topic,int qos){
        try {
            client.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}