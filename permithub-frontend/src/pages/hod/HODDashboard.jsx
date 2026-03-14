import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { motion } from 'framer-motion';
import { FiUsers, FiUserCheck, FiClock, FiCalendar, FiArrowRight, FiLogOut, FiUser } from 'react-icons/fi';
import { useAuth } from '../../hooks/useAuth';
import { Link } from 'react-router-dom';
import StatisticsCards from '../../components/hod/StatisticsCards';
import StudentDistributionChart from '../../components/hod/Charts/StudentDistributionChart';
import FacultyRoleChart from '../../components/hod/Charts/FacultyRoleChart';
import { 
    fetchDashboardStats, 
    fetchRecentActivities, 
    fetchYearWiseDistribution,
    fetchPendingCounts,
    selectHODStats,
    selectHODActivities,
    selectYearWiseDistribution,
    selectPendingCounts,
    selectHODLoading
} from '../../store/hodSlice';
import { formatters } from '../../utils/formatters';

const HODDashboard = () => {
    const dispatch = useDispatch();
    const stats = useSelector(selectHODStats);
    const activities = useSelector(selectHODActivities);
    const yearWiseDistribution = useSelector(selectYearWiseDistribution);
    const pendingCounts = useSelector(selectPendingCounts);
    const { user, logout } = useAuth();
    const loading = useSelector(selectHODLoading);

    useEffect(() => {
        dispatch(fetchDashboardStats());
        dispatch(fetchRecentActivities({ page: 0, size: 5 }));
        dispatch(fetchYearWiseDistribution());
        dispatch(fetchPendingCounts());
    }, [dispatch]);

    const quickActions = [
        {
            title: 'Faculty Management',
            description: 'Add, edit or manage faculty members',
            icon: <FiUsers className="w-6 h-6" />,
            color: 'bg-blue-500',
            link: '/hod/faculty'
        },
        {
            title: 'Pending Approvals',
            description: `${pendingCounts?.total || 0} requests waiting`,
            icon: <FiClock className="w-6 h-6" />,
            color: 'bg-yellow-500',
            link: '/hod/approvals'
        },
        {
            title: 'Semester Management',
            description: 'Manage semesters and promotions',
            icon: <FiCalendar className="w-6 h-6" />,
            color: 'bg-green-500',
            link: '/hod/semester'
        },
        {
            title: 'Student Overview',
            description: 'View student distribution',
            icon: <FiUserCheck className="w-6 h-6" />,
            color: 'bg-purple-500',
            link: '/hod/students'
        },
        {
            title: 'My Profile',
            description: 'View and update your profile',
            icon: <FiUser className="w-6 h-6" />,
            color: 'bg-orange-500',
            link: '/hod/profile'
        }
    ];

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 flex flex-col sm:flex-row justify-between items-start sm:items-center">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">HOD Dashboard</h1>
                        <p className="text-gray-600 mt-1">Welcome back, {user?.fullName}! Here's what's happening in your department.</p>
                    </div>
                    <div className="mt-4 sm:mt-0 flex space-x-3">
                        <Link
                            to="/hod/profile"
                            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all duration-200 shadow-sm"
                        >
                            <FiUser className="mr-2 h-4 w-4" />
                            Profile
                        </Link>
                        <button
                            onClick={logout}
                            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all duration-200 shadow-sm"
                        >
                            <FiLogOut className="mr-2 h-4 w-4" />
                            Logout
                        </button>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Statistics Cards */}
                <StatisticsCards stats={stats} loading={loading.stats} />

                {/* Quick Actions - Mobile Horizontal Scroll */}
                <div className="mt-8">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
                    <div className="flex overflow-x-auto pb-4 space-x-4 md:grid md:grid-cols-2 lg:grid-cols-4 md:space-x-0 md:gap-4">
                        {quickActions.map((action, index) => (
                            <motion.div
                                key={action.title}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.1 }}
                                className="flex-shrink-0 w-64 md:w-auto"
                            >
                                <Link
                                    to={action.link}
                                    className="block bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow duration-300"
                                >
                                    <div className={`w-12 h-12 ${action.color} rounded-lg flex items-center justify-center text-white mb-4`}>
                                        {action.icon}
                                    </div>
                                    <h3 className="font-semibold text-gray-900">{action.title}</h3>
                                    <p className="text-sm text-gray-500 mt-1">{action.description}</p>
                                    <div className="flex items-center text-primary-600 text-sm font-medium mt-4">
                                        View <FiArrowRight className="ml-1" />
                                    </div>
                                </Link>
                            </motion.div>
                        ))}
                    </div>
                </div>

                {/* Charts Section */}
                <div className="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Student Distribution */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                        className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                    >
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Student Distribution</h2>
                        <StudentDistributionChart 
                            yearWiseData={yearWiseDistribution}
                            viewType="year"
                        />
                        <div className="mt-4 flex items-center justify-between text-sm">
                            <span className="text-gray-600">Total Students: {stats?.totalStudents || 0}</span>
                            <Link to="/hod/students" className="text-primary-600 hover:text-primary-700">
                                View Details →
                            </Link>
                        </div>
                    </motion.div>

                    {/* Faculty Roles */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.3 }}
                        className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                    >
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Faculty Distribution</h2>
                        <FacultyRoleChart data={stats?.facultyRoleDistribution} />
                        <div className="mt-4 flex items-center justify-between text-sm">
                            <span className="text-gray-600">Total Faculty: {stats?.totalFaculty || 0}</span>
                            <Link to="/hod/faculty" className="text-primary-600 hover:text-primary-700">
                                Manage Faculty →
                            </Link>
                        </div>
                    </motion.div>
                </div>

                {/* Recent Activities & Pending Approvals */}
                <div className="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Recent Activities */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.4 }}
                        className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                    >
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h2>
                        <div className="space-y-4">
                            {loading.activities ? (
                                [1, 2, 3].map((i) => (
                                    <div key={i} className="animate-pulse flex items-center space-x-3">
                                        <div className="h-10 w-10 bg-gray-200 rounded-full"></div>
                                        <div className="flex-1">
                                            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                                            <div className="h-3 bg-gray-200 rounded w-1/2 mt-2"></div>
                                        </div>
                                    </div>
                                ))
                            ) : activities?.content?.length > 0 ? (
                                activities.content.map((activity, index) => (
                                    <div key={index} className="flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                                        <div className="flex-shrink-0">
                                            <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                                                <span className="text-primary-600 font-medium">
                                                    {formatters.getInitials(activity.studentName)}
                                                </span>
                                            </div>
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-sm font-medium text-gray-900">
                                                {activity.description}
                                            </p>
                                            <p className="text-xs text-gray-500 mt-1">
                                                {activity.registerNumber} • {formatters.formatTimeAgo(activity.timestamp)}
                                            </p>
                                        </div>
                                        <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium
                                            ${activity.status?.includes('APPROVED') ? 'bg-green-100 text-green-800' : 
                                              activity.status?.includes('REJECTED') ? 'bg-red-100 text-red-800' : 
                                              'bg-yellow-100 text-yellow-800'}`}>
                                            {activity.status}
                                        </span>
                                    </div>
                                ))
                            ) : (
                                <p className="text-gray-500 text-center py-4">No recent activities</p>
                            )}
                        </div>
                        <div className="mt-4 text-right">
                            <Link to="/hod/activities" className="text-sm text-primary-600 hover:text-primary-700">
                                View All Activities →
                            </Link>
                        </div>
                    </motion.div>

                    {/* Pending Approvals Summary */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.5 }}
                        className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                    >
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">Pending Approvals</h2>
                        <div className="space-y-4">
                            <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                                <div className="flex items-center">
                                    <span className="text-2xl mr-3">🏖️</span>
                                    <div>
                                        <p className="font-medium text-gray-900">Leave Requests</p>
                                        <p className="text-sm text-gray-600">Pending your approval</p>
                                    </div>
                                </div>
                                <span className="text-2xl font-bold text-blue-600">{pendingCounts?.LEAVE || 0}</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-purple-50 rounded-lg">
                                <div className="flex items-center">
                                    <span className="text-2xl mr-3">🎯</span>
                                    <div>
                                        <p className="font-medium text-gray-900">OD Requests</p>
                                        <p className="text-sm text-gray-600">Pending your approval</p>
                                    </div>
                                </div>
                                <span className="text-2xl font-bold text-purple-600">{pendingCounts?.OD || 0}</span>
                            </div>
                            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                                <div className="flex items-center">
                                    <span className="text-2xl mr-3">🚪</span>
                                    <div>
                                        <p className="font-medium text-gray-900">Outpass Requests</p>
                                        <p className="text-sm text-gray-600">Pending your approval</p>
                                    </div>
                                </div>
                                <span className="text-2xl font-bold text-green-600">{pendingCounts?.OUTPASS || 0}</span>
                            </div>
                        </div>
                        <div className="mt-6">
                            <Link
                                to="/hod/approvals"
                                className="block w-full text-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                            >
                                Review Approvals
                            </Link>
                        </div>
                    </motion.div>
                </div>
            </div>
        </div>
    );
};

export default HODDashboard;