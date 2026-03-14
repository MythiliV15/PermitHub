import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiMail, FiLock } from 'react-icons/fi';
import { Link } from 'react-router-dom';
import InputField from '../common/InputField';
import Button from '../common/Button';
import { loginSchema } from '../../utils/validators';

const Login = ({ onSubmit, loading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(loginSchema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <InputField
        label="Email Address"
        type="email"
        name="email"
        placeholder="Enter your email"
        icon={FiMail}
        register={register}
        error={errors.email?.message}
        required
      />

      <InputField
        label="Password"
        type="password"
        name="password"
        placeholder="Enter your password"
        icon={FiLock}
        register={register}
        error={errors.password?.message}
        required
      />

      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <input
            id="remember-me"
            name="remember-me"
            type="checkbox"
            className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
            Remember me
          </label>
        </div>

        <div className="text-sm">
          <Link
            to="/forgot-password"
            className="font-medium text-primary-600 hover:text-primary-500"
          >
            Forgot your password?
          </Link>
        </div>
      </div>

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Sign in
      </Button>

      {/* Demo credentials - remove in production */}
      <div className="mt-4 p-4 bg-gray-50 rounded-lg">
        <p className="text-xs text-gray-600 mb-2">Demo Credentials:</p>
        <p className="text-xs text-gray-500">HOD: hod@permithub.com / password123</p>
        <p className="text-xs text-gray-500">Student: student@permithub.com / password123</p>
      </div>
    </form>
  );
};

export default Login;