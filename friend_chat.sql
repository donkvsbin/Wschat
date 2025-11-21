-- ============================================
-- 好友添加和私聊功能数据库表
-- ============================================

USE ws;

-- 先检查并确保 users 表存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    is_online TINYINT(1) DEFAULT 0 COMMENT '是否在线: 0-离线 1-在线',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_status (status),
    INDEX idx_is_online (is_online)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1. 好友关系表
DROP TABLE IF EXISTS friendships;
CREATE TABLE friendships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待确认 1-已接受 2-已拒绝 3-已删除',
    requester_id BIGINT NOT NULL COMMENT '发起请求的用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_friend_status (friend_id, status),
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';

-- 2. 好友请求表
DROP TABLE IF EXISTS friend_requests;
CREATE TABLE friend_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '请求ID',
    from_user_id BIGINT NOT NULL COMMENT '发送请求的用户ID',
    to_user_id BIGINT NOT NULL COMMENT '接收请求的用户ID',
    message VARCHAR(200) COMMENT '附加消息',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理 1-已接受 2-已拒绝 3-已过期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    INDEX idx_to_user_status (to_user_id, status),
    INDEX idx_from_user (from_user_id),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_friend_request_from FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_request_to FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友请求表';

-- 3. 私聊消息表
DROP TABLE IF EXISTS private_messages;
CREATE TABLE private_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    from_user_id BIGINT NOT NULL COMMENT '发送者ID',
    to_user_id BIGINT NOT NULL COMMENT '接收者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type TINYINT DEFAULT 1 COMMENT '消息类型: 1-文本 2-图片 3-文件 4-语音',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-正常 1-发送者删除 2-接收者删除 3-双方删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    read_at DATETIME NULL COMMENT '阅读时间',
    INDEX idx_from_to_time (from_user_id, to_user_id, created_at),
    INDEX idx_to_from_time (to_user_id, from_user_id, created_at),
    INDEX idx_to_user_unread (to_user_id, is_read, created_at),
    CONSTRAINT fk_private_message_from FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_private_message_to FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私聊消息表';

-- 4. 会话列表表
DROP TABLE IF EXISTS conversations;
CREATE TABLE conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友ID',
    last_message_id BIGINT COMMENT '最后一条消息ID',
    last_message_time DATETIME COMMENT '最后消息时间',
    unread_count INT DEFAULT 0 COMMENT '未读消息数',
    is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶',
    is_muted TINYINT DEFAULT 0 COMMENT '是否免打扰',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    INDEX idx_user_time (user_id, last_message_time DESC),
    INDEX idx_user_pinned (user_id, is_pinned, last_message_time DESC),
    CONSTRAINT fk_conversation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conversation_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话列表表';

-- 5. 用户黑名单表（可选）
DROP TABLE IF EXISTS user_blacklist;
CREATE TABLE user_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '黑名单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blocked_user_id BIGINT NOT NULL COMMENT '被屏蔽的用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
    UNIQUE KEY uk_user_blocked (user_id, blocked_user_id),
    INDEX idx_user (user_id),
    CONSTRAINT fk_blacklist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_blacklist_blocked FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户黑名单表';

-- ============================================
-- 插入测试数据
-- ============================================

-- 插入更多测试用户
INSERT IGNORE INTO users (username, nickname, password_hash, status) VALUES
('alice', 'Alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE'),
('bob', 'Bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE'),
('charlie', 'Charlie', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE');

-- 插入好友请求示例（假设用户ID: test=1, alice=2, bob=3, charlie=4）
-- INSERT INTO friend_requests (from_user_id, to_user_id, message, status) VALUES
-- (1, 2, '你好，我是测试用户', 0),
-- (1, 3, '加个好友吧', 1);

-- 插入好友关系示例（已接受的好友关系需要双向插入）
-- INSERT INTO friendships (user_id, friend_id, status, requester_id) VALUES
-- (1, 3, 1, 1),
-- (3, 1, 1, 1);

-- 插入私聊消息示例
-- INSERT INTO private_messages (from_user_id, to_user_id, content, message_type) VALUES
-- (1, 3, '你好，Bob！', 1),
-- (3, 1, '你好，Test！', 1);

-- 插入会话列表示例
-- INSERT INTO conversations (user_id, friend_id, last_message_time, unread_count) VALUES
-- (1, 3, NOW(), 0),
-- (3, 1, NOW(), 1);

