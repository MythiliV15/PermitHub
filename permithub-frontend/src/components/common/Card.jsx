import React from 'react';

const Card = ({ children, className = '', padding = true, hover = false }) => {
  return (
    <div
      className={`
        bg-white rounded-xl shadow-lg border border-gray-100
        ${padding ? 'p-6 sm:p-8' : ''}
        ${hover ? 'hover:shadow-xl transition-shadow duration-300' : ''}
        ${className}
      `}
    >
      {children}
    </div>
  );
};

export default Card;