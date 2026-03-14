import axiosInstance from '../api/axiosConfig';

const approvalService = {
    // Get all pending approvals
    getPendingApprovals: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/approvals/pending', {
            params: { page, size }
        });
        return response.data;
    },

    // Get pending leave approvals
    getPendingLeaves: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/approvals/pending/leaves', {
            params: { page, size }
        });
        return response.data;
    },

    // Get pending OD approvals
    getPendingOD: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/approvals/pending/od', {
            params: { page, size }
        });
        return response.data;
    },

    // Get pending outpass approvals
    getPendingOutpass: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/approvals/pending/outpass', {
            params: { page, size }
        });
        return response.data;
    },

    // Approve request
    approveRequest: async (requestId, requestType, remarks = '') => {
        const response = await axiosInstance.post('/hod/approvals/approve', {
            requestId,
            requestType,
            action: 'APPROVE',
            remarks
        });
        return response.data;
    },

    // Reject request
    rejectRequest: async (requestId, requestType, remarks = '') => {
        const response = await axiosInstance.post('/hod/approvals/reject', {
            requestId,
            requestType,
            action: 'REJECT',
            remarks
        });
        return response.data;
    },

    // Bulk approve
    bulkApprove: async (type, requestIds, remarks = '') => {
        const response = await axiosInstance.post(`/hod/approvals/bulk-approve?type=${type}&remarks=${encodeURIComponent(remarks)}`, requestIds);
        return response.data;
    },

    // Bulk reject
    bulkReject: async (type, requestIds, remarks = '') => {
        const response = await axiosInstance.post(`/hod/approvals/bulk-reject?type=${type}&remarks=${encodeURIComponent(remarks)}`, requestIds);
        return response.data;
    },

    // Get approval history
    getApprovalHistory: async (type = null, page = 0, size = 10) => {
        const params = { page, size };
        if (type) params.type = type;
        const response = await axiosInstance.get('/hod/approvals/history', { params });
        return response.data;
    },

    // Get request details
    getRequestDetails: async (type, id) => {
        const response = await axiosInstance.get(`/hod/approvals/request/${type}/${id}`);
        return response.data;
    },

    // Get approval statistics
    getApprovalStatistics: async () => {
        const response = await axiosInstance.get('/hod/approvals/statistics');
        return response.data;
    }
};

export default approvalService;