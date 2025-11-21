-- ============================================
-- 修复好友和私聊表的创建脚本
-- ============================================

USE ws;

-- 步骤1：删除可能存在的外键依赖表
DROP TABLE IF EXISTS user_blacklist;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS private_messages;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS friendships;

-- 步骤2：创建好友关系表（不带外键）
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
    INDEX idx_friend_status (friend_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友关系表';

-- 步骤3：添加外键约束
ALTER TABLE friendships 
    ADD CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_friendship_requester FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE;

-- 步骤4：创建好友请求表
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
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友请求表';

ALTER TABLE friend_requests
    ADD CONSTRAINT fk_friend_request_from FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_friend_request_to FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 步骤5：创建私聊消息表
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
    INDEX idx_to_user_unread (to_user_id, is_read, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='私聊消息表';

ALTER TABLE private_messages
    ADD CONSTRAINT fk_private_message_from FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_private_message_to FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 步骤6：创建会话列表表
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
    INDEX idx_user_time (user_id, last_message_time),
    INDEX idx_user_pinned (user_id, is_pinned, last_message_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话列表表';

ALTER TABLE conversations
    ADD CONSTRAINT fk_conversation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_conversation_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE;

-- 步骤7：创建用户黑名单表
CREATE TABLE user_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '黑名单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blocked_user_id BIGINT NOT NULL COMMENT '被屏蔽的用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
    UNIQUE KEY uk_user_blocked (user_id, blocked_user_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户黑名单表';

ALTER TABLE user_blacklist
    ADD CONSTRAINT fk_blacklist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_blacklist_blocked FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 插入测试用户（如果不存在）
INSERT IGNORE INTO users (username, nickname, password_hash, status) VALUES
('alice', 'Alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE'),
('bob', 'Bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE'),
('charlie', 'Charlie', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE');

-- 验证表创建成功
SELECT 'Tables created successfully!' AS status;
SHOW TABLES LIKE '%friend%';
SHOW TABLES LIKE '%private%';
SHOW TABLES LIKE '%conversation%';

