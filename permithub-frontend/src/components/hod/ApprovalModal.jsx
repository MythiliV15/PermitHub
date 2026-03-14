import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiX, FiCheck, FiXCircle, FiMapPin, FiUser, FiMail, FiPhone } from 'react-icons/fi';
import { formatters } from '../../utils/formatters';

const ApprovalModal = ({ isOpen, onClose, request, type, onAction, loading }) => {
    const [remarks, setRemarks] = useState('');
    const [action, setAction] = useState(null);

    if (!isOpen || !request) return null;

    const renderRequestDetails = () => {
        switch(type) {
            case 'LEAVE':
                return (
                    <div className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">Start Date</p>
                                <p className="font-medium">{formatters.formatDate(request.startDate)}</p>
                            </div>
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">End Date</p>
                                <p className="font-medium">{formatters.formatDate(request.endDate)}</p>
                            </div>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Total Days</p>
                            <p className="font-medium">{request.totalDays} days</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Category</p>
                            <p className="font-medium">{request.category}</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Reason</p>
                            <p className="text-sm">{request.reason}</p>
                        </div>
                        {request.medicalCertificate && (
                            <div className="bg-blue-50 p-3 rounded-lg">
                                <p className="text-xs text-blue-600 mb-1">Medical Certificate Attached</p>
                                <a href={request.medicalCertificate} className="text-sm text-blue-600 underline">View Document</a>
                            </div>
                        )}
                    </div>
                );

            case 'OD':
                return (
                    <div className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">Start Date</p>
                                <p className="font-medium">{formatters.formatDate(request.startDate)}</p>
                            </div>
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">End Date</p>
                                <p className="font-medium">{formatters.formatDate(request.endDate)}</p>
                            </div>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Event Type</p>
                            <p className="font-medium">{request.eventType}</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Event Name</p>
                            <p className="font-medium">{request.eventName}</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Location</p>
                            <p className="font-medium">{request.location || 'Not specified'}</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Description</p>
                            <p className="text-sm">{request.description}</p>
                        </div>
                        {request.proofDocument && (
                            <div className="bg-blue-50 p-3 rounded-lg">
                                <p className="text-xs text-blue-600 mb-1">Proof Document</p>
                                <a href={request.proofDocument} className="text-sm text-blue-600 underline">View Document</a>
                            </div>
                        )}
                    </div>
                );

            case 'OUTPASS':
                return (
                    <div className="space-y-4">
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Destination</p>
                            <p className="font-medium flex items-center">
                                <FiMapPin className="mr-1" /> {request.destination}
                            </p>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">Out Date & Time</p>
                                <p className="font-medium text-sm">{formatters.formatDateTime(request.outDateTime)}</p>
                            </div>
                            <div className="bg-gray-50 p-3 rounded-lg">
                                <p className="text-xs text-gray-500">Return Date & Time</p>
                                <p className="font-medium text-sm">{formatters.formatDateTime(request.expectedReturnDateTime)}</p>
                            </div>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Reason</p>
                            <p className="text-sm">{request.reason}</p>
                        </div>
                        <div className="bg-gray-50 p-3 rounded-lg">
                            <p className="text-xs text-gray-500">Emergency Contact</p>
                            <p className="font-medium">{request.emergencyContact}</p>
                        </div>
                        <div className="bg-yellow-50 p-3 rounded-lg">
                            <p className="text-xs text-yellow-600 mb-2">Previous Approvals</p>
                            <div className="space-y-1 text-sm">
                                {request.parentApproved && (
                                    <p className="flex items-center text-green-600">
                                        <FiCheck className="mr-1" /> Parent Approved
                                    </p>
                                )}
                                {request.mentorApproved && (
                                    <p className="flex items-center text-green-600">
                                        <FiCheck className="mr-1" /> Mentor Approved
                                    </p>
                                )}
                                {request.wardenApproved && (
                                    <p className="flex items-center text-green-600">
                                        <FiCheck className="mr-1" /> Warden Approved
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>
                );

            default:
                return null;
        }
    };

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 overflow-y-auto">
                <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
                        onClick={onClose}
                    />

                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.95 }}
                        className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full"
                    >
                        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-lg font-medium text-gray-900">
                                    {type} Request Details
                                </h3>
                                <button
                                    onClick={onClose}
                                    className="text-gray-400 hover:text-gray-500 transition-colors"
                                >
                                    <FiX className="w-6 h-6" />
                                </button>
                            </div>

                            {/* Student Info */}
                            <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                                <div className="flex items-center">
                                    <div className="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                                        <FiUser className="h-6 w-6 text-primary-600" />
                                    </div>
                                    <div className="ml-4">
                                        <h4 className="text-sm font-medium text-gray-900">
                                            {request.studentName}
                                        </h4>
                                        <p className="text-sm text-gray-500">
                                            {request.registerNumber} • Year {request.year} - {request.section}
                                        </p>
                                        <div className="flex items-center mt-1 text-xs text-gray-500">
                                            <FiMail className="mr-1" /> {request.studentEmail}
                                            <FiPhone className="ml-3 mr-1" /> {request.studentPhone}
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Request Details */}
                            {renderRequestDetails()}

                            {/* Action Section */}
                            {!action ? (
                                <div className="mt-6 flex flex-col sm:flex-row gap-3">
                                    <button
                                        onClick={() => setAction('APPROVE')}
                                        className="flex-1 inline-flex justify-center items-center px-4 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                                    >
                                        <FiCheck className="mr-2" /> Approve
                                    </button>
                                    <button
                                        onClick={() => setAction('REJECT')}
                                        className="flex-1 inline-flex justify-center items-center px-4 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                                    >
                                        <FiXCircle className="mr-2" /> Reject
                                    </button>
                                </div>
                            ) : (
                                <div className="mt-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        {action === 'APPROVE' ? 'Approval' : 'Rejection'} Remarks <span className="text-red-500">*</span>
                                    </label>
                                    <textarea
                                        value={remarks}
                                        onChange={(e) => setRemarks(e.target.value)}
                                        rows="3"
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        placeholder={`Please provide ${action === 'APPROVE' ? 'approval' : 'rejection'} remarks...`}
                                        autoFocus
                                    />
                                    <div className="mt-4 flex flex-col sm:flex-row gap-3">
                                        <button
                                            onClick={() => onAction(action, remarks)}
                                            disabled={!remarks.trim() || loading}
                                            className={`flex-1 inline-flex justify-center items-center px-4 py-3 rounded-lg text-white transition-colors ${
                                                action === 'APPROVE' 
                                                    ? 'bg-green-600 hover:bg-green-700' 
                                                    : 'bg-red-600 hover:bg-red-700'
                                            } disabled:opacity-50 disabled:cursor-not-allowed`}
                                        >
                                            {loading ? 'Processing...' : `Confirm ${action}`}
                                        </button>
                                        <button
                                            onClick={() => setAction(null)}
                                            className="flex-1 inline-flex justify-center items-center px-4 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                                        >
                                            Back
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </motion.div>
                </div>
            </div>
        </AnimatePresence>
    );
};

export default ApprovalModal;