-- V11__create_hods_table.sql

-- Create hods table (extends faculty)
CREATE TABLE IF NOT EXISTS hods (
    faculty_id BIGINT PRIMARY KEY,
    office_location VARCHAR(255),
    appointment_date DATETIME,
    tenure_end_date DATETIME,
    is_acting_hod BOOLEAN DEFAULT FALSE,
    signature_image VARCHAR(255),
    FOREIGN KEY (faculty_id) REFERENCES faculty(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migrate existing HODs from departments table
-- For each unique hod_id in departments, if it's not already in hods, add it
INSERT INTO hods (faculty_id, appointment_date, is_acting_hod)
SELECT DISTINCT hod_id, NOW(), FALSE
FROM departments
WHERE hod_id IS NOT NULL 
AND hod_id NOT IN (SELECT faculty_id FROM hods);
