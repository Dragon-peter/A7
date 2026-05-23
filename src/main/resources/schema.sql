-- 确保resource表有正确的结构
-- 如果表不存在则创建，如果存在则添加缺失的字段

-- 创建resource表（如果不存在）
CREATE TABLE IF NOT EXISTS resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    title VARCHAR(255) NOT NULL COMMENT '资源标题',
    type VARCHAR(50) NOT NULL COMMENT '资源类型',
    url VARCHAR(500) COMMENT '资源链接',
    chapter_id BIGINT COMMENT '章节ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    size BIGINT COMMENT '文件大小',
    duration INT COMMENT '时长（秒）',
    file_name VARCHAR(255) COMMENT '本地文件名',
    original_name VARCHAR(255) COMMENT '原始文件名',
    description TEXT COMMENT '资源描述',
    uploader_id BIGINT COMMENT '上传者ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- 添加缺失的字段（如果不存在）
-- 注意：MySQL不支持IF NOT EXISTS for ALTER TABLE ADD COLUMN，所以我们需要用存储过程

DELIMITER $$

CREATE PROCEDURE AddColumnIfNotExists()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;
    
    -- 添加file_name字段
    ALTER TABLE resource ADD COLUMN file_name VARCHAR(255) COMMENT '本地文件名';
    
    -- 添加original_name字段
    ALTER TABLE resource ADD COLUMN original_name VARCHAR(255) COMMENT '原始文件名';
    
    -- 添加description字段
    ALTER TABLE resource ADD COLUMN description TEXT COMMENT '资源描述';
    
    -- 添加uploader_id字段
    ALTER TABLE resource ADD COLUMN uploader_id BIGINT COMMENT '上传者ID';
    
    -- 修改chapter_id允许为空
    ALTER TABLE resource MODIFY COLUMN chapter_id BIGINT NULL COMMENT '章节ID';
    
END$$

DELIMITER ;

-- 执行存储过程
CALL AddColumnIfNotExists();

-- 删除存储过程
DROP PROCEDURE AddColumnIfNotExists;
