import React, { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FiUpload, FiDownload, FiX, FiCheckCircle, FiAlertCircle } from 'react-icons/fi';
import { useDropzone } from 'react-dropzone';
import { excelUtils } from '../../utils/excelUtils';

const BulkUploadModal = ({ isOpen, onClose, type, onUpload, onDownloadTemplate }) => {
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [preview, setPreview] = useState([]);
    const [errors, setErrors] = useState([]);
    const [uploadResults, setUploadResults] = useState(null);
    const [uploadProgress, setUploadProgress] = useState(0);

    const onDrop = useCallback(async (acceptedFiles) => {
        const selectedFile = acceptedFiles[0];
        
        // Validate file
        if (!excelUtils.isValidExcelFile(selectedFile)) {
            setErrors(['Please upload a valid Excel file (.xlsx or .xls)']);
            return;
        }

        setFile(selectedFile);
        setErrors([]);

        // Parse and preview
        try {
            const data = await excelUtils.parseExcel(selectedFile);
            setPreview(data.slice(0, 5)); // Show first 5 rows
        } catch (error) {
            setErrors(['Failed to parse Excel file']);
        }
    }, []);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'application/vnd.ms-excel': ['.xls'],
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': ['.xlsx']
        },
        maxFiles: 1
    });

    const handleUpload = async () => {
        if (!file) return;

        setUploading(true);
        setUploadProgress(0);

        // Simulate progress
        const interval = setInterval(() => {
            setUploadProgress(prev => {
                if (prev >= 90) {
                    clearInterval(interval);
                    return 90;
                }
                return prev + 10;
            });
        }, 500);

        try {
            const response = await onUpload(file);
            clearInterval(interval);
            setUploadProgress(100);
            
            if (response && response.data) {
                setUploadResults(response.data);
            } else {
                // If SUCCESS status was returned directly
                setUploadResults({ status: 'SUCCESS', successfulRecords: 1 });
            }
        } catch (error) {
            setErrors([error.message || 'Upload failed']);
            setUploading(false);
            clearInterval(interval);
        } finally {
            setUploading(false);
            clearInterval(interval);
        }
    };

    const handleDownloadTemplate = () => {
        onDownloadTemplate();
    };

    const removeFile = () => {
        setFile(null);
        setPreview([]);
        setErrors([]);
        setUploadResults(null);
    };

    // Remove early return to let AnimatePresence handle it in parent
    // if (!isOpen) return null; 

    return (
        <div className="fixed inset-0 z-50 overflow-y-auto">
            <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
                {/* Background overlay */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="fixed inset-0 bg-gray-900/60 backdrop-blur-sm transition-opacity"
                    onClick={onClose}
                />

                {/* This element is to trick the browser into centering the modal contents. */}
                <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

                {/* Modal panel */}
                <motion.div
                    initial={{ opacity: 0, scale: 0.95, y: 20 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.95, y: 20 }}
                    className="relative inline-block align-bottom bg-white rounded-2xl text-left overflow-hidden shadow-2xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full z-10"
                >
                    <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-xl font-bold text-gray-900">
                                {type === 'faculty' ? 'Upload Faculty' : 'Upload Students'}
                            </h3>
                            <button
                                onClick={onClose}
                                className="p-2 text-gray-400 hover:text-gray-600 rounded-full hover:bg-gray-100 transition-all"
                            >
                                <FiX className="w-6 h-6" />
                            </button>
                        </div>

                        {/* Template download */}
                        <div className="mb-6 p-4 bg-primary-50 rounded-xl border border-primary-100">
                            <div className="flex items-start">
                                <div className="flex-shrink-0">
                                    <FiDownload className="h-5 w-5 text-primary-500" />
                                </div>
                                <div className="ml-3 flex-1">
                                    <p className="text-sm text-primary-900 font-medium">
                                        Download the template first to ensure correct format
                                    </p>
                                    <button
                                        onClick={handleDownloadTemplate}
                                        className="mt-2 text-sm font-bold text-primary-600 hover:text-primary-700 underline focus:outline-none"
                                    >
                                        Download Template
                                    </button>
                                </div>
                            </div>
                        </div>

                        {/* Dropzone */}
                        {!file ? (
                            <div
                                {...getRootProps()}
                                className={`mt-2 flex justify-center px-6 pt-10 pb-10 border-2 border-dashed rounded-xl cursor-pointer transition-all
                                    ${isDragActive 
                                        ? 'border-primary-500 bg-primary-50 ring-4 ring-primary-500/10' 
                                        : 'border-gray-300 hover:border-primary-400 hover:bg-gray-50'
                                    }`}
                            >
                                <div className="space-y-2 text-center">
                                    <div className="mx-auto h-16 w-16 bg-primary-100 rounded-full flex items-center justify-center mb-4 transition-transform hover:scale-110">
                                        <FiUpload className="h-8 w-8 text-primary-600" />
                                    </div>
                                    <div className="flex flex-col text-sm text-gray-600">
                                        <input {...getInputProps()} />
                                        <p className="font-semibold text-gray-900">
                                            {isDragActive 
                                                ? 'Drop the file here' 
                                                : 'Click to select or drag and drop'
                                            }
                                        </p>
                                        <p className="text-gray-500 mt-1">
                                            Your Excel file (.xlsx or .xls)
                                        </p>
                                    </div>
                                    <p className="text-xs text-gray-400 bg-gray-100 py-1 px-2 rounded-full inline-block">
                                        Max 10MB
                                    </p>
                                </div>
                            </div>
                        ) : (
                            <div className="mt-2 border border-gray-200 rounded-xl p-4 bg-gray-50">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center space-x-3">
                                        <div className="h-12 w-12 bg-white rounded-lg flex items-center justify-center shadow-sm border border-gray-100">
                                            <FiUpload className="h-6 w-6 text-primary-500" />
                                        </div>
                                        <div>
                                            <p className="text-sm font-bold text-gray-900 truncate max-w-[200px]">
                                                {file.name}
                                            </p>
                                            <p className="text-xs text-gray-500 font-medium">
                                                {excelUtils.formatFileSize(file.size)}
                                            </p>
                                        </div>
                                    </div>
                                    <button
                                        onClick={removeFile}
                                        className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-all"
                                    >
                                        <FiX className="w-5 h-5" />
                                    </button>
                                </div>

                                {/* Preview */}
                                {preview.length > 0 && (
                                    <div className="mt-4">
                                        <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">
                                            Data Preview:
                                        </p>
                                        <div className="bg-white rounded-lg border border-gray-200 overflow-x-auto">
                                            <table className="min-w-full text-[10px]">
                                                <thead className="bg-gray-50">
                                                    <tr>
                                                        {Object.keys(preview[0]).map((key) => (
                                                            <th key={key} className="px-3 py-2 text-left font-bold text-gray-600 border-b border-gray-200 whitespace-nowrap">
                                                                {key}
                                                            </th>
                                                        ))}
                                                    </tr>
                                                </thead>
                                                <tbody className="divide-y divide-gray-100">
                                                    {preview.map((row, idx) => (
                                                        <tr key={idx}>
                                                            {Object.values(row).map((value, i) => (
                                                                <td key={i} className="px-3 py-2 text-gray-600 whitespace-nowrap">
                                                                    {String(value).substring(0, 15)}
                                                                    {String(value).length > 15 ? '...' : ''}
                                                                </td>
                                                            ))}
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Errors */}
                        {errors.length > 0 && (
                            <div className="mt-4 p-4 bg-red-50 rounded-xl border border-red-100">
                                <div className="flex">
                                    <FiAlertCircle className="h-5 w-5 text-red-500" />
                                    <div className="ml-3">
                                        <h3 className="text-sm font-bold text-red-900">
                                            Validation Errors
                                        </h3>
                                        <div className="mt-2 text-sm text-red-700">
                                            <ul className="list-disc pl-5 space-y-1">
                                                {errors.map((error, idx) => (
                                                    <li key={idx} className="font-medium">{error}</li>
                                                ))}
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Progress Bar */}
                        {uploading && (
                            <div className="mt-6">
                                <div className="flex justify-between text-sm font-bold text-gray-700 mb-2">
                                    <span className="flex items-center">
                                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-primary-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                        </svg>
                                        Uploading and Processing...
                                    </span>
                                    <span>{uploadProgress}%</span>
                                </div>
                                <div className="w-full bg-gray-100 rounded-full h-2.5 overflow-hidden">
                                    <motion.div
                                        className="bg-primary-600 h-full rounded-full"
                                        initial={{ width: 0 }}
                                        animate={{ width: `${uploadProgress}%` }}
                                        transition={{ duration: 0.3 }}
                                    ></motion.div>
                                </div>
                            </div>
                        )}

                        {uploadResults && (
                            <div className={`mt-6 p-5 rounded-xl border ${uploadResults.status === 'SUCCESS' ? 'bg-green-50 border-green-100' : 'bg-yellow-50 border-yellow-100'}`}>
                                <div className="flex items-center mb-4">
                                    <div className={`p-2 rounded-lg ${uploadResults.status === 'SUCCESS' ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-600'}`}>
                                        {uploadResults.status === 'SUCCESS' ? 
                                            <FiCheckCircle className="h-6 w-6" /> : 
                                            <FiAlertCircle className="h-6 w-6" />
                                        }
                                    </div>
                                    <div className="ml-3">
                                        <h3 className={`text-sm font-bold ${uploadResults.status === 'SUCCESS' ? 'text-green-900' : 'text-yellow-900'}`}>
                                            Upload Result: {uploadResults.status.replace('_', ' ')}
                                        </h3>
                                        <p className="text-xs text-gray-500 font-medium">Processed {uploadResults.totalRecords} records</p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="bg-white/60 p-3 rounded-lg border border-black/5">
                                        <p className="text-xs font-medium text-gray-500 mb-1">Successful</p>
                                        <p className="text-xl font-bold text-green-600">{uploadResults.successfulRecords}</p>
                                    </div>
                                    <div className="bg-white/60 p-3 rounded-lg border border-black/5">
                                        <p className="text-xs font-medium text-gray-500 mb-1">Failed</p>
                                        <p className="text-xl font-bold text-red-500">{uploadResults.failedRecords}</p>
                                    </div>
                                </div>

                                {uploadResults.errors && uploadResults.errors.length > 0 && (
                                    <div className="mt-4">
                                        <p className="text-xs font-bold text-gray-600 uppercase tracking-wider mb-2">Error Details:</p>
                                        <div className="max-h-40 overflow-y-auto border border-yellow-200 rounded-xl p-1 bg-white/80 shadow-inner">
                                            {uploadResults.errors.map((err, idx) => (
                                                <div key={idx} className="p-2 border-b last:border-0 border-gray-100 text-[11px]">
                                                    <span className="inline-block px-1.5 py-0.5 bg-red-100 text-red-600 rounded font-bold mr-2">Row {err.rowNumber}</span>
                                                    <span className="text-gray-800 font-medium">{err.errorMessage}</span>
                                                    {err.employeeId && <span className="ml-2 px-1.5 py-0.5 bg-gray-100 text-gray-500 rounded font-mono">{err.employeeId}</span>}
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>

                    {/* Footer */}
                    <div className="bg-gray-50 px-6 py-4 flex flex-col sm:flex-row-reverse gap-3 border-t border-gray-100">
                        <button
                            type="button"
                            onClick={handleUpload}
                            disabled={!file || uploading}
                            className="flex-1 inline-flex justify-center items-center rounded-xl px-4 py-2.5 bg-primary-600 text-sm font-bold text-white hover:bg-primary-700 focus:outline-none focus:ring-4 focus:ring-primary-500/20 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg shadow-primary-500/30"
                        >
                            {uploading ? (
                                <>
                                    <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                    </svg>
                                    Uploading...
                                </>
                            ) : (
                                <>
                                    <FiUpload className="mr-2 h-4 w-4" />
                                    Confirm Upload
                                </>
                            )}
                        </button>
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={uploading}
                            className="flex-1 inline-flex justify-center items-center rounded-xl px-4 py-2.5 bg-white border border-gray-300 text-sm font-bold text-gray-700 hover:bg-gray-50 hover:border-gray-400 focus:outline-none focus:ring-4 focus:ring-gray-500/10 disabled:opacity-50 transition-all"
                        >
                            Cancel
                        </button>
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default BulkUploadModal;