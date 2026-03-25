-- Sample Data for PermitHub
-- Departments
INSERT INTO departments (id, name, code, description, isActive) VALUES
(1, 'Computer Science and Engineering', 'CSE', 'Core computing and software engineering', 1),
(2, 'Information Technology', 'IT', 'Information systems and network tech', 1),
(3, 'Artificial Intelligence and Data Science', 'AI&DS', 'Machine learning and big data', 1),
(4, 'Computer Science and Business Systems', 'CSBS', 'CS with business management', 1),
(5, 'Electronics and Communication Engineering', 'ECE', 'Hardware and telecom', 1),
(6, 'Electrical and Electronics Engineering', 'EEE', 'Power systems and electronics', 1),
(7, 'Mechanical Engineering', 'MECH', 'Thermodynamics and robotics', 1),
(8, 'Civil Engineering', 'CIVIL', 'Infrastructure and construction', 1);

-- Semesters (Required for students)
INSERT INTO semesters (id, departmentId, name, academicYear, startDate, endDate, isActive) VALUES
(1, 1, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(2, 2, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(3, 3, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(4, 4, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(5, 5, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(6, 6, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(7, 7, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1),
(8, 8, 'Semester 1', '2023-24', '2023-06-01', '2023-11-30', 1);

-- Users (HODs, Faculty, Students) - Password is 'password123' (BCrypt)
-- HODs (ID 1-8)
INSERT INTO users (id, email, password, role, departmentId, firstLogin, isActive) VALUES
(1, 'cse_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 1, 0, 1),
(2, 'it_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 2, 0, 1),
(3, 'aids_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 3, 0, 1),
(4, 'csbs_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 4, 0, 1),
(5, 'ece_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 5, 0, 1),
(6, 'eee_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 6, 0, 1),
(7, 'mech_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 7, 0, 1),
(8, 'civil_hod@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'HOD', 8, 0, 1);

-- Faculty (ID 9-16)image.pngimage.png
INSERT INTO users (id, email, password, role, departmentId, firstLogin, isActive) VALUES
(9, 'cse_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 1, 0, 1),
(10, 'it_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 2, 0, 1),
(11, 'aids_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 3, 0, 1),
(12, 'csbs_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 4, 0, 1),
(13, 'ece_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 5, 0, 1),
(14, 'eee_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 6, 0, 1),
(15, 'mech_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 7, 0, 1),
(16, 'civil_faculty@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'FACULTY', 8, 0, 1);

-- Students (ID 17-24)
INSERT INTO users (id, email, password, role, departmentId, firstLogin, isActive) VALUES
(17, 'cse_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 1, 0, 1),
(18, 'it_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 2, 0, 1),
(19, 'aids_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 3, 0, 1),
(20, 'csbs_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 4, 0, 1),
(21, 'ece_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 5, 0, 1),
(22, 'eee_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 6, 0, 1),
(23, 'mech_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 7, 0, 1),
(24, 'civil_student@permithub.com', '$2a$12$R.8vYmJzS9H2T2l8aU0.h.x9uGfRkPz0J2g5Xw.E.S5vQ.C.S.S.S', 'STUDENT', 8, 0, 1);

-- Faculty Profiles
INSERT INTO faculty_profiles (id, userId, departmentId, name, employeeId, designation) VALUES
(1, 1, 1, 'CSE HOD', 'CSE_HOD_001', 'Professor'),
(2, 2, 2, 'IT HOD', 'IT_HOD_001', 'Professor'),
(3, 3, 3, 'AI&DS HOD', 'AI_HOD_001', 'Professor'),
(4, 4, 4, 'CSBS HOD', 'BS_HOD_001', 'Professor'),
(5, 5, 5, 'ECE HOD', 'ECE_HOD_001', 'Professor'),
(6, 6, 6, 'EEE HOD', 'EEE_HOD_001', 'Professor'),
(7, 7, 7, 'MECH HOD', 'MECH_HOD_001', 'Professor'),
(8, 8, 8, 'CIVIL HOD', 'CIVIL_HOD_001', 'Professor'),
(9, 9, 1, 'John Doe', 'CSE_FAC_001', 'Assistant Professor'),
(10, 10, 2, 'Jane Smith', 'IT_FAC_001', 'Assistant Professor'),
(11, 11, 3, 'Alan Turing', 'AI_FAC_001', 'Assistant Professor'),
(12, 12, 4, 'Bill Gates', 'BS_FAC_001', 'Assistant Professor'),
(13, 13, 5, 'Nikola Tesla', 'ECE_FAC_001', 'Assistant Professor'),
(14, 14, 6, 'Thomas Edison', 'EEE_FAC_001', 'Assistant Professor'),
(15, 15, 7, 'Henry Ford', 'MECH_FAC_001', 'Assistant Professor'),
(16, 16, 8, 'Isambard Brunel', 'CIV_FAC_001', 'Assistant Professor');

-- Student Profiles
INSERT INTO student_profiles (id, userId, departmentId, semesterId, name, regNo, year, section) VALUES
(1, 17, 1, 1, 'Student CSE', '24CSE001', 1, 'A'),
(2, 18, 2, 2, 'Student IT', '24IT001', 1, 'A'),
(3, 19, 3, 3, 'Student AI', '24AI001', 1, 'A'),
(4, 20, 4, 4, 'Student CSBS', '24BS001', 1, 'A'),
(5, 21, 5, 5, 'Student ECE', '24EC001', 1, 'A'),
(6, 22, 6, 6, 'Student EEE', '24EE001', 1, 'A'),
(7, 23, 7, 7, 'Student MECH', '24ME001', 1, 'A'),
(8, 24, 8, 8, 'Student CIVIL', '24CV001', 1, 'A');

-- Faculty Roles (Assign HOD Role to the HOD users)
INSERT INTO faculty_roles (facultyId, roleName, isActive) VALUES
(1, 'HOD', 1),
(2, 'HOD', 1),
(3, 'HOD', 1),
(4, 'HOD', 1),
(5, 'HOD', 1),
(6, 'HOD', 1),
(7, 'HOD', 1),
(8, 'HOD', 1);
