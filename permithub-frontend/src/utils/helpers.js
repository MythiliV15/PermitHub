// Store user data in localStorage
export const setAuthData = (token, user) => {
  localStorage.setItem(process.env.REACT_APP_TOKEN_KEY, token);
  localStorage.setItem(process.env.REACT_APP_USER_KEY, JSON.stringify(user));
};

// Clear auth data from localStorage
export const clearAuthData = () => {
  localStorage.removeItem(process.env.REACT_APP_TOKEN_KEY);
  localStorage.removeItem(process.env.REACT_APP_USER_KEY);
};

// Get stored user
export const getStoredUser = () => {
  const userStr = localStorage.getItem(process.env.REACT_APP_USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

// Get stored token
export const getStoredToken = () => {
  return localStorage.getItem(process.env.REACT_APP_TOKEN_KEY);
};

// Format date
export const formatDate = (date, format = 'PPP') => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

// Format time
export const formatTime = (date) => {
  return new Date(date).toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
  });
};

// Check if device is mobile
export const isMobile = () => {
  return window.innerWidth < 768;
};

// Debounce function
export const debounce = (func, wait) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

// Generate random color
export const getRandomColor = (str) => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  const color = Math.floor(Math.abs((Math.sin(hash) * 16777215) % 16777215)).toString(16);
  return '#' + '0'.repeat(6 - color.length) + color;
};