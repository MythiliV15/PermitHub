-- V8: semester_history
CREATE TABLE semester_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    studentId BIGINT NOT NULL,
    semesterId BIGINT NOT NULL,
    year INT NOT NULL,
    section VARCHAR(10),
    attendancePercentage DECIMAL(5,2),
    gpa DECIMAL(3,2),
    arrearsCount INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'COMPLETED', -- ONGOING, COMPLETED, DROPPED
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (studentId) REFERENCES student_profiles(id),
    FOREIGN KEY (semesterId) REFERENCES semesters(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
