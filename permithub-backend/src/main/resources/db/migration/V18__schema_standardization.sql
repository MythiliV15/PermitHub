-- V18: Standardize schema with missing createdAt/updatedAt columns
-- Note: This is a fresh standardization run on a new history table.
-- If any columns already exist due to previous incomplete runs, we'll fix them.

-- Using procedure to add if not exists (MySQL version independent)
DELIMITER //
CREATE PROCEDURE AddColIfNotExists(IN tableName VARCHAR(100), IN colName VARCHAR(100), IN colDefinition VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS 
        WHERE TABLE_NAME = tableName AND COLUMN_NAME = colName AND TABLE_SCHEMA = DATABASE()
    ) THEN
        SET @s = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', colName, ' ', colDefinition);
        PREPARE stmt FROM @s;
        EXECUTE stmt;
    END IF;
END //
DELIMITER ;

CALL AddColIfNotExists('faculty_roles', 'createdAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColIfNotExists('faculty_roles', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColIfNotExists('leave_categories', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColIfNotExists('password_reset_tokens', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColIfNotExists('approval_history', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColIfNotExists('event_faculty', 'createdAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColIfNotExists('event_faculty', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL AddColIfNotExists('leave_requests', 'createdAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColIfNotExists('od_requests', 'createdAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColIfNotExists('outpass_requests', 'createdAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP');
CALL AddColIfNotExists('events', 'updatedAt', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');

DROP PROCEDURE AddColIfNotExists;
