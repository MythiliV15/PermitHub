import axiosInstance from '../api/axiosConfig';

const semesterService = {
    // Get all semesters
    getAllSemesters: async (page = 0, size = 10, sortBy = 'year', direction = 'desc') => {
        const response = await axiosInstance.get('/hod/semester', {
            params: { page, size, sortBy, direction }
        });
        return response.data;
    },

    // Get semester by ID
    getSemesterById: async (id) => {
        const response = await axiosInstance.get(`/hod/semester/${id}`);
        return response.data;
    },

    // Get active semester
    getActiveSemester: async () => {
        const response = await axiosInstance.get('/hod/semester/active');
        return response.data;
    },

    // Create semester
    createSemester: async (semesterData) => {
        const response = await axiosInstance.post('/hod/semester', semesterData);
        return response.data;
    },

    // Update semester
    updateSemester: async (id, semesterData) => {
        const response = await axiosInstance.put(`/hod/semester/${id}`, semesterData);
        return response.data;
    },

    // Activate semester
    activateSemester: async (id) => {
        const response = await axiosInstance.post(`/hod/semester/${id}/activate`);
        return response.data;
    },

    // Deactivate semester
    deactivateSemester: async (id) => {
        const response = await axiosInstance.post(`/hod/semester/${id}/deactivate`);
        return response.data;
    },

    // Promote students
    promoteStudents: async (promotionData) => {
        const response = await axiosInstance.post('/hod/semester/promote', promotionData);
        return response.data;
    },

    // Reset leave balance
    resetLeaveBalance: async (newBalance = 20) => {
        const response = await axiosInstance.post(`/hod/semester/reset-leave-balance?newBalance=${newBalance}`);
        return response.data;
    },

    // Get promotion eligibility
    getPromotionEligibility: async (year, section = null) => {
        const params = { year };
        if (section) params.section = section;
        const response = await axiosInstance.get('/hod/semester/promotion-eligibility', { params });
        return response.data;
    },

    // Set default leave limit
    setDefaultLeaveLimit: async (semesterId, leaveLimit) => {
        const response = await axiosInstance.put(`/hod/semester/${semesterId}/leave-limit?leaveLimit=${leaveLimit}`);
        return response.data;
    },

    // Get semester statistics
    getSemesterStatistics: async () => {
        const response = await axiosInstance.get('/hod/semester/statistics');
        return response.data;
    }
};

export default semesterService;