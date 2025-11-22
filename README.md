# WebSocket 即时通讯系统

基于 Spring Boot + Vue.js 的实时聊天应用，支持私聊和群聊功能。

## 技术栈

**后端**
- Spring Boot 3.5.7
- WebSocket
- MyBatis
- MySQL
- Spring Security

**前端**
- Vue.js
- Vite

## 快速开始

### 后端启动

1. 创建数据库并执行初始化脚本：
```bash
mysql -u root -p < init.sql
```

2. 修改 `application.properties` 中的数据库配置

3. 启动后端服务：
```bash
mvnw spring-boot:run
```

服务运行在 `http://localhost:8081`

### 前端启动
请务必运行第二项！
```bash
cd Vue/ws
npm install
npm run dev
```

## 主要功能

- 用户注册/登录
- 好友管理（添加/删除好友）
- 私聊消息
- 群组聊天
- 实时消息推送
- 用户在线状态

## 项目结构

```
├── src/main/java/cn/cdut/ws/    # 后端代码
│   ├── controller/              # 控制器层
│   ├── service/                 # 业务逻辑层
│   ├── mapper/                  # 数据访问层
│   ├── model/                   # 数据模型
│   └── handler/                 # WebSocket 处理器
└── Vue/ws/src/                  # 前端代码
    ├── api/                     # API 接口
    ├── views/                   # 页面组件
    └── router/                  # 路由配置
```
