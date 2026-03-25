-- V9: leave_categories
CREATE TABLE leave_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departmentId BIGINT NOT NULL,
    categoryName VARCHAR(50) NOT NULL,
    maxDays INT DEFAULT 5,
    requiresAttachment BOOLEAN DEFAULT FALSE,
    isAutoApprove BOOLEAN DEFAULT FALSE,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (departmentId) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
