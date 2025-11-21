package cn.cdut.ws.dto;

import cn.cdut.ws.model.User;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（用于返回给前端，不包含敏感信息）
 */
public class UserDTO {
    
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String status;
    private LocalDateTime createdAt;

    public UserDTO() {
    }

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.avatarUrl = user.getAvatarUrl();
            this.status = user.getStatus() != null ? user.getStatus().name() : null;
            this.createdAt = user.getCreatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
