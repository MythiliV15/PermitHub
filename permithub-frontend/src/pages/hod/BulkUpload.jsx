import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useDropzone } from 'react-dropzone';
import { FiUpload, FiDownload, FiCheckCircle, FiXCircle, FiAlertCircle, FiArrowLeft } from 'react-icons/fi';
import { excelUtils } from '../../utils/excelUtils';
import uploadService from '../../services/uploadService';
import { useToast } from '../../hooks/useToast';

const BulkUpload = () => {
    const navigate = useNavigate();
    const { showSuccess, showError, showLoading, dismiss } = useToast();
    
    const [file, setFile] = useState(null);
    const [uploadType, setUploadType] = useState('faculty');
    const [preview, setPreview] = useState([]);
    const [errors, setErrors] = useState([]);
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [uploadResult, setUploadResult] = useState(null);

    const onDrop = useCallback(async (acceptedFiles) => {
        const selectedFile = acceptedFiles[0];
        
        // Validate file
        if (!excelUtils.isValidExcelFile(selectedFile)) {
            setErrors(['Please upload a valid Excel file (.xlsx or .xls)']);
            return;
        }

        setFile(selectedFile);
        setErrors([]);
        setUploadResult(null);

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

    const handleDownloadTemplate = async () => {
        try {
            const template = uploadType === 'faculty' 
                ? await uploadService.downloadFacultyTemplate()
                : await uploadService.downloadStudentTemplate();
            
            const url = window.URL.createObjectURL(new Blob([template]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${uploadType}_upload_template.xlsx`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            showSuccess('Template downloaded successfully');
        } catch (error) {
            showError('Failed to download template');
        }
    };

    const handleUpload = async () => {
        if (!file) return;

        setUploading(true);
        setUploadProgress(0);
        setErrors([]);
        setUploadResult(null);

        const toastId = showLoading('Uploading...');

        try {
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

            let response;
            if (uploadType === 'faculty') {
                response = await uploadService.uploadFaculty(file, 1, (progressEvent) => {
                    const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    setUploadProgress(percent);
                });
            } else {
                response = await uploadService.uploadStudents(file, 1, 1, 'A', (progressEvent) => {
                    const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    setUploadProgress(percent);
                });
            }

            clearInterval(interval);
            setUploadProgress(100);
            setUploadResult(response.data);

            dismiss(toastId);
            if (response.data.status === 'SUCCESS') {
                showSuccess(`Successfully uploaded ${response.data.successfulRecords} records`);
            } else if (response.data.status === 'PARTIAL_SUCCESS') {
                showError(`Uploaded ${response.data.successfulRecords} records with ${response.data.failedRecords} errors`);
            } else {
                showError('Upload failed');
            }

        } catch (error) {
            dismiss(toastId);
            setErrors([error.message || 'Upload failed']);
            showError(error.message || 'Upload failed');
        } finally {
            setUploading(false);
        }
    };

    const removeFile = () => {
        setFile(null);
        setPreview([]);
        setErrors([]);
        setUploadResult(null);
        setUploadProgress(0);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-10">
                <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex items-center">
                        <button
                            onClick={() => navigate(-1)}
                            className="mr-4 p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
                        >
                            <FiArrowLeft className="w-5 h-5" />
                        </button>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">Bulk Upload</h1>
                            <p className="text-sm text-gray-600 mt-1">
                                Upload multiple records at once using Excel file
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                >
                    {/* Upload Type Selection */}
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Select Upload Type
                        </label>
                        <div className="grid grid-cols-2 gap-3">
                            <button
                                onClick={() => setUploadType('faculty')}
                                className={`p-4 border rounded-lg text-center transition-colors ${
                                    uploadType === 'faculty'
                                        ? 'border-primary-500 bg-primary-50 text-primary-700'
                                        : 'border-gray-200 hover:border-primary-300'
                                }`}
                            >
                                <span className="text-2xl mb-2 block">👨‍🏫</span>
                                <span className="font-medium">Faculty Upload</span>
                            </button>
                            <button
                                onClick={() => setUploadType('student')}
                                className={`p-4 border rounded-lg text-center transition-colors ${
                                    uploadType === 'student'
                                        ? 'border-primary-500 bg-primary-50 text-primary-700'
                                        : 'border-gray-200 hover:border-primary-300'
                                }`}
                            >
                                <span className="text-2xl mb-2 block">👩‍🎓</span>
                                <span className="font-medium">Student Upload</span>
                            </button>
                        </div>
                    </div>

                    {/* Template Download */}
                    <div className="mb-6 p-4 bg-blue-50 rounded-lg">
                        <div className="flex items-start">
                            <FiDownload className="h-5 w-5 text-blue-400 mt-0.5" />
                            <div className="ml-3 flex-1">
                                <p className="text-sm text-blue-700">
                                    Download the template first to ensure correct format
                                </p>
                                <button
                                    onClick={handleDownloadTemplate}
                                    className="mt-2 inline-flex items-center text-sm font-medium text-blue-700 hover:text-blue-600"
                                >
                                    <FiDownload className="mr-1" />
                                    Download {uploadType === 'faculty' ? 'Faculty' : 'Student'} Template
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
                        <div className="border rounded-lg p-4">
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
                                    <FiXCircle className="w-5 h-5" />
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

                    {/* Upload Result */}
                    {uploadResult && (
                        <div className="mt-4">
                            <div className={`p-4 rounded-lg ${
                                uploadResult.status === 'SUCCESS' ? 'bg-green-50' :
                                uploadResult.status === 'PARTIAL_SUCCESS' ? 'bg-yellow-50' : 'bg-red-50'
                            }`}>
                                <div className="flex">
                                    {uploadResult.status === 'SUCCESS' ? (
                                        <FiCheckCircle className="h-5 w-5 text-green-400" />
                                    ) : uploadResult.status === 'PARTIAL_SUCCESS' ? (
                                        <FiAlertCircle className="h-5 w-5 text-yellow-400" />
                                    ) : (
                                        <FiXCircle className="h-5 w-5 text-red-400" />
                                    )}
                                    <div className="ml-3">
                                        <h3 className={`text-sm font-medium ${
                                            uploadResult.status === 'SUCCESS' ? 'text-green-800' :
                                            uploadResult.status === 'PARTIAL_SUCCESS' ? 'text-yellow-800' : 'text-red-800'
                                        }`}>
                                            Upload {uploadResult.status}
                                        </h3>
                                        <p className={`text-sm mt-1 ${
                                            uploadResult.status === 'SUCCESS' ? 'text-green-700' :
                                            uploadResult.status === 'PARTIAL_SUCCESS' ? 'text-yellow-700' : 'text-red-700'
                                        }`}>
                                            {uploadResult.message}
                                        </p>
                                        {uploadResult.errors?.length > 0 && (
                                            <div className="mt-2">
                                                <p className="text-xs font-medium mb-1">Errors:</p>
                                                <ul className="text-xs space-y-1">
                                                    {uploadResult.errors.slice(0, 5).map((error, idx) => (
                                                        <li key={idx} className="text-red-600">
                                                            Row {error.rowNumber}: {error.errorMessage}
                                                        </li>
                                                    ))}
                                                </ul>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Errors */}
                    {errors.length > 0 && (
                        <div className="mt-4 p-4 bg-red-50 rounded-lg">
                            <div className="flex">
                                <FiXCircle className="h-5 w-5 text-red-400" />
                                <div className="ml-3">
                                    <h3 className="text-sm font-medium text-red-800">
                                        Upload Errors
                                    </h3>
                                    <ul className="mt-2 text-sm text-red-700 list-disc list-inside">
                                        {errors.map((error, idx) => (
                                            <li key={idx}>{error}</li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Action Buttons */}
                    {file && !uploading && !uploadResult && (
                        <div className="mt-6 flex flex-col sm:flex-row gap-3">
                            <button
                                onClick={handleUpload}
                                className="flex-1 inline-flex items-center justify-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                            >
                                <FiUpload className="mr-2" />
                                Start Upload
                            </button>
                            <button
                                onClick={removeFile}
                                className="flex-1 inline-flex items-center justify-center px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                Cancel
                            </button>
                        </div>
                    )}

                    {uploadResult && (
                        <div className="mt-6">
                            <button
                                onClick={() => {
                                    removeFile();
                                    setUploadResult(null);
                                }}
                                className="w-full inline-flex items-center justify-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                            >
                                Upload Another File
                            </button>
                        </div>
                    )}
                </motion.div>
            </div>
        </div>
    );
};

export default BulkUpload;