import React, { useEffect, useState, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiCalendar, FiClock, FiUsers, FiRefreshCw } from 'react-icons/fi';
import SemesterCard from '../../components/hod/SemesterCard';
import PromotionModal from '../../components/hod/PromotionModal';
import {
    fetchAllSemesters,
    fetchActiveSemester,
    fetchSemesterStatistics,
    activateSemester,
    deactivateSemester,
    resetLeaveBalance,
    selectAllSemesters,
    selectActiveSemester,
    selectSemesterStatistics,
    selectSemesterLoading,
    selectSemesterSuccessMessage,
    clearSuccessMessage
} from '../../store/semesterSlice';
import { useToast } from '../../hooks/useToast';

const SemesterManagement = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { showSuccess, showError, showLoading, dismiss } = useToast();

    const semesters = useSelector(selectAllSemesters);
    const activeSemester = useSelector(selectActiveSemester);
    const statistics = useSelector(selectSemesterStatistics);
    const loading = useSelector(selectSemesterLoading);
    const successMessage = useSelector(selectSemesterSuccessMessage);

    const [page, setPage] = useState(0);
    const [size] = useState(6);
    const [showPromotionModal, setShowPromotionModal] = useState(false);

    const loadSemesters = useCallback(() => {
        dispatch(fetchAllSemesters({
            page,
            size,
            sortBy: 'year',
            direction: 'desc'
        }));
    }, [dispatch, page, size]);

    useEffect(() => {
        loadSemesters();
        dispatch(fetchActiveSemester());
        dispatch(fetchSemesterStatistics());
    }, [loadSemesters, dispatch]);

    useEffect(() => {
        if (successMessage) {
            showSuccess(successMessage);
            dispatch(clearSuccessMessage());
        }
    }, [successMessage, showSuccess, dispatch]);

    const handleActivate = async (semesterId) => {
        const toastId = showLoading('Activating semester...');
        try {
            await dispatch(activateSemester(semesterId)).unwrap();
            dismiss(toastId);
            showSuccess('Semester activated successfully');
            loadSemesters();
            dispatch(fetchActiveSemester());
        } catch (error) {
            dismiss(toastId);
            showError(error || 'Failed to activate semester');
        }
    };

    const handleDeactivate = async (semesterId) => {
        if (window.confirm('Are you sure you want to deactivate this semester?')) {
            const toastId = showLoading('Deactivating semester...');
            try {
                await dispatch(deactivateSemester(semesterId)).unwrap();
                dismiss(toastId);
                showSuccess('Semester deactivated successfully');
                loadSemesters();
                dispatch(fetchActiveSemester());
            } catch (error) {
                dismiss(toastId);
                showError(error || 'Failed to deactivate semester');
            }
        }
    };

    const handleResetLeaveBalance = async () => {
        if (window.confirm('Are you sure you want to reset leave balance for all students to 20?')) {
            const toastId = showLoading('Resetting leave balance...');
            try {
                const result = await dispatch(resetLeaveBalance(20)).unwrap();
                dismiss(toastId);
                showSuccess(`Leave balance reset for ${result} students`);
            } catch (error) {
                dismiss(toastId);
                showError(error || 'Failed to reset leave balance');
            }
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Semester Management</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                {activeSemester ? (
                                    <>Active Semester: <span className="font-semibold text-primary-600">{activeSemester.name}</span></>
                                ) : (
                                    'No active semester'
                                )}
                            </p>
                        </div>
                        <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-3">
                            <button
                                onClick={() => navigate('/hod/semester/add')}
                                className="inline-flex items-center justify-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                            >
                                <FiPlus className="mr-2" />
                                <span>New Semester</span>
                            </button>
                            <button
                                onClick={() => setShowPromotionModal(true)}
                                className="inline-flex items-center justify-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiUsers className="mr-2" />
                                <span>Promote Students</span>
                            </button>
                            <button
                                onClick={handleResetLeaveBalance}
                                className="inline-flex items-center justify-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiRefreshCw className="mr-2" />
                                <span>Reset Leave Balance</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Statistics Cards */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-8">
                    <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-xs text-blue-600 font-medium">Total Semesters</p>
                                <p className="text-2xl font-bold text-blue-700">{statistics?.totalSemesters || 0}</p>
                            </div>
                            <FiCalendar className="w-8 h-8 text-blue-500 opacity-50" />
                        </div>
                    </div>
                    <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-xl p-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-xs text-green-600 font-medium">Active Semester</p>
                                <p className="text-lg font-bold text-green-700">{activeSemester?.name || 'None'}</p>
                            </div>
                            <FiClock className="w-8 h-8 text-green-500 opacity-50" />
                        </div>
                    </div>
                    <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl p-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-xs text-purple-600 font-medium">Total Students</p>
                                <p className="text-2xl font-bold text-purple-700">{statistics?.totalStudents || 0}</p>
                            </div>
                            <FiUsers className="w-8 h-8 text-purple-500 opacity-50" />
                        </div>
                    </div>
                    <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl p-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-xs text-yellow-600 font-medium">Upcoming</p>
                                <p className="text-2xl font-bold text-yellow-700">{statistics?.upcomingSemesters?.length || 0}</p>
                            </div>
                            <FiCalendar className="w-8 h-8 text-yellow-500 opacity-50" />
                        </div>
                    </div>
                </div>

                {/* Semester Grid */}
                {loading.list ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {[1, 2, 3, 4, 5, 6].map((i) => (
                            <div key={i} className="bg-white rounded-xl shadow-sm p-6 animate-pulse">
                                <div className="h-6 bg-gray-200 rounded w-3/4 mb-4"></div>
                                <div className="space-y-3">
                                    <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                                    <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                                    <div className="h-4 bg-gray-200 rounded w-1/3"></div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : semesters?.content?.length > 0 ? (
                    <>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {semesters.content.map((semester) => (
                                <SemesterCard
                                    key={semester.id}
                                    semester={semester}
                                    onEdit={(sem) => navigate(`/hod/semester/edit/${sem.id}`, { state: { semester: sem } })}
                                    onActivate={handleActivate}
                                    onDeactivate={handleDeactivate}
                                />
                            ))}
                        </div>

                        {/* Pagination */}
                        {semesters.totalPages > 1 && (
                            <div className="mt-8 flex items-center justify-between">
                                <p className="text-sm text-gray-700">
                                    Showing <span className="font-medium">{page * size + 1}</span> to{' '}
                                    <span className="font-medium">
                                        {Math.min((page + 1) * size, semesters.totalElements)}
                                    </span>{' '}
                                    of <span className="font-medium">{semesters.totalElements}</span> results
                                </p>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => setPage(p => Math.max(0, p - 1))}
                                        disabled={page === 0}
                                        className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                        Previous
                                    </button>
                                    <button
                                        onClick={() => setPage(p => p + 1)}
                                        disabled={page >= semesters.totalPages - 1}
                                        className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                        Next
                                    </button>
                                </div>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="bg-white rounded-xl shadow-sm p-12 text-center">
                        <div className="text-6xl mb-4">📅</div>
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No Semesters Found</h3>
                        <p className="text-gray-500 mb-6">Get started by creating your first semester</p>
                        <button
                            onClick={() => navigate('/hod/semester/add')}
                            className="inline-flex items-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                        >
                            <FiPlus className="mr-2" />
                            Create Semester
                        </button>
                    </div>
                )}
            </div>

            {/* Promotion Modal */}
            <PromotionModal
                isOpen={showPromotionModal}
                onClose={() => setShowPromotionModal(false)}
                semesters={semesters?.content}
                onPromote={async (data) => {
                    const toastId = showLoading('Promoting students...');
                    try {
                        // Dispatch promotion action here
                        dismiss(toastId);
                        showSuccess('Students promoted successfully');
                        setShowPromotionModal(false);
                        loadSemesters();
                    } catch (error) {
                        dismiss(toastId);
                        showError(error || 'Failed to promote students');
                    }
                }}
                loading={loading.action}
            />
        </div>
    );
};

export default SemesterManagement;