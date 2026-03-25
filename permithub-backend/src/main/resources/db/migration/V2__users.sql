CREATE TABLE users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    departmentId   BIGINT       REFERENCES departments(id),
    hostelType     VARCHAR(10)  DEFAULT NULL,
    firstLogin     BOOLEAN      NOT NULL DEFAULT TRUE,
    isActive       BOOLEAN      NOT NULL DEFAULT TRUE,
    createdAt      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email        (email),
    INDEX idx_users_dept         (departmentId),
    INDEX idx_users_role         (role)
);
