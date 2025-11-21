-- 为 users 表添加在线状态字段
USE ws;

ALTER TABLE users 
ADD COLUMN is_online TINYINT(1) DEFAULT 0 COMMENT '是否在线: 0-离线 1-在线' 
AFTER status;

-- 为现有用户设置默认值为离线
UPDATE users SET is_online = 0 WHERE is_online IS NULL;

-- 添加索引以优化在线用户查询
ALTER TABLE users ADD INDEX idx_is_online (is_online);
