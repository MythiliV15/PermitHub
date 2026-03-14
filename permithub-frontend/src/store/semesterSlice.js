import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import semesterService from '../services/semesterService';

// Async thunks
export const fetchAllSemesters = createAsyncThunk(
    'semester/fetchAll',
    async (params, { rejectWithValue }) => {
        try {
            const response = await semesterService.getAllSemesters(
                params.page, 
                params.size, 
                params.sortBy, 
                params.direction
            );
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch semesters');
        }
    }
);

export const fetchSemesterById = createAsyncThunk(
    'semester/fetchById',
    async (id, { rejectWithValue }) => {
        try {
            const response = await semesterService.getSemesterById(id);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch semester details');
        }
    }
);

export const fetchActiveSemester = createAsyncThunk(
    'semester/fetchActive',
    async (_, { rejectWithValue }) => {
        try {
            const response = await semesterService.getActiveSemester();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch active semester');
        }
    }
);

export const createSemester = createAsyncThunk(
    'semester/create',
    async (semesterData, { rejectWithValue }) => {
        try {
            const response = await semesterService.createSemester(semesterData);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to create semester');
        }
    }
);

export const updateSemester = createAsyncThunk(
    'semester/update',
    async ({ id, data }, { rejectWithValue }) => {
        try {
            const response = await semesterService.updateSemester(id, data);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to update semester');
        }
    }
);

export const activateSemester = createAsyncThunk(
    'semester/activate',
    async (id, { rejectWithValue }) => {
        try {
            const response = await semesterService.activateSemester(id);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to activate semester');
        }
    }
);

export const deactivateSemester = createAsyncThunk(
    'semester/deactivate',
    async (id, { rejectWithValue }) => {
        try {
            const response = await semesterService.deactivateSemester(id);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to deactivate semester');
        }
    }
);

export const promoteStudents = createAsyncThunk(
    'semester/promote',
    async (promotionData, { rejectWithValue }) => {
        try {
            const response = await semesterService.promoteStudents(promotionData);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to promote students');
        }
    }
);

export const resetLeaveBalance = createAsyncThunk(
    'semester/resetLeaveBalance',
    async (newBalance, { rejectWithValue }) => {
        try {
            const response = await semesterService.resetLeaveBalance(newBalance);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to reset leave balance');
        }
    }
);

export const fetchPromotionEligibility = createAsyncThunk(
    'semester/fetchPromotionEligibility',
    async ({ year, section }, { rejectWithValue }) => {
        try {
            const response = await semesterService.getPromotionEligibility(year, section);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch eligibility');
        }
    }
);

export const fetchSemesterStatistics = createAsyncThunk(
    'semester/fetchStatistics',
    async (_, { rejectWithValue }) => {
        try {
            const response = await semesterService.getSemesterStatistics();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch statistics');
        }
    }
);

// Initial state
const initialState = {
    semesters: {
        content: [],
        totalElements: 0,
        totalPages: 0
    },
    currentSemester: null,
    activeSemester: null,
    promotionEligibility: [],
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
const semesterSlice = createSlice({
    name: 'semester',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        clearSuccessMessage: (state) => {
            state.successMessage = null;
        },
        clearCurrentSemester: (state) => {
            state.currentSemester = null;
        }
    },
    extraReducers: (builder) => {
        builder
            // Fetch All Semesters
            .addCase(fetchAllSemesters.pending, (state) => {
                state.loading.list = true;
                state.error = null;
            })
            .addCase(fetchAllSemesters.fulfilled, (state, action) => {
                state.loading.list = false;
                state.semesters = action.payload;
            })
            .addCase(fetchAllSemesters.rejected, (state, action) => {
                state.loading.list = false;
                state.error = action.payload;
            })

            // Fetch Semester By ID
            .addCase(fetchSemesterById.pending, (state) => {
                state.loading.details = true;
                state.error = null;
            })
            .addCase(fetchSemesterById.fulfilled, (state, action) => {
                state.loading.details = false;
                state.currentSemester = action.payload;
            })
            .addCase(fetchSemesterById.rejected, (state, action) => {
                state.loading.details = false;
                state.error = action.payload;
            })

            // Fetch Active Semester
            .addCase(fetchActiveSemester.pending, (state) => {
                state.loading.details = true;
                state.error = null;
            })
            .addCase(fetchActiveSemester.fulfilled, (state, action) => {
                state.loading.details = false;
                state.activeSemester = action.payload;
            })
            .addCase(fetchActiveSemester.rejected, (state, action) => {
                state.loading.details = false;
                state.error = action.payload;
            })

            // Create Semester
            .addCase(createSemester.pending, (state) => {
                state.loading.action = true;
                state.error = null;
                state.successMessage = null;
            })
            .addCase(createSemester.fulfilled, (state, action) => {
                state.loading.action = false;
                state.successMessage = 'Semester created successfully';
                if (state.semesters.content) {
                    state.semesters.content.unshift(action.payload);
                }
            })
            .addCase(createSemester.rejected, (state, action) => {
                state.loading.action = false;
                state.error = action.payload;
            })

            // Update Semester
            .addCase(updateSemester.fulfilled, (state, action) => {
                state.successMessage = 'Semester updated successfully';
                state.currentSemester = action.payload;
                if (state.semesters.content) {
                    const index = state.semesters.content.findIndex(s => s.id === action.payload.id);
                    if (index !== -1) {
                        state.semesters.content[index] = action.payload;
                    }
                }
                if (state.activeSemester?.id === action.payload.id) {
                    state.activeSemester = action.payload;
                }
            })

            // Activate Semester
            .addCase(activateSemester.fulfilled, (state, action) => {
                state.successMessage = 'Semester activated successfully';
                state.activeSemester = action.payload;
                if (state.semesters.content) {
                    state.semesters.content = state.semesters.content.map(s => ({
                        ...s,
                        isActive: s.id === action.payload.id
                    }));
                }
            })

            // Deactivate Semester
            .addCase(deactivateSemester.fulfilled, (state, action) => {
                state.successMessage = 'Semester deactivated successfully';
                state.activeSemester = null;
                if (state.semesters.content) {
                    const index = state.semesters.content.findIndex(s => s.id === action.payload.id);
                    if (index !== -1) {
                        state.semesters.content[index].isActive = false;
                    }
                }
            })

            // Promote Students
            .addCase(promoteStudents.pending, (state) => {
                state.loading.action = true;
                state.error = null;
            })
            .addCase(promoteStudents.fulfilled, (state, action) => {
                state.loading.action = false;
                state.successMessage = `Successfully promoted ${action.payload.promotedCount} students`;
            })
            .addCase(promoteStudents.rejected, (state, action) => {
                state.loading.action = false;
                state.error = action.payload;
            })

            // Reset Leave Balance
            .addCase(resetLeaveBalance.fulfilled, (state, action) => {
                state.successMessage = `Leave balance reset for ${action.payload} students`;
            })

            // Fetch Promotion Eligibility
            .addCase(fetchPromotionEligibility.fulfilled, (state, action) => {
                state.promotionEligibility = action.payload;
            })

            // Fetch Statistics
            .addCase(fetchSemesterStatistics.fulfilled, (state, action) => {
                state.statistics = action.payload;
            });
    }
});

// Selectors
export const selectAllSemesters = (state) => state.semester.semesters;
export const selectCurrentSemester = (state) => state.semester.currentSemester;
export const selectActiveSemester = (state) => state.semester.activeSemester;
export const selectPromotionEligibility = (state) => state.semester.promotionEligibility;
export const selectSemesterStatistics = (state) => state.semester.statistics;
export const selectSemesterLoading = (state) => state.semester.loading;
export const selectSemesterError = (state) => state.semester.error;
export const selectSemesterSuccessMessage = (state) => state.semester.successMessage;

export const { clearError, clearSuccessMessage, clearCurrentSemester } = semesterSlice.actions;
export default semesterSlice.reducer;