# 快速启动指南

## 🚀 后端快速启动

### 前提条件

**只需要安装 Java 17**，Maven 不需要单独安装（项目已包含 Maven Wrapper）

### 步骤1: 安装 Java 17

#### 方法1: 使用 Chocolatey（推荐，最简单）

```powershell
# 1. 以管理员身份打开 PowerShell

# 2. 安装 Chocolatey（如果未安装）
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 3. 安装 OpenJDK 17
choco install openjdk17 -y

# 4. 验证安装
java -version
```

#### 方法2: 手动安装

1. 下载 OpenJDK 17: https://adoptium.net/
2. 安装 JDK
3. 配置环境变量：
   - `JAVA_HOME` = JDK安装路径
   - `Path` 中添加 `%JAVA_HOME%\bin`
4. 重新打开命令行窗口
5. 验证：`java -version`

### 步骤2: 启动后端服务

```bash
# 1. 进入项目目录
cd "E:\Project\比赛文件\代码"

# 2. 使用 Maven Wrapper 启动（不需要安装 Maven）
.\mvnw.cmd spring-boot:run

# 或者如果已安装 Maven
mvn spring-boot:run
```

### 步骤3: 验证启动

1. **查看启动日志**，应该看到：
   ```
   Started A7Application in X.XXX seconds
   ```

2. **测试服务**：
   - 浏览器访问: http://localhost:8080
   - 或测试 API: http://localhost:8080/api/ai/stream?message=测试&userId=1

3. **检查端口**：
   ```powershell
   Test-NetConnection -ComputerName localhost -Port 8080
   ```

## 📋 启动检查清单

### 必需项
- [ ] Java 17 已安装 (`java -version`)
- [ ] 数据库服务器可访问 (1.15.135.88:3306)
- [ ] Redis 服务器可访问 (1.15.135.88:6379)
- [ ] 8080 端口未被占用

### 配置项（已配置，通常不需要修改）
- [x] DashScope API 密钥已配置
- [x] 数据库连接信息已配置
- [x] Redis 连接信息已配置
- [x] 服务端口已配置 (8080)

## 🎯 完整启动流程

### 后端启动
```bash
# 方式1: 使用 Maven Wrapper（推荐，不需要安装 Maven）
.\mvnw.cmd spring-boot:run

# 方式2: 使用 Maven（需要先安装 Maven）
mvn spring-boot:run

# 方式3: 使用 IDE
# 在 IntelliJ IDEA 或 Eclipse 中打开项目，运行 A7Application.java
```

### 前端启动（已在运行）
```bash
npm run dev
# 访问: http://localhost:5173
```

## ⚠️ 常见问题

### 1. Java 未安装
**错误**: `'java' 不是内部或外部命令`

**解决**: 
- 安装 Java 17（参考步骤1）
- 配置环境变量
- 重新打开命令行窗口

### 2. 端口被占用
**错误**: `Port 8080 was already in use`

**解决**:
```powershell
# 查找占用端口的进程
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
# 结束进程
taskkill /PID <进程ID> /F
```

### 3. 数据库连接失败
**检查**:
```powershell
# 测试数据库连接
Test-NetConnection -ComputerName 1.15.135.88 -Port 3306
```

### 4. Maven Wrapper 无法运行
**解决**:
```bash
# 给 mvnw.cmd 添加执行权限，或直接使用
.\mvnw.cmd spring-boot:run

# 如果还是不行，安装 Maven 后使用
mvn spring-boot:run
```

## 📞 完整文档

- **详细后端启动指南**: 查看 `BACKEND_START_GUIDE.md`
- **前端启动指南**: 查看 `FRONTEND_START_GUIDE.md`

## 🎉 启动成功标志

### 后端启动成功
- 控制台显示: `Started A7Application in X.XXX seconds`
- 端口 8080 可访问
- 可以访问 API 接口

### 前端启动成功
- 控制台显示: `Local: http://localhost:5173/`
- 浏览器可以访问前端页面

## 🔗 服务地址

- **前端**: http://localhost:5173
- **后端**: http://localhost:8080
- **AI API**: http://localhost:8080/api/ai/stream

---

**提示**: 如果只需要使用前端和AI功能，必须先启动后端服务，因为AI API需要通过后端代理调用。
