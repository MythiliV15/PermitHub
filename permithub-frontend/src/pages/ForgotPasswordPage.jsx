import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { FiMail, FiArrowLeft } from 'react-icons/fi';
import AuthLayout from '../components/layout/AuthLayout';
import InputField from '../components/common/InputField';
import Button from '../components/common/Button';
import { useToast } from '../hooks/useToast';
import { forgotPasswordSchema } from '../utils/validators';
import authAPI from '../api/authAPI';

const ForgotPasswordPage = () => {
  const [isSubmitted, setIsSubmitted] = useState(false);
  const { showLoading, dismiss, showSuccess, showError } = useToast();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(forgotPasswordSchema),
  });

  const onSubmit = async (data) => {
    const toastId = showLoading('Sending reset link...');
    
    try {
      await authAPI.forgotPassword(data.email);
      dismiss(toastId);
      showSuccess('Password reset link sent to your email!');
      setIsSubmitted(true);
    } catch (error) {
      dismiss(toastId);
      showError(error.response?.data?.message || 'Failed to send reset link');
    }
  };

  return (
    <AuthLayout
      title="Reset your password"
      subtitle="Enter your email and we'll send you a reset link"
    >
      {!isSubmitted ? (
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

          <Button
            type="submit"
            variant="primary"
            fullWidth
            loading={isSubmitting}
          >
            Send Reset Link
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
          <div className="bg-green-50 text-green-800 p-4 rounded-lg">
            <p className="text-sm">
              We've sent a password reset link to your email. Please check your inbox.
            </p>
          </div>
          
          <div className="space-y-4">
            <p className="text-sm text-gray-600">
              Didn't receive the email? Check your spam folder or try again.
            </p>
            
            <Button
              variant="outline"
              onClick={() => setIsSubmitted(false)}
            >
              Try again
            </Button>
            
            <div>
              <Link
                to="/login"
                className="text-sm text-primary-600 hover:text-primary-500"
              >
                Return to login
              </Link>
            </div>
          </div>
        </div>
      )}
    </AuthLayout>
  );
};

export default ForgotPasswordPage;