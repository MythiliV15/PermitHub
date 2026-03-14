import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { FiArrowLeft, FiSearch, FiCalendar, FiUser, FiCheckCircle, FiXCircle, FiClock } from 'react-icons/fi';
import approvalService from '../../services/approvalService';
import { useToast } from '../../hooks/useToast';
import { formatters } from '../../utils/formatters';

const ApprovalHistory = () => {
    const navigate = useNavigate();
    const { showError } = useToast();

    const [history, setHistory] = useState({
        content: [],
        totalElements: 0,
        totalPages: 0
    });
    const [loading, setLoading] = useState(false);
    const [filters, setFilters] = useState({
        type: '',
        fromDate: '',
        toDate: '',
        search: ''
    });
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [showFilters, setShowFilters] = useState(false);

    const fetchHistory = useCallback(async () => {
        setLoading(true);
        try {
            const response = await approvalService.getApprovalHistory(
                filters.type || null,
                page,
                size
            );
            setHistory(response.data);
        } catch (error) {
            showError('Failed to fetch approval history');
        } finally {
            setLoading(false);
        }
    }, [filters.type, page, size, showError]);

    useEffect(() => {
        fetchHistory();
    }, [fetchHistory]);

    const getActionIcon = (action) => {
        switch (action?.toUpperCase()) {
            case 'APPROVED':
                return <FiCheckCircle className="w-5 h-5 text-green-500" />;
            case 'REJECTED':
                return <FiXCircle className="w-5 h-5 text-red-500" />;
            default:
                return <FiClock className="w-5 h-5 text-yellow-500" />;
        }
    };

    const getStatusColor = (action) => {
        switch (action?.toUpperCase()) {
            case 'APPROVED':
                return 'bg-green-100 text-green-800';
            case 'REJECTED':
                return 'bg-red-100 text-red-800';
            default:
                return 'bg-yellow-100 text-yellow-800';
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center">
                        <button
                            onClick={() => navigate(-1)}
                            className="mr-4 p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
                        >
                            <FiArrowLeft className="w-5 h-5" />
                        </button>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Approval History</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                View all past approvals and rejections
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Filters */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="font-semibold text-gray-900">Filters</h2>
                        <button
                            onClick={() => setShowFilters(!showFilters)}
                            className="text-sm text-primary-600 hover:text-primary-700"
                        >
                            {showFilters ? 'Hide Filters' : 'Show Filters'}
                        </button>
                    </div>

                    {showFilters && (
                        <motion.div
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: 'auto' }}
                            exit={{ opacity: 0, height: 0 }}
                            className="space-y-4"
                        >
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                                {/* Search */}
                                <div className="relative">
                                    <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="text"
                                        placeholder="Search by student name or ID"
                                        value={filters.search}
                                        onChange={(e) => setFilters(prev => ({ ...prev, search: e.target.value }))}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>

                                {/* Request Type */}
                                <select
                                    value={filters.type}
                                    onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                >
                                    <option value="">All Types</option>
                                    <option value="LEAVE">Leave</option>
                                    <option value="OD">OD</option>
                                    <option value="OUTPASS">Outpass</option>
                                </select>

                                {/* From Date */}
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        value={filters.fromDate}
                                        onChange={(e) => setFilters(prev => ({ ...prev, fromDate: e.target.value }))}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>

                                {/* To Date */}
                                <div className="relative">
                                    <FiCalendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                                    <input
                                        type="date"
                                        value={filters.toDate}
                                        onChange={(e) => setFilters(prev => ({ ...prev, toDate: e.target.value }))}
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>
                            </div>

                            <div className="flex justify-end space-x-3">
                                <button
                                    onClick={() => {
                                        setFilters({ type: '', fromDate: '', toDate: '', search: '' });
                                        setPage(0);
                                    }}
                                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                                >
                                    Reset
                                </button>
                                <button
                                    onClick={() => setPage(0)}
                                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700"
                                >
                                    Apply Filters
                                </button>
                            </div>
                        </motion.div>
                    )}
                </div>
            </div>

            {/* History List */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    {/* Desktop Table View */}
                    <div className="hidden md:block overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Date & Time
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Student
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Type
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Details
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Action
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Approved By
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Remarks
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {loading ? (
                                    <tr>
                                        <td colSpan="7" className="px-6 py-12 text-center">
                                            <div className="flex justify-center">
                                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
                                            </div>
                                        </td>
                                    </tr>
                                ) : history.content.length > 0 ? (
                                    history.content.map((item, index) => (
                                        <motion.tr
                                            key={index}
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            transition={{ delay: index * 0.05 }}
                                            className="hover:bg-gray-50"
                                        >
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {formatters.formatDateTime(item.actionDate)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    <div className="h-8 w-8 rounded-full bg-primary-100 flex items-center justify-center">
                                                        <span className="text-primary-600 text-xs font-medium">
                                                            {formatters.getInitials(item.studentName)}
                                                        </span>
                                                    </div>
                                                    <div className="ml-3">
                                                        <div className="text-sm font-medium text-gray-900">
                                                            {item.studentName}
                                                        </div>
                                                        <div className="text-xs text-gray-500">
                                                            {item.registerNumber}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                                    {item.type}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-500">
                                                {item.details}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(item.action)}`}>
                                                    {getActionIcon(item.action)}
                                                    <span className="ml-1">{item.action}</span>
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    <FiUser className="w-4 h-4 text-gray-400 mr-1" />
                                                    <span className="text-sm text-gray-900">{item.approvedBy}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">
                                                {item.remarks || '-'}
                                            </td>
                                        </motion.tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="7" className="px-6 py-12 text-center">
                                            <div className="text-6xl mb-4">📜</div>
                                            <h3 className="text-lg font-medium text-gray-900 mb-2">No History Found</h3>
                                            <p className="text-gray-500">No approval history matches your filters</p>
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>

                    {/* Mobile Card View */}
                    <div className="md:hidden divide-y divide-gray-200">
                        {loading ? (
                            <div className="p-8 text-center">
                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
                            </div>
                        ) : history.content.length > 0 ? (
                            history.content.map((item, index) => (
                                <motion.div
                                    key={index}
                                    initial={{ opacity: 0 }}
                                    animate={{ opacity: 1 }}
                                    className="p-4 hover:bg-gray-50"
                                >
                                    <div className="flex items-start justify-between">
                                        <div className="flex items-center space-x-3">
                                            <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                                                <span className="text-primary-600 font-medium">
                                                    {formatters.getInitials(item.studentName)}
                                                </span>
                                            </div>
                                            <div>
                                                <p className="font-medium text-gray-900">{item.studentName}</p>
                                                <p className="text-xs text-gray-500">{item.registerNumber}</p>
                                            </div>
                                        </div>
                                        <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(item.action)}`}>
                                            {getActionIcon(item.action)}
                                            <span className="ml-1">{item.action}</span>
                                        </span>
                                    </div>

                                    <div className="mt-3 grid grid-cols-2 gap-2 text-xs">
                                        <div>
                                            <span className="text-gray-500">Type:</span>
                                            <span className="ml-1 font-medium text-gray-900">{item.type}</span>
                                        </div>
                                        <div>
                                            <span className="text-gray-500">Date:</span>
                                            <span className="ml-1 font-medium text-gray-900">
                                                {formatters.formatDate(item.actionDate)}
                                            </span>
                                        </div>
                                        <div>
                                            <span className="text-gray-500">Approved By:</span>
                                            <span className="ml-1 font-medium text-gray-900">{item.approvedBy}</span>
                                        </div>
                                        <div>
                                            <span className="text-gray-500">Time:</span>
                                            <span className="ml-1 font-medium text-gray-900">
                                                {formatters.formatTime(item.actionDate)}
                                            </span>
                                        </div>
                                    </div>

                                    <div className="mt-2 p-2 bg-gray-50 rounded-lg">
                                        <p className="text-xs text-gray-700">
                                            <span className="font-medium">Details:</span> {item.details}
                                        </p>
                                        {item.remarks && (
                                            <p className="text-xs text-gray-600 mt-1">
                                                <span className="font-medium">Remarks:</span> {item.remarks}
                                            </p>
                                        )}
                                    </div>
                                </motion.div>
                            ))
                        ) : (
                            <div className="p-8 text-center">
                                <div className="text-6xl mb-4">📜</div>
                                <h3 className="text-lg font-medium text-gray-900 mb-2">No History Found</h3>
                                <p className="text-gray-500">No approval history matches your filters</p>
                            </div>
                        )}
                    </div>

                    {/* Pagination */}
                    {history.totalPages > 1 && (
                        <div className="px-6 py-4 bg-gray-50 border-t border-gray-200">
                            <div className="flex items-center justify-between">
                                <p className="text-sm text-gray-700">
                                    Showing <span className="font-medium">{page * size + 1}</span> to{' '}
                                    <span className="font-medium">
                                        {Math.min((page + 1) * size, history.totalElements)}
                                    </span>{' '}
                                    of <span className="font-medium">{history.totalElements}</span> results
                                </p>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => setPage(p => Math.max(0, p - 1))}
                                        disabled={page === 0}
                                        className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                        Previous
                                    </button>
                                    <button
                                        onClick={() => setPage(p => p + 1)}
                                        disabled={page >= history.totalPages - 1}
                                        className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                        Next
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ApprovalHistory;