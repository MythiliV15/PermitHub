-- V10__update_semester_history_table.sql
-- This migration adds missing columns to semester_student_history table as required by the SemesterStudentHistory entity

ALTER TABLE semester_student_history
ADD COLUMN IF NOT EXISTS sgpa DECIMAL(3,2) AFTER arrears_count,
ADD COLUMN IF NOT EXISTS cgpa DECIMAL(3,2) AFTER sgpa,
ADD COLUMN IF NOT EXISTS total_marks INT AFTER cgpa,
ADD COLUMN IF NOT EXISTS percentage DECIMAL(5,2) AFTER total_marks,
ADD COLUMN IF NOT EXISTS rank_in_class INT AFTER percentage,
ADD COLUMN IF NOT EXISTS backlog_subjects TEXT AFTER rank_in_class,
ADD COLUMN IF NOT EXISTS backlog_cleared BOOLEAN DEFAULT FALSE AFTER backlog_subjects,
ADD COLUMN IF NOT EXISTS backlog_cleared_date DATETIME AFTER backlog_cleared,
ADD COLUMN IF NOT EXISTS working_days INT AFTER backlog_cleared_date,
ADD COLUMN IF NOT EXISTS days_present INT AFTER working_days,
ADD COLUMN IF NOT EXISTS days_absent INT AFTER days_present,
ADD COLUMN IF NOT EXISTS leaves_taken INT AFTER days_absent,
ADD COLUMN IF NOT EXISTS ods_taken INT AFTER leaves_taken,
ADD COLUMN IF NOT EXISTS tuition_fee_paid BOOLEAN DEFAULT FALSE AFTER ods_taken,
ADD COLUMN IF NOT EXISTS exam_fee_paid BOOLEAN DEFAULT FALSE AFTER tuition_fee_paid,
ADD COLUMN IF NOT EXISTS library_fee_paid BOOLEAN DEFAULT FALSE AFTER exam_fee_paid,
ADD COLUMN IF NOT EXISTS hostel_fee_paid BOOLEAN DEFAULT FALSE AFTER library_fee_paid,
ADD COLUMN IF NOT EXISTS transport_fee_paid BOOLEAN DEFAULT FALSE AFTER hostel_fee_paid,
ADD COLUMN IF NOT EXISTS total_fee_paid DECIMAL(10,2) AFTER transport_fee_paid,
ADD COLUMN IF NOT EXISTS fee_due DECIMAL(10,2) AFTER total_fee_paid;
