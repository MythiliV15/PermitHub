-- V12: outpass_requests
CREATE TABLE outpass_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    studentId BIGINT NOT NULL,
    departmentId BIGINT NOT NULL,
    outpassType VARCHAR(50) NOT NULL, -- DAY_EXIT, HOME_VISIT
    departureTime DATETIME NOT NULL,
    arrivalTime DATETIME,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    wardenId BIGINT, -- faculty_profile_id or staff_id
    hodId BIGINT, -- user_id (HOD)
    qrCodePath VARCHAR(255),
    approvedAt DATETIME,
    appliedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (studentId) REFERENCES student_profiles(id),
    FOREIGN KEY (departmentId) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
