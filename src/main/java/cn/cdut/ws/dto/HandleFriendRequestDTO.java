package cn.cdut.ws.dto;

/**
 * 处理好友请求DTO
 */
public class HandleFriendRequestDTO {

    private Long requestId;
    private Boolean accept; // true-接受，false-拒绝

    public HandleFriendRequestDTO() {
    }

    public HandleFriendRequestDTO(Long requestId, Boolean accept) {
        this.requestId = requestId;
        this.accept = accept;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    @Override
    public String toString() {
        return "HandleFriendRequestDTO{" +
                "requestId=" + requestId +
                ", accept=" + accept +
                '}';
    }
}
