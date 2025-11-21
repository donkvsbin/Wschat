package cn.cdut.ws.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友关系实体类
 */
public class Friendship implements Serializable {

    private Long id;
    private Long userId;
    private Long friendId;
    private Status status = Status.PENDING;
    private Long requesterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联的好友信息（用于返回给前端）
    private User friend;

    public enum Status {
        PENDING(0, "待确认"),
        ACCEPTED(1, "已接受"),
        REJECTED(2, "已拒绝"),
        DELETED(3, "已删除");

        private final int code;
        private final String description;

        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Status fromCode(int code) {
            for (Status status : Status.values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知的状态码: " + code);
        }
    }

    public Friendship() {
    }

    public Friendship(Long userId, Long friendId, Long requesterId) {
        this.userId = userId;
        this.friendId = friendId;
        this.requesterId = requesterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
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

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", status=" + status +
                ", requesterId=" + requesterId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
