import React from 'react';
import { motion } from 'framer-motion';

const StatisticsCards = ({ stats, loading }) => {
    const cards = [
        {
            title: 'Total Students',
            value: stats?.totalStudents || 0,
            icon: '👥',
            color: 'bg-blue-500',
            bgColor: 'bg-blue-50',
            textColor: 'text-blue-600',
            borderColor: 'border-blue-200'
        },
        {
            title: 'Total Faculty',
            value: stats?.totalFaculty || 0,
            icon: '👨‍🏫',
            color: 'bg-green-500',
            bgColor: 'bg-green-50',
            textColor: 'text-green-600',
            borderColor: 'border-green-200'
        },
        {
            title: 'Pending Approvals',
            value: stats?.totalPendingApprovals || 0,
            icon: '⏳',
            color: 'bg-yellow-500',
            bgColor: 'bg-yellow-50',
            textColor: 'text-yellow-600',
            borderColor: 'border-yellow-200'
        },
        {
            title: 'Hostelers',
            value: stats?.hostelersCount || 0,
            icon: '🏠',
            color: 'bg-purple-500',
            bgColor: 'bg-purple-50',
            textColor: 'text-purple-600',
            borderColor: 'border-purple-200'
        }
    ];

    if (loading) {
        return (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                {[1, 2, 3, 4].map((i) => (
                    <div key={i} className="bg-white rounded-xl shadow-sm p-6 animate-pulse">
                        <div className="h-4 bg-gray-200 rounded w-1/2 mb-4"></div>
                        <div className="h-8 bg-gray-200 rounded w-1/3"></div>
                    </div>
                ))}
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {cards.map((card, index) => (
                <motion.div
                    key={card.title}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                    className={`bg-white rounded-xl shadow-sm border ${card.borderColor} p-6 hover:shadow-md transition-shadow duration-300`}
                >
                    <div className="flex items-center justify-between mb-4">
                        <div className={`w-12 h-12 ${card.bgColor} rounded-lg flex items-center justify-center text-2xl`}>
                            {card.icon}
                        </div>
                        <span className="text-3xl font-bold text-gray-800">{card.value}</span>
                    </div>
                    <h3 className="text-gray-600 font-medium">{card.title}</h3>
                    
                    {/* Mini progress or trend indicator (optional) */}
                    {card.title === 'Pending Approvals' && stats?.pendingLeaveApprovals && (
                        <div className="mt-3 flex items-center text-xs text-gray-500">
                            <span className="mr-3">Leave: {stats.pendingLeaveApprovals}</span>
                            <span className="mr-3">OD: {stats.pendingODApprovals}</span>
                            <span>Outpass: {stats.pendingOutpassApprovals}</span>
                        </div>
                    )}
                </motion.div>
            ))}
        </div>
    );
};

export default StatisticsCards;