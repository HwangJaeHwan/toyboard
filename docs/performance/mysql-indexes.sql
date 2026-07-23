SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'post'
    AND index_name = 'idx_post_type_created_id'
);
SET @sql := IF(
  @idx = 0,
  'CREATE INDEX idx_post_type_created_id ON post (post_type, created_time DESC, post_id DESC)',
  'SELECT ''idx_post_type_created_id already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'post'
    AND index_name = 'idx_post_type_hits_id'
);
SET @sql := IF(
  @idx = 0,
  'CREATE INDEX idx_post_type_hits_id ON post (post_type, hits DESC, post_id DESC)',
  'SELECT ''idx_post_type_hits_id already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'comment'
    AND index_name = 'idx_comment_post_id'
);
SET @sql := IF(
  @idx = 0,
  'CREATE INDEX idx_comment_post_id ON comment (post_id)',
  'SELECT ''idx_comment_post_id already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'recommendation'
    AND index_name = 'idx_recommendation_post_id'
);
SET @sql := IF(
  @idx = 0,
  'CREATE INDEX idx_recommendation_post_id ON recommendation (post_id)',
  'SELECT ''idx_recommendation_post_id already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

