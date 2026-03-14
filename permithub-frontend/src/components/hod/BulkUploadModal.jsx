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
                        className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
                        onClick={onClose}
                    />

                    {/* Modal panel */}
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.95 }}
                        className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
                    >
                        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-lg font-medium text-gray-900">
                                    {type === 'faculty' ? 'Upload Faculty' : 'Upload Students'}
                                </h3>
                                <button
                                    onClick={onClose}
                                    className="text-gray-400 hover:text-gray-500 transition-colors"
                                >
                                    <FiX className="w-6 h-6" />
                                </button>
                            </div>

                            {/* Template download */}
                            <div className="mb-4 p-4 bg-blue-50 rounded-lg">
                                <div className="flex items-start">
                                    <div className="flex-shrink-0">
                                        <FiDownload className="h-5 w-5 text-blue-400" />
                                    </div>
                                    <div className="ml-3 flex-1">
                                        <p className="text-sm text-blue-700">
                                            Download the template first to ensure correct format
                                        </p>
                                        <button
                                            onClick={handleDownloadTemplate}
                                            className="mt-2 text-sm font-medium text-blue-700 hover:text-blue-600 underline"
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
                                    className={`mt-2 flex justify-center px-6 pt-5 pb-6 border-2 border-dashed rounded-lg cursor-pointer transition-colors
                                        ${isDragActive 
                                            ? 'border-primary-500 bg-primary-50' 
                                            : 'border-gray-300 hover:border-primary-400'
                                        }`}
                                >
                                    <div className="space-y-1 text-center">
                                        <FiUpload className="mx-auto h-12 w-12 text-gray-400" />
                                        <div className="flex text-sm text-gray-600">
                                            <input {...getInputProps()} />
                                            <p className="pl-1">
                                                {isDragActive 
                                                    ? 'Drop the file here' 
                                                    : 'Drag and drop your Excel file here, or click to select'
                                                }
                                            </p>
                                        </div>
                                        <p className="text-xs text-gray-500">
                                            .xlsx or .xls files only (max 10MB)
                                        </p>
                                    </div>
                                </div>
                            ) : (
                                <div className="mt-2 border rounded-lg p-4">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center space-x-3">
                                            <div className="flex-shrink-0">
                                                <FiUpload className="h-8 w-8 text-primary-500" />
                                            </div>
                                            <div>
                                                <p className="text-sm font-medium text-gray-900">
                                                    {file.name}
                                                </p>
                                                <p className="text-xs text-gray-500">
                                                    {excelUtils.formatFileSize(file.size)}
                                                </p>
                                            </div>
                                        </div>
                                        <button
                                            onClick={removeFile}
                                            className="text-gray-400 hover:text-gray-500"
                                        >
                                            <FiX className="w-5 h-5" />
                                        </button>
                                    </div>

                                    {/* Preview */}
                                    {preview.length > 0 && (
                                        <div className="mt-4">
                                            <p className="text-xs font-medium text-gray-700 mb-2">
                                                Preview (first 5 rows):
                                            </p>
                                            <div className="bg-gray-50 rounded-lg p-2 overflow-x-auto">
                                                <table className="min-w-full text-xs">
                                                    <thead>
                                                        <tr className="text-gray-500">
                                                            {Object.keys(preview[0]).map((key) => (
                                                                <th key={key} className="px-2 py-1 text-left">
                                                                    {key}
                                                                </th>
                                                            ))}
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        {preview.map((row, idx) => (
                                                            <tr key={idx} className="border-t border-gray-200">
                                                                {Object.values(row).map((value, i) => (
                                                                    <td key={i} className="px-2 py-1 text-gray-600">
                                                                        {String(value).substring(0, 20)}
                                                                        {String(value).length > 20 ? '...' : ''}
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
                                <div className="mt-4 p-4 bg-red-50 rounded-lg">
                                    <div className="flex">
                                        <FiAlertCircle className="h-5 w-5 text-red-400" />
                                        <div className="ml-3">
                                            <h3 className="text-sm font-medium text-red-800">
                                                Upload Errors
                                            </h3>
                                            <div className="mt-2 text-sm text-red-700">
                                                <ul className="list-disc pl-5 space-y-1">
                                                    {errors.map((error, idx) => (
                                                        <li key={idx}>{error}</li>
                                                    ))}
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* Progress Bar */}
                            {uploading && (
                                <div className="mt-4">
                                    <div className="flex justify-between text-sm text-gray-600 mb-1">
                                        <span>Uploading...</span>
                                        <span>{uploadProgress}%</span>
                                    </div>
                                    <div className="w-full bg-gray-200 rounded-full h-2">
                                        <div
                                            className="bg-primary-600 h-2 rounded-full transition-all duration-300"
                                            style={{ width: `${uploadProgress}%` }}
                                        ></div>
                                    </div>
                                </div>
                            )}

                            {uploadResults && (
                                <div className={`mt-4 p-4 rounded-lg ${uploadResults.status === 'SUCCESS' ? 'bg-green-50' : 'bg-yellow-50'}`}>
                                    <div className="flex items-center mb-2">
                                        {uploadResults.status === 'SUCCESS' ? 
                                            <FiCheckCircle className="h-5 w-5 text-green-400 mr-2" /> : 
                                            <FiAlertCircle className="h-5 w-5 text-yellow-500 mr-2" />
                                        }
                                        <h3 className={`text-sm font-bold ${uploadResults.status === 'SUCCESS' ? 'text-green-800' : 'text-yellow-800'}`}>
                                            Upload Result: {uploadResults.status}
                                        </h3>
                                    </div>
                                    <div className="text-sm space-y-1">
                                        <p>Total Records: <span className="font-bold">{uploadResults.totalRecords}</span></p>
                                        <p className="text-green-700">Successful: <span className="font-bold">{uploadResults.successfulRecords}</span></p>
                                        <p className="text-red-600">Failed: <span className="font-bold">{uploadResults.failedRecords}</span></p>
                                    </div>

                                    {uploadResults.errors && uploadResults.errors.length > 0 && (
                                        <div className="mt-3">
                                            <p className="text-xs font-bold text-gray-700 mb-1">Error Details:</p>
                                            <div className="max-h-32 overflow-y-auto border border-yellow-200 rounded p-2 bg-white text-xs">
                                                {uploadResults.errors.map((err, idx) => (
                                                    <div key={idx} className="mb-2 last:mb-0 border-b last:border-0 pb-1 border-gray-100">
                                                        <span className="font-bold text-red-600">Row {err.rowNumber}:</span> {err.errorMessage}
                                                        {err.employeeId && <span className="ml-1 text-gray-400">({err.employeeId})</span>}
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>

                        {/* Footer */}
                        <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                            <button
                                type="button"
                                onClick={handleUpload}
                                disabled={!file || uploading}
                                className="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-primary-600 text-base font-medium text-white hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed sm:ml-3 sm:w-auto sm:text-sm"
                            >
                                {uploading ? 'Uploading...' : 'Upload'}
                            </button>
                            <button
                                type="button"
                                onClick={onClose}
                                disabled={uploading}
                                className="mt-3 w-full inline-flex justify-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
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