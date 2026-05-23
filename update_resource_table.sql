-- 更新resource表结构，添加新字段
-- 检查并添加新字段

-- 添加文件名字段
ALTER TABLE resource ADD COLUMN IF NOT EXISTS file_name VARCHAR(255) COMMENT '本地文件名';

-- 添加原始文件名字段
ALTER TABLE resource ADD COLUMN IF NOT EXISTS original_name VARCHAR(255) COMMENT '原始文件名';

-- 添加描述字段
ALTER TABLE resource ADD COLUMN IF NOT EXISTS description TEXT COMMENT '资源描述';

-- 添加上传者ID字段
ALTER TABLE resource ADD COLUMN IF NOT EXISTS uploader_id BIGINT COMMENT '上传者ID';

-- 修改chapter_id字段允许为空
ALTER TABLE resource MODIFY COLUMN chapter_id BIGINT NULL COMMENT '章节ID';

-- 查看表结构
DESCRIBE resource;
