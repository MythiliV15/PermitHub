-- V9__add_hod_to_outpass_requests.sql
-- This migration adds HOD approval columns to outpass_requests table as required by the OutpassRequest entity

ALTER TABLE outpass_requests 
ADD COLUMN IF NOT EXISTS hod_id BIGINT AFTER class_advisor_action_date,
ADD COLUMN IF NOT EXISTS hod_remark VARCHAR(500) AFTER hod_id,
ADD COLUMN IF NOT EXISTS hod_action_date DATETIME AFTER hod_remark;

-- Add foreign key constraint
ALTER TABLE outpass_requests
ADD CONSTRAINT fk_outpass_hod FOREIGN KEY (hod_id) REFERENCES faculty(user_id) ON DELETE SET NULL;
