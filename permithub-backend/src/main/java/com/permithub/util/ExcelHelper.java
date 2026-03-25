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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ExcelHelper {

    public List<FacultyRequestDTO> parseFacultyExcel(MultipartFile file) {
        List<FacultyRequestDTO> facultyList = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            if (rows.hasNext()) rows.next(); // skip header
            
            while (rows.hasNext()) {
                Row row = rows.next();
                if (isRowEmpty(row)) continue;
                
                FacultyRequestDTO dto = FacultyRequestDTO.builder()
                        .name(getCellValueAsString(row.getCell(0)))
                        .email(getCellValueAsString(row.getCell(1)))
                        .phone(getCellValueAsString(row.getCell(2)))
                        .designation(getCellValueAsString(row.getCell(3)))
                        .employeeId(getCellValueAsString(row.getCell(4)))
                        .build();
                facultyList.add(dto);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse Excel file");
        }
        return facultyList;
    }

    public List<Map<String, Object>> parseStudentExcel(MultipartFile file, Long deptId, Integer year, String section) {
        List<Map<String, Object>> studentList = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header
            
            while (rows.hasNext()) {
                Row row = rows.next();
                if (isRowEmpty(row)) continue;
                Map<String, Object> map = new HashMap<>();
                map.put("registerNumber", getCellValueAsString(row.getCell(0)));
                map.put("fullName", getCellValueAsString(row.getCell(1)));
                map.put("email", getCellValueAsString(row.getCell(2)));
                map.put("parentName", getCellValueAsString(row.getCell(3)));
                map.put("parentPhone", getCellValueAsString(row.getCell(4)));
                studentList.add(map);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse student Excel file");
        }
        return studentList;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}