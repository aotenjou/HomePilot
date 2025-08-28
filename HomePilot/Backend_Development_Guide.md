# 智能家居管理系统 - 后端开发指南

## 📋 目录

- [项目概述](#项目概述)
- [环境准备](#环境准备)
- [项目启动](#项目启动)
- [项目结构](#项目结构)
- [开发流程](#开发流程)
- [接口测试](#接口测试)
- [常见问题](#常见问题)
- [开发建议](#开发建议)

## 🎯 项目概述

这是一个基于Spring Boot 3.5.3的智能家居管理系统后端项目，主要功能包括：

- 用户认证与授权（JWT）
- 家庭管理
- 设备管理
- 场景自动化
- 权限控制
- MQTT设备通信
- AI聊天助手

## 🛠️ 环境准备

### 必需软件

1. **Java 17+** 
   - 下载地址：https://www.oracle.com/java/technologies/downloads/
   - 验证安装：`java -version`

2. **Maven 3.6+**
   - 下载地址：https://maven.apache.org/download.cgi
   - 验证安装：`mvn -version`

3. **MySQL 8.0+**
   - 下载地址：https://dev.mysql.com/downloads/mysql/
   - 或者使用Docker：`docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=your_password mysql:8.0`

4. **IDE推荐**
   - IntelliJ IDEA (推荐)
   - Eclipse
   - VS Code

### 环境变量配置

```bash
# Windows
JAVA_HOME=C:\Program Files\Java\jdk-17
MAVEN_HOME=C:\Program Files\Apache\maven
PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export MAVEN_HOME=/usr/local/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

## 🚀 项目启动

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd manager-main
```

### 2. 数据库配置

#### 方式一：使用提供的SQL脚本（推荐）

```bash
# 登录MySQL
mysql -u root -p

# 执行SQL脚本
source database_schema.sql
```

#### 方式二：Docker

`docker-compose.yml`中提供了容器化MySQL配置

### 3. 修改配置文件

编辑 `src/main/resources/application-cjy.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/manager
    username: root
    password: your_mysql_password  # 修改为你的MySQL密码

  mqtt:
    url: tcp://your_mqtt_server:1883  # 修改为你的MQTT服务器地址
    username: your_mqtt_username
    password: your_mqtt_password
```

### 4. 启动项目

#### 方式一：IDE启动
1. 在IDE中打开项目
2. 找到 `ManagerApplication.java`
3. 右键选择 "Run" 或 "Debug"

#### 方式二：命令行启动
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/manager-0.0.1-SNAPSHOT.jar
```

### 5. 验证启动

访问以下地址验证服务是否启动成功：
- 应用主页：http://localhost:8080（在`src/main/resources/application.yml`下进行你的配置。）

## 📁 项目结构

```
src/main/java/com/example/manager/
├── config/           # 配置类
├── controller/       # 控制器层（API接口）
├── service/         # 业务逻辑层
├── mapper/          # 数据访问层
├── entity/          # 实体类（数据库表映射）
├── DTO/             # 数据传输对象
├── utils/           # 工具类
├── middleware/      # 中间件
├── mqtt/            # MQTT相关
└── MCPtools/        # AI工具

src/main/resources/
├── mapper/          # MyBatis映射文件
├── db/              # 数据库相关
└── application*.yml # 配置文件
```

## 🔧 开发流程

### 1. 添加新功能

#### 步骤1：创建实体类
```java
// src/main/java/com/example/manager/entity/NewEntity.java
@Data
@TableName("new_table")
public class NewEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("name")
    private String name;
    
    // 其他字段...
}
```

#### 步骤2：创建Mapper接口
```java
// src/main/java/com/example/manager/mapper/NewEntityMapper.java
@Mapper
public interface NewEntityMapper extends BaseMapper<NewEntity> {
    // 自定义查询方法
}
```

#### 步骤3：创建Service类
```java
// src/main/java/com/example/manager/service/NewEntityService.java
@Service
public class NewEntityService {
    @Autowired
    private NewEntityMapper newEntityMapper;
    
    public List<NewEntity> getAll() {
        return newEntityMapper.selectList(null);
    }
    
    // 其他业务方法...
}
```

#### 步骤4：创建Controller
```java
// src/main/java/com/example/manager/controller/NewEntityController.java
@RestController
@RequestMapping("/api/new-entity")
@Tag(name = "新实体管理", description = "新实体的增删改查接口")
public class NewEntityController {
    @Autowired
    private NewEntityService newEntityService;
    
    @GetMapping("/list")
    public ResponseEntity<List<NewEntity>> getAll() {
        return ResponseEntity.ok(newEntityService.getAll());
    }
    
    // 其他接口...
}
```

### 2. 修改现有功能

1. **修改实体类**：更新字段、注解等
2. **修改数据库**：执行相应的SQL语句
3. **修改业务逻辑**：在Service层添加或修改方法
4. **修改接口**：在Controller层更新接口逻辑

### 3. 代码规范

- 使用Lombok简化代码
- 遵循RESTful API设计规范
- 添加Swagger注解文档
- 使用统一的响应格式
- 添加适当的异常处理

## 🧪 接口测试


###  1.使用Postman或者apifox

#### 认证接口测试
```http
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "phone": "13800138000",
  "password": "password123"
}
```

#### 需要认证的接口测试
```http
GET http://localhost:8081/home/get
Authorization: Bearer your_jwt_token
```

### 2. 单测

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=TestClassName

# 运行特定测试方法
mvn test -Dtest=TestClassName#testMethodName
```

## 🐛 常见问题

### 1. 启动失败

#### 问题：端口被占用
```bash
# Windows查看端口占用
netstat -ano | findstr 8081

# Linux/Mac查看端口占用
lsof -i :8081

# 杀死进程
kill -9 <PID>
```

#### 问题：数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库连接信息
- 确认数据库是否存在

#### 问题：Java版本不匹配
```bash
# 检查Java版本
java -version

# 确保使用Java 17+
```

### 2. 运行时错误

#### 问题：JWT Token无效
- 检查Token是否过期
- 验证Token格式是否正确
- 确认JWT密钥配置

#### 问题：权限不足
- 检查用户角色设置
- 验证权限配置
- 查看日志中的权限信息

### 3. 数据库问题

#### 问题：表不存在
```sql
-- 检查表是否存在
SHOW TABLES;

-- 重新执行建表脚本
source database_schema.sql;
```

#### 问题：外键约束失败
- 检查关联数据是否存在
- 确认外键关系配置
- 查看数据库日志

## 💡 开发建议

### 1. 开发前准备

- 仔细阅读项目代码结构
- 理解业务逻辑和数据流
- 熟悉使用的技术栈（Spring Boot、MyBatis-Plus等）

### 2. 开发过程中

- 遵循现有代码风格
- 及时添加注释和文档
- 做好版本控制
- 定期测试功能

### 3. 调试技巧

- 使用IDE的调试功能
- 添加日志输出
- 使用Swagger测试接口
- 查看控制台错误信息

### 4. 性能优化

- 合理使用数据库索引
- 避免N+1查询问题
- 使用缓存减少数据库访问
- 优化SQL查询语句

### 5. 安全考虑

- 验证用户输入
- 防止SQL注入
- 使用HTTPS（生产环境）
- 定期更新依赖版本

## 📚 学习资源

### 官方文档
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus文档](https://baomidou.com/)
- [MySQL官方文档](https://dev.mysql.com/doc/)

### 推荐书籍
- 《Spring Boot实战》
- 《MyBatis技术内幕》
- 《MySQL必知必会》

### 在线教程
- Spring Boot入门教程
- MyBatis-Plus使用指南
- JWT认证实现

## 🔄 部署流程

### 1. 打包应用
```bash
mvn clean package -DskipTests
```

### 2. 部署到服务器
```bash
# 上传jar文件到服务器
scp target/manager-0.0.1-SNAPSHOT.jar user@server:/app/

# 在服务器上运行
java -jar manager-0.0.1-SNAPSHOT.jar
```

### 3. 使用Docker部署
```dockerfile
FROM openjdk:17-jre-slim
COPY target/manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---


如有疑问，请随时查阅本文档或联系开发团队。
