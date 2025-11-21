package cn.cdut.ws.service;

import cn.cdut.ws.dto.LoginRequest;
import cn.cdut.ws.dto.RegisterRequest;
import cn.cdut.ws.mapper.UserMapper;
import cn.cdut.ws.model.User;
import cn.cdut.ws.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务逻辑层
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 2. 验证密码强度
        if (!PasswordUtil.isStrongPassword(request.getPassword())) {
            throw new IllegalArgumentException("密码必须至少8位，包含字母和数字");
        }

        // 3. 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPasswordHash(PasswordUtil.encryptPassword(request.getPassword()));
        user.setStatus(User.Status.ACTIVE);

        // 4. 保存到数据库
        userMapper.insert(user);

        return user;
    }

    /**
     * 用户登录
     */
    public User login(LoginRequest request) {
        // 1. 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == User.Status.BANNED) {
            throw new IllegalArgumentException("账号已被封禁");
        }
        if (user.getStatus() == User.Status.INACTIVE) {
            throw new IllegalArgumentException("账号未激活");
        }

        return user;
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 更新用户在线状态
     */
    @Transactional
    public void updateOnlineStatus(Long userId, Boolean isOnline) {
        userMapper.updateOnlineStatus(userId, isOnline);
    }

    /**
     * 更新用户昵称
     */
    @Transactional
    public int updateNickname(Long userId, String nickname) {
        System.out.println("[UserService] 更新用户昵称 - userId: " + userId + ", nickname: " + nickname);
        int result = userMapper.updateNickname(userId, nickname);
        System.out.println("[UserService] 更新结果: " + result);
        return result;
    }

    /**
     * 更新用户头像
     */
    @Transactional
    public int updateAvatar(Long userId, String avatarUrl) {
        System.out.println("[UserService] 更新头像 - userId: " + userId + ", avatarUrl: " + avatarUrl);
        int result = userMapper.updateAvatar(userId, avatarUrl);
        System.out.println("[UserService] 更新结果: " + result);
        return result;
    }
}
