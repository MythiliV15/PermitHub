import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiLock, FiAlertCircle } from 'react-icons/fi';
import InputField from '../common/InputField';
import Button from '../common/Button';
import { firstLoginSchema } from '../../utils/validators';

const FirstLoginChangePassword = ({ onSubmit, loading, isSuccess, newPassword }) => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(firstLoginSchema),
  });

  const password = watch('newPassword');

  if (isSuccess) {
    return (
      <div className="text-center space-y-6">
        <div className="bg-green-50 text-green-800 p-4 rounded-lg">
          <p className="text-sm">
            Your password has been changed successfully. You will be redirected to login page.
          </p>
        </div>
      </div>
    );
  }

  return (
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
        Change Password
      </Button>
    </form>
  );
};

export default FirstLoginChangePassword;