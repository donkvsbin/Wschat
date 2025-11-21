# 好友聊天功能使用说明

## 功能概述

已实现完整的好友之间的实时聊天功能，包括：
- WebSocket 实时消息推送
- 消息持久化存储
- 聊天历史记录查询
- 消息已读状态
- 用户在线状态显示

## 后端实现

### 1. 数据库表
已创建 `private_messages` 表用于存储私聊消息：
```sql
-- 运行此SQL创建表
source create_messages_table.sql
```

### 2. 核心组件

#### MessageMapper.java
- 消息数据库操作接口
- 支持插入、查询、已读标记

#### MessageService.java
- 消息业务逻辑
- 好友关系验证
- 消息发送与查询

#### MessageController.java
- HTTP API接口
- `/api/messages/send` - 发送消息
- `/api/messages/history/{friendId}` - 获取聊天记录
- `/api/messages/read/{friendId}` - 标记已读
- `/api/messages/unread/{friendId}` - 获取未读数

#### ChatWebSocketHandler.java (已更新)
- WebSocket 消息处理器
- 支持私聊消息实时推送
- 用户在线状态广播

## 前端实现

### 1. API模块
`src/api/message.js` - 消息相关API封装

### 2. Message.vue 组件
完整的聊天界面，包含：
- 实时消息显示
- WebSocket 连接管理
- 消息发送与接收
- 聊天历史加载
- 自动滚动到底部

## 使用步骤

### 1. 数据库准备
```bash
# 连接到MySQL
mysql -u root -p

# 执行建表脚本
source C:\Users\Mello\Desktop\Homework\JavaWeb\数据库系统原理\ws\create_messages_table.sql
```

### 2. 启动后端
```bash
# 在项目根目录运行
mvn spring-boot:run
```

### 3. 启动前端
```bash
cd Vue/ws
npm install
npm run dev
```

### 4. 测试聊天功能

#### 方式1: 使用测试页面
访问 http://localhost:8081/private-chat-test.html
- 在两个浏览器窗口中分别以不同用户ID登录
- 测试消息发送与接收

#### 方式2: 使用完整应用
1. 访问 http://localhost:5173
2. 登录两个不同的账号（使用不同浏览器或无痕模式）
3. 添加好友
4. 点击好友进入聊天界面
5. 发送消息测试

## WebSocket 消息格式

### 发送私聊消息
```json
{
  "type": "PRIVATE",
  "toUserId": 2,
  "content": "你好"
}
```

### 接收私聊消息
```json
{
  "type": "PRIVATE",
  "fromUserId": 1,
  "toUserId": 2,
  "content": "你好",
  "timestamp": 1234567890
}
```

### 用户状态变化
```json
{
  "type": "USER_STATUS",
  "userId": 1,
  "isOnline": true,
  "timestamp": 1234567890
}
```

### 消息确认
```json
{
  "type": "CONFIRM",
  "success": true,
  "message": "发送成功",
  "timestamp": 1234567890
}
```

## API接口说明

### 发送消息
- **URL**: POST `/api/messages/send`
- **参数**: 
```json
{
  "toUserId": 2,
  "content": "消息内容",
  "messageType": 1
}
```

### 获取聊天记录
- **URL**: GET `/api/messages/history/{friendId}`
- **参数**: `page=1&pageSize=50`
- **返回**: 消息列表（按时间倒序）

### 标记已读
- **URL**: POST `/api/messages/read/{friendId}`

### 获取未读数
- **URL**: GET `/api/messages/unread/{friendId}`

## 注意事项

1. **WebSocket连接**: 使用 `ws://localhost:8081/ws/chat?userId={userId}`
2. **好友验证**: 只能给已添加的好友发送消息
3. **消息持久化**: 所有消息会保存到数据库
4. **实时性**: WebSocket 保证消息实时推送
5. **已读状态**: 打开聊天窗口自动标记消息已读

## 扩展功能建议

- [ ] 图片、文件发送
- [ ] 消息撤回
- [ ] 消息搜索
- [ ] 表情包支持
- [ ] 语音消息
- [ ] 消息通知
- [ ] 会话置顶
- [ ] 消息免打扰
