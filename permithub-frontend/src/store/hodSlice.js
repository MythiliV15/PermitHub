import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import hodService from '../services/hodService';

// Async thunks
export const fetchDashboardStats = createAsyncThunk(
    'hod/fetchDashboardStats',
    async (_, { rejectWithValue }) => {
        try {
            const response = await hodService.getDashboardStats();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch dashboard stats');
        }
    }
);

export const fetchRecentActivities = createAsyncThunk(
    'hod/fetchRecentActivities',
    async ({ page = 0, size = 10 }, { rejectWithValue }) => {
        try {
            const response = await hodService.getRecentActivities(page, size);
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch activities');
        }
    }
);

export const fetchYearWiseDistribution = createAsyncThunk(
    'hod/fetchYearWiseDistribution',
    async (_, { rejectWithValue }) => {
        try {
            const response = await hodService.getYearWiseDistribution();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch distribution');
        }
    }
);

export const fetchPendingCounts = createAsyncThunk(
    'hod/fetchPendingCounts',
    async (_, { rejectWithValue }) => {
        try {
            const response = await hodService.getPendingCounts();
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data?.message || 'Failed to fetch pending counts');
        }
    }
);

// Initial state
const initialState = {
    stats: null,
    activities: {
        content: [],
        totalElements: 0,
        totalPages: 0
    },
    yearWiseDistribution: {},
    pendingCounts: {},
    loading: {
        stats: false,
        activities: false,
        distribution: false,
        pendingCounts: false
    },
    error: null
};

// Slice
const hodSlice = createSlice({
    name: 'hod',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        }
    },
    extraReducers: (builder) => {
        builder
            // Dashboard Stats
            .addCase(fetchDashboardStats.pending, (state) => {
                state.loading.stats = true;
                state.error = null;
            })
            .addCase(fetchDashboardStats.fulfilled, (state, action) => {
                state.loading.stats = false;
                state.stats = action.payload;
            })
            .addCase(fetchDashboardStats.rejected, (state, action) => {
                state.loading.stats = false;
                state.error = action.payload;
            })

            // Recent Activities
            .addCase(fetchRecentActivities.pending, (state) => {
                state.loading.activities = true;
                state.error = null;
            })
            .addCase(fetchRecentActivities.fulfilled, (state, action) => {
                state.loading.activities = false;
                state.activities = action.payload;
            })
            .addCase(fetchRecentActivities.rejected, (state, action) => {
                state.loading.activities = false;
                state.error = action.payload;
            })

            // Year Wise Distribution
            .addCase(fetchYearWiseDistribution.pending, (state) => {
                state.loading.distribution = true;
                state.error = null;
            })
            .addCase(fetchYearWiseDistribution.fulfilled, (state, action) => {
                state.loading.distribution = false;
                state.yearWiseDistribution = action.payload;
            })
            .addCase(fetchYearWiseDistribution.rejected, (state, action) => {
                state.loading.distribution = false;
                state.error = action.payload;
            })

            // Pending Counts
            .addCase(fetchPendingCounts.pending, (state) => {
                state.loading.pendingCounts = true;
                state.error = null;
            })
            .addCase(fetchPendingCounts.fulfilled, (state, action) => {
                state.loading.pendingCounts = false;
                state.pendingCounts = action.payload;
            })
            .addCase(fetchPendingCounts.rejected, (state, action) => {
                state.loading.pendingCounts = false;
                state.error = action.payload;
            });
    }
});

// Selectors
export const selectHODStats = (state) => state.hod.stats;
export const selectHODActivities = (state) => state.hod.activities;
export const selectYearWiseDistribution = (state) => state.hod.yearWiseDistribution;
export const selectPendingCounts = (state) => state.hod.pendingCounts;
export const selectHODLoading = (state) => state.hod.loading;
export const selectHODError = (state) => state.hod.error;

export const { clearError } = hodSlice.actions;
export default hodSlice.reducer;