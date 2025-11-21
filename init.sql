-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS ws CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ws;

-- 用户表
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

-- 房间表
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
    type ENUM('GROUP', 'DM') NOT NULL COMMENT '房间类型：群聊/私聊',
    name VARCHAR(100) COMMENT '房间名称',
    owner_id BIGINT COMMENT '创建者ID',
    last_message_id BIGINT COMMENT '最后一条消息ID',
    last_message_at DATETIME COMMENT '最后消息时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间',
    INDEX idx_type (type),
    INDEX idx_owner_id (owner_id),
    INDEX idx_last_message_at (last_message_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    seq BIGINT NOT NULL COMMENT '房间内消息序列号',
    content_type ENUM('TEXT', 'IMAGE', 'FILE', 'SYSTEM') DEFAULT 'TEXT' COMMENT '内容类型',
    content TEXT NOT NULL COMMENT '消息内容',
    edited_at DATETIME COMMENT '编辑时间',
    deleted_at DATETIME COMMENT '删除时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_room_seq (room_id, seq),
    INDEX idx_sender_id (sender_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 私聊关系表（dm_link）
CREATE TABLE IF NOT EXISTS dm_link (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    room_id BIGINT NOT NULL COMMENT '私聊房间ID',
    user_id_1 BIGINT NOT NULL COMMENT '用户1ID（较小的ID）',
    user_id_2 BIGINT NOT NULL COMMENT '用户2ID（较大的ID）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_users (user_id_1, user_id_2),
    INDEX idx_room_id (room_id),
    INDEX idx_user_id_1 (user_id_1),
    INDEX idx_user_id_2 (user_id_2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私聊关系表';

-- 房间成员表
CREATE TABLE IF NOT EXISTS room_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成员ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER' COMMENT '角色',
    last_read_seq BIGINT DEFAULT 0 COMMENT '最后已读消息序列号',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    left_at DATETIME COMMENT '离开时间',
    UNIQUE KEY uk_room_user (room_id, user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间成员表';

-- 好友关系表
CREATE TABLE IF NOT EXISTS friendships (
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

-- 好友请求表
CREATE TABLE IF NOT EXISTS friend_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '请求ID',
    from_user_id BIGINT NOT NULL COMMENT '发送请求的用户ID',
    to_user_id BIGINT NOT NULL COMMENT '接收请求的用户ID',
    message VARCHAR(200) COMMENT '附加消息',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理 1-已接受 2-已拒绝 3-已过期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    INDEX idx_to_user_status (to_user_id, status),
    INDEX idx_from_user (from_user_id),
    CONSTRAINT fk_friend_request_from FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_request_to FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友请求表';

-- 私聊消息表
CREATE TABLE IF NOT EXISTS private_messages (
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

-- 会话列表表
CREATE TABLE IF NOT EXISTS conversations (
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

-- 用户黑名单表
CREATE TABLE IF NOT EXISTS user_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '黑名单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blocked_user_id BIGINT NOT NULL COMMENT '被屏蔽的用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
    UNIQUE KEY uk_user_blocked (user_id, blocked_user_id),
    INDEX idx_user (user_id),
    CONSTRAINT fk_blacklist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_blacklist_blocked FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户黑名单表';

-- 插入测试数据（可选）
-- 测试用户：username=test, password=test12345
INSERT INTO users (username, nickname, password_hash, status) VALUES
('test', '测试用户', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE');
