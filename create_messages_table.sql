-- 确保private_messages表存在
USE ws;

-- 如果表不存在则创建
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

-- 插入测试消息（可选）
-- 假设用户ID: test=1, alice=2, bob=3
-- INSERT INTO private_messages (from_user_id, to_user_id, content) VALUES
-- (1, 2, '你好，Alice！这是测试消息'),
-- (2, 1, '你好，收到！');

