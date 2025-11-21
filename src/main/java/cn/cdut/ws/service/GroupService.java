package cn.cdut.ws.service;

import cn.cdut.ws.mapper.GroupMapper;
import cn.cdut.ws.model.Group;
import cn.cdut.ws.model.GroupMember;
import cn.cdut.ws.model.GroupMessage;
import cn.cdut.ws.util.GroupIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 群组服务
 */
@Service
public class GroupService {

    private final GroupMapper groupMapper;

    public GroupService(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    /**
     * 创建群组
     */
    @Transactional
    public Group createGroup(String name, String description, Long ownerId) {
        // 生成唯一的群组ID
        String groupId;
        do {
            groupId = GroupIdGenerator.generate();
        } while (groupMapper.countById(groupId) > 0);

        // 创建群组
        Group group = new Group();
        group.setId(groupId);
        group.setName(name);
        group.setDescription(description);
        group.setOwnerId(ownerId);

        groupMapper.insertGroup(group);

        // 添加群主为成员
        groupMapper.insertMember(groupId, ownerId, "OWNER");

        return group;
    }

    /**
     * 加入群组
     */
    @Transactional
    public void joinGroup(String groupId, Long userId) {
        // 检查群组是否存在
        Group group = groupMapper.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("群组不存在");
        }

        // 检查是否已加入
        if (groupMapper.countMember(groupId, userId) > 0) {
            throw new IllegalArgumentException("已经是群成员");
        }

        // 检查人数限制
        if (group.getMemberCount() >= group.getMaxMembers()) {
            throw new IllegalArgumentException("群组已满");
        }

        // 添加成员
        groupMapper.insertMember(groupId, userId, "MEMBER");

        // 更新成员数
        groupMapper.updateMemberCount(groupId, group.getMemberCount() + 1);
    }

    /**
     * 退出群组
     */
    @Transactional
    public void leaveGroup(String groupId, Long userId) {
        Group group = groupMapper.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("群组不存在");
        }

        // 群主不能退出
        if (group.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("群主不能退出群组");
        }

        int deleted = groupMapper.deleteMember(groupId, userId);
        if (deleted > 0) {
            groupMapper.updateMemberCount(groupId, group.getMemberCount() - 1);
        }
    }

    /**
     * 获取用户的群组列表
     */
    public List<Group> getUserGroups(Long userId) {
        return groupMapper.findByUserId(userId);
    }

    /**
     * 获取群组详情
     */
    public Group getGroupById(String groupId) {
        return groupMapper.findById(groupId);
    }

    /**
     * 更新群组信息
     */
    @Transactional
    public void updateGroup(String groupId, String name, String description, Long userId) {
        Group group = groupMapper.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("群组不存在");
        }

        // 检查是否是群主
        if (!group.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("只有群主才能修改群组信息");
        }

        group.setName(name);
        group.setDescription(description);
        groupMapper.updateGroup(group);
    }

    /**
     * 获取群组成员
     */
    public List<GroupMember> getGroupMembers(String groupId) {
        return groupMapper.findMembersByGroupId(groupId);
    }

    /**
     * 发送群组消息
     */
    public GroupMessage sendMessage(String groupId, Long senderId, String content) {
        // 检查是否是群成员
        if (groupMapper.countMember(groupId, senderId) == 0) {
            throw new IllegalArgumentException("不是群成员");
        }

        GroupMessage message = new GroupMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(1); // 文本消息

        groupMapper.insertMessage(message);
        return message;
    }

    /**
     * 获取群组消息历史
     */
    public List<GroupMessage> getGroupMessages(String groupId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return groupMapper.findMessagesByGroupId(groupId, size, offset);
    }
}
