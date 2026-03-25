-- V16: google_sheets_config
CREATE TABLE google_sheets_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departmentId BIGINT NOT NULL,
    configName VARCHAR(100) NOT NULL, -- BULK_UPLOAD, ATTENDANCE_SYNC
    spreadsheetId VARCHAR(255) NOT NULL,
    sheetName VARCHAR(100),
    rangeAddress VARCHAR(50),
    isActive BOOLEAN DEFAULT TRUE,
    apiKey VARCHAR(255),
    lastSyncedAt DATETIME,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (departmentId) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
