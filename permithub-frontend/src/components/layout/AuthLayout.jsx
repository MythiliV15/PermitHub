import React from 'react';
import { Link } from 'react-router-dom';

const AuthLayout = ({ children, title, subtitle }) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-secondary-100 flex items-center justify-center p-4 sm:p-6 lg:p-8">
      <div className="max-w-md w-full space-y-8 animate-slide-up">
        {/* Logo and Title */}
        <div className="text-center">
          <div className="flex justify-center">
            <div className="h-16 w-16 bg-primary-600 rounded-2xl flex items-center justify-center shadow-lg">
              <span className="text-white text-3xl font-bold">PH</span>
            </div>
          </div>
          <h2 className="mt-6 text-3xl sm:text-4xl font-extrabold text-gray-900">
            {title || 'Welcome to PermitHub'}
          </h2>
          {subtitle && (
            <p className="mt-2 text-sm sm:text-base text-gray-600">
              {subtitle}
            </p>
          )}
        </div>

        {/* Card with Form */}
        <div className="bg-white py-8 px-4 sm:px-10 shadow-xl rounded-2xl">
          {children}
        </div>

        {/* Footer Links */}
        <div className="text-center text-sm text-gray-600">
          <p>&copy; 2024 PermitHub. All rights reserved.</p>
          <div className="mt-2 space-x-4">
            <Link to="/privacy" className="text-primary-600 hover:text-primary-500">
              Privacy Policy
            </Link>
            <Link to="/terms" className="text-primary-600 hover:text-primary-500">
              Terms of Service
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;