CREATE TABLE faculty_roles (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    facultyId      BIGINT       NOT NULL REFERENCES faculty_profiles(id) ON DELETE CASCADE,
    roleName       VARCHAR(30)  NOT NULL,
    config         JSON         DEFAULT NULL,
    isActive       BOOLEAN      NOT NULL DEFAULT TRUE,
    assignedAt     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_faculty_role (facultyId, roleName),
    INDEX idx_fr_faculty  (facultyId)
);
