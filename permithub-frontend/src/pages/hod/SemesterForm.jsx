import React, { useEffect } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { motion } from 'framer-motion';
import { FiSave, FiCalendar, FiArrowLeft } from 'react-icons/fi';
import { semesterSchema } from '../../utils/validators';
import {
    createSemester,
    updateSemester,
    fetchSemesterById,
    selectCurrentSemester,
    clearCurrentSemester
} from '../../store/semesterSlice';
import { useToast } from '../../hooks/useToast';

const SemesterForm = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const location = useLocation();
    const dispatch = useDispatch();
    const { showSuccess, showError, showLoading, dismiss } = useToast();

    const currentSemester = useSelector(selectCurrentSemester);
    const isEditMode = !!id || location.state?.semester;

    const {
        register,
        handleSubmit,
        setValue,
        formState: { errors, isSubmitting }
    } = useForm({
        resolver: yupResolver(semesterSchema),
        defaultValues: isEditMode ? (location.state?.semester || currentSemester) : {
            defaultLeaveBalance: 20,
            semesterType: 'ODD'
        }
    });

    useEffect(() => {
        if (id) {
            dispatch(fetchSemesterById(id));
        }
        return () => {
            dispatch(clearCurrentSemester());
        };
    }, [id, dispatch]);

    useEffect(() => {
        if (currentSemester || location.state?.semester) {
            const semesterData = currentSemester || location.state?.semester;
            Object.keys(semesterData).forEach(key => {
                if (semesterData[key] !== null) {
                    setValue(key, semesterData[key]);
                }
            });
        }
    }, [currentSemester, location.state, setValue]);

    const onSubmit = async (data) => {
        const toastId = showLoading(isEditMode ? 'Updating semester...' : 'Creating semester...');

        try {
            if (isEditMode) {
                const semesterId = id || location.state?.semester.id;
                await dispatch(updateSemester({ id: semesterId, data })).unwrap();
                dismiss(toastId);
                showSuccess('Semester updated successfully');
            } else {
                await dispatch(createSemester(data)).unwrap();
                dismiss(toastId);
                showSuccess('Semester created successfully');
            }
            navigate('/hod/semester');
        } catch (error) {
            dismiss(toastId);
            showError(error || `Failed to ${isEditMode ? 'update' : 'create'} semester`);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center">
                        <button
                            onClick={() => navigate('/hod/semester')}
                            className="mr-4 p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
                        >
                            <FiArrowLeft className="w-5 h-5" />
                        </button>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">
                                {isEditMode ? 'Edit Semester' : 'Create New Semester'}
                            </h1>
                            <p className="text-sm text-gray-600 mt-1">
                                {isEditMode ? 'Update semester details' : 'Add a new academic semester'}
                            </p>
                        </div>
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
                                    Semester Name <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    {...register('name')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.name ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="e.g., Odd Semester 2024"
                                />
                                {errors.name && (
                                    <p className="mt-1 text-xs text-red-500">{errors.name.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Academic Year
                                </label>
                                <input
                                    type="text"
                                    {...register('academicYear')}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    placeholder="e.g., 2024-2025"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Year <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="number"
                                    {...register('year')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.year ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    placeholder="e.g., 2024"
                                />
                                {errors.year && (
                                    <p className="mt-1 text-xs text-red-500">{errors.year.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Semester Number <span className="text-red-500">*</span>
                                </label>
                                <select
                                    {...register('semesterNumber')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.semesterNumber ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                >
                                    <option value="">Select Semester</option>
                                    {[1, 2, 3, 4, 5, 6, 7, 8].map(num => (
                                        <option key={num} value={num}>Semester {num}</option>
                                    ))}
                                </select>
                                {errors.semesterNumber && (
                                    <p className="mt-1 text-xs text-red-500">{errors.semesterNumber.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Semester Type
                                </label>
                                <select
                                    {...register('semesterType')}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                >
                                    <option value="ODD">Odd Semester</option>
                                    <option value="EVEN">Even Semester</option>
                                    <option value="SUMMER">Summer Semester</option>
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Default Leave Balance <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="number"
                                    {...register('defaultLeaveBalance')}
                                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        errors.defaultLeaveBalance ? 'border-red-500' : 'border-gray-300'
                                    }`}
                                    min="0"
                                    max="30"
                                />
                                {errors.defaultLeaveBalance && (
                                    <p className="mt-1 text-xs text-red-500">{errors.defaultLeaveBalance.message}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Semester Dates */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Semester Dates</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Start Date <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('startDate')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.startDate ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                    />
                                </div>
                                {errors.startDate && (
                                    <p className="mt-1 text-xs text-red-500">{errors.startDate.message}</p>
                                )}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    End Date <span className="text-red-500">*</span>
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('endDate')}
                                        className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.endDate ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                    />
                                </div>
                                {errors.endDate && (
                                    <p className="mt-1 text-xs text-red-500">{errors.endDate.message}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Registration Dates */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Registration Period (Optional)</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Registration Start Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('registrationStartDate')}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Registration End Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('registrationEndDate')}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Exam Dates */}
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Exam Period (Optional)</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Exam Start Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('examStartDate')}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Exam End Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('examEndDate')}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Result Date
                                </label>
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        {...register('resultDate')}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Form Actions */}
                    <div className="flex flex-col sm:flex-row gap-3 pt-4 border-t">
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 inline-flex items-center justify-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            <FiSave className="mr-2" />
                            {isSubmitting ? 'Saving...' : (isEditMode ? 'Update Semester' : 'Create Semester')}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate('/hod/semester')}
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

export default SemesterForm;