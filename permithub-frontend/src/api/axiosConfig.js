import axios from 'axios';
import toast from 'react-hot-toast';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(process.env.REACT_APP_TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Log requests in development
    if (process.env.NODE_ENV === 'development') {
      console.log('🚀 Request:', {
        method: config.method,
        url: config.url,
        data: config.data,
        headers: config.headers,
      });
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    // Log responses in development
    if (process.env.NODE_ENV === 'development') {
      console.log('✅ Response:', {
        status: response.status,
        data: response.data,
      });
    }
    return response;
  },
  (error) => {
    // Handle errors
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      
      switch (status) {
        case 400:
          toast.error(data?.message || 'Bad request');
          break;
        case 401:
          // Only redirect if token exists (session expired), not on fresh login failures
          if (localStorage.getItem(process.env.REACT_APP_TOKEN_KEY)) {
            toast.error('Session expired. Please login again.');
            localStorage.removeItem(process.env.REACT_APP_TOKEN_KEY);
            localStorage.removeItem(process.env.REACT_APP_USER_KEY);
            window.location.href = '/login';
          } else {
            toast.error(data?.message || 'Invalid email or password');
          }
          break;
        case 403:
          toast.error('You do not have permission to perform this action');
          break;
        case 404:
          toast.error('Resource not found');
          break;
        case 500:
          toast.error('Server error. Please try again later.');
          break;
        default:
          toast.error(data?.message || 'An error occurred');
      }
    } else if (error.request) {
      // Request made but no response
      toast.error('Network error. Please check your connection.');
    } else {
      // Something else happened
      toast.error('An unexpected error occurred');
    }
    
    // Log errors in development
    if (process.env.NODE_ENV === 'development') {
      console.error('❌ Error:', error);
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;