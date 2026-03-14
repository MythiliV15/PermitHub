import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { 
  login, 
  logout, 
  selectUser, 
  selectIsAuthenticated,
  selectIsFirstLogin
} from '../store/authSlice';

export const useAuth = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector(selectUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const isFirstLogin = useSelector(selectIsFirstLogin);

  const handleLogin = useCallback(async (credentials) => {
    try {
      const payload = await dispatch(login(credentials)).unwrap();
      const userData = payload?.data || payload;
      
      if (userData.isFirstLogin) {
        navigate('/first-login');
      } else if (userData.roles?.includes('HOD')) {
        navigate('/hod/dashboard');
      } else {
        navigate('/dashboard');
      }
      
      return { success: true, data: userData };
    } catch (error) {
      return { success: false, error: error.message || error };
    }
  }, [dispatch, navigate]);

  const handleLogout = useCallback(async () => {
    await dispatch(logout());
    navigate('/login');
  }, [dispatch, navigate]);

  const checkAuth = useCallback(() => {
    return isAuthenticated;
  }, [isAuthenticated]);

  const hasRole = useCallback((role) => {
    return user?.roles?.includes(role) || false;
  }, [user]);

  const hasAnyRole = useCallback((roles) => {
    return roles.some(role => user?.roles?.includes(role));
  }, [user]);

  return {
    user,
    isAuthenticated,
    isFirstLogin,
    login: handleLogin,
    logout: handleLogout,
    checkAuth,
    hasRole,
    hasAnyRole,
  };
};