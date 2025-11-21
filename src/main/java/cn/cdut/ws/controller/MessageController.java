package cn.cdut.ws.controller;

import cn.cdut.ws.dto.ApiResponse;
import cn.cdut.ws.dto.SendMessageRequest;
import cn.cdut.ws.mapper.MessageMapper.MessageDTO;
import cn.cdut.ws.model.User;
import cn.cdut.ws.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ApiResponse<Boolean> sendMessage(@RequestBody SendMessageRequest request, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }

        try {
            boolean success = messageService.sendMessage(currentUser.getId(), request.getToUserId(), request.getContent());
            return success ? ApiResponse.success("发送成功", true) : ApiResponse.error("发送失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/history/{friendId}")
    public ApiResponse<List<MessageDTO>> getChatHistory(
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }

        List<MessageDTO> messages = messageService.getChatHistory(currentUser.getId(), friendId, page, pageSize);
        return ApiResponse.success(messages);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{friendId}")
    public ApiResponse<Boolean> markAsRead(@PathVariable Long friendId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }

        boolean success = messageService.markAsRead(currentUser.getId(), friendId);
        return ApiResponse.success("标记成功", success);
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread/{friendId}")
    public ApiResponse<Integer> getUnreadCount(@PathVariable Long friendId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }

        int count = messageService.getUnreadCount(currentUser.getId(), friendId);
        return ApiResponse.success(count);
    }
}
