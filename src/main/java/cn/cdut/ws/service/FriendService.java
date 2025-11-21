package cn.cdut.ws.service;

import cn.cdut.ws.mapper.FriendMapper;
import cn.cdut.ws.mapper.UserMapper;
import cn.cdut.ws.model.FriendRequest;
import cn.cdut.ws.model.Friendship;
import cn.cdut.ws.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友服务类
 */
@Service
public class FriendService {

    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    public FriendService(FriendMapper friendMapper, UserMapper userMapper) {
        this.friendMapper = friendMapper;
        this.userMapper = userMapper;
    }

    /**
     * 发送好友请求
     */
    @Transactional
    public FriendRequest sendFriendRequest(Long fromUserId, Long toUserId, String message) {
        // 1. 验证参数
        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("不能添加自己为好友");
        }

        // 2. 检查目标用户是否存在
        User toUser = userMapper.findById(toUserId);
        if (toUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        // 3. 检查是否已经是好友
        if (friendMapper.countFriendship(fromUserId, toUserId) > 0) {
            throw new IllegalArgumentException("对方已经是你的好友");
        }

        // 4. 检查是否已存在待处理的好友请求
        if (friendMapper.countPendingRequest(fromUserId, toUserId) > 0) {
            throw new IllegalArgumentException("已发送过好友请求，请等待对方处理");
        }

        // 5. 检查是否有对方发来的待处理请求（如果有，可以直接接受）
        if (friendMapper.countPendingRequest(toUserId, fromUserId) > 0) {
            throw new IllegalArgumentException("对方已向你发送好友请求，请在好友请求列表中处理");
        }

        // 6. 创建好友请求
        FriendRequest request = new FriendRequest(fromUserId, toUserId, message);
        request.setStatus(FriendRequest.Status.PENDING);
        friendMapper.insertFriendRequest(request);

        return request;
    }

    /**
     * 处理好友请求（接受或拒绝）
     */
    @Transactional
    public void handleFriendRequest(Long requestId, Long userId, boolean accept) {
        // 1. 查询好友请求
        FriendRequest request = friendMapper.findRequestById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("好友请求不存在");
        }

        // 2. 验证是否是请求的接收者
        if (!request.getToUserId().equals(userId)) {
            throw new IllegalArgumentException("无权处理此好友请求");
        }

        // 3. 检查请求状态
        if (request.getStatus() != FriendRequest.Status.PENDING) {
            throw new IllegalArgumentException("该请求已被处理");
        }

        // 4. 更新请求状态
        FriendRequest.Status newStatus = accept ? FriendRequest.Status.ACCEPTED : FriendRequest.Status.REJECTED;
        friendMapper.updateRequestStatus(requestId, newStatus.getCode());

        // 5. 如果接受，创建双向好友关系
        if (accept) {
            // 创建发送者 -> 接收者的好友关系
            Friendship friendship1 = new Friendship(request.getFromUserId(), request.getToUserId(), request.getFromUserId());
            friendship1.setStatus(Friendship.Status.ACCEPTED);
            friendMapper.insertFriendship(friendship1);

            // 创建接收者 -> 发送者的好友关系
            Friendship friendship2 = new Friendship(request.getToUserId(), request.getFromUserId(), request.getFromUserId());
            friendship2.setStatus(Friendship.Status.ACCEPTED);
            friendMapper.insertFriendship(friendship2);
        }
    }

    /**
     * 获取收到的好友请求列表
     */
    public List<FriendRequest> getReceivedRequests(Long userId) {
        List<FriendRequest> requests = friendMapper.findReceivedRequests(userId, FriendRequest.Status.PENDING.getCode());
        // 手动设置status，因为SQL中没有查询该字段
        requests.forEach(req -> req.setStatus(FriendRequest.Status.PENDING));
        return requests;
    }

    /**
     * 获取发送的好友请求列表
     */
    public List<FriendRequest> getSentRequests(Long userId) {
        List<FriendRequest> requests = friendMapper.findSentRequests(userId, FriendRequest.Status.PENDING.getCode());
        // 手动设置status
        requests.forEach(req -> req.setStatus(FriendRequest.Status.PENDING));
        return requests;
    }

    /**
     * 获取好友列表
     */
    public List<Friendship> getFriendList(Long userId) {
        return friendMapper.findFriendsByUserId(userId);
    }

    /**
     * 删除好友
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        // 删除双向好友关系
        friendMapper.deleteFriendship(userId, friendId);
        friendMapper.deleteFriendship(friendId, userId);
    }

    /**
     * 搜索用户
     */
    public List<User> searchUsers(String keyword, Long currentUserId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        return friendMapper.searchUsers(keyword.trim(), currentUserId);
    }

    /**
     * 检查是否是好友
     */
    public boolean isFriend(Long userId, Long friendId) {
        return friendMapper.countFriendship(userId, friendId) > 0;
    }
}
