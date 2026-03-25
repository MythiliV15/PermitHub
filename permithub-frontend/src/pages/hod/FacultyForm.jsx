import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { motion, AnimatePresence } from 'framer-motion';
import { FiSave, FiX, FiUser, FiMail, FiPhone, FiBriefcase, FiCalendar } from 'react-icons/fi';
import { facultySchema } from '../../utils/validators';
import {
    addFaculty,
    assignRoles,
    updateFaculty,
    fetchFacultyById,
    selectCurrentFaculty,
    clearCurrentFaculty
} from '../../store/facultySlice';
import { useToast } from '../../hooks/useToast';
import { FiChevronDown, FiChevronUp, FiInfo } from 'react-icons/fi';

const roleOptions = [
    { value: 'FACULTY_MENTOR', label: 'Mentor' },
    { value: 'FACULTY_CLASS_ADVISOR', label: 'Class Advisor' },
    { value: 'FACULTY_EVENT_COORDINATOR', label: 'Event Coordinator' }
];

const FacultyForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const location = useLocation();
    const dispatch = useDispatch();
    const { showSuccess, showError, showLoading, dismiss } = useToast();
    const currentFaculty = useSelector(selectCurrentFaculty);
    const isEditMode = !!id || location.state?.faculty;
    const [showAdvanced, setShowAdvanced] = useState(false);

    const [selectedRoles, setSelectedRoles] = useState([]);

    const {
        register,
        handleSubmit,
        setValue,
        formState: { errors, isSubmitting }
    } = useForm({
        resolver: yupResolver(facultySchema),
        defaultValues: isEditMode ? (location.state?.faculty || currentFaculty) : {}
    });

    useEffect(() => {
        if (id) {
            dispatch(fetchFacultyById(id));
        }
        return () => {
            dispatch(clearCurrentFaculty());
        };
    }, [id, dispatch]);

    useEffect(() => {
        if (currentFaculty || location.state?.faculty) {
            const facultyData = currentFaculty || location.state?.faculty;
            Object.keys(facultyData).forEach(key => {
                if (key !== 'roles' && facultyData[key] !== null) {
                    setValue(key, facultyData[key]);
                }
            });
            setSelectedRoles(facultyData.roles || []);
        }
    }, [currentFaculty, location.state, setValue]);

    const handleRoleToggle = (role) => {
        setSelectedRoles(prev =>
            prev.includes(role)
                ? prev.filter(r => r !== role)
                : [...prev, role]
        );
    };

    const onSubmit = async (data) => {
        const toastId = showLoading(isEditMode ? 'Updating faculty...' : 'Adding faculty...');
        
        try {
            const facultyData = {
                employeeId: data.employeeId,
                name: data.fullName,
                email: data.email,
                phone: data.phoneNumber,
                designation: data.designation
            };

            if (isEditMode) {
                const facultyId = id || location.state?.faculty.id;
                await dispatch(updateFaculty({ id: facultyId, data: facultyData })).unwrap();
                dismiss(toastId);
                showSuccess('Faculty updated successfully');
            } else {
                const addedFaculty = await dispatch(addFaculty(facultyData)).unwrap();
                dismiss(toastId);
                showSuccess(`Faculty added successfully. Default password: Welcome@123`);

                if (selectedRoles.length > 0 && addedFaculty?.id) {
                    const rolePayload = selectedRoles.map((roleName) => ({ roleName }));
                    await dispatch(assignRoles({ id: addedFaculty.id, roles: rolePayload })).unwrap();
                }
            }
            navigate('/hod/faculty');
        } catch (error) {
            dismiss(toastId);
            showError(error || `Failed to ${isEditMode ? 'update' : 'add'} faculty`);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center justify-between">
                        <h1 className="text-2xl font-bold text-gray-900">
                            {isEditMode ? 'Edit Faculty' : 'Add New Faculty'}
                        </h1>
                        <button
                            onClick={() => navigate('/hod/faculty')}
                            className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
                        >
                            <FiX className="w-6 h-6" />
                        </button>
                    </div>
                </div>
            </div>

            {/* Form */}
            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <motion.form
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    onSubmit={handleSubmit(onSubmit)}
                    className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 space-y-6"
                >
                    {/* Basic Information */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Employee ID <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <FiBriefcase className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        {...register('employeeId')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.employeeId ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="e.g., FAC001"
                                        disabled={isEditMode}
                                    />
                                </div>
                                {errors.employeeId && (
                                    <p className="mt-1 text-xs text-red-500">{errors.employeeId.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Full Name <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <FiUser className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        {...register('fullName')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.fullName ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="Enter full name"
                                    />
                                </div>
                                {errors.fullName && (
                                    <p className="mt-1 text-xs text-red-500">{errors.fullName.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Email <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <FiMail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="email"
                                        {...register('email')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.email ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="faculty@college.edu"
                                    />
                                </div>
                                {errors.email && (
                                    <p className="mt-1 text-xs text-red-500">{errors.email.message}</p>
                                )}
                            </div>

                            <div>
                                 <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Phone Number
                                </label>
                                <div className="relative">
                                    <FiPhone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        {...register('phoneNumber')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.phoneNumber ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                        placeholder="10 digit mobile number"
                                    />
                                </div>
                                {errors.phoneNumber && (
                                    <p className="mt-1 text-xs text-red-500">{errors.phoneNumber.message}</p>
                                )}
                            </div>
                        </div>
                        <div className="mt-4 p-3 bg-blue-50 rounded-lg flex items-start text-xs text-blue-700">
                            <FiInfo className="mr-2 h-4 w-4 flex-shrink-0" />
                            <p>Once added, the faculty can log in using their email and the default password: <strong>Welcome@123</strong>. They will be prompted to change it on their first login.</p>
                        </div>
                    </div>

                    {/* Professional Details */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Professional Details</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Designation
                                </label>
                                <select
                                    {...register('designation')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.designation ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                >
                                    <option value="">Select Designation</option>
                                    <option value="Professor">Professor</option>
                                    <option value="Associate Professor">Associate Professor</option>
                                    <option value="Assistant Professor">Assistant Professor</option>
                                    <option value="Lecturer">Lecturer</option>
                                </select>
                                {errors.designation && (
                                    <p className="mt-1 text-xs text-red-500">{errors.designation.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Qualification
                                </label>
                                <input
                                    type="text"
                                    {...register('qualification')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.qualification ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="e.g., Ph.D., M.E."
                                />
                                {errors.qualification && (
                                    <p className="mt-1 text-xs text-red-500">{errors.qualification.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Experience (Years)
                                </label>
                                <input
                                    type="number"
                                    {...register('experienceYears')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.experienceYears ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="Years of experience"
                                />
                                {errors.experienceYears && (
                                    <p className="mt-1 text-xs text-red-500">{errors.experienceYears.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Joining Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('joiningDate')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.joiningDate ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                    />
                                </div>
                                {errors.joiningDate && (
                                    <p className="mt-1 text-xs text-red-500">{errors.joiningDate.message}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Roles */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Assign Roles</h2>
                        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                            {roleOptions.map((role) => (
                                <label
                                    key={role.value}
                                    className={`flex items-center p-3 border rounded-lg cursor-pointer transition-colors ${
                                        selectedRoles.includes(role.value)
                                            ? 'border-primary-500 bg-primary-50'
                                            : 'border-gray-200 hover:border-primary-300'
                                    }`}
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedRoles.includes(role.value)}
                                        onChange={() => handleRoleToggle(role.value)}
                                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                    />
                                    <span className="ml-2 text-sm text-gray-700">{role.label}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Advanced Details Toggle */}
                    <div className="pt-2">
                        <button
                            type="button"
                            onClick={() => setShowAdvanced(!showAdvanced)}
                            className="flex items-center text-primary-600 font-medium hover:text-primary-700 transition-colors focus:outline-none"
                        >
                            {showAdvanced ? <FiChevronUp className="mr-1" /> : <FiChevronDown className="mr-1" />}
                            {showAdvanced ? 'Hide Additional Details' : 'Show Additional Details (Optional)'}
                        </button>
                    </div>

                    <AnimatePresence>
                    {showAdvanced && (
                        <motion.div
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: 'auto' }}
                            exit={{ opacity: 0, height: 0 }}
                            className="space-y-6 overflow-hidden"
                        >
                            <div className="pt-6 border-t border-gray-100">
                                <h2 className="text-lg font-semibold text-gray-900 mb-4">Additional Details</h2>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Specialization
                                        </label>
                                        <input
                                            type="text"
                                            {...register('specialization')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                            placeholder="Area of expertise"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Cabin Number
                                        </label>
                                        <input
                                            type="text"
                                            {...register('cabinNumber')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                            placeholder="e.g., A-101"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Office Phone
                                        </label>
                                        <input
                                            type="text"
                                            {...register('officePhone')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                            placeholder="Office phone number"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Blood Group
                                        </label>
                                        <select
                                            {...register('bloodGroup')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        >
                                            <option value="">Select Blood Group</option>
                                            <option value="A+">A+</option>
                                            <option value="A-">A-</option>
                                            <option value="B+">B+</option>
                                            <option value="B-">B-</option>
                                            <option value="O+">O+</option>
                                            <option value="O-">O-</option>
                                            <option value="AB+">AB+</option>
                                            <option value="AB-">AB-</option>
                                        </select>
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Date of Birth
                                        </label>
                                        <input
                                            type="date"
                                            {...register('dateOfBirth')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Max Mentees
                                        </label>
                                        <input
                                            type="number"
                                            {...register('maxMentees')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                            placeholder="Default: 20"
                                        />
                                    </div>
                                </div>

                                <div className="mt-4">
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Address
                                    </label>
                                    <textarea
                                        {...register('address')}
                                        rows="3"
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder="Residential address"
                                    />
                                </div>

                                <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            City
                                        </label>
                                        <input
                                            type="text"
                                            {...register('city')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            State
                                        </label>
                                        <input
                                            type="text"
                                            {...register('state')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Pincode
                                        </label>
                                        <input
                                            type="text"
                                            {...register('pincode')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="pt-6 border-t border-gray-100">
                                <h2 className="text-lg font-semibold text-gray-900 mb-4">Emergency Contact</h2>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Contact Name
                                        </label>
                                        <input
                                            type="text"
                                            {...register('emergencyContactName')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Contact Phone
                                        </label>
                                        <input
                                            type="text"
                                            {...register('emergencyContactPhone')}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    )}
                    </AnimatePresence>

                    {/* Form Actions */}
                    <div className="flex flex-col sm:flex-row gap-3 pt-4 border-t">
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 inline-flex items-center justify-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            <FiSave className="mr-2" />
                            {isSubmitting ? 'Saving...' : (isEditMode ? 'Update Faculty' : 'Add Faculty')}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate('/hod/faculty')}
                            className="flex-1 inline-flex items-center justify-center px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                        >
                            Cancel
                        </button>
                    </div>
                </motion.form>
            </div>
        </div>
    );
};

export default FacultyForm;