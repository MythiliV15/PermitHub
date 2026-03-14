import { format, formatDistance, formatRelative, parseISO } from 'date-fns';

export const formatters = {
    // Date formatters
    formatDate: (date, formatStr = 'dd MMM yyyy') => {
        if (!date) return '-';
        try {
            const dateObj = typeof date === 'string' ? parseISO(date) : date;
            return format(dateObj, formatStr);
        } catch {
            return '-';
        }
    },

    formatDateTime: (date) => {
        if (!date) return '-';
        try {
            const dateObj = typeof date === 'string' ? parseISO(date) : date;
            return format(dateObj, 'dd MMM yyyy, hh:mm a');
        } catch {
            return '-';
        }
    },

    formatTimeAgo: (date) => {
        if (!date) return '-';
        try {
            const dateObj = typeof date === 'string' ? parseISO(date) : date;
            return formatDistance(dateObj, new Date(), { addSuffix: true });
        } catch {
            return '-';
        }
    },

    formatRelativeTime: (date) => {
        if (!date) return '-';
        try {
            const dateObj = typeof date === 'string' ? parseISO(date) : date;
            return formatRelative(dateObj, new Date());
        } catch {
            return '-';
        }
    },

    // Number formatters
    formatNumber: (num) => {
        if (num === null || num === undefined) return '-';
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },

    formatPercentage: (value) => {
        if (value === null || value === undefined) return '-';
        return `${value}%`;
    },

    formatCurrency: (amount) => {
        if (amount === null || amount === undefined) return '-';
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    },

    // Status formatters
    formatStatus: (status) => {
        const statusMap = {
            'PENDING': { label: 'Pending', color: 'yellow', icon: '⏳' },
            'APPROVED_BY_MENTOR': { label: 'Mentor Approved', color: 'blue', icon: '👤' },
            'APPROVED_BY_CLASS_ADVISOR': { label: 'Class Advisor Approved', color: 'indigo', icon: '👥' },
            'APPROVED_BY_HOD': { label: 'HOD Approved', color: 'green', icon: '✅' },
            'APPROVED_BY_WARDEN': { label: 'Warden Approved', color: 'green', icon: '🏠' },
            'APPROVED_BY_AO': { label: 'AO Approved', color: 'green', icon: '📋' },
            'APPROVED_BY_PRINCIPAL': { label: 'Principal Approved', color: 'green', icon: '🎓' },
            'REJECTED_BY_MENTOR': { label: 'Mentor Rejected', color: 'red', icon: '❌' },
            'REJECTED_BY_HOD': { label: 'HOD Rejected', color: 'red', icon: '❌' },
            'PARENT_PENDING': { label: 'Parent Pending', color: 'purple', icon: '👪' },
            'PARENT_APPROVED': { label: 'Parent Approved', color: 'green', icon: '✓' },
            'PARENT_REJECTED': { label: 'Parent Rejected', color: 'red', icon: '✗' },
            'COMPLETED': { label: 'Completed', color: 'green', icon: '✓' },
            'EXPIRED': { label: 'Expired', color: 'gray', icon: '⌛' },
            'CANCELLED': { label: 'Cancelled', color: 'gray', icon: '✕' }
        };
        return statusMap[status] || { label: status, color: 'gray', icon: '❓' };
    },

    formatRole: (role) => {
        const roleMap = {
            'STUDENT': 'Student',
            'FACULTY_MENTOR': 'Mentor',
            'FACULTY_CLASS_ADVISOR': 'Class Advisor',
            'FACULTY_EVENT_COORDINATOR': 'Event Coordinator',
            'HOD': 'Head of Department',
            'WARDEN': 'Warden',
            'AO': 'Administrative Officer',
            'PRINCIPAL': 'Principal',
            'SECURITY': 'Security',
            'PARENT': 'Parent'
        };
        return roleMap[role] || role;
    },

    // Name formatters
    formatFullName: (user) => {
        if (!user) return '-';
        if (typeof user === 'string') return user;
        return user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || '-';
    },

    getInitials: (name) => {
        if (!name) return '?';
        return name
            .split(' ')
            .map(word => word[0])
            .join('')
            .toUpperCase()
            .slice(0, 2);
    },

    // File formatters
    formatFileName: (fileName, maxLength = 30) => {
        if (!fileName) return '-';
        if (fileName.length <= maxLength) return fileName;
        const ext = fileName.split('.').pop();
        const name = fileName.substring(0, fileName.lastIndexOf('.'));
        const trimmedName = name.substring(0, maxLength - ext.length - 3);
        return `${trimmedName}...${ext}`;
    },

    // Address formatters
    formatAddress: (address) => {
        if (!address) return '-';
        const parts = [];
        if (address.street) parts.push(address.street);
        if (address.city) parts.push(address.city);
        if (address.state) parts.push(address.state);
        if (address.pincode) parts.push(address.pincode);
        return parts.join(', ');
    },

    // Phone formatter
    formatPhone: (phone) => {
        if (!phone) return '-';
        const cleaned = phone.replace(/\D/g, '');
        const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return `${match[1]}-${match[2]}-${match[3]}`;
        }
        return phone;
    },

    // Truncate text
    truncate: (text, length = 50) => {
        if (!text) return '-';
        if (text.length <= length) return text;
        return text.substring(0, length) + '...';
    },

    // Array formatters
    formatList: (list, separator = ', ') => {
        if (!list || !list.length) return '-';
        return list.join(separator);
    },

    // Boolean formatters
    formatBoolean: (value) => {
        return value ? 'Yes' : 'No';
    }
};

export default formatters;