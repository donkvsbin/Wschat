package cn.cdut.ws.controller;

import cn.cdut.ws.dto.ApiResponse;
import cn.cdut.ws.dto.LoginRequest;
import cn.cdut.ws.dto.RegisterRequest;
import cn.cdut.ws.model.User;
import cn.cdut.ws.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            return ApiResponse.success("注册成功", data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = userService.login(request);
            
            // 将用户信息存入 Session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("user", user);
            
            // 设置用户为在线状态
            userService.updateOnlineStatus(user.getId(), true);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());  // 前端需要的字段
            data.put("userId", user.getId());  // 兼容字段
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("avatarUrl", user.getAvatarUrl());
            data.put("status", user.getStatus());
            data.put("isOnline", true);
            
            return ApiResponse.success("登录成功", data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            // 设置用户为离线状态
            userService.updateOnlineStatus(userId, false);
        }
        session.invalidate();
        return ApiResponse.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ApiResponse<Map<String, Object>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("status", user.getStatus());
        data.put("isOnline", user.getIsOnline());
        
        return ApiResponse.success(data);
    }
}
