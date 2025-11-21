package cn.cdut.ws.handler;

import cn.cdut.ws.model.GroupMessage;
import cn.cdut.ws.model.User;
import cn.cdut.ws.service.GroupService;
import cn.cdut.ws.service.MessageService;
import cn.cdut.ws.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 * 处理客户端的连接、断开和消息收发
 */
@Component
public class  ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储所有在线用户的 WebSocket 会话，key 为用户ID，value 为 Session
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // JSON 序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private GroupService groupService;

    /**
     * 连接建立后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            session.close(CloseStatus.BAD_DATA.withReason("无效的用户ID"));
            return;
        }
        
        sessions.put(userId, session);
        System.out.println("用户 " + userId + " 已连接，当前在线人数：" + sessions.size());
        
        // 更新数据库中的在线状态
        userService.updateOnlineStatus(userId, true);
        
        // 通知用户上线
        notifyUserStatus(userId, true);
    }

    /**
     * 接收到消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            return;
        }
        
        String payload = message.getPayload();
        System.out.println("收到用户 " + userId + " 的消息：" + payload);
        
        try {
            // 解析消息（假设格式为 JSON）
            Map<String, Object> msg = objectMapper.readValue(payload, Map.class);
            String type = (String) msg.get("type");
            
            if ("PRIVATE".equals(type)) {
                // 私聊消息
                Long toUserId = Long.valueOf(msg.get("toUserId").toString());
                String content = (String) msg.get("content");
                
                // 保存消息到数据库
                messageService.sendMessage(userId, toUserId, content);
                
                // 实时发送给接收者
                sendPrivateMessage(userId, toUserId, content);
                
                // 发送确认给发送者
                sendMessageConfirm(session, true, "发送成功");
            } else if ("GROUP".equals(type)) {
                // 群聊消息
                String groupId = (String) msg.get("groupId");
                String content = (String) msg.get("content");
                
                // 保存消息到数据库
                GroupMessage groupMessage = groupService.sendMessage(groupId, userId, content);
                
                // 广播给群成员
                broadcastGroupMessage(groupId, userId, content, groupMessage.getId());
                
                // 发送确认给发送者
                sendMessageConfirm(session, true, "发送成功");
            } else {
                session.sendMessage(new TextMessage("{\"error\":\"未知消息类型\"}"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageConfirm(session, false, "发送失败: " + e.getMessage());
        }
    }

    /**
     * 连接关闭后调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("用户 " + userId + " 已断开，当前在线人数：" + sessions.size());
            
            // 更新数据库中的在线状态
            userService.updateOnlineStatus(userId, false);
            
            // 通知用户下线
            notifyUserStatus(userId, false);
        }
    }

    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = getUserId(session);
        System.err.println("用户 " + userId + " 连接异常：" + exception.getMessage());
        session.close();
    }

    /**
     * 发送私聊消息
     */
    private void sendPrivateMessage(Long fromUserId, Long toUserId, String content) {
        System.out.println("[sendPrivateMessage] 从用户 " + fromUserId + " 发送消息给用户 " + toUserId);
        WebSocketSession toSession = sessions.get(toUserId);
        
        if (toSession == null) {
            System.out.println("[sendPrivateMessage] 用户 " + toUserId + " 的session不存在");
            System.out.println("[sendPrivateMessage] 当前在线用户: " + sessions.keySet());
            return;
        }
        
        if (!toSession.isOpen()) {
            System.out.println("[sendPrivateMessage] 用户 " + toUserId + " 的session已关闭");
            return;
        }
        
        try {
            Map<String, Object> msg = Map.of(
                "type", "PRIVATE",
                "fromUserId", fromUserId,
                "toUserId", toUserId,
                "content", content,
                "timestamp", System.currentTimeMillis()
            );
            String messageJson = objectMapper.writeValueAsString(msg);
            System.out.println("[sendPrivateMessage] 发送消息: " + messageJson);
            sendMessageSafely(toSession, messageJson);
            System.out.println("[sendPrivateMessage] 消息发送成功");
        } catch (Exception e) {
            System.err.println("[sendPrivateMessage] 发送私聊消息失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送消息确认
     */
    private void sendMessageConfirm(WebSocketSession session, boolean success, String message) {
        try {
            Map<String, Object> confirm = Map.of(
                "type", "CONFIRM",
                "success", success,
                "message", message,
                "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(confirm);
            sendMessageSafely(session, json);
        } catch (Exception e) {
            System.err.println("发送确认消息失败：" + e.getMessage());
        }
    }

    /**
     * 广播群聊消息
     */
    private void broadcastGroupMessage(String groupId, Long senderId, String content, Long messageId) {
        System.out.println("[群聊消息] 群组 " + groupId + " 中的用户 " + senderId + " 发送消息");
        
        try {
            // 获取发送者信息
            var sender = userService.getUserById(senderId);
            
            // 获取群成员
            var members = groupService.getGroupMembers(groupId);
            
            Map<String, Object> msg = Map.of(
                "type", "GROUP",
                "groupId", groupId,
                "senderId", senderId,
                "senderUsername", sender != null ? sender.getUsername() : "unknown",
                "senderNickname", sender != null && sender.getNickname() != null ? sender.getNickname() : (sender != null ? sender.getUsername() : "unknown"),
                "senderAvatarUrl", sender != null && sender.getAvatarUrl() != null ? sender.getAvatarUrl() : "",
                "content", content,
                "messageId", messageId,
                "timestamp", System.currentTimeMillis()
            );
            String messageJson = objectMapper.writeValueAsString(msg);
            
            // 发送给所有在线的群成员
            for (var member : members) {
                WebSocketSession memberSession = sessions.get(member.getUserId());
                if (memberSession != null && memberSession.isOpen()) {
                    sendMessageSafely(memberSession, messageJson);
                    System.out.println("[群聊消息] 发送给用户 " + member.getUserId());
                }
            }
        } catch (Exception e) {
            System.err.println("[群聊消息] 广播失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 通知用户上线/下线状态
     */
    private void notifyUserStatus(Long userId, boolean isOnline) {
        try {
            Map<String, Object> statusMsg = Map.of(
                "type", "USER_STATUS",
                "userId", userId,
                "isOnline", isOnline,
                "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(statusMsg);
            
            // 通知所有在线用户该用户的状态变化
            sessions.values().forEach(session -> {
                sendMessageSafely(session, json);
            });
        } catch (Exception e) {
            System.err.println("发送状态通知失败：" + e.getMessage());
        }
    }

    /**
     * 线程安全地发送消息
     */
    private void sendMessageSafely(WebSocketSession session, String message) {
        if (session == null || !session.isOpen()) {
            return;
        }
        
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("发送消息失败：" + e.getMessage());
            }
        }
    }

    /**
     * 从 WebSocket Session 中获取用户ID
     */
    private Long getUserId(WebSocketSession session) {
        try {
            // 从 URL 参数获取 userId
            // 例如：ws://localhost:8081/ws/chat?userId=123
            String query = session.getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                String userIdStr = query.split("userId=")[1].split("&")[0];
                return Long.parseLong(userIdStr);
            }
            
            // 从 session 属性获取（如果有的话）
            Object userIdObj = session.getAttributes().get("userId");
            if (userIdObj != null) {
                return Long.parseLong(userIdObj.toString());
            }
        } catch (Exception e) {
            System.err.println("获取用户ID失败：" + e.getMessage());
        }
        return null;
    }
}
