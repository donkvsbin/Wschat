package cn.cdut.ws.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 消息实体，与数据库 messages 表字段对应。
 * seq 字段为房间内单调递增序号，用于严格排序与分页。
 */
public class Message implements Serializable {

    private Long id;
    private Long roomId;
    private Long senderId;
    private Long seq;
    private ContentType contentType = ContentType.TEXT;
    private String content;
    private LocalDateTime editedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;

    public enum ContentType {
        TEXT, IMAGE, FILE, SYSTEM
    }

    public Message() {
    }

    public Message(Long id,
                   Long roomId,
                   Long senderId,
                   Long seq,
                   ContentType contentType,
                   String content,
                   LocalDateTime editedAt,
                   LocalDateTime deletedAt,
                   LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.seq = seq;
        this.contentType = contentType;
        this.content = content;
        this.editedAt = editedAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", senderId=" + senderId +
                ", seq=" + seq +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                ", editedAt=" + editedAt +
                ", deletedAt=" + deletedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
