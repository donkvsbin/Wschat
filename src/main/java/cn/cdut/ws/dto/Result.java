package cn.cdut.ws.dto;

import java.io.Serializable;

/**
 * 通用返回结果类
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, true);
    }
    
    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, true);
    }
    
    /**
     * 成功返回（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, true);
    }
    
    /**
     * 成功返回（自定义状态码、消息和数据）
     */
    public static <T> Result<T> success(Integer code, String message, T data) {
        return new Result<>(code, message, data, true);
    }
    
    /**
     * 失败返回（默认消息）
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null, false);
    }
    
    /**
     * 失败返回（带消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, false);
    }
    
    /**
     * 失败返回（带状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, false);
    }
    
    /**
     * 失败返回（带状态码、消息和数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data, false);
    }
    
    /**
     * 自定义返回
     */
    public static <T> Result<T> build(Integer code, String message, T data, Boolean success) {
        return new Result<>(code, message, data, success);
    }
    
    // Getter and Setter
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}
