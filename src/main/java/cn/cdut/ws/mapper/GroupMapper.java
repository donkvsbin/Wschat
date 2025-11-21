package cn.cdut.ws.mapper;

import cn.cdut.ws.model.Group;
import cn.cdut.ws.model.GroupMember;
import cn.cdut.ws.model.GroupMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 群组 Mapper
 */
@Mapper
public interface GroupMapper {

    /**
     * 插入群组
     */
    @Insert("INSERT INTO `groups` (id, name, description, owner_id, member_count, created_at, updated_at) " +
            "VALUES (#{id}, #{name}, #{description}, #{ownerId}, 1, NOW(), NOW())")
    int insertGroup(Group group);

    /**
     * 根据ID查询群组
     */
    @Select("SELECT id, name, description, avatar_url as avatarUrl, owner_id as ownerId, " +
            "member_count as memberCount, max_members as maxMembers, created_at as createdAt, updated_at as updatedAt " +
            "FROM `groups` WHERE id = #{id}")
    Group findById(String id);

    /**
     * 检查群组ID是否存在
     */
    @Select("SELECT COUNT(*) FROM `groups` WHERE id = #{id}")
    int countById(String id);

    /**
     * 获取用户加入的所有群组
     */
    @Select("SELECT g.id, g.name, g.description, g.avatar_url as avatarUrl, g.owner_id as ownerId, " +
            "g.member_count as memberCount, g.max_members as maxMembers, g.created_at as createdAt, g.updated_at as updatedAt " +
            "FROM `groups` g " +
            "INNER JOIN group_members gm ON g.id = gm.group_id " +
            "WHERE gm.user_id = #{userId} " +
            "ORDER BY gm.joined_at DESC")
    List<Group> findByUserId(Long userId);

    /**
     * 更新群组成员数
     */
    @Update("UPDATE `groups` SET member_count = #{count}, updated_at = NOW() WHERE id = #{groupId}")
    int updateMemberCount(@Param("groupId") String groupId, @Param("count") Integer count);

    /**
     * 更新群组信息
     */
    @Update("UPDATE `groups` SET name = #{name}, description = #{description}, updated_at = NOW() WHERE id = #{id}")
    int updateGroup(Group group);

    /**
     * 插入群组成员
     */
    @Insert("INSERT INTO group_members (group_id, user_id, role, joined_at) " +
            "VALUES (#{groupId}, #{userId}, #{role}, NOW())")
    int insertMember(@Param("groupId") String groupId, @Param("userId") Long userId, @Param("role") String role);

    /**
     * 检查用户是否在群组中
     */
    @Select("SELECT COUNT(*) FROM group_members WHERE group_id = #{groupId} AND user_id = #{userId}")
    int countMember(@Param("groupId") String groupId, @Param("userId") Long userId);

    /**
     * 获取群组成员列表
     */
    @Select("SELECT gm.id, gm.group_id as groupId, gm.user_id as userId, gm.role, gm.nickname, gm.joined_at as joinedAt, " +
            "u.username, u.nickname as userNickname, u.avatar_url as avatarUrl, u.is_online as isOnline " +
            "FROM group_members gm " +
            "INNER JOIN users u ON gm.user_id = u.id " +
            "WHERE gm.group_id = #{groupId} " +
            "ORDER BY gm.joined_at ASC")
    List<GroupMember> findMembersByGroupId(String groupId);

    /**
     * 插入群组消息
     */
    @Insert("INSERT INTO group_messages (group_id, sender_id, content, message_type, created_at) " +
            "VALUES (#{groupId}, #{senderId}, #{content}, #{messageType}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMessage(GroupMessage message);

    /**
     * 获取群组消息历史
     */
    @Select("SELECT gm.id, gm.group_id as groupId, gm.sender_id as senderId, gm.content, " +
            "gm.message_type as messageType, gm.created_at as createdAt, " +
            "u.username as senderUsername, u.nickname as senderNickname, u.avatar_url as senderAvatarUrl " +
            "FROM group_messages gm " +
            "LEFT JOIN users u ON gm.sender_id = u.id " +
            "WHERE gm.group_id = #{groupId} " +
            "ORDER BY gm.created_at ASC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<GroupMessage> findMessagesByGroupId(@Param("groupId") String groupId, 
                                             @Param("limit") Integer limit, 
                                             @Param("offset") Integer offset);

    /**
     * 退出群组
     */
    @Delete("DELETE FROM group_members WHERE group_id = #{groupId} AND user_id = #{userId}")
    int deleteMember(@Param("groupId") String groupId, @Param("userId") Long userId);
}
