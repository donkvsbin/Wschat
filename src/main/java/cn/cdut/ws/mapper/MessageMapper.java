package cn.cdut.ws.mapper;

import cn.cdut.ws.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 私聊消息 Mapper
 */
@Mapper
public interface MessageMapper {

    /**
     * 插入一条消息
     */
    @Insert("INSERT INTO private_messages (from_user_id, to_user_id, content, message_type, created_at) " +
            "VALUES (#{fromUserId}, #{toUserId}, #{content}, #{messageType}, NOW())")
    int insertMessage(@Param("fromUserId") Long fromUserId,
                      @Param("toUserId") Long toUserId,
                      @Param("content") String content,
                      @Param("messageType") Integer messageType);

    /**
     * 获取两个用户之间的聊天记录（分页）
     */
    @Select("SELECT id, from_user_id as fromUserId, to_user_id as toUserId, content, " +
            "message_type as messageType, is_read as isRead, created_at as createdAt, read_at as readAt " +
            "FROM private_messages " +
            "WHERE ((from_user_id = #{userId} AND to_user_id = #{friendId}) " +
            "   OR (from_user_id = #{friendId} AND to_user_id = #{userId})) " +
            "  AND is_deleted = 0 " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<MessageDTO> getMessagesBetweenUsers(@Param("userId") Long userId,
                                             @Param("friendId") Long friendId,
                                             @Param("limit") Integer limit,
                                             @Param("offset") Integer offset);

    /**
     * 标记消息为已读
     */
    @Update("UPDATE private_messages SET is_read = 1, read_at = NOW() " +
            "WHERE to_user_id = #{userId} AND from_user_id = #{friendId} AND is_read = 0")
    int markMessagesAsRead(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(*) FROM private_messages " +
            "WHERE to_user_id = #{userId} AND from_user_id = #{friendId} AND is_read = 0 AND is_deleted = 0")
    int getUnreadCount(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 删除消息（软删除）
     */
    @Update("UPDATE private_messages SET is_deleted = #{deleteFlag} WHERE id = #{messageId}")
    int deleteMessage(@Param("messageId") Long messageId, @Param("deleteFlag") Integer deleteFlag);

    /**
     * 消息DTO
     */
    class MessageDTO {
        private Long id;
        private Long fromUserId;
        private Long toUserId;
        private String content;
        private Integer messageType;
        private Integer isRead;
        private String createdAt;
        private String readAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(Long fromUserId) {
            this.fromUserId = fromUserId;
        }

        public Long getToUserId() {
            return toUserId;
        }

        public void setToUserId(Long toUserId) {
            this.toUserId = toUserId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Integer getMessageType() {
            return messageType;
        }

        public void setMessageType(Integer messageType) {
            this.messageType = messageType;
        }

        public Integer getIsRead() {
            return isRead;
        }

        public void setIsRead(Integer isRead) {
            this.isRead = isRead;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getReadAt() {
            return readAt;
        }

        public void setReadAt(String readAt) {
            this.readAt = readAt;
        }
    }
}
