package cn.cdut.ws.service;

import cn.cdut.ws.mapper.FriendMapper;
import cn.cdut.ws.mapper.MessageMapper;
import cn.cdut.ws.mapper.MessageMapper.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 消息服务
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private FriendMapper friendMapper;

    /**
     * 发送消息
     */
    @Transactional
    public boolean sendMessage(Long fromUserId, Long toUserId, String content) {
        // 检查是否为好友关系
        if (!friendMapper.areFriends(fromUserId, toUserId)) {
            throw new RuntimeException("只能给好友发送消息");
        }

        int result = messageMapper.insertMessage(fromUserId, toUserId, content, 1);
        return result > 0;
    }

    /**
     * 获取聊天记录
     */
    public List<MessageDTO> getChatHistory(Long userId, Long friendId, Integer page, Integer pageSize) {
        // 检查是否为好友关系
        if (!friendMapper.areFriends(userId, friendId)) {
            return Collections.emptyList();
        }

        int offset = (page - 1) * pageSize;
        List<MessageDTO> messages = messageMapper.getMessagesBetweenUsers(userId, friendId, pageSize, offset);
        
        // 反转列表，使最新的消息在最后
        Collections.reverse(messages);
        
        return messages;
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public boolean markAsRead(Long userId, Long friendId) {
        messageMapper.markMessagesAsRead(userId, friendId);
        return true;
    }

    /**
     * 获取未读消息数
     */
    public int getUnreadCount(Long userId, Long friendId) {
        return messageMapper.getUnreadCount(userId, friendId);
    }

    /**
     * 删除消息
     */
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        // TODO: 验证消息所有权
        // 1 = 发送者删除, 2 = 接收者删除, 3 = 双方删除
        messageMapper.deleteMessage(messageId, 1);
        return true;
    }
}
