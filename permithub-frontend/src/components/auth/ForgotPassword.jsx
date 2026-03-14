import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiMail } from 'react-icons/fi';
import { Link } from 'react-router-dom';
import InputField from '../common/InputField';
import Button from '../common/Button';
import { forgotPasswordSchema } from '../../utils/validators';

const ForgotPassword = ({ onSubmit, loading, isSubmitted }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(forgotPasswordSchema),
  });

  if (isSubmitted) {
    return (
      <div className="text-center space-y-6">
        <div className="bg-green-50 text-green-800 p-4 rounded-lg">
          <p className="text-sm">
            We've sent a password reset link to your email. Please check your inbox.
          </p>
        </div>
        
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            Didn't receive the email? Check your spam folder or try again.
          </p>
          
          <Button variant="outline" onClick={() => window.location.reload()}>
            Try again
          </Button>
          
          <div>
            <Link to="/login" className="text-sm text-primary-600 hover:text-primary-500">
              Return to login
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <InputField
        label="Email Address"
        type="email"
        name="email"
        placeholder="Enter your registered email"
        icon={FiMail}
        register={register}
        error={errors.email?.message}
        required
      />

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Send Reset Link
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

export default ForgotPassword;