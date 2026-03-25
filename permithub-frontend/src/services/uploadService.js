import axiosInstance from '../api/axiosConfig';

const uploadService = {
    // Upload faculty via Excel
    uploadFaculty: async (file, onUploadProgress) => {
        const formData = new FormData();
        formData.append('file', file);

        const response = await axiosInstance.post('/hod/upload/faculty', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
            onUploadProgress
        });
        return response.data;
    },

    // Upload students via Excel
    uploadStudents: async (file, year, section, onUploadProgress) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('year', year);
        formData.append('section', section);

        const response = await axiosInstance.post('/hod/upload/students', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
            onUploadProgress
        });
        return response.data;
    },

    // Download faculty template
    downloadFacultyTemplate: async () => {
        const response = await axiosInstance.get('/hod/upload/template/faculty', {
            responseType: 'blob'
        });
        return response.data;
    },

    // Download student template
    downloadStudentTemplate: async () => {
        const response = await axiosInstance.get('/hod/upload/template/student', {
            responseType: 'blob'
        });
        return response.data;
    },

    // Get upload history
    getUploadHistory: async (page = 0, size = 10) => {
        const response = await axiosInstance.get('/hod/upload/history', {
            params: { page, size }
        });
        return response.data;
    },

    // Get upload details
    getUploadDetails: async (uploadId) => {
        const response = await axiosInstance.get(`/hod/upload/history/${uploadId}`);
        return response.data;
    },

    // Validate file
    validateFile: async (file, type) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('type', type);

        const response = await axiosInstance.post('/hod/upload/validate', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });
        return response.data;
    },

    // Preview upload
    previewUpload: async (file, type) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('type', type);

        const response = await axiosInstance.post('/hod/upload/preview', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });
        return response.data;
    }
};

export default uploadService;