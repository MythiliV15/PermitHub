import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiX, FiUser } from 'react-icons/fi';

const RoleAssignmentModal = ({ isOpen, onClose, faculty, onAssign, loading }) => {
    const [selectedRoles, setSelectedRoles] = useState(faculty?.roles || []);
    const [remarks, setRemarks] = useState('');

    const roleOptions = [
        { value: 'FACULTY_MENTOR', label: 'Mentor', description: 'Can mentor students and approve requests' },
        { value: 'FACULTY_CLASS_ADVISOR', label: 'Class Advisor', description: 'Manages class and student promotions' },
        { value: 'FACULTY_EVENT_COORDINATOR', label: 'Event Coordinator', description: 'Approves OD requests for events' }
    ];

    const handleToggleRole = (role) => {
        setSelectedRoles(prev =>
            prev.includes(role)
                ? prev.filter(r => r !== role)
                : [...prev, role]
        );
    };

    const handleSubmit = () => {
        onAssign(faculty.id, selectedRoles, remarks);
    };

    if (!isOpen || !faculty) return null;

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
                        className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
                    >
                        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-lg font-medium text-gray-900">
                                    Assign Roles
                                </h3>
                                <button
                                    onClick={onClose}
                                    className="text-gray-400 hover:text-gray-500 transition-colors"
                                >
                                    <FiX className="w-6 h-6" />
                                </button>
                            </div>

                            {/* Faculty Info */}
                            <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                                <div className="flex items-center">
                                    <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                                        {faculty.profilePicture ? (
                                            <img
                                                className="h-12 w-12 rounded-full object-cover"
                                                src={faculty.profilePicture}
                                                alt={faculty.fullName}
                                            />
                                        ) : (
                                            <FiUser className="h-6 w-6 text-primary-600" />
                                        )}
                                    </div>
                                    <div className="ml-4">
                                        <h4 className="text-sm font-medium text-gray-900">
                                            {faculty.fullName}
                                        </h4>
                                        <p className="text-sm text-gray-500">
                                            {faculty.employeeId} • {faculty.designation || 'No designation'}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {/* Role Options */}
                            <div className="space-y-3">
                                {roleOptions.map((role) => (
                                    <label
                                        key={role.value}
                                        className={`block p-4 border rounded-lg cursor-pointer transition-colors
                                            ${selectedRoles.includes(role.value)
                                                ? 'border-primary-500 bg-primary-50'
                                                : 'border-gray-200 hover:border-primary-300'
                                            }`}
                                    >
                                        <div className="flex items-start">
                                            <input
                                                type="checkbox"
                                                checked={selectedRoles.includes(role.value)}
                                                onChange={() => handleToggleRole(role.value)}
                                                className="mt-1 h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                            />
                                            <div className="ml-3">
                                                <span className="text-sm font-medium text-gray-900">
                                                    {role.label}
                                                </span>
                                                <p className="text-xs text-gray-500 mt-1">
                                                    {role.description}
                                                </p>
                                            </div>
                                        </div>
                                    </label>
                                ))}
                            </div>

                            {/* Remarks */}
                            <div className="mt-4">
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Remarks (Optional)
                                </label>
                                <textarea
                                    value={remarks}
                                    onChange={(e) => setRemarks(e.target.value)}
                                    rows="3"
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                                    placeholder="Add any additional notes..."
                                />
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
                                {loading ? 'Assigning...' : 'Assign Roles'}
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

export default RoleAssignmentModal;