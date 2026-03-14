import { useCallback } from 'react';
import toast from 'react-hot-toast';

export const useToast = () => {
  const showSuccess = useCallback((message, duration = 3000) => {
    toast.success(message, { duration });
  }, []);

  const showError = useCallback((message, duration = 4000) => {
    toast.error(message, { duration });
  }, []);

  const showInfo = useCallback((message, duration = 3000) => {
    toast(message, {
      duration,
      icon: 'ℹ️',
      style: {
        background: '#3b82f6',
        color: '#fff',
      },
    });
  }, []);

  const showWarning = useCallback((message, duration = 4000) => {
    toast(message, {
      duration,
      icon: '⚠️',
      style: {
        background: '#f59e0b',
        color: '#fff',
      },
    });
  }, []);

  const showLoading = useCallback((message) => {
    return toast.loading(message);
  }, []);

  const dismiss = useCallback((toastId) => {
    toast.dismiss(toastId);
  }, []);

  const promise = useCallback((promiseObj, messages) => {
    return toast.promise(promiseObj, messages);
  }, []);

  return {
    showSuccess,
    showError,
    showInfo,
    showWarning,
    showLoading,
    dismiss,
    promise,
  };
};