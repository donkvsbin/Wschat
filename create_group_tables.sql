-- ============================================
-- 群聊功能数据库表
-- ============================================

USE ws;

-- 1. 群组表（先创建不带外键的表结构）
CREATE TABLE IF NOT EXISTS `groups` (
    id VARCHAR(20) PRIMARY KEY COMMENT '群组ID（随机字母数字）',
    name VARCHAR(100) NOT NULL COMMENT '群组名称',
    description VARCHAR(500) COMMENT '群组描述',
    avatar_url VARCHAR(500) COMMENT '群组头像',
    owner_id BIGINT UNSIGNED NOT NULL COMMENT '群主ID',
    member_count INT DEFAULT 1 COMMENT '成员数量',
    max_members INT DEFAULT 100 COMMENT '最大成员数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_owner (owner_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组表';

-- 2. 群组成员表（先创建不带外键的表结构）
CREATE TABLE IF NOT EXISTS group_members (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '成员ID',
    group_id VARCHAR(20) NOT NULL COMMENT '群组ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    role ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER' COMMENT '角色：群主、管理员、普通成员',
    nickname VARCHAR(100) COMMENT '群内昵称',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE KEY uk_group_user (group_id, user_id),
    INDEX idx_user (user_id),
    INDEX idx_group (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组成员表';

-- 3. 群组消息表（先创建不带外键的表结构）
CREATE TABLE IF NOT EXISTS group_messages (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    group_id VARCHAR(20) NOT NULL COMMENT '群组ID',
    sender_id BIGINT UNSIGNED NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type TINYINT DEFAULT 1 COMMENT '消息类型: 1-文本 2-图片 3-文件 4-系统消息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    INDEX idx_group_time (group_id, created_at DESC),
    INDEX idx_sender (sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组消息表';

-- 4. 添加外键约束（在表创建完成后）
ALTER TABLE `groups` 
    ADD CONSTRAINT fk_group_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE group_members 
    ADD CONSTRAINT fk_group_member_group FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_group_member_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE group_messages 
    ADD CONSTRAINT fk_group_message_group FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_group_message_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;

-- 插入测试数据
-- INSERT INTO `groups` (id, name, description, owner_id) VALUES
-- ('ABC123', '测试群组', '这是一个测试群组', 1);

-- INSERT INTO group_members (group_id, user_id, role) VALUES
-- ('ABC123', 1, 'OWNER');
