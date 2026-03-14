import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { FiUser, FiMail, FiPhone, FiHome, FiMapPin } from 'react-icons/fi';
import { formatters } from '../../utils/formatters';

const StudentTable = ({ students, loading, onViewDetails, onAssignMentor }) => {
    const [expandedRow, setExpandedRow] = useState(null);

    if (loading) {
        return (
            <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                <div className="p-4 border-b border-gray-200">
                    <div className="h-6 bg-gray-200 rounded w-1/4 animate-pulse"></div>
                </div>
                <div className="divide-y divide-gray-200">
                    {[1, 2, 3, 4, 5].map((i) => (
                        <div key={i} className="p-4 animate-pulse">
                            <div className="flex items-center space-x-4">
                                <div className="h-10 w-10 bg-gray-200 rounded-full"></div>
                                <div className="flex-1">
                                    <div className="h-4 bg-gray-200 rounded w-1/4 mb-2"></div>
                                    <div className="h-3 bg-gray-200 rounded w-1/3"></div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    if (!students?.length) {
        return (
            <div className="bg-white rounded-xl shadow-sm p-12 text-center">
                <div className="text-6xl mb-4">📚</div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">No Students Found</h3>
                <p className="text-gray-500">No students match your current filters</p>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            {/* Desktop Table View */}
            <div className="hidden md:block overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Student
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Register No
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Year & Section
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Contact
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Hostel
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Mentor
                            </th>
                            <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Actions
                            </th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {students.map((student, index) => (
                            <motion.tr
                                key={student.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                className="hover:bg-gray-50 transition-colors duration-200"
                            >
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex items-center">
                                        <div className="h-10 w-10 flex-shrink-0">
                                            {student.profilePicture ? (
                                                <img
                                                    className="h-10 w-10 rounded-full object-cover"
                                                    src={student.profilePicture}
                                                    alt={student.fullName}
                                                />
                                            ) : (
                                                <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                                                    <span className="text-primary-600 font-medium">
                                                        {formatters.getInitials(student.fullName)}
                                                    </span>
                                                </div>
                                            )}
                                        </div>
                                        <div className="ml-4">
                                            <div className="text-sm font-medium text-gray-900">
                                                {student.fullName}
                                            </div>
                                            <div className="text-sm text-gray-500">
                                                {student.email}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {student.registerNumber}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    Year {student.year} - {student.section}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {student.phoneNumber}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                        student.isHosteler 
                                            ? 'bg-purple-100 text-purple-800' 
                                            : 'bg-gray-100 text-gray-800'
                                    }`}>
                                        {student.isHosteler ? 'Hosteler' : 'Day Scholar'}
                                    </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {student.mentorName || 'Not Assigned'}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <button
                                        onClick={() => onViewDetails(student)}
                                        className="text-primary-600 hover:text-primary-900 mr-3"
                                    >
                                        View
                                    </button>
                                    <button
                                        onClick={() => onAssignMentor(student)}
                                        className="text-green-600 hover:text-green-900"
                                    >
                                        Assign Mentor
                                    </button>
                                </td>
                            </motion.tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Mobile Card View */}
            <div className="md:hidden divide-y divide-gray-200">
                {students.map((student) => (
                    <motion.div
                        key={student.id}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="p-4 hover:bg-gray-50 transition-colors duration-200"
                    >
                        <div className="flex items-start space-x-3">
                            <div className="flex-shrink-0">
                                {student.profilePicture ? (
                                    <img
                                        className="h-12 w-12 rounded-full object-cover"
                                        src={student.profilePicture}
                                        alt={student.fullName}
                                    />
                                ) : (
                                    <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                                        <span className="text-primary-600 font-medium text-lg">
                                            {formatters.getInitials(student.fullName)}
                                        </span>
                                    </div>
                                )}
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between">
                                    <p className="text-sm font-medium text-gray-900 truncate">
                                        {student.fullName}
                                    </p>
                                    <span className={`ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                                        student.isHosteler 
                                            ? 'bg-purple-100 text-purple-800' 
                                            : 'bg-gray-100 text-gray-800'
                                    }`}>
                                        {student.isHosteler ? '🏠 Hosteler' : '📚 Day Scholar'}
                                    </span>
                                </div>
                                <p className="text-xs text-gray-500 mt-1">{student.email}</p>
                                
                                <div className="mt-2 grid grid-cols-2 gap-2 text-xs">
                                    <div className="flex items-center text-gray-600">
                                        <FiUser className="w-3 h-3 mr-1" />
                                        {student.registerNumber}
                                    </div>
                                    <div className="flex items-center text-gray-600">
                                        <FiPhone className="w-3 h-3 mr-1" />
                                        {student.phoneNumber}
                                    </div>
                                    <div className="flex items-center text-gray-600">
                                        <FiMapPin className="w-3 h-3 mr-1" />
                                        Year {student.year} - {student.section}
                                    </div>
                                    <div className="flex items-center text-gray-600">
                                        <FiHome className="w-3 h-3 mr-1" />
                                        {student.mentorName || 'No Mentor'}
                                    </div>
                                </div>

                                <div className="mt-3 flex items-center space-x-2">
                                    <button
                                        onClick={() => onViewDetails(student)}
                                        className="flex-1 px-3 py-1.5 bg-primary-50 text-primary-700 text-xs font-medium rounded-lg hover:bg-primary-100"
                                    >
                                        View Details
                                    </button>
                                    <button
                                        onClick={() => onAssignMentor(student)}
                                        className="flex-1 px-3 py-1.5 bg-green-50 text-green-700 text-xs font-medium rounded-lg hover:bg-green-100"
                                    >
                                        Assign Mentor
                                    </button>
                                </div>
                            </div>
                        </div>
                    </motion.div>
                ))}
            </div>
        </div>
    );
};

export default StudentTable;