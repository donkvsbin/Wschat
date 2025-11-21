package cn.cdut.ws.controller;

import cn.cdut.ws.dto.ApiResponse;
import cn.cdut.ws.dto.UpdateUserRequest;
import cn.cdut.ws.model.User;
import cn.cdut.ws.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_DIR = "file/userAvatar/";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("[上传头像] 用户ID: " + userId);
        
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }

        try {
            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println("[上传头像] 创建目录: " + uploadDir.getAbsolutePath());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = userId + "_" + UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Files.write(filePath, file.getBytes());
            System.out.println("[上传头像] 文件保存成功: " + filePath.toAbsolutePath());

            // 构建访问URL
            String avatarUrl = "/file/userAvatar/" + filename;
            System.out.println("[上传头像] 头像URL: " + avatarUrl);
            
            // 更新数据库
            int updated = userService.updateAvatar(userId, avatarUrl);
            System.out.println("[上传头像] 数据库更新结果: " + updated);

            Map<String, String> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            
            return ApiResponse.success("上传成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户昵称
     */
    @PostMapping("/update-nickname")
    public ApiResponse<Void> updateNickname(
            @RequestBody UpdateUserRequest request,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("[更新昵称] 用户ID: " + userId);
        System.out.println("[更新昵称] 新昵称: " + request.getNickname());
        
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        try {
            int updated = userService.updateNickname(userId, request.getNickname());
            System.out.println("[更新昵称] 数据库更新结果: " + updated);
            
            return ApiResponse.success("昵称更新成功", null);
        } catch (IllegalArgumentException e) {
            System.err.println("[更新昵称] 失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getUserInfo(HttpSession session) {
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
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("isOnline", user.getIsOnline());

        return ApiResponse.success(data);
    }
}
