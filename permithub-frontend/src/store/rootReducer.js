import { combineReducers } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import hodReducer from './hodSlice';
import facultyReducer from './facultySlice';
import semesterReducer from './semesterSlice';

const rootReducer = combineReducers({
  auth: authReducer,
  hod: hodReducer,
  faculty: facultyReducer,
  semester: semesterReducer,
  // Add more reducers as we build
});

export default rootReducer;