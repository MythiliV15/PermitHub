-- V10: leave_requests
CREATE TABLE leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    studentId BIGINT NOT NULL,
    departmentId BIGINT NOT NULL,
    categoryId BIGINT NOT NULL,
    startDate DATETIME NOT NULL,
    endDate DATETIME NOT NULL,
    reason TEXT,
    attachmentPath VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, MENTOR_APPROVED, ADVISOR_APPROVED, HOD_APPROVED, REJECTED
    mentorId BIGINT, -- faculty_profile_id
    advisorId BIGINT, -- faculty_profile_id
    hodId BIGINT, -- user_id (HOD)
    approvedAt DATETIME,
    appliedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (studentId) REFERENCES student_profiles(id),
    FOREIGN KEY (departmentId) REFERENCES departments(id),
    FOREIGN KEY (categoryId) REFERENCES leave_categories(id),
    FOREIGN KEY (mentorId) REFERENCES faculty_profiles(id),
    FOREIGN KEY (advisorId) REFERENCES faculty_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
