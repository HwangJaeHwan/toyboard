CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    created_time DATETIME(6),
    last_modified_time DATETIME(6),
    login_id VARCHAR(255),
    password VARCHAR(255),
    nickname VARCHAR(255),
    email VARCHAR(255),
    user_type VARCHAR(255),
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_user_login_id (login_id),
    UNIQUE KEY uk_user_nickname (nickname),
    UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post (
    post_id BIGINT NOT NULL AUTO_INCREMENT,
    created_time DATETIME(6),
    last_modified_time DATETIME(6),
    title VARCHAR(255),
    content LONGTEXT,
    post_type VARCHAR(255),
    hits BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT,
    PRIMARY KEY (post_id),
    KEY idx_post_type_created_time (post_type, created_time),
    KEY idx_post_type_hits (post_type, hits),
    KEY idx_post_user_id (user_id),
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES `user` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `comment` (
    comment_id BIGINT NOT NULL AUTO_INCREMENT,
    created_time DATETIME(6),
    last_modified_time DATETIME(6),
    `comment` VARCHAR(255),
    user_id BIGINT,
    post_id BIGINT,
    reply_id BIGINT,
    PRIMARY KEY (comment_id),
    KEY idx_comment_post_id (post_id),
    KEY idx_comment_user_id (user_id),
    KEY idx_comment_reply_id (reply_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user` (user_id),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post (post_id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (reply_id) REFERENCES `comment` (comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recommendation (
    recommendation_id BIGINT NOT NULL AUTO_INCREMENT,
    created_time DATETIME(6),
    last_modified_time DATETIME(6),
    user_id BIGINT,
    post_id BIGINT,
    PRIMARY KEY (recommendation_id),
    UNIQUE KEY uk_recommendation_user_post (user_id, post_id),
    KEY idx_recommendation_post_id (post_id),
    CONSTRAINT fk_recommendation_user FOREIGN KEY (user_id) REFERENCES `user` (user_id),
    CONSTRAINT fk_recommendation_post FOREIGN KEY (post_id) REFERENCES post (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
