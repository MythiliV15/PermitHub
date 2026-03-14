import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { FiLogOut, FiClock } from 'react-icons/fi';
import Button from '../components/common/Button';

const Dashboard = () => {
    const { user, logout, hasRole } = useAuth();

    // If HOD, redirect to HOD Dashboard
    if (hasRole('HOD')) {
        return <Navigate to="/hod/dashboard" replace />;
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            {/* Header */}
            <header className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
                    <div className="flex items-center">
                        <div className="h-10 w-10 bg-primary-600 rounded-xl flex items-center justify-center shadow-md mr-3">
                            <span className="text-white font-bold">PH</span>
                        </div>
                        <h1 className="text-xl font-bold text-gray-900">PermitHub</h1>
                    </div>
                    <div className="flex items-center space-x-4">
                        <div className="text-right hidden sm:block">
                            <p className="text-sm font-medium text-gray-900">{user?.fullName}</p>
                            <p className="text-xs text-gray-500 uppercase">{user?.roles?.join(', ')}</p>
                        </div>
                        <button
                            onClick={logout}
                            className="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none transition-colors"
                        >
                            <FiLogOut className="mr-2" />
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="flex-1 flex items-center justify-center p-4">
                <div className="max-w-lg w-full bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100 animate-slide-up">
                    <div className="p-8 text-center">
                        <div className="w-20 h-20 bg-primary-50 rounded-full flex items-center justify-center mx-auto mb-6">
                            <FiClock className="w-10 h-10 text-primary-600 animate-pulse" />
                        </div>
                        <h2 className="text-3xl font-extrabold text-gray-900 mb-2">
                            Dashboard Coming Soon
                        </h2>
                        <p className="text-gray-600 mb-8">
                            Hello {user?.fullName}! We are working hard to bring you the best experience for your {user?.roles?.[0]?.toLowerCase()} role. Stay tuned!
                        </p>
                        <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-8 text-left">
                            <div className="flex">
                                <div className="ml-3">
                                    <p className="text-sm text-blue-700">
                                        Your account is active. You will be notified once your specific module is ready for use.
                                    </p>
                                </div>
                            </div>
                        </div>
                        <Button 
                            variant="secondary" 
                            fullWidth 
                            onClick={logout}
                            icon={FiLogOut}
                        >
                            Sign Out for now
                        </Button>
                    </div>
                    <div className="bg-gray-50 px-8 py-4 text-center">
                        <p className="text-xs text-gray-500">
                            &copy; 2024 Smart Campus PermitHub. Phase 2 Development in Progress.
                        </p>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default Dashboard;
