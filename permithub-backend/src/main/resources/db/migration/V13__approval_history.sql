-- V13: approval_history
CREATE TABLE approval_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requestId BIGINT NOT NULL,
    requestType VARCHAR(50) NOT NULL, -- LEAVE, OD, OUTPASS
    approverId BIGINT NOT NULL, -- user_id
    approverRole VARCHAR(50) NOT NULL, -- MENTOR, ADVISOR, HOD, WARDEN
    status VARCHAR(50) NOT NULL, -- APPROVED, REJECTED
    remarks TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
