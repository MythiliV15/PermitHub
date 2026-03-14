import React from 'react';
import { motion } from 'framer-motion';
import { FiCalendar, FiClock, FiCheckCircle, FiXCircle } from 'react-icons/fi';
import { formatters } from '../../utils/formatters';

const SemesterCard = ({ semester, onEdit, onActivate, onDeactivate }) => {
    const getStatusColor = () => {
        if (semester.isActive) {
            return {
                bg: 'bg-green-100',
                text: 'text-green-800',
                icon: <FiCheckCircle className="w-4 h-4 text-green-600" />
            };
        }
        return {
            bg: 'bg-gray-100',
            text: 'text-gray-800',
            icon: <FiXCircle className="w-4 h-4 text-gray-600" />
        };
    };

    const status = getStatusColor();

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow duration-300"
        >
            <div className="p-5">
                {/* Header */}
                <div className="flex items-center justify-between mb-3">
                    <h3 className="text-lg font-semibold text-gray-900">
                        {semester.name}
                    </h3>
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${status.bg} ${status.text}`}>
                        {status.icon}
                        <span className="ml-1">{semester.isActive ? 'Active' : 'Inactive'}</span>
                    </span>
                </div>

                {/* Details */}
                <div className="space-y-2 mb-4">
                    <div className="flex items-center text-sm text-gray-600">
                        <FiCalendar className="w-4 h-4 mr-2 text-gray-400" />
                        <span>Year: {semester.year} • Semester {semester.semesterNumber}</span>
                    </div>
                    <div className="flex items-center text-sm text-gray-600">
                        <FiClock className="w-4 h-4 mr-2 text-gray-400" />
                        <span>{formatters.formatDate(semester.startDate)} - {formatters.formatDate(semester.endDate)}</span>
                    </div>
                    <div className="flex items-center text-sm text-gray-600">
                        <span className="font-medium mr-2">Leave Balance:</span>
                        <span className="text-primary-600 font-semibold">{semester.defaultLeaveBalance}</span>
                    </div>
                </div>

                {/* Additional Info - Responsive grid */}
                <div className="grid grid-cols-2 gap-2 mb-4 text-xs">
                    {semester.registrationStartDate && (
                        <div className="bg-gray-50 p-2 rounded">
                            <span className="text-gray-500 block">Registration</span>
                            <span className="font-medium text-gray-700">
                                {formatters.formatDate(semester.registrationStartDate, 'dd MMM')}
                            </span>
                        </div>
                    )}
                    {semester.examStartDate && (
                        <div className="bg-gray-50 p-2 rounded">
                            <span className="text-gray-500 block">Exams</span>
                            <span className="font-medium text-gray-700">
                                {formatters.formatDate(semester.examStartDate, 'dd MMM')}
                            </span>
                        </div>
                    )}
                </div>

                {/* Actions - Responsive buttons */}
                <div className="flex flex-col sm:flex-row gap-2 mt-4">
                    <button
                        onClick={() => onEdit(semester)}
                        className="flex-1 px-3 py-2 text-sm font-medium text-primary-600 bg-primary-50 rounded-lg hover:bg-primary-100 transition-colors"
                    >
                        Edit
                    </button>
                    {semester.isActive ? (
                        <button
                            onClick={() => onDeactivate(semester.id)}
                            className="flex-1 px-3 py-2 text-sm font-medium text-yellow-600 bg-yellow-50 rounded-lg hover:bg-yellow-100 transition-colors"
                        >
                            Deactivate
                        </button>
                    ) : (
                        <button
                            onClick={() => onActivate(semester.id)}
                            className="flex-1 px-3 py-2 text-sm font-medium text-green-600 bg-green-50 rounded-lg hover:bg-green-100 transition-colors"
                        >
                            Activate
                        </button>
                    )}
                </div>
            </div>
        </motion.div>
    );
};

export default SemesterCard;