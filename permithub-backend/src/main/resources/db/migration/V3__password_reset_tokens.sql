CREATE TABLE password_reset_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expiresAt  DATETIME     NOT NULL,
    usedAt     DATETIME     DEFAULT NULL,
    createdAt  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_prt_token  (token),
    INDEX idx_prt_user   (userId)
);
