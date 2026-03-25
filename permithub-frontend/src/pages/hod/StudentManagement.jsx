import React, { useEffect, useMemo, useState } from 'react';
import { FiSearch, FiUsers } from 'react-icons/fi';
import axiosInstance from '../../api/axiosConfig';

const StudentManagement = () => {
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [year, setYear] = useState('');
    const [section, setSection] = useState('');
    const [stats, setStats] = useState({ total: 0, hostelers: 0, dayScholars: 0 });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [studentsRes, statsRes] = await Promise.all([
                    axiosInstance.get('/hod/students', {
                        params: {
                            page: 0,
                            size: 200,
                            sortBy: 'regNo',
                            direction: 'asc'
                        }
                    }),
                    axiosInstance.get('/hod/students/stats')
                ]);
                setStudents(studentsRes?.data?.data?.content || []);
                setStats({
                    total: statsRes?.data?.data?.totalActive || 0,
                    hostelers: statsRes?.data?.data?.hostelers || 0,
                    dayScholars: statsRes?.data?.data?.dayScholars || 0
                });
                setLoading(false);
            } catch (err) {
                console.error(err);
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const filteredStudents = useMemo(() => {
        return students.filter((student) => {
            const term = searchTerm.trim().toLowerCase();
            const termMatch = !term ||
                student?.name?.toLowerCase().includes(term) ||
                student?.regNo?.toLowerCase().includes(term) ||
                student?.phone?.toLowerCase().includes(term);
            const yearMatch = !year || String(student?.year) === year;
            const sectionMatch = !section || String(student?.section || '').toUpperCase() === section.toUpperCase();
            return termMatch && yearMatch && sectionMatch;
        });
    }, [students, searchTerm, year, section]);

    return (
        <div className="p-6 max-w-7xl mx-auto animate-fade-in">
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Student Management</h1>
                    <p className="text-gray-600 mt-1">Department students list with year and section filters.</p>
                </div>
            </div>

            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="p-3 bg-blue-50 rounded-xl mr-4 text-blue-600">
                        <FiUsers className="w-8 h-8" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500">Total Students</p>
                        <h3 className="text-2xl font-bold text-gray-900">{stats.total}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="p-3 bg-purple-50 rounded-xl mr-4 text-purple-600">
                        <FiUsers className="w-8 h-8" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500">Showing</p>
                        <h3 className="text-2xl font-bold text-gray-900">{filteredStudents.length}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="p-3 bg-green-50 rounded-xl mr-4 text-green-600">
                        <FiUsers className="w-8 h-8" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500">Dept Active</p>
                        <h3 className="text-2xl font-bold text-gray-900">{stats.total}</h3>
                    </div>
                </div>
            </div>

            {/* Filters */}
            <div className="bg-white p-4 rounded-2xl mb-8 border border-gray-100 shadow-sm">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1 relative">
                        <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search by name, roll no or email..."
                            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    <select
                        className="px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none"
                        value={year}
                        onChange={(e) => setYear(e.target.value)}
                    >
                        <option value="">All Years</option>
                        <option value="1">Year 1</option>
                        <option value="2">Year 2</option>
                        <option value="3">Year 3</option>
                        <option value="4">Year 4</option>
                    </select>
                    <input
                        type="text"
                        placeholder="Section (e.g., A)"
                        className="px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none"
                        value={section}
                        onChange={(e) => setSection(e.target.value)}
                    />
                </div>
            </div>

            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
                {loading ? (
                    <div className="p-8 text-center text-gray-500">Loading students...</div>
                ) : filteredStudents.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">No students found for the selected filters.</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reg No</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Year</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Section</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Phone</th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {filteredStudents.map((student) => (
                                    <tr key={student.id}>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{student.regNo}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{student.name}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{student.year}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{student.section}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{student.phone || '-'}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
};

export default StudentManagement;
