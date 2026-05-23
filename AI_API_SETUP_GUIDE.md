# AI API 密钥配置指南

## 🎯 概述

本项目支持两种AI服务：
1. **DashScope (阿里云通义千问)** - 推荐使用，国内访问稳定
2. **OpenAI GPT** - 功能强大，需要海外网络

至少需要配置一个API密钥才能使用AI功能。

## 🔑 获取API密钥

### 1. DashScope API密钥 (推荐)

#### 获取步骤：
1. 访问 [阿里云DashScope控制台](https://dashscope.console.aliyun.com/)
2. 登录阿里云账号（没有账号需要先注册）
3. 进入"API-KEY管理"页面
4. 点击"创建新的API-KEY"
5. 复制生成的API密钥（格式：sk-xxxxxxxxxx）

#### 优势：
- ✅ 国内访问稳定，无需翻墙
- ✅ 每月100万tokens免费额度
- ✅ 支持中文优化
- ✅ 响应速度快

### 2. OpenAI API密钥

#### 获取步骤：
1. 访问 [OpenAI平台](https://platform.openai.com/api-keys)
2. 登录OpenAI账号（需要海外手机号验证）
3. 点击"Create new secret key"
4. 复制生成的API密钥（格式：sk-xxxxxxxxxx）

#### 注意事项：
- ⚠️ 需要海外网络访问
- ⚠️ 需要绑定信用卡付费使用
- ⚠️ 新用户有$5免费额度

## ⚙️ 配置方法

### 方法1：使用启动脚本 (推荐)

#### Windows用户：
```bash
# 双击运行
setup-ai-keys.bat
```

#### Linux/Mac用户：
```bash
# 给脚本执行权限
chmod +x setup-ai-keys.sh

# 运行脚本
./setup-ai-keys.sh
```

### 方法2：手动设置环境变量

#### Windows (命令行)：
```cmd
# 设置DashScope密钥
set DASHSCOPE_API_KEY=sk-your-dashscope-key

# 设置OpenAI密钥
set OPENAI_API_KEY=sk-your-openai-key

# 启动应用
mvn spring-boot:run
```

#### Windows (PowerShell)：
```powershell
# 设置DashScope密钥
$env:DASHSCOPE_API_KEY="sk-your-dashscope-key"

# 设置OpenAI密钥
$env:OPENAI_API_KEY="sk-your-openai-key"

# 启动应用
mvn spring-boot:run
```

#### Linux/Mac：
```bash
# 设置DashScope密钥
export DASHSCOPE_API_KEY=sk-your-dashscope-key

# 设置OpenAI密钥
export OPENAI_API_KEY=sk-your-openai-key

# 启动应用
mvn spring-boot:run
```

### 方法3：修改配置文件

编辑 `src/main/resources/application.properties`：

```properties
# DashScope API配置
dashscope.api-key=sk-your-dashscope-key

# OpenAI API配置
openai.api.key=sk-your-openai-key
```

⚠️ **注意**：不建议将真实API密钥直接写入配置文件，特别是在版本控制中。

## 🔍 验证配置

启动应用后，查看控制台日志：

```
✅ DashScope API密钥已配置
✅ OpenAI API密钥已配置
🚀 AI服务已准备就绪！
```

如果看到以下警告：
```
⚠️  DashScope API密钥未配置或使用测试密钥！
❌ 没有配置有效的AI服务API密钥！
```

说明需要重新配置API密钥。

## 🧪 测试AI功能

1. 启动前端服务：
   ```bash
   cd 前端/front
   npm run dev
   ```

2. 访问测试页面：`http://localhost:5173/home/ai-test`

3. 测试各项AI功能：
   - AI对话
   - 智能备课
   - 习题生成

## ❗ 常见问题

### Q1: 提示"AI服务暂时不可用"
**A**: 检查API密钥是否正确配置，确保密钥格式正确（以sk-开头）

### Q2: DashScope API调用失败
**A**: 
- 检查网络连接
- 确认API密钥有效
- 检查是否超出免费额度

### Q3: OpenAI API调用失败
**A**: 
- 检查网络连接（可能需要代理）
- 确认账户余额充足
- 检查API密钥权限

### Q4: 环境变量设置后仍然无效
**A**: 
- 重启IDE和终端
- 检查环境变量名称是否正确
- 确认没有多余的空格或特殊字符

## 💰 费用说明

### DashScope费用：
- 免费额度：每月100万tokens
- 超出后按量计费：约0.002元/1000tokens

### OpenAI费用：
- GPT-3.5-turbo：$0.002/1000tokens
- GPT-4：$0.03/1000tokens

## 🔒 安全建议

1. **不要将API密钥提交到版本控制系统**
2. **定期轮换API密钥**
3. **监控API使用量，避免异常消耗**
4. **在生产环境中使用专门的密钥管理服务**

## 📞 技术支持

如果遇到配置问题，请：
1. 检查控制台日志输出
2. 确认网络连接正常
3. 验证API密钥格式和有效性
4. 查看项目README.md获取更多信息
