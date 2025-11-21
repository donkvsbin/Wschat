package cn.cdut.ws.mapper;

import cn.cdut.ws.model.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT id, username, nickname, avatar_url, password_hash, status, is_online, created_at, updated_at " +
            "FROM users WHERE username = #{username}")
    @Results({
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "passwordHash", column = "password_hash"),
        @Result(property = "isOnline", column = "is_online"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    User findByUsername(String username);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT id, username, nickname, avatar_url, password_hash, status, is_online, created_at, updated_at " +
            "FROM users WHERE id = #{id}")
    @Results({
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "passwordHash", column = "password_hash"),
        @Result(property = "isOnline", column = "is_online"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    User findById(Long id);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO users (username, nickname, avatar_url, password_hash, status, created_at, updated_at) " +
            "VALUES (#{username}, #{nickname}, #{avatarUrl}, #{passwordHash}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET nickname = #{nickname}, avatar_url = #{avatarUrl}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(User user);

    /**
     * 更新用户昵称
     */
    @Update("UPDATE users SET nickname = #{nickname}, updated_at = NOW() WHERE id = #{id}")
    int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);

    /**
     * 更新用户头像
     */
    @Update("UPDATE users SET avatar_url = #{avatarUrl}, updated_at = NOW() WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);

    /**
     * 更新用户在线状态
     */
    @Update("UPDATE users SET is_online = #{isOnline}, updated_at = NOW() WHERE id = #{id}")
    int updateOnlineStatus(@Param("id") Long id, @Param("isOnline") Boolean isOnline);
}
