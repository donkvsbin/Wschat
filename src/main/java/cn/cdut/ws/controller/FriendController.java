package cn.cdut.ws.controller;

import cn.cdut.ws.dto.AddFriendRequest;
import cn.cdut.ws.dto.HandleFriendRequestDTO;
import cn.cdut.ws.dto.Result;
import cn.cdut.ws.model.FriendRequest;
import cn.cdut.ws.model.Friendship;
import cn.cdut.ws.model.User;
import cn.cdut.ws.service.FriendService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友管理控制器
 */
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * 发送好友请求
     * POST /api/friend/request
     * 请求体: { "friendId": 2, "message": "你好" }
     */
    @PostMapping("/request")
    public Result<FriendRequest> sendFriendRequest(@RequestBody AddFriendRequest request,
                                                     @RequestAttribute("userId") Long userId) {
        try {
            FriendRequest friendRequest = friendService.sendFriendRequest(
                    userId, 
                    request.getFriendId(), 
                    request.getMessage()
            );
            return Result.success("好友请求已发送", friendRequest);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送好友请求失败");
        }
    }

    /**
     * 处理好友请求（接受或拒绝）
     * POST /api/friend/request/handle
     * 请求体: { "requestId": 1, "accept": true }
     */
    @PostMapping("/request/handle")
    public Result<Void> handleFriendRequest(@RequestBody HandleFriendRequestDTO dto,
                                             @RequestAttribute("userId") Long userId) {
        try {
            friendService.handleFriendRequest(dto.getRequestId(), userId, dto.getAccept());
            String message = dto.getAccept() ? "已接受好友请求" : "已拒绝好友请求";
            return Result.success(message, null);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理好友请求失败");
        }
    }

    /**
     * 获取收到的好友请求列表
     * GET /api/friend/request/received
     */
    @GetMapping("/request/received")
    public Result<List<FriendRequest>> getReceivedRequests(@RequestAttribute("userId") Long userId) {
        try {
            List<FriendRequest> requests = friendService.getReceivedRequests(userId);
            return Result.success(requests);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取好友请求列表失败");
        }
    }

    /**
     * 获取发送的好友请求列表
     * GET /api/friend/request/sent
     */
    @GetMapping("/request/sent")
    public Result<List<FriendRequest>> getSentRequests(@RequestAttribute("userId") Long userId) {
        try {
            List<FriendRequest> requests = friendService.getSentRequests(userId);
            return Result.success(requests);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取发送的请求列表失败");
        }
    }

    /**
     * 获取好友列表
     * GET /api/friend/list
     */
    @GetMapping("/list")
    public Result<List<Friendship>> getFriendList(@RequestAttribute("userId") Long userId) {
        try {
            List<Friendship> friends = friendService.getFriendList(userId);
            return Result.success(friends);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取好友列表失败");
        }
    }

    /**
     * 删除好友
     * DELETE /api/friend/{friendId}
     */
    @DeleteMapping("/{friendId}")
    public Result<Void> deleteFriend(@PathVariable Long friendId,
                                      @RequestAttribute("userId") Long userId) {
        try {
            friendService.deleteFriend(userId, friendId);
            return Result.success("删除好友成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除好友失败");
        }
    }

    /**
     * 搜索用户
     * GET /api/friend/search?keyword=xxx
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword,
                                           @RequestAttribute("userId") Long userId) {
        try {
            List<User> users = friendService.searchUsers(keyword, userId);
            return Result.success(users);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索用户失败");
        }
    }

    /**
     * 检查是否是好友
     * GET /api/friend/check/{friendId}
     */
    @GetMapping("/check/{friendId}")
    public Result<Map<String, Boolean>> checkFriend(@PathVariable Long friendId,
                                                      @RequestAttribute("userId") Long userId) {
        try {
            boolean isFriend = friendService.isFriend(userId, friendId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("isFriend", isFriend);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("检查好友关系失败");
        }
    }
}
