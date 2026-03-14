import axiosInstance from '../api/axiosConfig';

const hodService = {
    // Dashboard Stats
    getDashboardStats: async () => {
        const response = await axiosInstance.get('/hod/dashboard/stats');
        return response.data;
    },

    getRecentActivities: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/dashboard/activities', {
            params: { page, size }
        });
        return response.data;
    },

    getDepartmentOverview: async () => {
        const response = await axiosInstance.get('/hod/dashboard/overview');
        return response.data;
    },

    getYearWiseDistribution: async () => {
        const response = await axiosInstance.get('/hod/dashboard/distribution/year');
        return response.data;
    },

    getSectionWiseDistribution: async (year = null) => {
        const response = await axiosInstance.get('/hod/dashboard/distribution/section', {
            params: { year }
        });
        return response.data;
    },

    getPendingCounts: async () => {
        const response = await axiosInstance.get('/hod/dashboard/pending-counts');
        return response.data;
    },

    getFacultyRoleDistribution: async () => {
        const response = await axiosInstance.get('/hod/dashboard/faculty-roles');
        return response.data;
    },

    getProfile: async () => {
        const response = await axiosInstance.get('/hod/profile');
        return response.data;
    },

    updateProfile: async (profileData) => {
        const response = await axiosInstance.put('/hod/profile', profileData);
        return response.data;
    }
};

export default hodService;