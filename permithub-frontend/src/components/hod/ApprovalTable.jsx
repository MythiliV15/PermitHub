import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { FiCheck, FiX, FiEye, FiCalendar, FiClock } from 'react-icons/fi';
import { formatters } from '../../utils/formatters';

const ApprovalTable = ({ approvals, type, loading, onApprove, onReject, onViewDetails }) => {
    const [selectedItems, setSelectedItems] = useState([]);
    const [selectAll, setSelectAll] = useState(false);

    const handleSelectAll = () => {
        if (selectAll) {
            setSelectedItems([]);
        } else {
            setSelectedItems(approvals.map(a => a.id));
        }
        setSelectAll(!selectAll);
    };

    const handleSelectItem = (id) => {
        if (selectedItems.includes(id)) {
            setSelectedItems(selectedItems.filter(item => item !== id));
        } else {
            setSelectedItems([...selectedItems, id]);
        }
    };

    const getTypeIcon = (type) => {
        switch(type) {
            case 'LEAVE': return '🏖️';
            case 'OD': return '🎯';
            case 'OUTPASS': return '🚪';
            default: return '📋';
        }
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
                                <div className="h-10 w-10 bg-gray-200 rounded"></div>
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

    if (!approvals?.length) {
        return (
            <div className="bg-white rounded-xl shadow-sm p-12 text-center">
                <div className="text-6xl mb-4">✅</div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">No Pending Approvals</h3>
                <p className="text-gray-500">All caught up! No pending requests at the moment.</p>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            {/* Bulk Actions */}
            {selectedItems.length > 0 && (
                <div className="bg-primary-50 px-4 py-3 flex items-center justify-between">
                    <span className="text-sm text-primary-700">
                        {selectedItems.length} item(s) selected
                    </span>
                    <div className="flex space-x-2">
                        <button
                            onClick={() => onApprove(selectedItems)}
                            className="px-3 py-1 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700"
                        >
                            Approve All
                        </button>
                        <button
                            onClick={() => onReject(selectedItems)}
                            className="px-3 py-1 bg-red-600 text-white text-sm rounded-lg hover:bg-red-700"
                        >
                            Reject All
                        </button>
                    </div>
                </div>
            )}

            {/* Desktop Table View */}
            <div className="hidden md:block overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th scope="col" className="px-6 py-3 text-left">
                                <input
                                    type="checkbox"
                                    checked={selectAll}
                                    onChange={handleSelectAll}
                                    className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                />
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Student
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Type
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Details
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Date
                            </th>
                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Previous Approvals
                            </th>
                            <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Actions
                            </th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {approvals.map((approval, index) => (
                            <motion.tr
                                key={approval.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                className="hover:bg-gray-50 transition-colors duration-200"
                            >
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <input
                                        type="checkbox"
                                        checked={selectedItems.includes(approval.id)}
                                        onChange={() => handleSelectItem(approval.id)}
                                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                    />
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex items-center">
                                        <div className="h-10 w-10 flex-shrink-0">
                                            <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                                                <span className="text-primary-600 font-medium">
                                                    {formatters.getInitials(approval.studentName)}
                                                </span>
                                            </div>
                                        </div>
                                        <div className="ml-4">
                                            <div className="text-sm font-medium text-gray-900">
                                                {approval.studentName}
                                            </div>
                                            <div className="text-sm text-gray-500">
                                                {approval.registerNumber}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span className="text-2xl">{getTypeIcon(approval.type)}</span>
                                </td>
                                <td className="px-6 py-4">
                                    <div className="text-sm text-gray-900">
                                        {approval.type === 'LEAVE' && (
                                            <>
                                                <span className="font-medium">{approval.category}</span>
                                                <br />
                                                <span className="text-gray-500">{approval.totalDays} days</span>
                                            </>
                                        )}
                                        {approval.type === 'OD' && (
                                            <>
                                                <span className="font-medium">{approval.eventName}</span>
                                                <br />
                                                <span className="text-gray-500">{approval.eventType}</span>
                                            </>
                                        )}
                                        {approval.type === 'OUTPASS' && (
                                            <>
                                                <span className="font-medium">{approval.destination}</span>
                                                <br />
                                                <span className="text-gray-500">{approval.reason}</span>
                                            </>
                                        )}
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="flex items-center text-sm text-gray-500">
                                        <FiCalendar className="mr-1" />
                                        {formatters.formatDate(approval.appliedDate)}
                                    </div>
                                    <div className="flex items-center text-xs text-gray-400 mt-1">
                                        <FiClock className="mr-1" />
                                        {formatters.formatTimeAgo(approval.appliedDate)}
                                    </div>
                                </td>
                                <td className="px-6 py-4">
                                    <div className="space-y-1">
                                        {approval.mentorRemark && (
                                            <div className="text-xs">
                                                <span className="font-medium text-blue-600">Mentor:</span>
                                                <span className="text-gray-500 ml-1">{approval.mentorRemark}</span>
                                            </div>
                                        )}
                                        {approval.classAdvisorRemark && (
                                            <div className="text-xs">
                                                <span className="font-medium text-green-600">Class Advisor:</span>
                                                <span className="text-gray-500 ml-1">{approval.classAdvisorRemark}</span>
                                            </div>
                                        )}
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <div className="flex items-center justify-end space-x-2">
                                        <button
                                            onClick={() => onViewDetails(approval)}
                                            className="text-gray-400 hover:text-gray-600 p-1"
                                            title="View Details"
                                        >
                                            <FiEye className="w-5 h-5" />
                                        </button>
                                        <button
                                            onClick={() => onApprove([approval.id])}
                                            className="text-green-600 hover:text-green-900 p-1"
                                            title="Approve"
                                        >
                                            <FiCheck className="w-5 h-5" />
                                        </button>
                                        <button
                                            onClick={() => onReject([approval.id])}
                                            className="text-red-600 hover:text-red-900 p-1"
                                            title="Reject"
                                        >
                                            <FiX className="w-5 h-5" />
                                        </button>
                                    </div>
                                </td>
                            </motion.tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Mobile Card View */}
            <div className="md:hidden divide-y divide-gray-200">
                {approvals.map((approval) => (
                    <motion.div
                        key={approval.id}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="p-4 hover:bg-gray-50 transition-colors duration-200"
                    >
                        <div className="flex items-start space-x-3">
                            <div className="flex-shrink-0">
                                <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center text-2xl">
                                    {getTypeIcon(approval.type)}
                                </div>
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between">
                                    <p className="text-sm font-medium text-gray-900">
                                        {approval.studentName}
                                    </p>
                                    <span className="text-xs text-gray-500">
                                        {formatters.formatTimeAgo(approval.appliedDate)}
                                    </span>
                                </div>
                                <p className="text-xs text-gray-500">{approval.registerNumber}</p>
                                
                                <div className="mt-2 p-2 bg-gray-50 rounded-lg">
                                    {approval.type === 'LEAVE' && (
                                        <p className="text-sm">
                                            <span className="font-medium">{approval.category}:</span>{' '}
                                            {approval.totalDays} days ({formatters.formatDate(approval.startDate)} - {formatters.formatDate(approval.endDate)})
                                        </p>
                                    )}
                                    {approval.type === 'OD' && (
                                        <p className="text-sm">
                                            <span className="font-medium">{approval.eventName}</span>
                                            <br />
                                            <span className="text-xs text-gray-500">{approval.eventType}</span>
                                        </p>
                                    )}
                                    {approval.type === 'OUTPASS' && (
                                        <p className="text-sm">
                                            <span className="font-medium">{approval.destination}</span>
                                            <br />
                                            <span className="text-xs text-gray-500">{approval.reason}</span>
                                        </p>
                                    )}
                                </div>

                                <div className="mt-3 flex items-center space-x-2">
                                    <button
                                        onClick={() => onViewDetails(approval)}
                                        className="flex-1 px-3 py-2 bg-gray-100 text-gray-700 text-xs font-medium rounded-lg hover:bg-gray-200"
                                    >
                                        Details
                                    </button>
                                    <button
                                        onClick={() => onApprove([approval.id])}
                                        className="flex-1 px-3 py-2 bg-green-600 text-white text-xs font-medium rounded-lg hover:bg-green-700"
                                    >
                                        Approve
                                    </button>
                                    <button
                                        onClick={() => onReject([approval.id])}
                                        className="flex-1 px-3 py-2 bg-red-600 text-white text-xs font-medium rounded-lg hover:bg-red-700"
                                    >
                                        Reject
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

export default ApprovalTable;