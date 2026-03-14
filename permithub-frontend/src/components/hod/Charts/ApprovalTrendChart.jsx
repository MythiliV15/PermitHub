import React from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';

const ApprovalTrendChart = ({ data }) => {
    // Transform data for the chart
    const chartData = data?.map(item => ({
        month: item.month,
        approved: item.approved,
        rejected: item.rejected,
        pending: item.pending
    })) || [];

    return (
        <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line 
                        type="monotone" 
                        dataKey="approved" 
                        stroke="#10b981" 
                        strokeWidth={2}
                        dot={{ r: 4 }}
                        activeDot={{ r: 6 }}
                    />
                    <Line 
                        type="monotone" 
                        dataKey="rejected" 
                        stroke="#ef4444" 
                        strokeWidth={2}
                        dot={{ r: 4 }}
                    />
                    <Line 
                        type="monotone" 
                        dataKey="pending" 
                        stroke="#f59e0b" 
                        strokeWidth={2}
                        dot={{ r: 4 }}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
};

export default ApprovalTrendChart;