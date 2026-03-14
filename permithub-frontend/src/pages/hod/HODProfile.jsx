import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FiUser, FiMail, FiPhone, FiMapPin, FiBriefcase, FiBookOpen, FiSave, FiEdit2, FiCamera } from 'react-icons/fi';
import hodService from '../../services/hodService';
import toast from 'react-hot-toast';

const HODProfile = () => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({});

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            setLoading(true);
            const response = await hodService.getProfile();
            if (response && response.data) {
                setProfile(response.data);
                setFormData(response.data);
            } else {
                toast.error('Profile data is incomplete');
            }
        } catch (error) {
            console.error('Failed to fetch profile:', error);
            toast.error('Failed to load profile details');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await hodService.updateProfile(formData);
            setProfile(response.data);
            setIsEditing(false);
            toast.success('Profile updated successfully');
        } catch (error) {
            console.error('Failed to update profile:', error);
            toast.error('Failed to update profile');
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
            <div className="max-w-4xl mx-auto">
                {/* Header Section */}
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden mb-8 transform transition-all duration-300 hover:shadow-2xl">
                    <div className="h-32 bg-gradient-to-r from-primary-600 to-indigo-600"></div>
                    <div className="px-8 pb-8">
                        <div className="relative -mt-16 flex items-end space-x-5">
                            <div className="relative group">
                                {profile?.profilePicture ? (
                                    <img 
                                        src={profile.profilePicture} 
                                        alt="Profile" 
                                        className="h-32 w-32 rounded-2xl object-cover border-4 border-white shadow-lg"
                                    />
                                ) : (
                                    <div className="h-32 w-32 rounded-2xl bg-gray-200 border-4 border-white shadow-lg flex items-center justify-center group-hover:bg-gray-300 transition-colors">
                                        <FiUser className="h-16 w-16 text-gray-400" />
                                    </div>
                                )}
                                <button className="absolute bottom-2 right-2 p-2 bg-primary-600 text-white rounded-full shadow-lg opacity-0 group-hover:opacity-100 transition-opacity">
                                    <FiCamera className="w-4 h-4" />
                                </button>
                            </div>
                            <div className="flex-1 min-w-0">
                                <h1 className="text-3xl font-bold text-gray-900 truncate">{profile?.fullName}</h1>
                                <p className="text-lg text-gray-500 font-medium">{profile?.designation}</p>
                            </div>
                            <button
                                onClick={() => setIsEditing(!isEditing)}
                                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-xl shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-all"
                            >
                                {isEditing ? <FiEdit2 className="mr-2" /> : <FiEdit2 className="mr-2" />}
                                {isEditing ? 'Cancel Edit' : 'Edit Profile'}
                            </button>
                        </div>
                    </div>
                </div>

                {/* Profile Details */}
                <form onSubmit={handleSubmit}>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Personal Info */}
                        <motion.div
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                            className="bg-white rounded-2xl shadow-lg p-8"
                        >
                            <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center">
                                <FiUser className="mr-3 text-primary-600" /> Personal Information
                            </h2>
                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Full Name</label>
                                    <input
                                        type="text"
                                        name="fullName"
                                        disabled={!isEditing}
                                        value={formData.fullName || ''}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Email Address</label>
                                    <div className="flex items-center px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 text-gray-600">
                                        <FiMail className="mr-3" />
                                        <span className="font-medium">{profile?.email}</span>
                                    </div>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Phone Number</label>
                                    <div className="flex items-center relative">
                                        <FiPhone className="absolute left-4 text-gray-400" />
                                        <input
                                            type="text"
                                            name="phoneNumber"
                                            disabled={!isEditing}
                                            value={formData.phoneNumber || ''}
                                            onChange={handleInputChange}
                                            className="w-full pl-12 pr-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                        />
                                    </div>
                                </div>
                            </div>
                        </motion.div>

                        {/* Professional Info */}
                        <motion.div
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            className="bg-white rounded-2xl shadow-lg p-8"
                        >
                            <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center">
                                <FiBriefcase className="mr-3 text-primary-600" /> Professional Details
                            </h2>
                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Designation</label>
                                    <input
                                        type="text"
                                        name="designation"
                                        disabled={!isEditing}
                                        value={formData.designation || ''}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Qualification</label>
                                    <div className="flex items-center relative">
                                        <FiBookOpen className="absolute left-4 text-gray-400" />
                                        <input
                                            type="text"
                                            name="qualification"
                                            disabled={!isEditing}
                                            value={formData.qualification || ''}
                                            onChange={handleInputChange}
                                            className="w-full pl-12 pr-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                        />
                                    </div>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Office Location</label>
                                    <div className="flex items-center relative">
                                        <FiMapPin className="absolute left-4 text-gray-400" />
                                        <input
                                            type="text"
                                            name="officeLocation"
                                            disabled={!isEditing}
                                            value={formData.officeLocation || ''}
                                            onChange={handleInputChange}
                                            className="w-full pl-12 pr-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                        />
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    </div>

                    {/* Additional Info */}
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                        className="mt-8 bg-white rounded-2xl shadow-lg p-8"
                    >
                        <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center">
                            <FiEdit2 className="mr-3 text-primary-600" /> Other Details
                        </h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1">Specialization</label>
                                <input
                                    type="text"
                                    name="specialization"
                                    disabled={!isEditing}
                                    value={formData.specialization || ''}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1">Cabin Number</label>
                                <input
                                    type="text"
                                    name="cabinNumber"
                                    disabled={!isEditing}
                                    value={formData.cabinNumber || ''}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-primary-500 disabled:bg-gray-50 transition-all font-medium"
                                />
                            </div>
                        </div>
                    </motion.div>

                    {/* Save Button */}
                    {isEditing && (
                        <div className="fixed bottom-8 right-8 z-50">
                            <button
                                type="submit"
                                className="flex items-center px-8 py-4 bg-primary-600 text-white rounded-2xl shadow-2xl hover:bg-primary-700 transform transition-all duration-300 hover:scale-105 active:scale-95 font-bold"
                            >
                                <FiSave className="mr-2 w-5 h-5" />
                                Save Changes
                            </button>
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
};

export default HODProfile;
