# 智能教育平台

本项目是一个基于Spring Boot和AI技术的智能教育平台，提供课程管理、智能备课、学习进度跟踪、在线提问和学习数据分析等功能。

## 功能模块

### 1. 用户管理
- 支持学生、教师和管理员三种角色
- 提供用户注册、登录、查询、更新和删除功能

### 2. 课程管理
- 课程的创建、更新、查询和删除
- 章节和资源的管理
- 学生选课和学习进度跟踪

### 3. 智能备课
- 基于AI的课程大纲生成
- 章节内容智能生成
- 教学计划自动制定
- 习题集智能生成

### 4. 学习进度跟踪
- 学习行为记录
- 学习进度统计
- 学习数据可视化

### 5. 在线提问
- 学生问题提交
- 教师/学生回答
- AI智能回答

### 6. 考核评估
- 习题管理
- 学生答题记录
- 成绩分析和评估

## API接口说明

### 用户管理接口

| 接口 | 方法 | 功能说明 |
|------|------|---------|
| `/api/user/register` | POST | 用户注册 |
| `/api/user/login` | POST | 用户登录 |
| `/api/user/page` | GET | 获取所有用户（分页） |
| `/api/user/username/{username}` | GET | 根据用户名查询 |
| `/api/user/add` | POST | 添加用户 |
| `/api/user/update/{id}` | PUT | 更新用户 |
| `/api/user/delete/{id}` | DELETE | 删除用户 |
| `/api/user/findById/{id}` | GET | 根据ID查询用户 |

### 课程管理接口

| 接口 | 方法 | 功能说明 |
|------|------|---------|
| `/api/course` | POST | 创建课程 |
| `/api/course/{id}` | PUT | 更新课程 |
| `/api/course/{id}` | DELETE | 删除课程 |
| `/api/course/{id}` | GET | 获取课程详情 |
| `/api/course/list` | GET | 获取所有课程（分页） |
| `/api/course/teacher/{teacherId}` | GET | 按教师ID获取课程列表 |
| `/api/course/chapter` | POST | 添加章节 |
| `/api/course/chapter/{id}` | PUT | 更新章节 |
| `/api/course/chapter/{id}` | DELETE | 删除章节 |
| `/api/course/{courseId}/chapters` | GET | 获取课程下的所有章节 |
| `/api/course/resource` | POST | 上传资源 |
| `/api/course/resource/{id}` | PUT | 更新资源 |
| `/api/course/resource/{id}` | DELETE | 删除资源 |
| `/api/course/chapter/{chapterId}/resources` | GET | 获取章节下的所有资源 |
| `/api/course/enroll` | POST | 学生选课 |
| `/api/course/drop` | POST | 学生退课 |
| `/api/course/student/{studentId}` | GET | 获取学生选修的课程列表 |
| `/api/course/{courseId}/statistics` | GET | 获取课程统计信息 |

### 智能备课接口

| 接口 | 方法 | 功能说明 |
|------|------|---------|
| `/api/teaching-ai/course-outline` | GET | 生成课程大纲 |
| `/api/teaching-ai/chapter-content` | GET | 生成章节内容 |
| `/api/teaching-ai/teaching-plan` | GET | 生成教学计划 |
| `/api/teaching-ai/exercises` | GET | 生成习题集 |
| `/api/teaching-ai/evaluate-answer` | POST | 评估学生回答 |

### 在线提问接口

| 接口 | 方法 | 功能说明 |
|------|------|---------|
| `/api/question` | POST | 学生提问 |
| `/api/question/answer` | POST | 回答问题 |
| `/api/question/{id}/ai-answer` | POST | 生成AI回答 |
| `/api/question/{id}` | PUT | 更新问题 |
| `/api/question/{id}` | DELETE | 删除问题 |
| `/api/question/{id}` | GET | 获取问题详情 |
| `/api/question/{id}/answers` | GET | 获取问题的所有回答 |
| `/api/question/course/{courseId}` | GET | 按课程ID获取问题列表 |
| `/api/question/chapter/{chapterId}` | GET | 按章节ID获取问题列表 |
| `/api/question/student/{studentId}` | GET | 按学生ID获取问题列表 |
| `/api/question/list` | GET | 分页获取所有问题 |

### 学习进度跟踪接口

| 接口 | 方法 | 功能说明 |
|------|------|---------|
| `/api/learning/record` | POST | 记录学习行为 |
| `/api/learning/progress/{id}` | PUT | 更新学习进度 |
| `/api/learning/student/{studentId}` | GET | 获取学生的所有学习记录 |
| `/api/learning/student/{studentId}/resource/{resourceId}` | GET | 获取学生对特定资源的学习记录 |
| `/api/learning/student/{studentId}/course/{courseId}` | GET | 获取学生在特定课程的学习记录 |
| `/api/learning/statistics/resource/{resourceId}` | GET | 获取资源的学习统计 |
| `/api/learning/progress/student/{studentId}/course/{courseId}` | GET | 获取学生的课程进度统计 |
| `/api/learning/statistics/course/{courseId}` | GET | 获取课程的整体学习统计 |

## 🚀 快速开始

### 首次配置

**详细配置步骤请查看：[项目配置完整指南](PROJECT_SETUP_GUIDE.md)**

### 快速启动

#### 前端启动
```bash
npm install          # 首次运行
npm run dev          # 启动前端服务
```
访问：http://localhost:5173

#### 后端启动（如需要使用AI功能）
```bash
.\mvnw.cmd spring-boot:run    # 启动后端服务
```
服务：http://localhost:8080

### 配置文档

- 📖 **[项目配置完整指南](PROJECT_SETUP_GUIDE.md)** - 从零开始的完整配置步骤
- 🚀 **[快速参考](QUICK_REFERENCE.md)** - 快速启动命令和故障排查
- 🎨 **[前端启动指南](FRONTEND_START_GUIDE.md)** - 前端详细配置说明
- 🔙 **[后端启动指南](BACKEND_START_GUIDE.md)** - 后端详细配置说明

## 环境配置

### 运行环境
- **Node.js**: 16+ (前端必需)
- **JDK**: 17+ (后端/AI功能需要)
- **MySQL**: 8.0+ (远程: 1.15.135.88:3306)
- **Redis**: 6.0+ (远程: 1.15.135.88:6379)
- **Maven**: 项目包含 Maven Wrapper，不需要单独安装

### 配置说明

#### 前端配置
- ✅ Vite 代理已配置（`vite.config.js`）
- ✅ 前端依赖已配置（`package.json`）
- ✅ 无需额外配置，直接运行 `npm install` 和 `npm run dev` 即可

#### 后端配置
- ✅ DashScope API密钥已配置
- ✅ 数据库连接信息已配置
- ✅ Redis连接信息已配置
- ⚠️ 需要确保数据库和Redis服务可访问

### 快速启动命令

```bash
# 前端
npm install
npm run dev

# 后端（如需要AI功能）
.\mvnw.cmd spring-boot:run
```

## 项目结构
- `DO` - 数据对象，与数据库表对应
- `Mapper` - MyBatis映射接口
- `Service` - 业务逻辑接口及实现
- `Controller` - API接口控制器
- `AI` - AI服务相关功能
- `Config` - 项目配置类 # A7
