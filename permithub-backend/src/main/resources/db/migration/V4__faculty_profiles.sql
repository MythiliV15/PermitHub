CREATE TABLE faculty_profiles (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId         BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    departmentId   BIGINT       NOT NULL REFERENCES departments(id),
    name           VARCHAR(150) NOT NULL,
    phone          VARCHAR(20),
    designation    VARCHAR(100),
    employeeId     VARCHAR(50)  UNIQUE,
    profilePicPath VARCHAR(255),
    createdAt      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fp_dept  (departmentId)
);
