import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectIsAuthenticated, selectIsFirstLogin } from '../store/authSlice';

const PublicRoute = ({ children }) => {
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const isFirstLogin = useSelector(selectIsFirstLogin);
  const location = useLocation();

  if (isAuthenticated) {
    if (isFirstLogin) {
      if (location.pathname !== '/first-login') {
        return <Navigate to="/first-login" replace />;
      }
      return children;
    }
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default PublicRoute;