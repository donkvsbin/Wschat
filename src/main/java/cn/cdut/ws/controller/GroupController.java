package cn.cdut.ws.controller;

import cn.cdut.ws.dto.ApiResponse;
import cn.cdut.ws.dto.CreateGroupRequest;
import cn.cdut.ws.model.Group;
import cn.cdut.ws.model.GroupMember;
import cn.cdut.ws.model.GroupMessage;
import cn.cdut.ws.service.GroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群组控制器
 */
@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 创建群组
     */
    @PostMapping("/create")
    public ApiResponse<Group> createGroup(@RequestBody CreateGroupRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        try {
            Group group = groupService.createGroup(request.getName(), request.getDescription(), userId);
            return ApiResponse.success("创建成功", group);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 加入群组
     */
    @PostMapping("/join/{groupId}")
    public ApiResponse<Void> joinGroup(@PathVariable String groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        try {
            groupService.joinGroup(groupId, userId);
            return ApiResponse.success("加入成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("加入失败");
        }
    }

    /**
     * 退出群组
     */
    @PostMapping("/leave/{groupId}")
    public ApiResponse<Void> leaveGroup(@PathVariable String groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        try {
            groupService.leaveGroup(groupId, userId);
            return ApiResponse.success("退出成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("退出失败");
        }
    }

    /**
     * 获取用户的群组列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Group>> getGroupList(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        List<Group> groups = groupService.getUserGroups(userId);
        return ApiResponse.success(groups);
    }

    /**
     * 获取群组详情
     */
    @GetMapping("/{groupId}")
    public ApiResponse<Group> getGroupDetail(@PathVariable String groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            return ApiResponse.error("群组不存在");
        }

        return ApiResponse.success(group);
    }

    /**
     * 获取群组成员
     */
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMember>> getGroupMembers(@PathVariable String groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        List<GroupMember> members = groupService.getGroupMembers(groupId);
        return ApiResponse.success(members);
    }

    /**
     * 获取群组消息历史
     */
    @GetMapping("/{groupId}/messages")
    public ApiResponse<List<GroupMessage>> getGroupMessages(
            @PathVariable String groupId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        List<GroupMessage> messages = groupService.getGroupMessages(groupId, page, size);
        return ApiResponse.success(messages);
    }

    /**
     * 更新群组信息
     */
    @PutMapping("/{groupId}/update")
    public ApiResponse<Void> updateGroup(
            @PathVariable String groupId,
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        try {
            String name = request.get("name");
            String description = request.get("description");
            groupService.updateGroup(groupId, name, description, userId);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败");
        }
    }
}
