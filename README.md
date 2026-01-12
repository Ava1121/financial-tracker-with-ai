# Financial Tracker with AI

一个集成AI理财助手的个人收支管理系统，帮助您轻松管理财务，获取智能理财建议。

## 功能特性

### 核心功能
- ✅ 收支记录管理（添加、编辑、删除、查询）
- ✅ 月度/年度收支统计分析
- ✅ 收支分类管理
- ✅ 数据可视化展示

### AI理财助手
- 🤖 自然语言交互，支持多种财务问题
- 📊 基于您的实际交易数据提供个性化建议
- 💡 支出优化建议、储蓄目标设定、投资建议
- 💰 完全免费，基于本地部署的AI模型
- 🌐 支持中文对话

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **安全**: Spring Security
- **AI集成**: Ollama API（支持deepseek-r1模型）

### 前端
- **框架**: Vue 3
- **UI组件**: Element Plus
- **构建工具**: Vite
- **路由**: Vue Router
- **HTTP客户端**: Axios

## 项目结构

```
financial-tracker-with-ai/
├── src/                      # 后端源代码
│   ├── main/java/com/piems/  # Java源代码
│   └── main/resources/       # 配置文件
├── piems-frontend/           # 前端项目
│   ├── src/                  # 前端源代码
│   └── public/               # 静态资源
├── .gitignore                # Git忽略文件
└── README.md                 # 项目说明文档
```

## 安装与运行

### 环境要求
- JDK 11+
- Node.js 16+
- MySQL 8.0+
- Ollama（用于AI功能，可选）

### 1. 克隆仓库

```bash
git clone https://github.com/Ava1121/financial-tracker-with-ai.git
cd financial-tracker-with-ai
```

### 2. 配置数据库

1. 创建MySQL数据库：
   ```sql
   CREATE DATABASE piems CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 执行SQL脚本：
   ```bash
   mysql -u root -p piems < src/main/resources/sql/schema.sql
   ```

3. 修改数据库配置：
   编辑 `src/main/resources/application.yml` 文件，更新数据库连接信息。

### 3. 启动后端服务

```bash
# 使用Maven启动
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/piems-1.0-SNAPSHOT.jar
```

后端服务将运行在：http://localhost:9090/piems

### 4. 启动前端服务

```bash
cd piems-frontend
npm install
npm run dev
```

前端服务将运行在：http://localhost:5173

## 使用说明

### 1. 访问应用

在浏览器中打开：http://localhost:5173

### 2. 使用AI理财助手

1. 在收支记录页面，点击右下角的"AI助手"按钮
2. 在聊天窗口中输入您的财务问题，例如：
   - "如何增加收入？"
   - "我的支出结构合理吗？"
   - "购买什么基金比较好？"
   - "如何提高储蓄率？"
3. AI助手将基于您的财务数据提供个性化建议

### 3. 支持的AI问题类型

- 支出分析与优化建议
- 储蓄目标设定
- 投资建议
- 财务规划
- 收支结构分析
- 其他财务相关问题

## AI模型配置

### 本地部署AI模型

1. 安装Ollama：https://ollama.com/download
2. 拉取模型：
   ```bash
   ollama pull deepseek-r1
   ```
3. 启动Ollama服务

### 使用其他AI服务

如果您想使用其他AI服务，可以修改 `RecordServiceImpl.java` 文件中的AI集成部分。

## 开发与贡献

### 后端开发

```bash
# 编译代码
mvn compile

# 运行测试
mvn test

# 生成API文档
mvn swagger:generate
```

### 前端开发

```bash
cd piems-frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build
```

### 代码规范

- 后端：遵循Java代码规范，使用Maven进行依赖管理
- 前端：遵循Vue 3最佳实践，使用ESLint进行代码检查

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提交Issue或Pull Request。

---

**Financial Tracker with AI** - 智能财务管家，让理财更简单！