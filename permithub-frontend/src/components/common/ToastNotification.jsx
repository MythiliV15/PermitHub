import React, { useEffect } from 'react';
import { toast } from 'react-hot-toast';

const ToastNotification = ({ type, message, duration = 4000, onClose }) => {
  useEffect(() => {
    let toastId;
    
    switch (type) {
      case 'success':
        toastId = toast.success(message, { duration, onClose });
        break;
      case 'error':
        toastId = toast.error(message, { duration, onClose });
        break;
      case 'info':
        toastId = toast(message, { 
          duration, 
          onClose,
          icon: 'ℹ️',
          style: {
            background: '#3b82f6',
            color: '#fff',
          },
        });
        break;
      case 'warning':
        toastId = toast(message, { 
          duration, 
          onClose,
          icon: '⚠️',
          style: {
            background: '#f59e0b',
            color: '#fff',
          },
        });
        break;
      default:
        toastId = toast(message, { duration, onClose });
    }

    return () => toast.dismiss(toastId);
  }, [type, message, duration, onClose]);

  return null;
};

export default ToastNotification;