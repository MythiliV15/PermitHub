import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiLock, FiAlertCircle } from 'react-icons/fi';
import AuthLayout from '../components/layout/AuthLayout';
import InputField from '../components/common/InputField';
import Button from '../components/common/Button';
import { useAuth } from '../hooks/useAuth';
import { useToast } from '../hooks/useToast';
import { firstLoginSchema } from '../utils/validators';
import authAPI from '../api/authAPI';

const FirstLoginPage = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { showLoading, dismiss, showSuccess, showError } = useToast();
  const [isSuccess, setIsSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(firstLoginSchema),
  });

  const newPassword = watch('newPassword');

  const onSubmit = async (data) => {
    const toastId = showLoading('Changing password...');
    
    try {
      await authAPI.changePassword(user.id, data.currentPassword, data.newPassword);
      dismiss(toastId);
      showSuccess('Password changed successfully!');
      setIsSuccess(true);
      
      // Logout and redirect to login after 3 seconds
      setTimeout(() => {
        logout();
        navigate('/login');
      }, 3000);
    } catch (error) {
      dismiss(toastId);
      showError(error.response?.data?.message || 'Failed to change password');
    }
  };

  return (
    <AuthLayout
      title={isSuccess ? "Password Changed!" : "First Time Login"}
      subtitle={isSuccess 
        ? "Your password has been changed successfully" 
        : "Please change your password to continue"
      }
    >
      {!isSuccess ? (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Warning message */}
          <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4">
            <div className="flex">
              <FiAlertCircle className="h-5 w-5 text-yellow-400" />
              <div className="ml-3">
                <p className="text-sm text-yellow-700">
                  This is your first time logging in. For security reasons, you must change your password.
                </p>
              </div>
            </div>
          </div>

          <InputField
            label="Current Password"
            type="password"
            name="currentPassword"
            placeholder="Enter your temporary password"
            icon={FiLock}
            register={register}
            error={errors.currentPassword?.message}
            required
          />

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
            Change Password
          </Button>

          <div className="text-center">
            <button
              type="button"
              onClick={logout}
              className="text-sm text-gray-500 hover:text-gray-700 font-medium transition-colors"
            >
              Cancel and Logout
            </button>
          </div>
        </form>
      ) : (
        <div className="text-center space-y-6">
          <div className="bg-green-50 text-green-800 p-4 rounded-lg">
            <p className="text-sm">
              Your password has been changed successfully. You will be redirected to login page.
            </p>
          </div>
        </div>
      )}
    </AuthLayout>
  );
};

export default FirstLoginPage;