export const ROLES = {
  STUDENT: 'STUDENT',
  FACULTY_MENTOR: 'FACULTY_MENTOR',
  FACULTY_CLASS_ADVISOR: 'FACULTY_CLASS_ADVISOR',
  FACULTY_EVENT_COORDINATOR: 'FACULTY_EVENT_COORDINATOR',
  HOD: 'HOD',
  WARDEN: 'WARDEN',
  AO: 'AO',
  PRINCIPAL: 'PRINCIPAL',
  SECURITY: 'SECURITY',
  PARENT: 'PARENT',
};

export const TOAST_MESSAGES = {
  LOGIN_SUCCESS: 'Login successful! Redirecting...',
  LOGIN_ERROR: 'Invalid email or password',
  FORGOT_PASSWORD_SUCCESS: 'Password reset link sent to your email',
  RESET_PASSWORD_SUCCESS: 'Password reset successful! Please login',
  PASSWORD_CHANGE_SUCCESS: 'Password changed successfully',
  NETWORK_ERROR: 'Network error. Please check your connection',
  SESSION_EXPIRED: 'Session expired. Please login again',
};

export const APP_NAME = process.env.REACT_APP_APP_NAME || 'PermitHub';

export const BREAKPOINTS = {
  sm: 640,
  md: 768,
  lg: 1024,
  xl: 1280,
  '2xl': 1536,
};

export const ANIMATION_DURATIONS = {
  fast: 200,
  normal: 300,
  slow: 500,
};