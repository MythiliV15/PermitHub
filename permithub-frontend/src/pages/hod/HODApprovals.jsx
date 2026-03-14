import React, { useState, useEffect, useCallback } from 'react';
import { motion } from 'framer-motion';
import { FiFilter, FiDownload } from 'react-icons/fi';
import ApprovalTable from '../../components/hod/ApprovalTable';
import ApprovalModal from '../../components/hod/ApprovalModal';
import approvalService from '../../services/approvalService';
import { useToast } from '../../hooks/useToast';

const HODApprovals = () => {
    const { showSuccess, showError, showLoading, dismiss } = useToast();

    const [activeTab, setActiveTab] = useState('all');
    const [approvals, setApprovals] = useState({
        all: { content: [], totalElements: 0 },
        leave: { content: [], totalElements: 0 },
        od: { content: [], totalElements: 0 },
        outpass: { content: [], totalElements: 0 }
    });
    const [loading, setLoading] = useState({
        all: false,
        leave: false,
        od: false,
        outpass: false
    });
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState(null);
    const [stats, setStats] = useState(null);
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [filters, setFilters] = useState({
        fromDate: '',
        toDate: '',
        department: '',
        status: ''
    });
    const [showFilters, setShowFilters] = useState(false);

    const tabs = [
        { id: 'all', label: 'All Requests', icon: '📋', count: approvals.all.totalElements },
        { id: 'leave', label: 'Leave', icon: '🏖️', count: approvals.leave.totalElements },
        { id: 'od', label: 'OD', icon: '🎯', count: approvals.od.totalElements },
        { id: 'outpass', label: 'Outpass', icon: '🚪', count: approvals.outpass.totalElements }
    ];

    const fetchApprovals = useCallback(async () => {
        setLoading(prev => ({ ...prev, [activeTab]: true }));
        try {
            let response;
            switch (activeTab) {
                case 'leave':
                    response = await approvalService.getPendingLeaves(page, size);
                    setApprovals(prev => ({ ...prev, leave: response.data }));
                    break;
                case 'od':
                    response = await approvalService.getPendingOD(page, size);
                    setApprovals(prev => ({ ...prev, od: response.data }));
                    break;
                case 'outpass':
                    response = await approvalService.getPendingOutpass(page, size);
                    setApprovals(prev => ({ ...prev, outpass: response.data }));
                    break;
                default:
                    response = await approvalService.getPendingApprovals(page, size);
                    setApprovals(prev => ({ ...prev, all: response.data }));
            }
        } catch (error) {
            showError('Failed to fetch approvals');
        } finally {
            setLoading(prev => ({ ...prev, [activeTab]: false }));
        }
    }, [activeTab, page, size, showError]);

    const fetchStats = useCallback(async () => {
        try {
            const response = await approvalService.getApprovalStatistics();
            setStats(response.data);
        } catch (error) {
            console.error('Failed to fetch stats:', error);
        }
    }, []);

    useEffect(() => {
        fetchApprovals();
        fetchStats();
    }, [fetchApprovals, fetchStats, filters]);

    const handleApprove = async (requestIds, remarks = '') => {
        if (!requestIds || requestIds.length === 0) return;

        const toastId = showLoading('Processing approval...');
        try {
            if (requestIds.length === 1) {
                await approvalService.approveRequest(requestIds[0], activeTab === 'all' ? 'LEAVE' : activeTab.toUpperCase(), remarks);
            } else {
                await approvalService.bulkApprove(activeTab === 'all' ? 'LEAVE' : activeTab.toUpperCase(), requestIds, remarks);
            }
            dismiss(toastId);
            showSuccess(`${requestIds.length} request(s) approved successfully`);
            fetchApprovals();
            fetchStats();
            setShowModal(false);
            setSelectedRequest(null);
        } catch (error) {
            dismiss(toastId);
            showError(error.message || 'Failed to approve request');
        }
    };

    const handleReject = async (requestIds, remarks = '') => {
        if (!requestIds || requestIds.length === 0) return;

        const toastId = showLoading('Processing rejection...');
        try {
            if (requestIds.length === 1) {
                await approvalService.rejectRequest(requestIds[0], activeTab === 'all' ? 'LEAVE' : activeTab.toUpperCase(), remarks);
            } else {
                await approvalService.bulkReject(activeTab === 'all' ? 'LEAVE' : activeTab.toUpperCase(), requestIds, remarks);
            }
            dismiss(toastId);
            showSuccess(`${requestIds.length} request(s) rejected successfully`);
            fetchApprovals();
            fetchStats();
            setShowModal(false);
            setSelectedRequest(null);
        } catch (error) {
            dismiss(toastId);
            showError(error.message || 'Failed to reject request');
        }
    };

    const handleViewDetails = async (request) => {
        try {
            const details = await approvalService.getRequestDetails(
                request.type || activeTab.toUpperCase(),
                request.id
            );
            setSelectedRequest(details.data);
            setModalType(request.type || activeTab.toUpperCase());
            setShowModal(true);
        } catch (error) {
            showError('Failed to fetch request details');
        }
    };

    const getCurrentApprovals = () => {
        switch (activeTab) {
            case 'leave': return approvals.leave;
            case 'od': return approvals.od;
            case 'outpass': return approvals.outpass;
            default: return approvals.all;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Pending Approvals</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                Review and manage pending requests
                            </p>
                        </div>
                        <div className="flex items-center space-x-3">
                            <button
                                onClick={() => setShowFilters(!showFilters)}
                                className="inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiFilter className="mr-2" />
                                Filters
                            </button>
                            <button
                                onClick={() => {/* Export functionality */}}
                                className="inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiDownload className="mr-2" />
                                Export
                            </button>
                        </div>
                    </div>

                    {/* Stats Cards */}
                    {stats && (
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-4">
                            <div className="bg-blue-50 rounded-lg p-3">
                                <p className="text-xs text-blue-600 font-medium">Pending</p>
                                <p className="text-xl font-bold text-blue-700">
                                    {stats.pendingLeaves + stats.pendingODs + stats.pendingOutpass}
                                </p>
                            </div>
                            <div className="bg-green-50 rounded-lg p-3">
                                <p className="text-xs text-green-600 font-medium">Approved (This Month)</p>
                                <p className="text-xl font-bold text-green-700">{stats.approvedThisMonth || 0}</p>
                            </div>
                            <div className="bg-red-50 rounded-lg p-3">
                                <p className="text-xs text-red-600 font-medium">Rejected (This Month)</p>
                                <p className="text-xl font-bold text-red-700">{stats.rejectedThisMonth || 0}</p>
                            </div>
                            <div className="bg-purple-50 rounded-lg p-3">
                                <p className="text-xs text-purple-600 font-medium">Avg. Processing</p>
                                <p className="text-xl font-bold text-purple-700">{stats.avgProcessingTime || 0}d</p>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Filters Panel */}
            {showFilters && (
                <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="bg-white border-b border-gray-200"
                >
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    From Date
                                </label>
                                <input
                                    type="date"
                                    value={filters.fromDate}
                                    onChange={(e) => setFilters(prev => ({ ...prev, fromDate: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    To Date
                                </label>
                                <input
                                    type="date"
                                    value={filters.toDate}
                                    onChange={(e) => setFilters(prev => ({ ...prev, toDate: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Status
                                </label>
                                <select
                                    value={filters.status}
                                    onChange={(e) => setFilters(prev => ({ ...prev, status: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                >
                                    <option value="">All Status</option>
                                    <option value="PENDING">Pending</option>
                                    <option value="APPROVED">Approved</option>
                                    <option value="REJECTED">Rejected</option>
                                </select>
                            </div>
                            <div className="flex items-end space-x-2">
                                <button
                                    onClick={() => {
                                        setFilters({ fromDate: '', toDate: '', department: '', status: '' });
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
                                    Apply
                                </button>
                            </div>
                        </div>
                    </div>
                </motion.div>
            )}

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Tabs - Mobile Responsive */}
                <div className="flex overflow-x-auto pb-2 mb-6 -mx-4 px-4 sm:mx-0 sm:px-0">
                    <div className="flex space-x-2">
                        {tabs.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => {
                                    setActiveTab(tab.id);
                                    setPage(0);
                                }}
                                className={`flex items-center px-4 py-2 rounded-lg whitespace-nowrap transition-colors ${
                                    activeTab === tab.id
                                        ? 'bg-primary-600 text-white'
                                        : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-200'
                                }`}
                            >
                                <span className="mr-2 text-lg">{tab.icon}</span>
                                <span className="font-medium">{tab.label}</span>
                                {tab.count > 0 && (
                                    <span className={`ml-2 px-2 py-0.5 rounded-full text-xs ${
                                        activeTab === tab.id
                                            ? 'bg-white text-primary-600'
                                            : 'bg-primary-100 text-primary-600'
                                    }`}>
                                        {tab.count}
                                    </span>
                                )}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Approval Table */}
                <ApprovalTable
                    approvals={getCurrentApprovals().content}
                    type={activeTab}
                    loading={loading[activeTab]}
                    onApprove={(ids) => {
                        if (ids.length === 1) {
                            const request = getCurrentApprovals().content.find(r => r.id === ids[0]);
                            setSelectedRequest(request);
                            setModalType(activeTab === 'all' ? request.type : activeTab.toUpperCase());
                            setShowModal(true);
                        } else {
                            handleApprove(ids, 'Bulk approved by HOD');
                        }
                    }}
                    onReject={(ids) => {
                        if (ids.length === 1) {
                            const request = getCurrentApprovals().content.find(r => r.id === ids[0]);
                            setSelectedRequest(request);
                            setModalType(activeTab === 'all' ? request.type : activeTab.toUpperCase());
                            setShowModal(true);
                        } else {
                            handleReject(ids, 'Bulk rejected by HOD');
                        }
                    }}
                    onViewDetails={handleViewDetails}
                />

                {/* Pagination */}
                {getCurrentApprovals().totalPages > 1 && (
                    <div className="mt-6 flex items-center justify-between">
                        <p className="text-sm text-gray-700">
                            Showing <span className="font-medium">{page * size + 1}</span> to{' '}
                            <span className="font-medium">
                                {Math.min((page + 1) * size, getCurrentApprovals().totalElements)}
                            </span>{' '}
                            of <span className="font-medium">{getCurrentApprovals().totalElements}</span> results
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
                                disabled={page >= getCurrentApprovals().totalPages - 1}
                                className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                            >
                                Next
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Approval Modal */}
            <ApprovalModal
                isOpen={showModal}
                onClose={() => {
                    setShowModal(false);
                    setSelectedRequest(null);
                }}
                request={selectedRequest}
                type={modalType}
                onAction={async (action, remarks) => {
                    if (action === 'APPROVE') {
                        await handleApprove([selectedRequest.id], remarks);
                    } else {
                        await handleReject([selectedRequest.id], remarks);
                    }
                }}
                loading={loading[activeTab]}
            />
        </div>
    );
};

export default HODApprovals;