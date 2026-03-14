import React from 'react';
import { motion } from 'framer-motion';
import { FiEdit2, FiUserCheck, FiUserX } from 'react-icons/fi';
import { formatters } from '../../utils/formatters';

const FacultyTable = ({ faculty, loading, onEdit, onDeactivate, onActivate, onAssignRoles }) => {

    const getRoleBadges = (roles) => {
        const roleColors = {
            'FACULTY_MENTOR': 'bg-blue-100 text-blue-800',
            'FACULTY_CLASS_ADVISOR': 'bg-green-100 text-green-800',
            'FACULTY_EVENT_COORDINATOR': 'bg-purple-100 text-purple-800',
            'HOD': 'bg-yellow-100 text-yellow-800'
        };

        return roles?.map(role => (
            <span
                key={role}
                className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${roleColors[role] || 'bg-gray-100 text-gray-800'} mr-1 mb-1`}
            >
                {formatters.formatRole(role)}
            </span>
        ));
    };

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

    if (!faculty?.content?.length) {
        return (
            <div className="bg-white rounded-xl shadow-sm p-12 text-center">
                <div className="text-6xl mb-4">👥</div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">No Faculty Found</h3>
                <p className="text-gray-500">Get started by adding your first faculty member</p>
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
                                Faculty
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Employee ID
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Designation
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Roles
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Status
                            </th>
                            <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Actions
                            </th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {faculty.content.map((member, index) => (
                            <motion.tr
                                key={member.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                className="hover:bg-gray-50 transition-colors duration-200"
                            >
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex items-center">
                                        <div className="h-10 w-10 flex-shrink-0">
                                            {member.profilePicture ? (
                                                <img
                                                    className="h-10 w-10 rounded-full object-cover"
                                                    src={member.profilePicture}
                                                    alt={member.fullName}
                                                />
                                            ) : (
                                                <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                                                    <span className="text-primary-600 font-medium">
                                                        {formatters.getInitials(member.fullName)}
                                                    </span>
                                                </div>
                                            )}
                                        </div>
                                        <div className="ml-4">
                                            <div className="text-sm font-medium text-gray-900">
                                                {member.fullName}
                                            </div>
                                            <div className="text-sm text-gray-500">
                                                {member.email}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {member.employeeId}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {member.designation || '-'}
                                </td>
                                <td className="px-6 py-4">
                                    <div className="flex flex-wrap">
                                        {getRoleBadges(member.roles)}
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                        member.isActive 
                                            ? 'bg-green-100 text-green-800' 
                                            : 'bg-red-100 text-red-800'
                                    }`}>
                                        {member.isActive ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <div className="flex items-center justify-end space-x-3">
                                        <button
                                            onClick={() => onEdit(member)}
                                            className="text-primary-600 hover:text-primary-900 transition-colors"
                                            title="Edit"
                                        >
                                            <FiEdit2 className="w-5 h-5" />
                                        </button>
                                        <button
                                            onClick={() => onAssignRoles(member)}
                                            className="text-purple-600 hover:text-purple-900 transition-colors"
                                            title="Assign Roles"
                                        >
                                            <FiUserCheck className="w-5 h-5" />
                                        </button>
                                        {member.isActive ? (
                                            <button
                                                onClick={() => onDeactivate(member)}
                                                className="text-red-600 hover:text-red-900 transition-colors"
                                                title="Deactivate"
                                            >
                                                <FiUserX className="w-5 h-5" />
                                            </button>
                                        ) : (
                                            <button
                                                onClick={() => onActivate(member)}
                                                className="text-green-600 hover:text-green-900 transition-colors"
                                                title="Activate"
                                            >
                                                <FiUserCheck className="w-5 h-5" />
                                            </button>
                                        )}
                                    </div>
                                </td>
                            </motion.tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Mobile Card View */}
            <div className="md:hidden divide-y divide-gray-200">
                {faculty.content.map((member) => (
                    <motion.div
                        key={member.id}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="p-4 hover:bg-gray-50 transition-colors duration-200 relative"
                    >
                        <div className="flex items-start space-x-3">
                            <div className="flex-shrink-0">
                                {member.profilePicture ? (
                                    <img
                                        className="h-12 w-12 rounded-full object-cover"
                                        src={member.profilePicture}
                                        alt={member.fullName}
                                    />
                                ) : (
                                    <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                                        <span className="text-primary-600 font-medium text-lg">
                                            {formatters.getInitials(member.fullName)}
                                        </span>
                                    </div>
                                )}
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between">
                                    <p className="text-sm font-medium text-gray-900 truncate">
                                        {member.fullName}
                                    </p>
                                    <span className={`ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                                        member.isActive 
                                            ? 'bg-green-100 text-green-800' 
                                            : 'bg-red-100 text-red-800'
                                    }`}>
                                        {member.isActive ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                                <p className="text-sm text-gray-500 truncate">{member.email}</p>
                                <p className="text-xs text-gray-400 mt-1">{member.employeeId} • {member.designation || 'No designation'}</p>
                                <div className="mt-2 flex flex-wrap">
                                    {getRoleBadges(member.roles)}
                                </div>
                                <div className="mt-3 flex items-center space-x-3">
                                    <button
                                        onClick={() => onEdit(member)}
                                        className="flex-1 inline-flex justify-center items-center px-3 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
                                    >
                                        <FiEdit2 className="w-4 h-4 mr-1" />
                                        Edit
                                    </button>
                                    <button
                                        onClick={() => onAssignRoles(member)}
                                        className="flex-1 inline-flex justify-center items-center px-3 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
                                    >
                                        <FiUserCheck className="w-4 h-4 mr-1" />
                                        Roles
                                    </button>
                                    {member.isActive ? (
                                        <button
                                            onClick={() => onDeactivate(member)}
                                            className="flex-1 inline-flex justify-center items-center px-3 py-1.5 border border-red-300 shadow-sm text-xs font-medium rounded-md text-red-700 bg-white hover:bg-red-50"
                                        >
                                            <FiUserX className="w-4 h-4 mr-1" />
                                            Deactivate
                                        </button>
                                    ) : (
                                        <button
                                            onClick={() => onActivate(member)}
                                            className="flex-1 inline-flex justify-center items-center px-3 py-1.5 border border-green-300 shadow-sm text-xs font-medium rounded-md text-green-700 bg-white hover:bg-green-50"
                                        >
                                            <FiUserCheck className="w-4 h-4 mr-1" />
                                            Activate
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    </motion.div>
                ))}
            </div>

            {/* Pagination */}
            {faculty.totalPages > 1 && (
                <div className="px-6 py-4 bg-gray-50 border-t border-gray-200">
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-gray-700">
                            Showing <span className="font-medium">{faculty.content.length}</span> of{' '}
                            <span className="font-medium">{faculty.totalElements}</span> results
                        </div>
                        <div className="flex space-x-2">
                            {/* Add pagination buttons here */}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default FacultyTable;