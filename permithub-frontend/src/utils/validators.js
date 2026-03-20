import * as yup from 'yup';

// ================= LOGIN VALIDATIONS =================

// Login validation schema
export const loginSchema = yup.object().shape({
    email: yup.string()
        .required('Email is required')
        .email('Invalid email format'),

    password: yup.string()
        .required('Password is required')
        .min(6, 'Password must be at least 6 characters')
});

// Forgot password validation
export const forgotPasswordSchema = yup.object().shape({
    email: yup.string()
        .required('Email is required')
        .email('Invalid email format')
});

// Reset password validation
export const resetPasswordSchema = yup.object().shape({
    newPassword: yup.string()
        .required('Password is required')
        .min(8, 'Password must be at least 8 characters')
        .matches(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/, 
                 'Password must contain at least one digit, one lowercase, one uppercase, one special character and no whitespace'),

    confirmPassword: yup.string()
        .required('Confirm password is required')
        .oneOf([yup.ref('newPassword')], 'Passwords must match')
});

// First login password setup
export const firstLoginSchema = yup.object().shape({
    currentPassword: yup.string()
        .required('Current password is required'),

    newPassword: yup.string()
        .required('Password is required')
        .min(8, 'Password must be at least 8 characters')
        .matches(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/, 
                 'Password must contain at least one digit, one lowercase, one uppercase, one special character and no whitespace'),

    confirmPassword: yup.string()
        .required('Confirm password is required')
        .oneOf([yup.ref('newPassword')], 'Passwords must match')
});


// ================= FACULTY VALIDATION =================

export const facultySchema = yup.object().shape({
    employeeId: yup.string()
        .required('Employee ID is required')
        .matches(/^[A-Z0-9]{3,20}$/, 'Employee ID must be 3-20 alphanumeric characters'),
    
    fullName: yup.string()
        .required('Full name is required')
        .min(3, 'Name must be at least 3 characters')
        .max(100, 'Name must be less than 100 characters'),
    
    email: yup.string()
        .required('Email is required')
        .email('Invalid email format'),
    
    phoneNumber: yup.string()
        .nullable()
        .matches(/^[0-9]{10}$/, { message: 'Phone number must be 10 digits', excludeEmptyString: true }),
    
    designation: yup.string()
        .nullable(),
    
    qualification: yup.string()
        .nullable(),
    
    experienceYears: yup.number()
        .typeError('Experience must be a number')
        .min(0, 'Experience cannot be negative')
        .max(50, 'Experience cannot exceed 50 years'),
    
    joiningDate: yup.date()
        .typeError('Invalid date')
        .max(new Date(), 'Joining date cannot be in the future'),
    
    roles: yup.array()
        .min(1, 'At least one role must be selected'),
    
    maxMentees: yup.number()
        .typeError('Max mentees must be a number')
        .min(1, 'Max mentees must be at least 1')
        .max(50, 'Max mentees cannot exceed 50'),
    
    cabinNumber: yup.string()
        .nullable(),
    
    officePhone: yup.string()
        .matches(/^[0-9]{10}$/, 'Office phone must be 10 digits')
        .nullable(),
    
    bloodGroup: yup.string()
        .oneOf(['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'], 'Invalid blood group')
        .nullable(),
    
    dateOfBirth: yup.date()
        .typeError('Invalid date')
        .max(new Date(), 'Date of birth cannot be in the future')
        .nullable()
});


// ================= SEMESTER VALIDATION =================

export const semesterSchema = yup.object().shape({
    name: yup.string()
        .required('Semester name is required')
        .min(3, 'Name must be at least 3 characters'),
    
    year: yup.number()
        .required('Year is required')
        .typeError('Year must be a number')
        .min(2000, 'Year must be 2000 or later')
        .max(2100, 'Year must be 2100 or earlier'),
    
    semesterNumber: yup.number()
        .required('Semester number is required')
        .typeError('Semester number must be a number')
        .min(1, 'Semester number must be between 1 and 8')
        .max(8, 'Semester number must be between 1 and 8'),
    
    startDate: yup.date()
        .required('Start date is required')
        .typeError('Invalid start date'),
    
    endDate: yup.date()
        .required('End date is required')
        .typeError('Invalid end date')
        .min(yup.ref('startDate'), 'End date must be after start date'),
    
    defaultLeaveBalance: yup.number()
        .required('Default leave balance is required')
        .typeError('Leave balance must be a number')
        .min(0, 'Leave balance cannot be negative')
        .max(30, 'Leave balance cannot exceed 30'),
    
    registrationStartDate: yup.date()
        .typeError('Invalid registration start date')
        .nullable(),
    
    registrationEndDate: yup.date()
        .typeError('Invalid registration end date')
        .min(yup.ref('registrationStartDate'), 'Registration end date must be after start date')
        .nullable(),
    
    examStartDate: yup.date()
        .typeError('Invalid exam start date')
        .nullable(),
    
    examEndDate: yup.date()
        .typeError('Invalid exam end date')
        .min(yup.ref('examStartDate'), 'Exam end date must be after start date')
        .nullable(),
    
    semesterType: yup.string()
        .oneOf(['ODD', 'EVEN', 'SUMMER'], 'Invalid semester type')
        .nullable()
});


// ================= PROMOTION VALIDATION =================

export const promotionSchema = yup.object().shape({
    fromYear: yup.number()
        .required('From year is required')
        .typeError('From year must be a number')
        .min(1, 'Year must be between 1 and 4')
        .max(4, 'Year must be between 1 and 4'),
    
    toYear: yup.number()
        .required('To year is required')
        .typeError('To year must be a number')
        .min(2, 'To year must be between 2 and 5')
        .max(5, 'To year must be between 2 and 5')
        .test('is-greater', 'To year must be greater than from year',
            function(value) { return value > this.parent.fromYear; }),
    
    newSemesterId: yup.number()
        .required('New semester is required')
        .typeError('Invalid semester selection'),
    
    newLeaveBalance: yup.number()
        .typeError('Leave balance must be a number')
        .min(0, 'Leave balance cannot be negative')
        .max(30, 'Leave balance cannot exceed 30')
        .default(20)
});


// ================= APPROVAL VALIDATION =================

export const approvalSchema = yup.object().shape({
    remarks: yup.string()
        .required('Remarks are required for approval/rejection')
        .min(5, 'Remarks must be at least 5 characters')
        .max(500, 'Remarks cannot exceed 500 characters')
});


// ================= EXCEL FILE VALIDATION =================

export const validateExcelFile = (file) => {
    const errors = [];

    if (!file) {
        errors.push('No file selected');
        return errors;
    }

    const validTypes = [
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    ];

    if (!validTypes.includes(file.type) &&
        !file.name.endsWith('.xlsx') &&
        !file.name.endsWith('.xls')) {
        errors.push('Please upload a valid Excel file (.xlsx or .xls)');
    }

    const maxSize = 10 * 1024 * 1024;

    if (file.size > maxSize) {
        errors.push('File size must be less than 10MB');
    }

    return errors;
};