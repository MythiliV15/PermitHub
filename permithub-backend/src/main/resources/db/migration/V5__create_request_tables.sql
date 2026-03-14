-- V5__create_request_tables.sql
-- This migration creates all request tables (Leave, OD, Outpass)

-- ============================================
-- ENUM: Request Status (will be stored as VARCHAR in MySQL)
-- ============================================

-- ============================================
-- TABLE: leave_requests
-- ============================================
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days INT NOT NULL,
    category VARCHAR(50) NOT NULL, -- SICK, EMERGENCY, OTHER
    reason VARCHAR(500),
    medical_certificate_path VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    
    -- Mentor approval
    mentor_id BIGINT,
    mentor_remark VARCHAR(500),
    mentor_action_date DATETIME,
    
    -- Class Advisor approval
    class_advisor_id BIGINT,
    class_advisor_remark VARCHAR(500),
    class_advisor_action_date DATETIME,
    
    -- HOD approval
    hod_id BIGINT,
    hod_remark VARCHAR(500),
    hod_action_date DATETIME,
    
    applied_date DATETIME NOT NULL,
    is_emergency BOOLEAN DEFAULT FALSE,
    parent_notified BOOLEAN DEFAULT FALSE,
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (mentor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (class_advisor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (hod_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    
    INDEX idx_leave_student (student_id),
    INDEX idx_leave_status (status),
    INDEX idx_leave_dates (start_date, end_date),
    INDEX idx_leave_mentor (mentor_id),
    INDEX idx_leave_hod (hod_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: od_requests
-- ============================================
CREATE TABLE IF NOT EXISTS od_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_name VARCHAR(255) NOT NULL,
    organizer VARCHAR(255),
    location VARCHAR(255),
    description VARCHAR(500),
    proof_document_path VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    
    -- Mentor approval
    mentor_id BIGINT,
    mentor_remark VARCHAR(500),
    mentor_action_date DATETIME,
    
    -- Event Coordinator approval
    event_coordinator_id BIGINT,
    coordinator_remark VARCHAR(500),
    coordinator_action_date DATETIME,
    
    -- Class Advisor approval
    class_advisor_id BIGINT,
    class_advisor_remark VARCHAR(500),
    class_advisor_action_date DATETIME,
    
    -- HOD approval
    hod_id BIGINT,
    hod_remark VARCHAR(500),
    hod_action_date DATETIME,
    
    applied_date DATETIME NOT NULL,
    is_outstation BOOLEAN DEFAULT FALSE,
    accommodation_required BOOLEAN DEFAULT FALSE,
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (mentor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (event_coordinator_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (class_advisor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (hod_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    
    INDEX idx_od_student (student_id),
    INDEX idx_od_status (status),
    INDEX idx_od_dates (start_date, end_date),
    INDEX idx_od_event_type (event_type),
    INDEX idx_od_coordinator (event_coordinator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: outpass_requests
-- ============================================
CREATE TABLE IF NOT EXISTS outpass_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    out_datetime DATETIME NOT NULL,
    expected_return_datetime DATETIME NOT NULL,
    actual_return_datetime DATETIME,
    destination VARCHAR(255) NOT NULL,
    reason VARCHAR(500),
    emergency_contact VARCHAR(20),
    status VARCHAR(50) NOT NULL,
    
    -- Parent approval
    parent_token VARCHAR(255) UNIQUE,
    parent_token_expiry DATETIME,
    parent_remark VARCHAR(500),
    parent_action_date DATETIME,
    
    -- Mentor approval
    mentor_id BIGINT,
    mentor_remark VARCHAR(500),
    mentor_action_date DATETIME,
    
    -- Class Advisor approval
    class_advisor_id BIGINT,
    class_advisor_remark VARCHAR(500),
    class_advisor_action_date DATETIME,
    
    -- Warden approval
    warden_id BIGINT,
    warden_remark VARCHAR(500),
    warden_action_date DATETIME,
    
    -- AO approval
    ao_id BIGINT,
    ao_remark VARCHAR(500),
    ao_action_date DATETIME,
    
    -- Principal approval
    principal_id BIGINT,
    principal_remark VARCHAR(500),
    principal_action_date DATETIME,
    
    -- QR Code
    qr_code_path VARCHAR(500),
    qr_generated_date DATETIME,
    qr_scanned_exit DATETIME,
    qr_scanned_entry DATETIME,
    is_late_entry BOOLEAN DEFAULT FALSE,
    late_minutes INT DEFAULT 0,
    
    applied_date DATETIME NOT NULL,
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (mentor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (class_advisor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (warden_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (ao_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (principal_id) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_outpass_student (student_id),
    INDEX idx_outpass_status (status),
    INDEX idx_outpass_dates (out_datetime, expected_return_datetime),
    INDEX idx_outpass_parent_token (parent_token),
    INDEX idx_outpass_warden (warden_id),
    INDEX idx_outpass_principal (principal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;