import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiLock, FiArrowLeft, FiCheckCircle } from 'react-icons/fi';
import AuthLayout from '../components/layout/AuthLayout';
import InputField from '../components/common/InputField';
import Button from '../components/common/Button';
import { useToast } from '../hooks/useToast';
import { resetPasswordSchema } from '../utils/validators';
import authAPI from '../api/authAPI';

const ResetPasswordPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isSuccess, setIsSuccess] = useState(false);
  const { showLoading, dismiss, showSuccess, showError } = useToast();

  // Get token from URL query params
  const queryParams = new URLSearchParams(location.search);
  const token = queryParams.get('token');

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(resetPasswordSchema),
    defaultValues: {
      token: token || '',
    },
  });

  const newPassword = watch('newPassword');

  useEffect(() => {
    if (!token) {
      showError('Invalid reset link');
      navigate('/forgot-password');
    }
  }, [token, navigate, showError]);

  const onSubmit = async (data) => {
    const toastId = showLoading('Resetting password...');
    
    try {
      await authAPI.resetPassword(data);
      dismiss(toastId);
      showSuccess('Password reset successful!');
      setIsSuccess(true);
      
      // Redirect to login after 3 seconds
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (error) {
      dismiss(toastId);
      showError(error.response?.data?.message || 'Password reset failed');
    }
  };

  if (!token) {
    return null;
  }

  return (
    <AuthLayout
      title={isSuccess ? "Password Reset Successful!" : "Create new password"}
      subtitle={isSuccess 
        ? "Your password has been reset successfully" 
        : "Enter your new password below"
      }
    >
      {!isSuccess ? (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <input type="hidden" {...register('token')} />

          <InputField
            label="New Password"
            type="password"
            name="newPassword"
            placeholder="Enter new password"
            icon={FiLock}
            register={register}
            error={errors.newPassword?.message}
            required
          />

          <InputField
            label="Confirm Password"
            type="password"
            name="confirmPassword"
            placeholder="Confirm new password"
            icon={FiLock}
            register={register}
            error={errors.confirmPassword?.message}
            required
          />

          {/* Password requirements */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <p className="text-sm font-medium text-gray-700 mb-2">Password must contain:</p>
            <ul className="text-xs space-y-1">
              <li className={newPassword?.length >= 8 ? 'text-green-600' : 'text-gray-500'}>
                ✓ At least 8 characters
              </li>
              <li className={/[A-Z]/.test(newPassword) ? 'text-green-600' : 'text-gray-500'}>
                ✓ One uppercase letter
              </li>
              <li className={/[a-z]/.test(newPassword) ? 'text-green-600' : 'text-gray-500'}>
                ✓ One lowercase letter
              </li>
              <li className={/\d/.test(newPassword) ? 'text-green-600' : 'text-gray-500'}>
                ✓ One number
              </li>
              <li className={/[@$!%*?&]/.test(newPassword) ? 'text-green-600' : 'text-gray-500'}>
                ✓ One special character (@$!%*?&)
              </li>
            </ul>
          </div>

          <Button
            type="submit"
            variant="primary"
            fullWidth
            loading={isSubmitting}
          >
            Reset Password
          </Button>

          <div className="text-center">
            <Link
              to="/login"
              className="inline-flex items-center text-sm text-primary-600 hover:text-primary-500"
            >
              <FiArrowLeft className="mr-2" />
              Back to login
            </Link>
          </div>
        </form>
      ) : (
        <div className="text-center space-y-6">
          <div className="flex justify-center">
            <FiCheckCircle className="h-16 w-16 text-green-500" />
          </div>
          
          <div className="bg-green-50 text-green-800 p-4 rounded-lg">
            <p className="text-sm">
              Your password has been reset successfully. You will be redirected to login page shortly.
            </p>
          </div>
          
          <Button
            variant="primary"
            onClick={() => navigate('/login')}
          >
            Go to Login
          </Button>
        </div>
      )}
    </AuthLayout>
  );
};

export default ResetPasswordPage;