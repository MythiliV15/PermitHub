import axiosInstance from './axiosConfig';

const authAPI = {
  // Login user
  login: async (credentials) => {
    const response = await axiosInstance.post('/auth/login', credentials);
    return response; // return full Axios response so authSlice can access .data
  },

  // Forgot password
  forgotPassword: async (email) => {
    const response = await axiosInstance.post('/auth/forgot-password', { email });
    return response;
  },

  // Reset password
  resetPassword: async (data) => {
    const response = await axiosInstance.post('/auth/reset-password', data);
    return response;
  },

  // Change password on first login
  changePassword: async (userId, oldPassword, newPassword) => {
    const response = await axiosInstance.post(`/users/${userId}/change-password`, {
      oldPassword,
      newPassword,
    });
    return response;
  },

  // Logout
  logout: async () => {
    const token = localStorage.getItem(process.env.REACT_APP_TOKEN_KEY);
    if (token) {
      await axiosInstance.post('/auth/logout', null, {
        headers: { Authorization: `Bearer ${token}` },
      });
    }
  },

  // Refresh token
  refreshToken: async () => {
    const response = await axiosInstance.post('/auth/refresh-token');
    return response;
  },
};

export default authAPI;