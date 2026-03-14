import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import FirstLoginPage from './pages/FirstLoginPage';
import Dashboard from './pages/Dashboard';
import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';

// HOD Pages
import HODDashboard from './pages/hod/HODDashboard';
import FacultyManagement from './pages/hod/FacultyManagement';
import FacultyForm from './pages/hod/FacultyForm';
import BulkUpload from './pages/hod/BulkUpload';
import SemesterManagement from './pages/hod/SemesterManagement';
import SemesterForm from './pages/hod/SemesterForm';
import StudentPromotion from './pages/hod/StudentPromotion';
import HODApprovals from './pages/hod/HODApprovals';
import ApprovalHistory from './pages/hod/ApprovalHistory';
import HODProfile from './pages/hod/HODProfile';

function App() {
  return (
    <>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        <Route path="/login" element={
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        } />
        
        <Route path="/forgot-password" element={
          <PublicRoute>
            <ForgotPasswordPage />
          </PublicRoute>
        } />
        
        <Route path="/reset-password" element={
          <PublicRoute>
            <ResetPasswordPage />
          </PublicRoute>
        } />
        
        <Route path="/first-login" element={
          <PublicRoute>
            <FirstLoginPage />
          </PublicRoute>
        } />

        <Route path="/dashboard" element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        } />
        
        {/* HOD Routes */}
        <Route path="/hod" element={
          <PrivateRoute>
            <HODDashboard />
          </PrivateRoute>
        } />
        
        <Route path="/hod/dashboard" element={
          <PrivateRoute>
            <HODDashboard />
          </PrivateRoute>
        } />
        
        <Route path="/hod/faculty" element={
          <PrivateRoute>
            <FacultyManagement />
          </PrivateRoute>
        } />
        
        <Route path="/hod/faculty/add" element={
          <PrivateRoute>
            <FacultyForm />
          </PrivateRoute>
        } />
        
        <Route path="/hod/faculty/edit/:id" element={
          <PrivateRoute>
            <FacultyForm />
          </PrivateRoute>
        } />
        
        <Route path="/hod/faculty/bulk-upload" element={
          <PrivateRoute>
            <BulkUpload />
          </PrivateRoute>
        } />
        
        <Route path="/hod/semester" element={
          <PrivateRoute>
            <SemesterManagement />
          </PrivateRoute>
        } />
        
        <Route path="/hod/semester/add" element={
          <PrivateRoute>
            <SemesterForm />
          </PrivateRoute>
        } />
        
        <Route path="/hod/semester/edit/:id" element={
          <PrivateRoute>
            <SemesterForm />
          </PrivateRoute>
        } />
        
        <Route path="/hod/semester/promotion" element={
          <PrivateRoute>
            <StudentPromotion />
          </PrivateRoute>
        } />
        
        <Route path="/hod/approvals" element={
          <PrivateRoute>
            <HODApprovals />
          </PrivateRoute>
        } />
        
        <Route path="/hod/approvals/history" element={
          <PrivateRoute>
            <ApprovalHistory />
          </PrivateRoute>
        } />
        
        <Route path="/hod/profile" element={
          <PrivateRoute>
            <HODProfile />
          </PrivateRoute>
        } />
        
        {/* 404 Route */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </>
  );
}

export default App;