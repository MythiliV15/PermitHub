import React from 'react';
import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';

const COLORS = {
    MENTORS: '#6366f1',
    CLASS_ADVISORS: '#8b5cf6',
    EVENT_COORDINATORS: '#ec4899'
};

const FacultyRoleChart = ({ data }) => {
    const chartData = [
        { name: 'Mentors', value: data?.MENTORS || 0 },
        { name: 'Class Advisors', value: data?.CLASS_ADVISORS || 0 },
        { name: 'Event Coordinators', value: data?.EVENT_COORDINATORS || 0 }
    ].filter(item => item.value > 0);

    return (
        <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                    <Pie
                        data={chartData}
                        cx="50%"
                        cy="50%"
                        labelLine={true}
                        label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                    >
                        {chartData.map((entry, index) => (
                            <Cell 
                                key={`cell-${index}`} 
                                fill={COLORS[entry.name.replace(' ', '_').toUpperCase()] || '#6366f1'} 
                            />
                        ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
};

export default FacultyRoleChart;