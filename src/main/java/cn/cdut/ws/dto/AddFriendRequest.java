package cn.cdut.ws.dto;

/**
 * 添加好友请求DTO
 */
public class AddFriendRequest {

    private Long friendId;
    private String message;

    public AddFriendRequest() {
    }

    public AddFriendRequest(Long friendId, String message) {
        this.friendId = friendId;
        this.message = message;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AddFriendRequest{" +
                "friendId=" + friendId +
                ", message='" + message + '\'' +
                '}';
    }
}
