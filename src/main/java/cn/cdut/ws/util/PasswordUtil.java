package cn.cdut.ws.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 使用 BCrypt 算法进行密码哈希，符合安全规范
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 将明文密码加密为哈希值
     * @param rawPassword 明文密码
     * @return BCrypt 哈希后的密码
     */
    public static String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 明文密码
     * @param passwordHash 数据库中存储的哈希密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String passwordHash) {
        if (rawPassword == null || passwordHash == null) {
            return false;
        }
        return encoder.matches(rawPassword, passwordHash);
    }

    /**
     * 检查密码强度（可选）
     * @param password 待检查的密码
     * @return 是否符合强度要求
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // 至少包含一个数字、一个字母
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        return hasDigit && hasLetter;
    }
}
