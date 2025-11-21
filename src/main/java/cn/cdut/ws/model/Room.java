package cn.cdut.ws.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 房间/频道实体，与数据库 rooms 表字段对应。
 * 支持群聊(GROUP)和私聊(DM)两种类型。
 */
public class Room implements Serializable {

    private Long id;
    private Type type;
    private String name;
    private Long ownerId;
    private Long lastMessageId;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public enum Type {
        GROUP, DM
    }

    public Room() {
    }

    public Room(Long id,
                Type type,
                String name,
                Long ownerId,
                Long lastMessageId,
                LocalDateTime lastMessageAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                LocalDateTime deletedAt) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.ownerId = ownerId;
        this.lastMessageId = lastMessageId;
        this.lastMessageAt = lastMessageAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object  o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", lastMessageId=" + lastMessageId +
                ", lastMessageAt=" + lastMessageAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
