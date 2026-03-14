import React, { useState } from 'react';
import { FiSearch, FiFilter } from 'react-icons/fi';

const FacultyFilters = ({ filters, onFilterChange }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const [localFilters, setLocalFilters] = useState(filters);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setLocalFilters(prev => ({ ...prev, [name]: value }));
    };

    const handleApply = () => {
        onFilterChange(localFilters);
        if (window.innerWidth < 768) {
            setIsExpanded(false);
        }
    };

    const handleReset = () => {
        const resetFilters = {
            search: '',
            designation: '',
            role: '',
            isActive: ''
        };
        setLocalFilters(resetFilters);
        onFilterChange(resetFilters);
    };

    return (
        <div className="bg-white rounded-xl shadow-sm p-4">
            {/* Mobile Filter Toggle */}
            <div className="md:hidden">
                <button
                    onClick={() => setIsExpanded(!isExpanded)}
                    className="w-full flex items-center justify-between p-2 bg-gray-50 rounded-lg"
                >
                    <span className="flex items-center text-gray-700">
                        <FiFilter className="mr-2" />
                        Filters
                    </span>
                    <span className="text-sm text-gray-500">
                        {isExpanded ? 'Hide' : 'Show'}
                    </span>
                </button>
            </div>

            {/* Filter Content */}
            <div className={`mt-4 md:mt-0 ${isExpanded ? 'block' : 'hidden md:block'}`}>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    {/* Search */}
                    <div className="relative">
                        <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type="text"
                            name="search"
                            value={localFilters.search || ''}
                            onChange={handleChange}
                            placeholder="Search by name, email, ID..."
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                    </div>

                    {/* Designation Filter */}
                    <select
                        name="designation"
                        value={localFilters.designation || ''}
                        onChange={handleChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                        <option value="">All Designations</option>
                        <option value="Professor">Professor</option>
                        <option value="Associate Professor">Associate Professor</option>
                        <option value="Assistant Professor">Assistant Professor</option>
                        <option value="Lecturer">Lecturer</option>
                    </select>

                    {/* Role Filter */}
                    <select
                        name="role"
                        value={localFilters.role || ''}
                        onChange={handleChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                        <option value="">All Roles</option>
                        <option value="FACULTY_MENTOR">Mentor</option>
                        <option value="FACULTY_CLASS_ADVISOR">Class Advisor</option>
                        <option value="FACULTY_EVENT_COORDINATOR">Event Coordinator</option>
                        <option value="HOD">HOD</option>
                    </select>

                    {/* Status Filter */}
                    <select
                        name="isActive"
                        value={localFilters.isActive || ''}
                        onChange={handleChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                        <option value="">All Status</option>
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>
                </div>

                {/* Action Buttons */}
                <div className="flex items-center justify-end space-x-3 mt-4">
                    <button
                        onClick={handleReset}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
                    >
                        Reset
                    </button>
                    <button
                        onClick={handleApply}
                        className="px-4 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 transition-colors"
                    >
                        Apply Filters
                    </button>
                </div>
            </div>
        </div>
    );
};

export default FacultyFilters;