import axiosInstance from '../api/axiosConfig';

const facultyService = {
    // Get all faculty with filters
    getAllFaculty: async (params = {}) => {
        const response = await axiosInstance.get('/hod/faculty', { params });
        return response.data;
    },

    // Get faculty by ID
    getFacultyById: async (id) => {
        const response = await axiosInstance.get(`/hod/faculty/${id}`);
        return response.data;
    },

    // Add new faculty
    addFaculty: async (facultyData) => {
        const response = await axiosInstance.post('/hod/faculty', facultyData);
        return response.data;
    },

    // Update faculty
    updateFaculty: async (id, facultyData) => {
        const response = await axiosInstance.put(`/hod/faculty/${id}`, facultyData);
        return response.data;
    },

    // Deactivate faculty
    deactivateFaculty: async (id, reason = '') => {
        const response = await axiosInstance.patch(`/hod/faculty/${id}/deactivate?reason=${encodeURIComponent(reason)}`);
        return response.data;
    },

    // Activate faculty
    activateFaculty: async (id) => {
        const response = await axiosInstance.patch(`/hod/faculty/${id}/activate`);
        return response.data;
    },

    // Assign roles
    assignRoles: async (id, roles) => {
        const response = await axiosInstance.post(`/hod/faculty/${id}/roles`, roles);
        return response.data;
    },

    // Get faculty statistics
    getFacultyStatistics: async () => {
        const response = await axiosInstance.get('/hod/faculty/stats');
        return response.data;
    },

    // Check if employee ID exists
    checkEmployeeId: async (employeeId) => {
        const response = await axiosInstance.get(`/hod/faculty/check/employee/${employeeId}`);
        return response.data;
    },

    // Check if email exists
    checkEmail: async (email) => {
        const response = await axiosInstance.get(`/hod/faculty/check/email/${email}`);
        return response.data;
    }
};

export default facultyService;