package cn.cdut.ws.mapper;

import cn.cdut.ws.model.FriendRequest;
import cn.cdut.ws.model.Friendship;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 好友关系Mapper接口
 */
@Mapper
public interface FriendMapper {

    // ==================== 好友请求相关 ====================

    /**
     * 插入好友请求
     */
    @Insert("INSERT INTO friend_requests (from_user_id, to_user_id, message, status) " +
            "VALUES (#{fromUserId}, #{toUserId}, #{message}, #{status.code})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFriendRequest(FriendRequest request);

    /**
     * 根据ID查询好友请求
     */
    @Select("SELECT id, from_user_id, to_user_id, message, status, created_at, updated_at FROM friend_requests WHERE id = #{id}")
    FriendRequest findRequestById(Long id);

    /**
     * 查询用户收到的好友请求
     */
    @Select("SELECT fr.id, fr.from_user_id, fr.to_user_id, fr.message, fr.created_at, fr.updated_at, " +
            "u.id as from_id, u.username as from_username, u.nickname as from_nickname, u.avatar_url as from_avatar_url " +
            "FROM friend_requests fr " +
            "LEFT JOIN users u ON fr.from_user_id = u.id " +
            "WHERE fr.to_user_id = #{userId} AND fr.status = #{status} " +
            "ORDER BY fr.created_at DESC")
    @Results(id = "friendRequestWithUserMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "fromUserId", column = "from_user_id"),
            @Result(property = "toUserId", column = "to_user_id"),
            @Result(property = "message", column = "message"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "fromUser.id", column = "from_id"),
            @Result(property = "fromUser.username", column = "from_username"),
            @Result(property = "fromUser.nickname", column = "from_nickname"),
            @Result(property = "fromUser.avatarUrl", column = "from_avatar_url")
    })
    List<FriendRequest> findReceivedRequests(@Param("userId") Long userId, @Param("status") int status);

    /**
     * 查询用户发送的好友请求
     */
    @Select("SELECT fr.id, fr.from_user_id, fr.to_user_id, fr.message, fr.created_at, fr.updated_at, " +
            "u.id as to_id, u.username as to_username, u.nickname as to_nickname, u.avatar_url as to_avatar_url " +
            "FROM friend_requests fr " +
            "LEFT JOIN users u ON fr.to_user_id = u.id " +
            "WHERE fr.from_user_id = #{userId} AND fr.status = #{status} " +
            "ORDER BY fr.created_at DESC")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "fromUserId", column = "from_user_id"),
            @Result(property = "toUserId", column = "to_user_id"),
            @Result(property = "message", column = "message"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "toUser.id", column = "to_id"),
            @Result(property = "toUser.username", column = "to_username"),
            @Result(property = "toUser.nickname", column = "to_nickname"),
            @Result(property = "toUser.avatarUrl", column = "to_avatar_url")
    })
    List<FriendRequest> findSentRequests(@Param("userId") Long userId, @Param("status") int status);

    /**
     * 检查是否存在待处理的好友请求
     */
    @Select("SELECT COUNT(*) FROM friend_requests " +
            "WHERE from_user_id = #{fromUserId} AND to_user_id = #{toUserId} AND status = 0")
    int countPendingRequest(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    /**
     * 更新好友请求状态
     */
    @Update("UPDATE friend_requests SET status = #{status} WHERE id = #{id}")
    int updateRequestStatus(@Param("id") Long id, @Param("status") int status);

    // ==================== 好友关系相关 ====================

    /**
     * 插入好友关系
     */
    @Insert("INSERT INTO friendships (user_id, friend_id, status, requester_id) " +
            "VALUES (#{userId}, #{friendId}, #{status.code}, #{requesterId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFriendship(Friendship friendship);

    /**
     * 查询用户的好友列表
     */
    @Select("SELECT f.*, " +
            "u.id as friend_id, u.username as friend_username, u.nickname as friend_nickname, " +
            "u.avatar_url as friend_avatar_url, u.status as friend_status, u.is_online as friend_is_online " +
            "FROM friendships f " +
            "LEFT JOIN users u ON f.friend_id = u.id " +
            "WHERE f.user_id = #{userId} AND f.status = 1 " +
            "ORDER BY f.created_at DESC")
    @Results(id = "friendshipWithUserMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "friendId", column = "friend_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "requesterId", column = "requester_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "friend.id", column = "friend_id"),
            @Result(property = "friend.username", column = "friend_username"),
            @Result(property = "friend.nickname", column = "friend_nickname"),
            @Result(property = "friend.avatarUrl", column = "friend_avatar_url"),
            @Result(property = "friend.status", column = "friend_status", javaType = cn.cdut.ws.model.User.Status.class),
            @Result(property = "friend.isOnline", column = "friend_is_online")
    })
    List<Friendship> findFriendsByUserId(Long userId);

    /**
     * 检查是否是好友关系
     */
    @Select("SELECT COUNT(*) FROM friendships " +
            "WHERE user_id = #{userId} AND friend_id = #{friendId} AND status = 1")
    int countFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 检查两个用户是否为好友
     */
    default boolean areFriends(Long userId, Long friendId) {
        return countFriendship(userId, friendId) > 0;
    }

    /**
     * 删除好友关系
     */
    @Delete("DELETE FROM friendships WHERE user_id = #{userId} AND friend_id = #{friendId}")
    int deleteFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 搜索用户（通过用户名或昵称）
     */
    @Select("SELECT * FROM users " +
            "WHERE (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND id != #{currentUserId} " +
            "LIMIT 20")
    List<cn.cdut.ws.model.User> searchUsers(@Param("keyword") String keyword, @Param("currentUserId") Long currentUserId);
}
