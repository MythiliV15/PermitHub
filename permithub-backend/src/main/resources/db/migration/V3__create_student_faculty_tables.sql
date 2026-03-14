-- V3__create_student_faculty_tables.sql

-- Create faculty table (extends users)
CREATE TABLE IF NOT EXISTS faculty (
    user_id BIGINT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    department_id BIGINT,
    designation VARCHAR(100),
    qualification VARCHAR(255),
    experience_years INT,
    joining_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create faculty_event_types table for event coordinators
CREATE TABLE IF NOT EXISTS faculty_event_types (
    faculty_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    PRIMARY KEY (faculty_id, event_type),
    FOREIGN KEY (faculty_id) REFERENCES faculty(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create students table (extends users)
CREATE TABLE IF NOT EXISTS students (
    user_id BIGINT PRIMARY KEY,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    department_id BIGINT,
    year INT,
    section VARCHAR(10),
    mentor_id BIGINT,
    class_advisor_id BIGINT,
    is_hosteler BOOLEAN DEFAULT FALSE,
    hostel_name VARCHAR(100),
    room_number VARCHAR(20),
    parent_name VARCHAR(255),
    parent_phone VARCHAR(20),
    parent_email VARCHAR(255),
    emergency_contact VARCHAR(20),
    batch VARCHAR(20),
    admission_year INT,
    current_semester INT,
    leave_balance INT DEFAULT 20,
    date_of_birth DATE,
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (mentor_id) REFERENCES faculty(user_id) ON DELETE SET NULL,
    FOREIGN KEY (class_advisor_id) REFERENCES faculty(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_faculty_employee_id ON faculty(employee_id);
CREATE INDEX idx_faculty_department ON faculty(department_id);
CREATE INDEX idx_students_register_number ON students(register_number);
CREATE INDEX idx_students_department ON students(department_id);
CREATE INDEX idx_students_mentor ON students(mentor_id);
CREATE INDEX idx_students_class_advisor ON students(class_advisor_id);
CREATE INDEX idx_students_batch ON students(batch);
CREATE INDEX idx_students_year_section ON students(year, section);