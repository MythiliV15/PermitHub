-- V8__update_bulk_upload_history_table.sql
-- Adds missing columns required by BulkUploadHistory entity

ALTER TABLE bulk_upload_history
ADD COLUMN IF NOT EXISTS processing_time_ms BIGINT,
ADD COLUMN IF NOT EXISTS start_time DATETIME,
ADD COLUMN IF NOT EXISTS end_time DATETIME,
ADD COLUMN IF NOT EXISTS password_reset_required BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS file_size_bytes BIGINT,
ADD COLUMN IF NOT EXISTS original_filename VARCHAR(255),
ADD COLUMN IF NOT EXISTS upload_ip VARCHAR(50);