-- V7__add_semester_relations.sql
-- This migration adds semester relationships and promotion tracking

-- ============================================
-- MODIFY: semesters table - add more fields
-- ============================================
ALTER TABLE semesters 
ADD COLUMN IF NOT EXISTS registration_start_date DATE,
ADD COLUMN IF NOT EXISTS registration_end_date DATE,
ADD COLUMN IF NOT EXISTS exam_start_date DATE,
ADD COLUMN IF NOT EXISTS exam_end_date DATE,
ADD COLUMN IF NOT EXISTS result_date DATE,
ADD COLUMN IF NOT EXISTS created_by BIGINT,
ADD COLUMN IF NOT EXISTS promoted_from_semester_id BIGINT,
ADD COLUMN IF NOT EXISTS promoted_date DATETIME,
ADD COLUMN IF NOT EXISTS is_registration_open BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS is_exam_period BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS is_result_declared BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS academic_year VARCHAR(20), -- e.g., "2024-2025"
ADD COLUMN IF NOT EXISTS semester_type VARCHAR(20) DEFAULT 'ODD', -- ODD, EVEN, SUMMER
ADD FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
ADD FOREIGN KEY (promoted_from_semester_id) REFERENCES semesters(id) ON DELETE SET NULL;

-- ============================================
-- TABLE: semester_student_history
-- Tracks which semester each student was in
-- ============================================
CREATE TABLE IF NOT EXISTS semester_student_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    
    -- Student details at that time
    year INT NOT NULL,
    section VARCHAR(10) NOT NULL,
    leave_balance INT NOT NULL,
    attendance_percentage DECIMAL(5,2),
    
    -- Promotion details
    promoted_from_semester_id BIGINT,
    promoted_by BIGINT,
    promoted_date DATETIME NOT NULL,
    promotion_remarks VARCHAR(500),
    
    -- Academic status
    is_passed BOOLEAN DEFAULT TRUE,
    is_arrear BOOLEAN DEFAULT FALSE,
    arrears_count INT DEFAULT 0,
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (promoted_from_semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (promoted_by) REFERENCES users(id) ON DELETE SET NULL,
    
    UNIQUE KEY uk_student_semester (student_id, semester_id),
    INDEX idx_semester_history_student (student_id),
    INDEX idx_semester_history_semester (semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: bulk_upload_history
-- Tracks all bulk uploads by HOD
-- ============================================
CREATE TABLE IF NOT EXISTS bulk_upload_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    upload_type VARCHAR(50) NOT NULL, -- FACULTY, STUDENT
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    content_type VARCHAR(100),
    uploaded_by BIGINT NOT NULL,
    department_id BIGINT,
    uploaded_date DATETIME NOT NULL,
    
    -- Statistics
    total_records INT NOT NULL,
    successful_records INT NOT NULL,
    failed_records INT NOT NULL,
    
    -- Error details
    error_log TEXT, -- JSON array of errors
    
    -- Status
    status VARCHAR(20) NOT NULL, -- SUCCESS, PARTIAL_SUCCESS, FAILED
    
    default_password_used VARCHAR(255),
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    
    INDEX idx_bulk_upload_date (uploaded_date),
    INDEX idx_bulk_upload_type (upload_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- MODIFY: faculty table - add more fields for management
-- ============================================
ALTER TABLE faculty
ADD COLUMN IF NOT EXISTS is_mentor BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS is_class_advisor BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS is_event_coordinator BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS max_mentees INT DEFAULT 20,
ADD COLUMN IF NOT EXISTS current_mentees INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS specialization VARCHAR(255),
ADD COLUMN IF NOT EXISTS cabin_number VARCHAR(50),
ADD COLUMN IF NOT EXISTS office_phone VARCHAR(20),
ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(20),
ADD COLUMN IF NOT EXISTS blood_group VARCHAR(5),
ADD COLUMN IF NOT EXISTS date_of_birth DATE,
ADD COLUMN IF NOT EXISTS address TEXT,
ADD COLUMN IF NOT EXISTS city VARCHAR(100),
ADD COLUMN IF NOT EXISTS state VARCHAR(100),
ADD COLUMN IF NOT EXISTS pincode VARCHAR(10),
ADD COLUMN IF NOT EXISTS updated_by BIGINT,
ADD COLUMN IF NOT EXISTS deactivated_reason VARCHAR(500),
ADD COLUMN IF NOT EXISTS deactivated_date DATETIME,
ADD FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL;

-- ============================================
-- MODIFY: students table - add more fields for management
-- ============================================
ALTER TABLE students
ADD COLUMN IF NOT EXISTS blood_group VARCHAR(5),
ADD COLUMN IF NOT EXISTS community VARCHAR(50),
ADD COLUMN IF NOT EXISTS religion VARCHAR(50),
ADD COLUMN IF NOT EXISTS nationality VARCHAR(50) DEFAULT 'Indian',
ADD COLUMN IF NOT EXISTS aadhar_number VARCHAR(12),
ADD COLUMN IF NOT EXISTS pan_number VARCHAR(10),
ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100),
ADD COLUMN IF NOT EXISTS account_number VARCHAR(20),
ADD COLUMN IF NOT EXISTS ifsc_code VARCHAR(15),
ADD COLUMN IF NOT EXISTS father_occupation VARCHAR(100),
ADD COLUMN IF NOT EXISTS mother_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS mother_phone VARCHAR(20),
ADD COLUMN IF NOT EXISTS mother_occupation VARCHAR(100),
ADD COLUMN IF NOT EXISTS annual_income DECIMAL(10,2),
ADD COLUMN IF NOT EXISTS updated_by BIGINT,
ADD COLUMN IF NOT EXISTS deactivated_reason VARCHAR(500),
ADD COLUMN IF NOT EXISTS deactivated_date DATETIME,
ADD FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL;

-- ============================================
-- CREATE VIEWS for easier queries
-- ============================================

-- View: Current semester students
CREATE OR REPLACE VIEW v_current_semester_students AS
SELECT 
    s.user_id,
    u.full_name,
    u.email,
    s.register_number,
    s.year,
    s.section,
    s.current_semester,
    s.leave_balance,
    s.is_hosteler,
    d.name AS department_name,
    d.code AS department_code,
    m.full_name AS mentor_name,
    ca.full_name AS class_advisor_name
FROM students s
JOIN users u ON s.user_id = u.id
JOIN departments d ON s.department_id = d.id
LEFT JOIN users m ON s.mentor_id = m.id
LEFT JOIN users ca ON s.class_advisor_id = ca.id
WHERE u.is_active = true;

-- View: Pending HOD approvals
CREATE OR REPLACE VIEW v_hod_pending_approvals AS
SELECT 
    'LEAVE' AS request_type,
    lr.id AS request_id,
    lr.applied_date,
    u.full_name AS student_name,
    s.register_number,
    s.year,
    s.section,
    lr.start_date,
    lr.end_date,
    lr.total_days,
    lr.category,
    lr.reason,
    lr.status,
    lr.mentor_remark,
    lr.class_advisor_remark
FROM leave_requests lr
JOIN students s ON lr.student_id = s.user_id
JOIN users u ON s.user_id = u.id
WHERE (lr.status = 'APPROVED_BY_CLASS_ADVISOR' 
   OR lr.status = 'PENDING')
   AND lr.is_active = true

UNION ALL

SELECT 
    'OD' AS request_type,
    odr.id AS request_id,
    odr.applied_date,
    u.full_name AS student_name,
    s.register_number,
    s.year,
    s.section,
    odr.start_date,
    odr.end_date,
    odr.total_days,
    odr.event_type,
    odr.event_name,
    odr.status,
    odr.mentor_remark,
    odr.coordinator_remark
FROM od_requests odr
JOIN students s ON odr.student_id = s.user_id
JOIN users u ON s.user_id = u.id
WHERE (odr.status = 'APPROVED_BY_CLASS_ADVISOR'
   OR odr.status = 'PENDING')
   AND odr.is_active = true

UNION ALL

SELECT 
    'OUTPASS' AS request_type,
    opr.id AS request_id,
    opr.applied_date,
    u.full_name AS student_name,
    s.register_number,
    s.year,
    s.section,
    opr.out_datetime,
    opr.expected_return_datetime,
    NULL,
    opr.destination,
    opr.reason,
    opr.status,
    opr.mentor_remark,
    opr.class_advisor_remark
FROM outpass_requests opr
JOIN students s ON opr.student_id = s.user_id
JOIN users u ON s.user_id = u.id
WHERE (opr.status = 'APPROVED_BY_WARDEN'
   OR opr.status = 'APPROVED_BY_CLASS_ADVISOR')
   AND opr.is_active = true;