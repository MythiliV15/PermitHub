-- V2__create_department_tables.sql

-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    hod_id BIGINT,
    total_students INT DEFAULT 0,
    total_faculty INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (hod_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create semesters table
CREATE TABLE IF NOT EXISTS semesters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    semester_number INT NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT FALSE,
    default_leave_balance INT DEFAULT 20,
    department_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    is_active_record BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_semesters_department ON semesters(department_id);
CREATE INDEX idx_semesters_active ON semesters(is_active);