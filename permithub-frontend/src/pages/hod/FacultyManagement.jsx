import React, { useEffect, useState, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiUpload, FiDownload } from 'react-icons/fi';
import FacultyTable from '../../components/hod/FacultyTable';
import FacultyFilters from '../../components/hod/FacultyFilters';
import BulkUploadModal from '../../components/hod/BulkUploadModal';
import RoleAssignmentModal from '../../components/hod/RoleAssignmentModal';
import {
    fetchAllFaculty,
    fetchFacultyStatistics,
    deactivateFaculty,
    activateFaculty,
    assignRoles,
    selectAllFaculty,
    selectFacultyStatistics,
    selectFacultyLoading,
    selectFacultySuccessMessage,
    clearSuccessMessage
} from '../../store/facultySlice';
import { useToast } from '../../hooks/useToast';
import uploadService from '../../services/uploadService';

const FacultyManagement = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { showSuccess, showError, showLoading, dismiss } = useToast();
    
    const faculty = useSelector(selectAllFaculty);
    const statistics = useSelector(selectFacultyStatistics);
    const loading = useSelector(selectFacultyLoading);
    const successMessage = useSelector(selectFacultySuccessMessage);

    const [filters, setFilters] = useState({
        search: '',
        designation: '',
        role: '',
        isActive: ''
    });
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [showBulkUpload, setShowBulkUpload] = useState(false);
    const [showRoleModal, setShowRoleModal] = useState(false);
    const [selectedFaculty, setSelectedFaculty] = useState(null);
    const [uploadType, setUploadType] = useState('faculty');

    const loadFaculty = useCallback(() => {
        dispatch(fetchAllFaculty({
            ...filters,
            page,
            size,
            sortBy: 'id',
            direction: 'desc'
        }));
    }, [dispatch, filters, page, size]);

    useEffect(() => {
        loadFaculty();
        dispatch(fetchFacultyStatistics());
    }, [loadFaculty, dispatch]);

    useEffect(() => {
        if (successMessage) {
            showSuccess(successMessage);
            dispatch(clearSuccessMessage());
        }
    }, [successMessage, showSuccess, dispatch]);

    const handleFilterChange = (newFilters) => {
        setFilters(newFilters);
        setPage(0);
    };

    const handleEdit = (faculty) => {
        navigate(`/hod/faculty/edit/${faculty.id}`, { state: { faculty } });
    };

    const handleDeactivate = async (faculty) => {
        if (window.confirm(`Are you sure you want to deactivate ${faculty.fullName}?`)) {
            const toastId = showLoading('Deactivating faculty...');
            try {
                await dispatch(deactivateFaculty({ 
                    id: faculty.id, 
                    reason: 'Deactivated by HOD' 
                })).unwrap();
                dismiss(toastId);
                showSuccess('Faculty deactivated successfully');
                loadFaculty();
            } catch (error) {
                dismiss(toastId);
                showError(error || 'Failed to deactivate faculty');
            }
        }
    };

    const handleActivate = async (faculty) => {
        const toastId = showLoading('Activating faculty...');
        try {
            await dispatch(activateFaculty(faculty.id)).unwrap();
            dismiss(toastId);
            showSuccess('Faculty activated successfully');
            loadFaculty();
        } catch (error) {
            dismiss(toastId);
            showError(error || 'Failed to activate faculty');
        }
    };

    const handleAssignRoles = (faculty) => {
        setSelectedFaculty(faculty);
        setShowRoleModal(true);
    };

    const handleRoleAssign = async (facultyId, roles, remarks) => {
        const toastId = showLoading('Assigning roles...');
        try {
            await dispatch(assignRoles({ id: facultyId, roles })).unwrap();
            dismiss(toastId);
            showSuccess('Roles assigned successfully');
            setShowRoleModal(false);
            setSelectedFaculty(null);
            loadFaculty();
        } catch (error) {
            dismiss(toastId);
            showError(error || 'Failed to assign roles');
        }
    };

    const handleBulkUpload = async (file) => {
        const toastId = showLoading('Uploading faculty data...');
        try {
            const response = await uploadService.uploadFaculty(file, 1);
            dismiss(toastId);
            if (response.data.status === 'SUCCESS' || response.data.status === 'PARTIAL_SUCCESS') {
                if (response.data.status === 'SUCCESS') {
                    showSuccess(`Successfully uploaded ${response.data.successfulRecords} faculty members`);
                }
                loadFaculty();
                return response;
            } else {
                showError(response.data.message || 'Upload failed');
                return response;
            }
        } catch (error) {
            dismiss(toastId);
            showError(error.message || 'Upload failed');
            throw error;
        }
    };

    const handleDownloadTemplate = async () => {
        try {
            const template = await uploadService.downloadFacultyTemplate();
            const url = window.URL.createObjectURL(new Blob([template]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'faculty_upload_template.xlsx');
            document.body.appendChild(link);
            link.click();
            link.remove();
            showSuccess('Template downloaded successfully');
        } catch (error) {
            showError('Failed to download template');
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Faculty Management</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                Total Faculty: {statistics?.totalFaculty || 0} • 
                                Mentors: {statistics?.totalMentors || 0} • 
                                Class Advisors: {statistics?.totalClassAdvisors || 0}
                            </p>
                        </div>
                        <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-3">
                            <button
                                onClick={() => navigate('/hod/faculty/add')}
                                className="inline-flex items-center justify-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                            >
                                <FiPlus className="mr-2" />
                                <span>Add Faculty</span>
                            </button>
                            <button
                                onClick={() => {
                                    setUploadType('faculty');
                                    setShowBulkUpload(true);
                                }}
                                className="inline-flex items-center justify-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiUpload className="mr-2" />
                                <span>Bulk Upload</span>
                            </button>
                            <button
                                onClick={handleDownloadTemplate}
                                className="inline-flex items-center justify-center px-4 py-2 border border-gray-300 bg-white text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <FiDownload className="mr-2" />
                                <span>Template</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Filters */}
                <FacultyFilters filters={filters} onFilterChange={handleFilterChange} />

                {/* Statistics Cards - Mobile Responsive */}
                <div className="mt-6 grid grid-cols-2 sm:grid-cols-4 gap-3">
                    <div className="bg-blue-50 rounded-lg p-3 text-center">
                        <p className="text-xs text-blue-600 font-medium">Total Faculty</p>
                        <p className="text-xl font-bold text-blue-700">{statistics?.totalFaculty || 0}</p>
                    </div>
                    <div className="bg-green-50 rounded-lg p-3 text-center">
                        <p className="text-xs text-green-600 font-medium">Active</p>
                        <p className="text-xl font-bold text-green-700">{statistics?.totalFaculty || 0}</p>
                    </div>
                    <div className="bg-purple-50 rounded-lg p-3 text-center">
                        <p className="text-xs text-purple-600 font-medium">Mentors</p>
                        <p className="text-xl font-bold text-purple-700">{statistics?.totalMentors || 0}</p>
                    </div>
                    <div className="bg-yellow-50 rounded-lg p-3 text-center">
                        <p className="text-xs text-yellow-600 font-medium">Class Advisors</p>
                        <p className="text-xl font-bold text-yellow-700">{statistics?.totalClassAdvisors || 0}</p>
                    </div>
                </div>

                {/* Faculty Table */}
                <div className="mt-6">
                    <FacultyTable
                        faculty={faculty}
                        loading={loading.list}
                        onEdit={handleEdit}
                        onDeactivate={handleDeactivate}
                        onActivate={handleActivate}
                        onAssignRoles={handleAssignRoles}
                    />
                </div>

                {/* Pagination */}
                {faculty?.totalPages > 1 && (
                    <div className="mt-6 flex items-center justify-between">
                        <p className="text-sm text-gray-700">
                            Showing <span className="font-medium">{page * size + 1}</span> to{' '}
                            <span className="font-medium">
                                {Math.min((page + 1) * size, faculty.totalElements)}
                            </span>{' '}
                            of <span className="font-medium">{faculty.totalElements}</span> results
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
                                disabled={page >= faculty.totalPages - 1}
                                className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                            >
                                Next
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Modals */}
            <AnimatePresence>
                {showBulkUpload && (
                    <BulkUploadModal
                        isOpen={showBulkUpload}
                        onClose={() => setShowBulkUpload(false)}
                        type={uploadType}
                        onUpload={handleBulkUpload}
                        onDownloadTemplate={handleDownloadTemplate}
                    />
                )}
            </AnimatePresence>

            <RoleAssignmentModal
                isOpen={showRoleModal}
                onClose={() => {
                    setShowRoleModal(false);
                    setSelectedFaculty(null);
                }}
                faculty={selectedFaculty}
                onAssign={handleRoleAssign}
                loading={loading.action}
            />
        </div>
    );
};

export default FacultyManagement;