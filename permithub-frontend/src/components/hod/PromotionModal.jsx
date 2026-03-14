import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiX, FiUsers, FiAlertCircle } from 'react-icons/fi';
import { promotionSchema } from '../../utils/validators';

const PromotionModal = ({ isOpen, onClose, semesters, onPromote, loading }) => {
    const [formData, setFormData] = useState({
        fromYear: '',
        fromSection: '',
        toYear: '',
        toSection: '',
        newSemesterId: '',
        newLeaveBalance: 20,
        promoteOnlyPassedStudents: true,
        remarks: ''
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
        
        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: null }));
        }
    };

    const handleSubmit = async () => {
        try {
            await promotionSchema.validate(formData, { abortEarly: false });
            onPromote(formData);
        } catch (err) {
            const validationErrors = {};
            err.inner.forEach(error => {
                validationErrors[error.path] = error.message;
            });
            setErrors(validationErrors);
        }
    };

    const yearOptions = [1, 2, 3, 4];
    const toYearOptions = formData.fromYear ? [parseInt(formData.fromYear) + 1] : [];

    if (!isOpen) return null;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 overflow-y-auto">
                <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
                        onClick={onClose}
                    />

                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.95 }}
                        className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full"
                    >
                        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-lg font-medium text-gray-900">
                                    Promote Students
                                </h3>
                                <button
                                    onClick={onClose}
                                    className="text-gray-400 hover:text-gray-500 transition-colors"
                                >
                                    <FiX className="w-6 h-6" />
                                </button>
                            </div>

                            <div className="space-y-4">
                                {/* From Section */}
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            From Year <span className="text-red-500">*</span>
                                        </label>
                                        <select
                                            name="fromYear"
                                            value={formData.fromYear}
                                            onChange={handleChange}
                                            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                                errors.fromYear ? 'border-red-500' : 'border-gray-300'
                                            }`}
                                        >
                                            <option value="">Select Year</option>
                                            {yearOptions.map(year => (
                                                <option key={year} value={year}>Year {year}</option>
                                            ))}
                                        </select>
                                        {errors.fromYear && (
                                            <p className="mt-1 text-xs text-red-500">{errors.fromYear}</p>
                                        )}
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            From Section
                                        </label>
                                        <input
                                            type="text"
                                            name="fromSection"
                                            value={formData.fromSection}
                                            onChange={handleChange}
                                            placeholder="e.g., A (leave empty for all)"
                                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                </div>

                                {/* To Section */}
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            To Year <span className="text-red-500">*</span>
                                        </label>
                                        <select
                                            name="toYear"
                                            value={formData.toYear}
                                            onChange={handleChange}
                                            disabled={!formData.fromYear}
                                            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                                !formData.fromYear ? 'bg-gray-100 cursor-not-allowed' : ''
                                            } ${errors.toYear ? 'border-red-500' : 'border-gray-300'}`}
                                        >
                                            <option value="">Select Year</option>
                                            {toYearOptions.map(year => (
                                                <option key={year} value={year}>Year {year}</option>
                                            ))}
                                        </select>
                                        {errors.toYear && (
                                            <p className="mt-1 text-xs text-red-500">{errors.toYear}</p>
                                        )}
                                    </div>

                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            To Section
                                        </label>
                                        <input
                                            type="text"
                                            name="toSection"
                                            value={formData.toSection}
                                            onChange={handleChange}
                                            placeholder="New section (optional)"
                                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        />
                                    </div>
                                </div>

                                {/* Semester Selection */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        New Semester <span className="text-red-500">*</span>
                                    </label>
                                    <select
                                        name="newSemesterId"
                                        value={formData.newSemesterId}
                                        onChange={handleChange}
                                        className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                            errors.newSemesterId ? 'border-red-500' : 'border-gray-300'
                                        }`}
                                    >
                                        <option value="">Select Semester</option>
                                        {semesters?.map(sem => (
                                            <option key={sem.id} value={sem.id}>
                                                {sem.name} ({sem.semesterType || 'Regular'})
                                            </option>
                                        ))}
                                    </select>
                                    {errors.newSemesterId && (
                                        <p className="mt-1 text-xs text-red-500">{errors.newSemesterId}</p>
                                    )}
                                </div>

                                {/* Leave Balance */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        New Leave Balance
                                    </label>
                                    <input
                                        type="number"
                                        name="newLeaveBalance"
                                        value={formData.newLeaveBalance}
                                        onChange={handleChange}
                                        min="0"
                                        max="30"
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>

                                {/* Options */}
                                <div className="flex items-center">
                                    <input
                                        type="checkbox"
                                        name="promoteOnlyPassedStudents"
                                        checked={formData.promoteOnlyPassedStudents}
                                        onChange={handleChange}
                                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                    />
                                    <label className="ml-2 block text-sm text-gray-900">
                                        Promote only students who have passed
                                    </label>
                                </div>

                                {/* Remarks */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Remarks
                                    </label>
                                    <textarea
                                        name="remarks"
                                        value={formData.remarks}
                                        onChange={handleChange}
                                        rows="3"
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder="Add any remarks about this promotion..."
                                    />
                                </div>

                                {/* Warning for pending requests */}
                                <div className="p-4 bg-yellow-50 rounded-lg">
                                    <div className="flex">
                                        <FiAlertCircle className="h-5 w-5 text-yellow-400" />
                                        <div className="ml-3">
                                            <p className="text-sm text-yellow-700">
                                                Students with pending requests will be auto-rejected upon promotion.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Footer */}
                        <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                            <button
                                type="button"
                                onClick={handleSubmit}
                                disabled={loading}
                                className="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-primary-600 text-base font-medium text-white hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed sm:ml-3 sm:w-auto sm:text-sm"
                            >
                                {loading ? 'Promoting...' : 'Promote Students'}
                            </button>
                            <button
                                type="button"
                                onClick={onClose}
                                className="mt-3 w-full inline-flex justify-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                            >
                                Cancel
                            </button>
                        </div>
                    </motion.div>
                </div>
            </div>
        </AnimatePresence>
    );
};

export default PromotionModal;