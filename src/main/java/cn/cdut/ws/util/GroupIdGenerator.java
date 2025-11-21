package cn.cdut.ws.util;

import java.security.SecureRandom;

/**
 * 群组ID生成器
 * 生成随机的字母数字组合，长度为6-8位
 */
public class GroupIdGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成唯一的群组ID
     * @return 6位随机字母数字组合
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
