-- ============================================
-- INSERT DEPARTMENT
-- ============================================
INSERT INTO departments (name, code, description, total_students, total_faculty, created_at, updated_at, is_active)
VALUES ('Information Technology', 'IT', 'Department of Information Technology', 0, 0, NOW(), NOW(), true);

SET @dept_id = LAST_INSERT_ID();

-- ============================================
-- INSERT HOD
-- ============================================
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES ('hod.it@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Dr. S. Rajesh', '9876543200', false, true, NOW(), NOW(), true);

SET @hod_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role) VALUES (@hod_id, 'HOD');

INSERT INTO faculty (user_id, employee_id, department_id, designation, qualification, experience_years, joining_date)
VALUES (@hod_id, 'HOD001', @dept_id, 'Professor & HOD', 'Ph.D. in Information Technology', 15, '2010-06-01');

INSERT INTO hods (faculty_id, office_location, appointment_date, is_acting_hod, signature_image)
VALUES (@hod_id, 'Admin Block, Room 101', '2010-06-01 09:00:00', false, 'signature_ राजेश.png');

-- Update department with HOD
UPDATE departments SET hod_id = @hod_id WHERE id = @dept_id;

-- ============================================
-- INSERT FACULTY (Mentor & Class Advisor)
-- ============================================
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES 
('priya.faculty@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Dr. K. Priya', '9876543201', false, true, NOW(), NOW(), true),
('kumar.faculty@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Mr. R. Kumar', '9876543202', false, true, NOW(), NOW(), true);

SET @faculty1_id = LAST_INSERT_ID(); -- This will get the second insert ID? Better to store separately
-- Better approach:
SELECT id INTO @faculty1_id FROM users WHERE email = 'priya.faculty@kiot.ac.in';
SELECT id INTO @faculty2_id FROM users WHERE email = 'kumar.faculty@kiot.ac.in';

-- Insert roles for faculty
INSERT INTO user_roles (user_id, role) VALUES 
(@faculty1_id, 'FACULTY_MENTOR'),
(@faculty1_id, 'FACULTY_CLASS_ADVISOR'),
(@faculty2_id, 'FACULTY_MENTOR'),
(@faculty2_id, 'FACULTY_EVENT_COORDINATOR');

-- Insert faculty details
INSERT INTO faculty (user_id, employee_id, department_id, designation, qualification, experience_years, joining_date)
VALUES 
(@faculty1_id, 'FAC002', @dept_id, 'Assistant Professor', 'M.E. CSE', 8, '2016-08-01'),
(@faculty2_id, 'FAC003', @dept_id, 'Assistant Professor', 'M.E. IT', 5, '2019-08-01');

-- ============================================
-- INSERT STUDENTS (Batch of 4 students)
-- ============================================
-- Student 1: Mythili V (2nd Year)
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES ('2k22it30@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Mythili V', '9876543210', true, true, NOW(), NOW(), true);
SET @student1_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role) VALUES (@student1_id, 'STUDENT');

INSERT INTO students (user_id, register_number, department_id, year, section, is_hosteler, parent_name, parent_phone, parent_email, emergency_contact, batch, admission_year, current_semester, leave_balance, date_of_birth, address)
VALUES (@student1_id, '2k22it30', @dept_id, 2, 'A', true, 'Vijayakumar', '9876543211', 'vijayakumar@email.com', '9876543212', '2024-2028', 2024, 3, 20, '2004-05-15', '123, Main Street, City');

-- Student 2: Arun K (2nd Year)
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES ('2k22it31@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Arun K', '9876543220', true, true, NOW(), NOW(), true);
SET @student2_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role) VALUES (@student2_id, 'STUDENT');

INSERT INTO students (user_id, register_number, department_id, year, section, is_hosteler, parent_name, parent_phone, parent_email, emergency_contact, batch, admission_year, current_semester, leave_balance, date_of_birth, address)
VALUES (@student2_id, '2k22it31', @dept_id, 2, 'A', true, 'Kumar', '9876543221', 'kumar@email.com', '9876543222', '2024-2028', 2024, 3, 20, '2004-08-20', '456, College Road, City');

-- Student 3: Priya S (2nd Year)
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES ('2k22it32@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Priya S', '9876543230', true, true, NOW(), NOW(), true);
SET @student3_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role) VALUES (@student3_id, 'STUDENT');

INSERT INTO students (user_id, register_number, department_id, year, section, is_hosteler, parent_name, parent_phone, parent_email, emergency_contact, batch, admission_year, current_semester, leave_balance, date_of_birth, address)
VALUES (@student3_id, '2k22it32', @dept_id, 2, 'A', false, 'Selvi', '9876543231', 'selvi@email.com', '9876543232', '2024-2028', 2024, 3, 20, '2004-12-10', '789, Gandhi Street, City');

-- Student 4: Karthik R (2nd Year)
INSERT INTO users (email, password, full_name, phone_number, is_first_login, email_verified, created_at, updated_at, is_active)
VALUES ('2k22it33@kiot.ac.in', '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK', 'Karthik R', '9876543240', true, true, NOW(), NOW(), true);
SET @student4_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role) VALUES (@student4_id, 'STUDENT');

INSERT INTO students (user_id, register_number, department_id, year, section, is_hosteler, parent_name, parent_phone, parent_email, emergency_contact, batch, admission_year, current_semester, leave_balance, date_of_birth, address)
VALUES (@student4_id, '2k22it33', @dept_id, 2, 'A', true, 'Ramesh', '9876543241', 'ramesh@email.com', '9876543242', '2024-2028', 2024, 3, 20, '2004-03-25', '321, Nehru Street, City');

-- ============================================
-- ASSIGN MENTORS AND CLASS ADVISORS
-- ============================================
-- Assign faculty1 as Mentor for students 1 & 2
UPDATE students SET mentor_id = @faculty1_id, class_advisor_id = @faculty1_id 
WHERE register_number IN ('2k22it30', '2k22it31');

-- Assign faculty2 as Mentor for students 3 & 4
UPDATE students SET mentor_id = @faculty2_id, class_advisor_id = @faculty1_id 
WHERE register_number IN ('2k22it32', '2k22it33');

-- ============================================
-- INSERT SEMESTER
-- ============================================
INSERT INTO semesters (name, year, semester_number, start_date, end_date, is_active, default_leave_balance, department_id, created_at, updated_at, is_active_record)
VALUES ('Odd Semester 2024', 2024, 3, '2024-07-01', '2024-11-30', true, 20, @dept_id, NOW(), NOW(), true);

-- ============================================
-- VERIFY ALL DATA
-- ============================================
-- Department info
SELECT * FROM departments WHERE id = @dept_id;

-- Faculty list
SELECT u.id, u.full_name, u.email, f.employee_id, f.designation, 
       GROUP_CONCAT(ur.role) AS roles
FROM users u
JOIN faculty f ON u.id = f.user_id
JOIN user_roles ur ON u.id = ur.user_id
WHERE u.id IN (@faculty1_id, @faculty2_id, @hod_id)
GROUP BY u.id;

-- Student list with mentors
SELECT u.full_name AS student_name, s.register_number, s.year, s.section,
       s.is_hosteler, s.leave_balance,
       m.full_name AS mentor_name,
       ca.full_name AS class_advisor_name
FROM students s
JOIN users u ON s.user_id = u.id
LEFT JOIN users m ON s.mentor_id = m.id
LEFT JOIN users ca ON s.class_advisor_id = ca.id
ORDER BY s.register_number;

-- ============================================
--🔑 Login Credentials for Testing
-- After inserting the data, you can test with these credentials:

-- Role	    Email	                                Password	    First Login?
-- HOD	    hod.it@kiot.ac.in	                         Pass@123 	    No
-- Faculty (Mentor)	priya.faculty@kiot.ac.in	           Pass@123 	    No
-- Faculty (Coordinator) kumar.faculty@kiot.ac.in	    Pass@123	    No
-- Student	2k22it30@kiot.ac.in	                         Pass@123	    Yes (will redirect to change password)
-- Student	2k22it31@kiot.ac.in	                         Pass@123	    Yes
-- Student	2k22it32@kiot.ac.in	                         Pass@123	    Yes
-- Student	2k22it33@kiot.ac.in	                         Pass@123	    Yes

-- Password: Pass@123 
--UPDATE users
--SET password = '$2a$12$9LF5q38mCUNzA/ELGoR7ZOGBWzpGKDgPHv3/UaS7Jjl4EVOn0CKVK';
-- ============================================