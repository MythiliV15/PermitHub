package com.permithub.util;

import com.permithub.dto.hod.FacultyRequestDTO;
import com.permithub.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ExcelHelper {

    private static final String[] FACULTY_HEADERS = {
        "Employee ID", "Full Name", "Email", "Phone Number", 
        "Designation", "Qualification", "Experience Years", "Joining Date (YYYY-MM-DD)",
        "Roles (comma separated: MENTOR,CLASS_ADVISOR,EVENT_COORDINATOR)"
    };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] generateFacultyTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Faculty Upload Template");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < FACULTY_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(FACULTY_HEADERS[i]);
                
                // Style header
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                cell.setCellStyle(headerStyle);
                
                // Auto-size columns
                sheet.autoSizeColumn(i);
            }
            
            // Add example row
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("FAC001");
            exampleRow.createCell(1).setCellValue("John Doe");
            exampleRow.createCell(2).setCellValue("john.doe@college.edu");
            exampleRow.createCell(3).setCellValue("9876543210");
            exampleRow.createCell(4).setCellValue("Assistant Professor");
            exampleRow.createCell(5).setCellValue("M.E. CSE");
            exampleRow.createCell(6).setCellValue("5");
            exampleRow.createCell(7).setCellValue("2023-06-01");
            exampleRow.createCell(8).setCellValue("MENTOR,CLASS_ADVISOR");
            
            // Style example row as italic
            CellStyle exampleStyle = workbook.createCellStyle();
            Font exampleFont = workbook.createFont();
            exampleFont.setItalic(true);
            exampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            exampleStyle.setFont(exampleFont);
            
            for (int i = 0; i < FACULTY_HEADERS.length; i++) {
                exampleRow.getCell(i).setCellStyle(exampleStyle);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("Error generating Excel template", e);
            throw new BadRequestException("Failed to generate Excel template");
        }
    }

    public List<FacultyRequestDTO> parseFacultyExcel(MultipartFile file, Long departmentId, 
                                                      String defaultDesignation, Integer defaultExperience) {
        List<FacultyRequestDTO> facultyList = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            
            int rowNum = 1;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNum++;
                
                // Skip empty rows
                if (isRowEmpty(currentRow)) {
                    continue;
                }
                
                try {
                    FacultyRequestDTO faculty = parseFacultyRow(currentRow, departmentId, 
                            defaultDesignation, defaultExperience);
                    facultyList.add(faculty);
                } catch (Exception e) {
                    throw new BadRequestException("Error at row " + rowNum + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            log.error("Error parsing Excel file", e);
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }
        
        return facultyList;
    }

    private FacultyRequestDTO parseFacultyRow(Row row, Long departmentId, 
                                               String defaultDesignation, Integer defaultExperience) {
        FacultyRequestDTO.FacultyRequestDTOBuilder builder = FacultyRequestDTO.builder()
                .departmentId(departmentId);
        
        // Employee ID (Cell 0)
        builder.employeeId(getCellValueAsString(row.getCell(0)));
        
        // Full Name (Cell 1)
        builder.fullName(getCellValueAsString(row.getCell(1)));
        
        // Email (Cell 2)
        builder.email(getCellValueAsString(row.getCell(2)));
        
        // Phone Number (Cell 3)
        builder.phoneNumber(getCellValueAsString(row.getCell(3)));
        
        // Designation (Cell 4) - use default if empty
        String designation = getCellValueAsString(row.getCell(4));
        builder.designation(designation.isEmpty() ? defaultDesignation : designation);
        
        // Qualification (Cell 5)
        builder.qualification(getCellValueAsString(row.getCell(5)));
        
        // Experience Years (Cell 6) - use default if empty
        String expStr = getCellValueAsString(row.getCell(6));
        if (!expStr.isEmpty()) {
            builder.experienceYears(Integer.parseInt(expStr));
        } else if (defaultExperience != null) {
            builder.experienceYears(defaultExperience);
        }
        
        // Joining Date (Cell 7)
        String dateStr = getCellValueAsString(row.getCell(7));
        if (!dateStr.isEmpty()) {
            builder.joiningDate(LocalDate.parse(dateStr, DATE_FORMATTER));
        }
        
        // Roles (Cell 8)
        String rolesStr = getCellValueAsString(row.getCell(8));
        if (!rolesStr.isEmpty()) {
            // Parse roles - this will be handled separately in service
        }
        
        FacultyRequestDTO dto = builder.build();
        
        // Validate required fields
        if (dto.getEmployeeId() == null || dto.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        return dto;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && 
                !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    // Add these to the existing ExcelHelper class

private static final String[] STUDENT_HEADERS = {
    "Register Number", "Full Name", "Email", "Phone Number",
    "Date of Birth (YYYY-MM-DD)", "Blood Group", "Address",
    "Parent Name", "Parent Phone", "Parent Email", "Emergency Contact",
    "Is Hosteler (true/false)"
};

public byte[] generateStudentTemplate() {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Student Upload Template");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < STUDENT_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(STUDENT_HEADERS[i]);
            
            // Style header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
            
            // Auto-size columns
            sheet.autoSizeColumn(i);
        }
        
        // Add example row
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("2024IT001");
        exampleRow.createCell(1).setCellValue("John Student");
        exampleRow.createCell(2).setCellValue("john.student@college.edu");
        exampleRow.createCell(3).setCellValue("9876543210");
        exampleRow.createCell(4).setCellValue("2005-05-15");
        exampleRow.createCell(5).setCellValue("O+");
        exampleRow.createCell(6).setCellValue("123 Main St, City");
        exampleRow.createCell(7).setCellValue("Father Name");
        exampleRow.createCell(8).setCellValue("9876543211");
        exampleRow.createCell(9).setCellValue("father@email.com");
        exampleRow.createCell(10).setCellValue("9876543212");
        exampleRow.createCell(11).setCellValue("true");
        
        // Style example row as italic
        CellStyle exampleStyle = workbook.createCellStyle();
        Font exampleFont = workbook.createFont();
        exampleFont.setItalic(true);
        exampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        exampleStyle.setFont(exampleFont);
        
        for (int i = 0; i < STUDENT_HEADERS.length; i++) {
            exampleRow.getCell(i).setCellStyle(exampleStyle);
        }
        
        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
        
    } catch (IOException e) {
        log.error("Error generating student template", e);
        throw new BadRequestException("Failed to generate student template");
    }
}

public List<Map<String, Object>> parseStudentExcel(MultipartFile file, Long departmentId, 
                                                    Integer year, String section) {
    List<Map<String, Object>> studentList = new ArrayList<>();
    
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        
        // Skip header row
        if (rows.hasNext()) {
            rows.next();
        }
        
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            
            // Skip empty rows
            if (isRowEmpty(currentRow)) {
                continue;
            }
            
            Map<String, Object> studentData = parseStudentRow(currentRow);
            studentList.add(studentData);
        }
        
    } catch (IOException e) {
        log.error("Error parsing student Excel file", e);
        throw new BadRequestException("Failed to parse student Excel file: " + e.getMessage());
    }
    
    return studentList;
}

private Map<String, Object> parseStudentRow(Row row) {
    Map<String, Object> studentData = new HashMap<>();
    
    studentData.put("registerNumber", getCellValueAsString(row.getCell(0)));
    studentData.put("fullName", getCellValueAsString(row.getCell(1)));
    studentData.put("email", getCellValueAsString(row.getCell(2)));
    studentData.put("phoneNumber", getCellValueAsString(row.getCell(3)));
    studentData.put("dateOfBirth", getCellValueAsString(row.getCell(4)));
    studentData.put("bloodGroup", getCellValueAsString(row.getCell(5)));
    studentData.put("address", getCellValueAsString(row.getCell(6)));
    studentData.put("parentName", getCellValueAsString(row.getCell(7)));
    studentData.put("parentPhone", getCellValueAsString(row.getCell(8)));
    studentData.put("parentEmail", getCellValueAsString(row.getCell(9)));
    studentData.put("emergencyContact", getCellValueAsString(row.getCell(10)));
    studentData.put("isHosteler", getCellValueAsString(row.getCell(11)));
    
    // Validate required fields
    String regNo = (String) studentData.get("registerNumber");
    String name = (String) studentData.get("fullName");
    String email = (String) studentData.get("email");
    
    if (regNo == null || regNo.trim().isEmpty()) {
        throw new IllegalArgumentException("Register Number is required");
    }
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Full Name is required");
    }
    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException("Email is required");
    }
    
    return studentData;
}

public void validateFacultyExcel(MultipartFile file) {
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        
        if (headerRow == null || headerRow.getLastCellNum() < 9) {
            throw new BadRequestException("Invalid Excel format. Please use the template.");
        }
        
        // Check headers
        for (int i = 0; i < FACULTY_HEADERS.length; i++) {
            String header = getCellValueAsString(headerRow.getCell(i));
            if (!header.equalsIgnoreCase(FACULTY_HEADERS[i])) {
                throw new BadRequestException("Invalid header at column " + (i+1) + 
                        ". Expected: " + FACULTY_HEADERS[i]);
            }
        }
        
    } catch (IOException e) {
        throw new BadRequestException("Failed to validate Excel file: " + e.getMessage());
    }
}

public void validateStudentExcel(MultipartFile file) {
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        
        if (headerRow == null || headerRow.getLastCellNum() < STUDENT_HEADERS.length) {
            throw new BadRequestException("Invalid Excel format. Please use the template.");
        }
        
        // Check headers
        for (int i = 0; i < STUDENT_HEADERS.length; i++) {
            String header = getCellValueAsString(headerRow.getCell(i));
            if (!header.equalsIgnoreCase(STUDENT_HEADERS[i])) {
                throw new BadRequestException("Invalid header at column " + (i+1) + 
                        ". Expected: " + STUDENT_HEADERS[i]);
            }
        }
        
    } catch (IOException e) {
        throw new BadRequestException("Failed to validate Excel file: " + e.getMessage());
    }
}

public List<Map<String, Object>> previewFacultyExcel(MultipartFile file) {
    List<Map<String, Object>> preview = new ArrayList<>();
    
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        
        // Skip header
        if (rows.hasNext()) {
            rows.next();
        }
        
        int count = 0;
        while (rows.hasNext() && count < 5) { // Preview first 5 rows
            Row row = rows.next();
            if (isRowEmpty(row)) {
                continue;
            }
            
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("employeeId", getCellValueAsString(row.getCell(0)));
            rowData.put("fullName", getCellValueAsString(row.getCell(1)));
            rowData.put("email", getCellValueAsString(row.getCell(2)));
            rowData.put("phoneNumber", getCellValueAsString(row.getCell(3)));
            rowData.put("designation", getCellValueAsString(row.getCell(4)));
            
            preview.add(rowData);
            count++;
        }
        
    } catch (IOException e) {
        throw new BadRequestException("Failed to preview file: " + e.getMessage());
    }
    
    return preview;
}

public List<Map<String, Object>> previewStudentExcel(MultipartFile file) {
    List<Map<String, Object>> preview = new ArrayList<>();
    
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        
        // Skip header
        if (rows.hasNext()) {
            rows.next();
        }
        
        int count = 0;
        while (rows.hasNext() && count < 5) { // Preview first 5 rows
            Row row = rows.next();
            if (isRowEmpty(row)) {
                continue;
            }
            
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("registerNumber", getCellValueAsString(row.getCell(0)));
            rowData.put("fullName", getCellValueAsString(row.getCell(1)));
            rowData.put("email", getCellValueAsString(row.getCell(2)));
            rowData.put("phoneNumber", getCellValueAsString(row.getCell(3)));
            rowData.put("parentName", getCellValueAsString(row.getCell(7)));
            
            preview.add(rowData);
            count++;
        }
        
    } catch (IOException e) {
        throw new BadRequestException("Failed to preview file: " + e.getMessage());
    }
    
    return preview;
}
}