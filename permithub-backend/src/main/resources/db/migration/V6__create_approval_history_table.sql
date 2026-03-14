-- V6__create_approval_history_table.sql
-- This migration creates approval history tracking table

-- ============================================
-- TABLE: approval_history
-- ============================================
CREATE TABLE IF NOT EXISTS approval_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Reference to the request
    request_type VARCHAR(50) NOT NULL, -- LEAVE, OD, OUTPASS
    request_id BIGINT NOT NULL,
    
    -- Who approved/rejected
    approver_id BIGINT NOT NULL,
    approver_role VARCHAR(50) NOT NULL, -- MENTOR, CLASS_ADVISOR, HOD, etc.
    approver_name VARCHAR(255) NOT NULL,
    
    -- Action details
    action VARCHAR(20) NOT NULL, -- APPROVED, REJECTED, PENDING, CANCELLED
    remarks VARCHAR(500),
    
    -- Timestamp
    action_date DATETIME NOT NULL,
    
    -- Previous and next status
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    
    -- Approval workflow level
    approval_level INT,
    
    -- Auto approval info
    is_auto_approved BOOLEAN DEFAULT FALSE,
    
    -- Notification tracking
    notification_sent BOOLEAN DEFAULT FALSE,
    notification_sent_at DATETIME,
    
    -- Additional info
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    
    -- Base entity fields
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_approval_request (request_type, request_id),
    INDEX idx_approval_approver (approver_id),
    INDEX idx_approval_dates (action_date),
    INDEX idx_approval_status (new_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;