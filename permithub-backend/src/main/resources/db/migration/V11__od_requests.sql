-- V11: od_requests
CREATE TABLE od_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    studentId BIGINT NOT NULL,
    departmentId BIGINT NOT NULL,
    odType VARCHAR(50) NOT NULL, -- ACADEMIC, SPORTS, CULTURAL, PLACEMENT
    startDate DATETIME NOT NULL,
    endDate DATETIME NOT NULL,
    eventName VARCHAR(150),
    reason TEXT,
    attachmentPath VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    advisorId BIGINT, -- faculty_profile_id
    hodId BIGINT, -- user_id (HOD)
    approvedAt DATETIME,
    appliedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (studentId) REFERENCES student_profiles(id),
    FOREIGN KEY (departmentId) REFERENCES departments(id),
    FOREIGN KEY (advisorId) REFERENCES faculty_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
