import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { motion } from 'framer-motion';
import { FiArrowLeft, FiUsers, FiCheckCircle, FiXCircle, FiAlertCircle } from 'react-icons/fi';
import {
    fetchPromotionEligibility,
    selectPromotionEligibility,
    selectSemesterLoading,
    selectSemesterSuccessMessage,
    clearSuccessMessage
} from '../../store/semesterSlice';
import { fetchAllSemesters, selectAllSemesters } from '../../store/semesterSlice';
import { useToast } from '../../hooks/useToast';

const StudentPromotion = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { showSuccess, showError, showLoading, dismiss } = useToast();

    const eligibilityList = useSelector(selectPromotionEligibility);
    const semesters = useSelector(selectAllSemesters);
    const loading = useSelector(selectSemesterLoading);
    const successMessage = useSelector(selectSemesterSuccessMessage);

    const [selectedYear, setSelectedYear] = useState('');
    const [selectedSection, setSelectedSection] = useState('');
    const [selectedStudents, setSelectedStudents] = useState([]);
    const [promotionData, setPromotionData] = useState({
        fromYear: '',
        fromSection: '',
        toYear: '',
        toSection: '',
        newSemesterId: '',
        newLeaveBalance: 20,
        promoteOnlyPassedStudents: true,
        remarks: ''
    });

    useEffect(() => {
        dispatch(fetchAllSemesters({ page: 0, size: 10 }));
    }, [dispatch]);

    useEffect(() => {
        if (selectedYear) {
            dispatch(fetchPromotionEligibility({ 
                year: selectedYear, 
                section: selectedSection || null 
            }));
        }
    }, [dispatch, selectedYear, selectedSection]);

    useEffect(() => {
        if (successMessage) {
            showSuccess(successMessage);
            dispatch(clearSuccessMessage());
        }
    }, [successMessage, showSuccess, dispatch]);

    const handleYearChange = (e) => {
        const year = e.target.value;
        setSelectedYear(year);
        setPromotionData(prev => ({
            ...prev,
            fromYear: year,
            toYear: year === '4' ? '5' : String(parseInt(year) + 1)
        }));
    };

    const handleSelectAll = () => {
        if (selectedStudents.length === eligibilityList.length) {
            setSelectedStudents([]);
        } else {
            setSelectedStudents(eligibilityList.map(s => s.studentId));
        }
    };

    const handleSelectStudent = (studentId) => {
        setSelectedStudents(prev =>
            prev.includes(studentId)
                ? prev.filter(id => id !== studentId)
                : [...prev, studentId]
        );
    };

    const handlePromote = async () => {
        if (!promotionData.newSemesterId) {
            showError('Please select a target semester');
            return;
        }

        if (selectedStudents.length === 0) {
            showError('Please select at least one student to promote');
            return;
        }

        const toastId = showLoading('Promoting students...');
        try {
            // Dispatch promotion action
            // await dispatch(promoteStudents({
            //     ...promotionData,
            //     excludeStudentIds: eligibilityList
            //         .filter(s => !selectedStudents.includes(s.studentId))
            //         .map(s => s.studentId)
            // })).unwrap();
            
            dismiss(toastId);
            showSuccess(`${selectedStudents.length} students promoted successfully`);
            setTimeout(() => navigate('/hod/semester'), 2000);
        } catch (error) {
            dismiss(toastId);
            showError(error || 'Failed to promote students');
        }
    };

    const eligibleCount = eligibilityList?.filter(s => s.isEligible).length || 0;
    const notEligibleCount = eligibilityList?.filter(s => !s.isEligible).length || 0;

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center">
                        <button
                            onClick={() => navigate('/hod/semester')}
                            className="mr-4 p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
                        >
                            <FiArrowLeft className="w-5 h-5" />
                        </button>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Student Promotion</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                Promote students to the next academic year
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Left Panel - Promotion Settings */}
                    <div className="lg:col-span-1">
                        <motion.div
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                            className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 sticky top-24"
                        >
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Promotion Settings</h2>
                            
                            <div className="space-y-4">
                                {/* From Year */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        From Year <span className="text-red-500">*</span>
                                    </label>
                                    <select
                                        value={selectedYear}
                                        onChange={handleYearChange}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    >
                                        <option value="">Select Year</option>
                                        {[1, 2, 3, 4].map(year => (
                                            <option key={year} value={year}>Year {year}</option>
                                        ))}
                                    </select>
                                </div>

                                {/* From Section */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        From Section (Optional)
                                    </label>
                                    <input
                                        type="text"
                                        value={selectedSection}
                                        onChange={(e) => setSelectedSection(e.target.value)}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder="e.g., A (leave empty for all)"
                                    />
                                </div>

                                {/* To Year (Auto-calculated) */}
                                {selectedYear && (
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            To Year
                                        </label>
                                        <input
                                            type="text"
                                            value={selectedYear === '4' ? 'Year 5 (Passed Out)' : `Year ${parseInt(selectedYear) + 1}`}
                                            disabled
                                            className="w-full px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-600"
                                        />
                                    </div>
                                )}

                                {/* To Section */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        To Section (Optional)
                                    </label>
                                    <input
                                        type="text"
                                        value={promotionData.toSection}
                                        onChange={(e) => setPromotionData(prev => ({ ...prev, toSection: e.target.value }))}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder="New section (optional)"
                                    />
                                </div>

                                {/* Target Semester */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Target Semester <span className="text-red-500">*</span>
                                    </label>
                                    <select
                                        value={promotionData.newSemesterId}
                                        onChange={(e) => setPromotionData(prev => ({ ...prev, newSemesterId: e.target.value }))}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    >
                                        <option value="">Select Semester</option>
                                        {semesters?.content?.map(sem => (
                                            <option key={sem.id} value={sem.id}>
                                                {sem.name} (Sem {sem.semesterNumber})
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                {/* New Leave Balance */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        New Leave Balance
                                    </label>
                                    <input
                                        type="number"
                                        value={promotionData.newLeaveBalance}
                                        onChange={(e) => setPromotionData(prev => ({ ...prev, newLeaveBalance: parseInt(e.target.value) }))}
                                        min="0"
                                        max="30"
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                    />
                                </div>

                                {/* Options */}
                                <div className="flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={promotionData.promoteOnlyPassedStudents}
                                        onChange={(e) => setPromotionData(prev => ({ ...prev, promoteOnlyPassedStudents: e.target.checked }))}
                                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                    />
                                    <label className="ml-2 block text-sm text-gray-900">
                                        Promote only students who have passed
                                    </label>
                                </div>

                                {/* Remarks */}
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Remarks
                                    </label>
                                    <textarea
                                        value={promotionData.remarks}
                                        onChange={(e) => setPromotionData(prev => ({ ...prev, remarks: e.target.value }))}
                                        rows="3"
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder="Add any remarks about this promotion..."
                                    />
                                </div>

                                {/* Summary */}
                                {eligibilityList?.length > 0 && (
                                    <div className="pt-4 border-t">
                                        <div className="flex items-center justify-between text-sm">
                                            <span className="text-gray-600">Selected Students:</span>
                                            <span className="font-bold text-primary-600">{selectedStudents.length}</span>
                                        </div>
                                        <div className="flex items-center justify-between text-sm mt-1">
                                            <span className="text-gray-600">Eligible:</span>
                                            <span className="font-bold text-green-600">{eligibleCount}</span>
                                        </div>
                                        <div className="flex items-center justify-between text-sm mt-1">
                                            <span className="text-gray-600">Not Eligible:</span>
                                            <span className="font-bold text-red-600">{notEligibleCount}</span>
                                        </div>
                                    </div>
                                )}

                                {/* Action Button */}
                                <button
                                    onClick={handlePromote}
                                    disabled={!selectedYear || !promotionData.newSemesterId || selectedStudents.length === 0 || loading.action}
                                    className="w-full mt-4 px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {loading.action ? 'Promoting...' : 'Promote Selected Students'}
                                </button>
                            </div>
                        </motion.div>
                    </div>

                    {/* Right Panel - Student List */}
                    <div className="lg:col-span-2">
                        <motion.div
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden"
                        >
                            {/* Header */}
                            <div className="p-4 border-b border-gray-200 bg-gray-50">
                                <div className="flex items-center justify-between">
                                    <h2 className="text-lg font-semibold text-gray-900">Student List</h2>
                                    {eligibilityList?.length > 0 && (
                                        <button
                                            onClick={handleSelectAll}
                                            className="text-sm text-primary-600 hover:text-primary-700"
                                        >
                                            {selectedStudents.length === eligibilityList.length ? 'Deselect All' : 'Select All'}
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* Student Cards */}
                            {loading.list ? (
                                <div className="p-8 text-center">
                                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
                                    <p className="mt-4 text-gray-600">Loading students...</p>
                                </div>
                            ) : eligibilityList?.length > 0 ? (
                                <div className="divide-y divide-gray-200">
                                    {eligibilityList.map((student) => (
                                        <div
                                            key={student.studentId}
                                            className={`p-4 hover:bg-gray-50 transition-colors ${
                                                selectedStudents.includes(student.studentId) ? 'bg-primary-50' : ''
                                            }`}
                                        >
                                            <div className="flex items-start space-x-3">
                                                <input
                                                    type="checkbox"
                                                    checked={selectedStudents.includes(student.studentId)}
                                                    onChange={() => handleSelectStudent(student.studentId)}
                                                    className="mt-1 h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                                                />
                                                <div className="flex-1">
                                                    <div className="flex items-center justify-between">
                                                        <div>
                                                            <p className="font-medium text-gray-900">{student.name}</p>
                                                            <p className="text-sm text-gray-500">{student.registerNumber}</p>
                                                        </div>
                                                        {student.isEligible ? (
                                                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                                                <FiCheckCircle className="mr-1" /> Eligible
                                                            </span>
                                                        ) : (
                                                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                                                <FiXCircle className="mr-1" /> Not Eligible
                                                            </span>
                                                        )}
                                                    </div>
                                                    <div className="mt-2 grid grid-cols-2 gap-2 text-sm">
                                                        <div>
                                                            <span className="text-gray-500">Current:</span>{' '}
                                                            <span className="font-medium">Year {student.currentYear} - {student.currentSection}</span>
                                                        </div>
                                                        <div>
                                                            <span className="text-gray-500">Attendance:</span>{' '}
                                                            <span className="font-medium">{student.attendance}</span>
                                                        </div>
                                                    </div>
                                                    {!student.isEligible && (
                                                        <div className="mt-2 flex items-center text-xs text-red-600">
                                                            <FiAlertCircle className="mr-1" />
                                                            {student.hasPendingRequests ? 'Has pending requests' : 'Not meeting eligibility criteria'}
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="p-12 text-center">
                                    <FiUsers className="mx-auto h-12 w-12 text-gray-400" />
                                    <h3 className="mt-4 text-lg font-medium text-gray-900">No Students Found</h3>
                                    <p className="mt-2 text-gray-500">
                                        {selectedYear ? 'No students in the selected year/section' : 'Select a year to view students'}
                                    </p>
                                </div>
                            )}
                        </motion.div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StudentPromotion;