# 后端启动指南

## 📋 前置要求

### 1. 必需环境

- **Java 17** 或更高版本
- **Maven 3.8+** 构建工具
- **MySQL 8.0+** 数据库（远程：1.15.135.88:3306）
- **Redis 6.0+** （远程：1.15.135.88:6379）

### 2. 配置信息

- **服务端口**: 8080
- **数据库**: MySQL (1.15.135.88:3306/project)
- **Redis**: 1.15.135.88:6379
- **DashScope API密钥**: 已配置在 application.properties

## 🚀 安装步骤

### 步骤1: 安装 Java 17

#### Windows 系统

1. **下载 JDK 17**
   - 访问 Oracle 官网或 OpenJDK 官网
   - 下载 Windows x64 版本的 JDK 17
   - 推荐使用 OpenJDK: https://adoptium.net/

2. **安装 JDK**
   - 运行安装程序
   - 记住安装路径（例如：`C:\Program Files\Java\jdk-17`）

3. **配置环境变量**
   - 右键"此电脑" -> "属性" -> "高级系统设置" -> "环境变量"
   - 在"系统变量"中添加：
     - 变量名：`JAVA_HOME`
     - 变量值：JDK安装路径（例如：`C:\Program Files\Java\jdk-17`）
   - 在"Path"变量中添加：
     - `%JAVA_HOME%\bin`

4. **验证安装**
   ```bash
   java -version
   ```
   应该显示 Java 17 或更高版本

#### 使用 Chocolatey 安装（推荐）

```powershell
# 安装 Chocolatey（如果未安装）
# 以管理员身份运行 PowerShell，执行：
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 安装 OpenJDK 17
choco install openjdk17 -y
```

### 步骤2: 安装 Maven

#### Windows 系统

1. **下载 Maven**
   - 访问 Apache Maven 官网：https://maven.apache.org/download.cgi
   - 下载 `apache-maven-3.9.x-bin.zip`

2. **解压 Maven**
   - 解压到目录（例如：`C:\Program Files\Apache\maven`）

3. **配置环境变量**
   - 添加系统变量：
     - 变量名：`MAVEN_HOME`
     - 变量值：Maven解压路径（例如：`C:\Program Files\Apache\maven`）
   - 在"Path"变量中添加：
     - `%MAVEN_HOME%\bin`

4. **验证安装**
   ```bash
   mvn -version
   ```
   应该显示 Maven 版本信息

#### 使用 Chocolatey 安装（推荐）

```powershell
choco install maven -y
```

### 步骤3: 验证环境

在项目根目录执行以下命令验证环境：

```bash
# 检查 Java 版本
java -version
# 应该显示: openjdk version "17" 或更高

# 检查 Maven 版本
mvn -version
# 应该显示: Apache Maven 3.9.x 或更高

# 检查 Java 编译器
javac -version
# 应该显示: javac 17 或更高
```

## 🔧 配置检查

### 1. 数据库连接配置

配置文件：`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://1.15.135.88:3306/project?serverTimezone=GMT%2B8
spring.datasource.username=hzx
spring.datasource.password=36190411Jason
```

**检查项**：
- [ ] 数据库服务器可访问（1.15.135.88:3306）
- [ ] 数据库 `project` 存在
- [ ] 用户名和密码正确
- [ ] 网络连接正常

### 2. Redis 连接配置

```properties
spring.data.redis.host=1.15.135.88
spring.data.redis.port=6379
spring.data.redis.password=36190411Jason
```

**检查项**：
- [ ] Redis 服务器可访问（1.15.135.88:6379）
- [ ] Redis 密码正确
- [ ] 网络连接正常

### 3. DashScope API 配置

```properties
dashscope.api-key=sk-dbb6f27871f94f0ebff6e60321ce9568
dashscope.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
```

**检查项**：
- [ ] API 密钥有效
- [ ] API 密钥未过期

### 4. 端口配置

```properties
server.port=8080
```

**检查项**：
- [ ] 8080 端口未被占用

## 🎯 启动后端服务

### 方式1: 使用 Maven 命令（推荐）

```bash
# 1. 进入项目根目录
cd "E:\Project\比赛文件\代码"

# 2. 清理并编译项目
mvn clean compile

# 3. 运行 Spring Boot 应用
mvn spring-boot:run
```

### 方式2: 使用 Maven Wrapper

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 方式3: 使用 IDE（IntelliJ IDEA / Eclipse）

1. **导入项目**
   - 打开 IDE
   - File -> Open -> 选择项目根目录
   - 等待 Maven 自动下载依赖

2. **运行应用**
   - 找到 `A7Application.java`
   - 右键 -> Run 'A7Application.main()'
   - 或者点击运行按钮

3. **配置运行参数**（可选）
   - Run -> Edit Configurations
   - 设置 VM options（如果需要）
   - 设置 Environment variables（如果需要）

### 方式4: 打包后运行

```bash
# 1. 打包项目
mvn clean package

# 2. 运行 jar 文件
java -jar target/A7-0.0.1-SNAPSHOT.jar
```

## ✅ 验证启动

### 1. 检查启动日志

启动成功后，应该看到类似以下日志：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.0)

2024-01-XX XX:XX:XX.XXX  INFO --- [           main] com.example.A7.A7Application  : Starting A7Application
2024-01-XX XX:XX:XX.XXX  INFO --- [           main] com.example.A7.A7Application  : The following 1 profile is active: "default"
...
2024-01-XX XX:XX:XX.XXX  INFO --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2024-01-XX XX:XX:XX.XXX  INFO --- [           main] com.example.A7.A7Application  : Started A7Application in X.XXX seconds
```

### 2. 测试 API 接口

#### 测试健康检查（如果配置了）

```bash
# 使用 curl
curl http://localhost:8080/actuator/health

# 或使用浏览器访问
# http://localhost:8080/actuator/health
```

#### 测试 AI 接口

```bash
# 测试 AI 流式接口（需要参数）
curl "http://localhost:8080/api/ai/stream?message=你好&userId=1"
```

### 3. 检查端口

```bash
# Windows PowerShell
Test-NetConnection -ComputerName localhost -Port 8080

# 应该返回: TcpTestSucceeded : True
```

### 4. 访问 Swagger 文档（如果配置了）

如果项目配置了 Swagger，可以访问：
- http://localhost:8080/swagger-ui.html
- 或 http://localhost:8080/doc.html

## 🔍 故障排查

### 问题1: Java 未安装或未配置

**症状**：
```
'java' 不是内部或外部命令，也不是可运行的程序
```

**解决方案**：
1. 检查 Java 是否安装：`where java`
2. 检查环境变量 `JAVA_HOME` 是否设置
3. 检查 `Path` 中是否包含 `%JAVA_HOME%\bin`
4. 重新打开命令行窗口（环境变量更改后需要重启）

### 问题2: Maven 未安装或未配置

**症状**：
```
'mvn' 不是内部或外部命令，也不是可运行的程序
```

**解决方案**：
1. 检查 Maven 是否安装：`where mvn`
2. 检查环境变量 `MAVEN_HOME` 是否设置
3. 检查 `Path` 中是否包含 `%MAVEN_HOME%\bin`
4. 重新打开命令行窗口

### 问题3: 端口被占用

**症状**：
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案**：
1. **查找占用端口的进程**：
   ```bash
   # Windows
   netstat -ano | findstr :8080
   # 或
   Get-NetTCPConnection -LocalPort 8080
   
   # 查看进程详情
   tasklist | findstr <PID>
   ```

2. **结束进程**：
   ```bash
   # Windows
   taskkill /PID <PID> /F
   ```

3. **或修改端口**：
   修改 `application.properties`：
   ```properties
   server.port=8081
   ```

### 问题4: 数据库连接失败

**症状**：
```
Could not connect to database server
Communications link failure
```

**解决方案**：
1. 检查数据库服务器是否可访问：
   ```bash
   ping 1.15.135.88
   telnet 1.15.135.88 3306
   ```

2. 检查数据库配置：
   - 数据库地址、端口是否正确
   - 用户名、密码是否正确
   - 数据库名称是否存在

3. 检查防火墙设置：
   - 确保 3306 端口未被防火墙阻止

4. 检查数据库服务：
   - 确保 MySQL 服务正在运行

### 问题5: Redis 连接失败

**症状**：
```
Unable to connect to Redis
Connection refused
```

**解决方案**：
1. 检查 Redis 服务器是否可访问：
   ```bash
   ping 1.15.135.88
   telnet 1.15.135.88 6379
   ```

2. 检查 Redis 配置：
   - Redis 地址、端口是否正确
   - 密码是否正确

3. 检查防火墙设置：
   - 确保 6379 端口未被防火墙阻止

### 问题6: 依赖下载失败

**症状**：
```
Could not transfer artifact
Failed to read artifact descriptor
```

**解决方案**：
1. **检查网络连接**
2. **配置 Maven 镜像**（推荐使用阿里云镜像）
   
   编辑 `~/.m2/settings.xml`（如果不存在则创建）：
   ```xml
   <settings>
     <mirrors>
       <mirror>
         <id>aliyunmaven</id>
         <mirrorOf>*</mirrorOf>
         <name>阿里云公共仓库</name>
         <url>https://maven.aliyun.com/repository/public</url>
       </mirror>
     </mirrors>
   </settings>
   ```

3. **清理 Maven 缓存**：
   ```bash
   mvn clean
   rm -rf ~/.m2/repository
   mvn install
   ```

### 问题7: 编译错误

**症状**：
```
Compilation failure
Cannot find symbol
```

**解决方案**：
1. **清理项目**：
   ```bash
   mvn clean
   ```

2. **重新编译**：
   ```bash
   mvn compile
   ```

3. **检查 Java 版本**：
   - 确保使用 Java 17
   - 检查 `pom.xml` 中的 Java 版本配置

### 问题8: DashScope API 调用失败

**症状**：
```
API调用失败
Invalid API key
```

**解决方案**：
1. 检查 API 密钥是否有效
2. 检查 API 密钥是否过期
3. 检查网络连接
4. 检查 API 配置是否正确

## 📝 启动检查清单

### 启动前检查
- [ ] Java 17 已安装并配置
- [ ] Maven 已安装并配置
- [ ] 数据库服务器可访问
- [ ] Redis 服务器可访问
- [ ] 8080 端口未被占用
- [ ] DashScope API 密钥有效

### 启动后验证
- [ ] 服务成功启动（看到启动成功日志）
- [ ] 端口 8080 可访问
- [ ] 数据库连接成功
- [ ] Redis 连接成功
- [ ] AI API 接口可访问

## 🎯 快速启动命令

```bash
# 1. 进入项目目录
cd "E:\Project\比赛文件\代码"

# 2. 清理并启动（首次运行）
mvn clean spring-boot:run

# 3. 或直接启动（后续运行）
mvn spring-boot:run

# 4. 或使用 Maven Wrapper
.\mvnw.cmd spring-boot:run
```

## 📞 下一步

1. **验证后端服务**: 访问 http://localhost:8080
2. **测试 AI 接口**: 通过前端或 Postman 测试
3. **查看日志**: 检查控制台输出
4. **测试完整功能**: 结合前端测试所有功能

## ⚙️ 配置说明

### 关键配置文件

- `src/main/resources/application.properties` - 主配置文件
- `pom.xml` - Maven 依赖配置
- `src/main/java/com/example/A7/A7Application.java` - 主启动类

### 服务信息

- **服务地址**: http://localhost:8080
- **API 前缀**: /api
- **AI 接口**: /api/ai/stream
- **认证接口**: /api/auth/login

---

**最后更新**: 2024-01-XX
**状态**: 待启动 ⏳
