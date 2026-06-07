-- 清空所有模拟数据（保留表结构）
-- 执行此脚本后重启后端应用，会重新生成带图片的新数据
-- 数据库：PostgreSQL

-- 方式1：使用TRUNCATE（更快，推荐）
TRUNCATE TABLE dogs, stories, adoptions, volunteers, lost_found, donations RESTART IDENTITY CASCADE;

-- 方式2：使用DELETE（如果需要保留序列值，请注释上面一行，使用下面的DELETE语句）
-- DELETE FROM dogs;
-- DELETE FROM stories;
-- DELETE FROM adoptions;
-- DELETE FROM volunteers;
-- DELETE FROM lost_found;
-- DELETE FROM donations;

-- 注意：
-- TRUNCATE RESTART IDENTITY 会自动重置所有自增序列
-- CASCADE 会自动处理外键依赖关系
-- 执行成功后，重启后端应用即可重新生成带图片的新数据
