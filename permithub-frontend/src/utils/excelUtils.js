import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export const excelUtils = {
    // Download template
    downloadTemplate: (data, filename) => {
        const ws = XLSX.utils.json_to_sheet(data);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Template');
        const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, filename);
    },

    // Parse Excel file
    parseExcel: async (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const data = new Uint8Array(e.target.result);
                    const workbook = XLSX.read(data, { type: 'array' });
                    const sheetName = workbook.SheetNames[0];
                    const worksheet = workbook.Sheets[sheetName];
                    const json = XLSX.utils.sheet_to_json(worksheet);
                    resolve(json);
                } catch (error) {
                    reject(error);
                }
            };
            reader.onerror = (error) => reject(error);
            reader.readAsArrayBuffer(file);
        });
    },

    // Validate file type
    isValidExcelFile: (file) => {
        const validTypes = [
            'application/vnd.ms-excel',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'application/vnd.oasis.opendocument.spreadsheet'
        ];
        return validTypes.includes(file.type) || 
               file.name.endsWith('.xlsx') || 
               file.name.endsWith('.xls');
    },

    // Format file size
    formatFileSize: (bytes) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },

    // Generate faculty template data
    getFacultyTemplateData: () => {
        return [
            {
                'Employee ID': 'FAC001',
                'Full Name': 'John Doe',
                'Email': 'john.doe@college.edu',
                'Phone Number': '9876543210',
                'Designation': 'Assistant Professor',
                'Qualification': 'M.E. CSE',
                'Experience Years': 5,
                'Joining Date': '2023-06-01',
                'Roles': 'MENTOR,CLASS_ADVISOR'
            }
        ];
    },

    // Generate student template data
    getStudentTemplateData: () => {
        return [
            {
                'Register Number': '2024IT001',
                'Full Name': 'John Student',
                'Email': 'john.student@college.edu',
                'Phone Number': '9876543210',
                'Date of Birth': '2005-05-15',
                'Blood Group': 'O+',
                'Address': '123 Main St, City',
                'Parent Name': 'Father Name',
                'Parent Phone': '9876543211',
                'Parent Email': 'father@email.com',
                'Emergency Contact': '9876543212',
                'Is Hosteler': 'true'
            }
        ];
    },

    // Convert JSON to CSV
    jsonToCsv: (json) => {
        if (json.length === 0) return '';
        const headers = Object.keys(json[0]);
        const csvRows = [];
        csvRows.push(headers.join(','));
        for (const row of json) {
            const values = headers.map(header => {
                const val = row[header] || '';
                return `"${val.toString().replace(/"/g, '""')}"`;
            });
            csvRows.push(values.join(','));
        }
        return csvRows.join('\n');
    }
};

export default excelUtils;