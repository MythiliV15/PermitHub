import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiLock } from 'react-icons/fi';
import { Link } from 'react-router-dom';
import InputField from '../common/InputField';
import Button from '../common/Button';
import { resetPasswordSchema } from '../../utils/validators';

const ResetPassword = ({ onSubmit, loading, isSuccess, newPassword }) => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(resetPasswordSchema),
  });

  const password = watch('newPassword');

  if (isSuccess) {
    return (
      <div className="text-center space-y-6">
        <div className="flex justify-center">
          <div className="h-16 w-16 bg-green-100 rounded-full flex items-center justify-center">
            <svg className="h-8 w-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
            </svg>
          </div>
        </div>
        
        <div className="bg-green-50 text-green-800 p-4 rounded-lg">
          <p className="text-sm">
            Your password has been reset successfully. You will be redirected to login page shortly.
          </p>
        </div>
        
        <Button variant="primary" onClick={() => window.location.href = '/login'}>
          Go to Login
        </Button>
      </div>
    );
  }

  return (
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
          <li className={password?.length >= 8 ? 'text-green-600' : 'text-gray-500'}>
            ✓ At least 8 characters
          </li>
          <li className={/[A-Z]/.test(password) ? 'text-green-600' : 'text-gray-500'}>
            ✓ One uppercase letter
          </li>
          <li className={/[a-z]/.test(password) ? 'text-green-600' : 'text-gray-500'}>
            ✓ One lowercase letter
          </li>
          <li className={/\d/.test(password) ? 'text-green-600' : 'text-gray-500'}>
            ✓ One number
          </li>
          <li className={/[@$!%*?&]/.test(password) ? 'text-green-600' : 'text-gray-500'}>
            ✓ One special character (@$!%*?&)
          </li>
        </ul>
      </div>

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Reset Password
      </Button>

      <div className="text-center">
        <Link
          to="/login"
          className="inline-flex items-center text-sm text-primary-600 hover:text-primary-500"
        >
          ← Back to login
        </Link>
      </div>
    </form>
  );
};

export default ResetPassword;