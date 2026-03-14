import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import facultyService from '../services/facultyService';

// Async thunks
export const fetchAllFaculty = createAsyncThunk(
    'faculty/fetchAll',
    async (params, { rejectWithValue }) => {
        try {
            const response = await facultyService.getAllFaculty(params);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch faculty');
        }
    }
);

export const fetchFacultyById = createAsyncThunk(
    'faculty/fetchById',
    async (id, { rejectWithValue }) => {
        try {
            const response = await facultyService.getFacultyById(id);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch faculty details');
        }
    }
);

export const addFaculty = createAsyncThunk(
    'faculty/add',
    async (facultyData, { rejectWithValue }) => {
        try {
            const response = await facultyService.addFaculty(facultyData);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to add faculty');
        }
    }
);

export const updateFaculty = createAsyncThunk(
    'faculty/update',
    async ({ id, data }, { rejectWithValue }) => {
        try {
            const response = await facultyService.updateFaculty(id, data);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to update faculty');
        }
    }
);

export const deactivateFaculty = createAsyncThunk(
    'faculty/deactivate',
    async ({ id, reason }, { rejectWithValue }) => {
        try {
            const response = await facultyService.deactivateFaculty(id, reason);
            return { id, ...response.data };
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to deactivate faculty');
        }
    }
);

export const activateFaculty = createAsyncThunk(
    'faculty/activate',
    async (id, { rejectWithValue }) => {
        try {
            const response = await facultyService.activateFaculty(id);
            return { id, ...response.data };
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to activate faculty');
        }
    }
);

export const assignRoles = createAsyncThunk(
    'faculty/assignRoles',
    async ({ id, roles }, { rejectWithValue }) => {
        try {
            const response = await facultyService.assignRoles(id, roles);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to assign roles');
        }
    }
);

export const fetchFacultyStatistics = createAsyncThunk(
    'faculty/fetchStatistics',
    async (_, { rejectWithValue }) => {
        try {
            const response = await facultyService.getFacultyStatistics();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch statistics');
        }
    }
);

// Initial state
const initialState = {
    faculty: {
        content: [],
        totalElements: 0,
        totalPages: 0
    },
    currentFaculty: null,
    statistics: null,
    loading: {
        list: false,
        details: false,
        action: false
    },
    error: null,
    successMessage: null
};

// Slice
const facultySlice = createSlice({
    name: 'faculty',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        clearSuccessMessage: (state) => {
            state.successMessage = null;
        },
        clearCurrentFaculty: (state) => {
            state.currentFaculty = null;
        }
    },
    extraReducers: (builder) => {
        builder
            // Fetch All Faculty
            .addCase(fetchAllFaculty.pending, (state) => {
                state.loading.list = true;
                state.error = null;
            })
            .addCase(fetchAllFaculty.fulfilled, (state, action) => {
                state.loading.list = false;
                state.faculty = action.payload;
            })
            .addCase(fetchAllFaculty.rejected, (state, action) => {
                state.loading.list = false;
                state.error = action.payload;
            })

            // Fetch Faculty By ID
            .addCase(fetchFacultyById.pending, (state) => {
                state.loading.details = true;
                state.error = null;
            })
            .addCase(fetchFacultyById.fulfilled, (state, action) => {
                state.loading.details = false;
                state.currentFaculty = action.payload;
            })
            .addCase(fetchFacultyById.rejected, (state, action) => {
                state.loading.details = false;
                state.error = action.payload;
            })

            // Add Faculty
            .addCase(addFaculty.pending, (state) => {
                state.loading.action = true;
                state.error = null;
                state.successMessage = null;
            })
            .addCase(addFaculty.fulfilled, (state, action) => {
                state.loading.action = false;
                state.successMessage = 'Faculty added successfully';
                // Optionally add to list
                if (state.faculty.content) {
                    state.faculty.content.unshift(action.payload);
                }
            })
            .addCase(addFaculty.rejected, (state, action) => {
                state.loading.action = false;
                state.error = action.payload;
            })

            // Update Faculty
            .addCase(updateFaculty.pending, (state) => {
                state.loading.action = true;
                state.error = null;
                state.successMessage = null;
            })
            .addCase(updateFaculty.fulfilled, (state, action) => {
                state.loading.action = false;
                state.successMessage = 'Faculty updated successfully';
                state.currentFaculty = action.payload;
                // Update in list
                if (state.faculty.content) {
                    const index = state.faculty.content.findIndex(f => f.id === action.payload.id);
                    if (index !== -1) {
                        state.faculty.content[index] = action.payload;
                    }
                }
            })
            .addCase(updateFaculty.rejected, (state, action) => {
                state.loading.action = false;
                state.error = action.payload;
            })

            // Deactivate Faculty
            .addCase(deactivateFaculty.fulfilled, (state, action) => {
                state.successMessage = 'Faculty deactivated successfully';
                // Update in list
                if (state.faculty.content) {
                    const index = state.faculty.content.findIndex(f => f.id === action.payload.id);
                    if (index !== -1) {
                        state.faculty.content[index].isActive = false;
                    }
                }
                if (state.currentFaculty?.id === action.payload.id) {
                    state.currentFaculty.isActive = false;
                }
            })

            // Activate Faculty
            .addCase(activateFaculty.fulfilled, (state, action) => {
                state.successMessage = 'Faculty activated successfully';
                // Update in list
                if (state.faculty.content) {
                    const index = state.faculty.content.findIndex(f => f.id === action.payload.id);
                    if (index !== -1) {
                        state.faculty.content[index].isActive = true;
                    }
                }
                if (state.currentFaculty?.id === action.payload.id) {
                    state.currentFaculty.isActive = true;
                }
            })

            // Assign Roles
            .addCase(assignRoles.fulfilled, (state, action) => {
                state.successMessage = 'Roles assigned successfully';
                state.currentFaculty = action.payload;
                // Update in list
                if (state.faculty.content) {
                    const index = state.faculty.content.findIndex(f => f.id === action.payload.id);
                    if (index !== -1) {
                        state.faculty.content[index] = action.payload;
                    }
                }
            })

            // Fetch Statistics
            .addCase(fetchFacultyStatistics.fulfilled, (state, action) => {
                state.statistics = action.payload;
            });
    }
});

// Selectors
export const selectAllFaculty = (state) => state.faculty.faculty;
export const selectCurrentFaculty = (state) => state.faculty.currentFaculty;
export const selectFacultyStatistics = (state) => state.faculty.statistics;
export const selectFacultyLoading = (state) => state.faculty.loading;
export const selectFacultyError = (state) => state.faculty.error;
export const selectFacultySuccessMessage = (state) => state.faculty.successMessage;

export const { clearError, clearSuccessMessage, clearCurrentFaculty } = facultySlice.actions;
export default facultySlice.reducer;