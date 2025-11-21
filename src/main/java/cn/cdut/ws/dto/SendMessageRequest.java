package cn.cdut.ws.dto;

/**
 * 发送消息请求
 */
public class SendMessageRequest {
    private Long toUserId;
    private String content;
    private Integer messageType = 1; // 默认文本消息

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
}
