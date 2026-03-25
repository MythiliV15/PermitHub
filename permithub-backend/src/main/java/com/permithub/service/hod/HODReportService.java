package com.permithub.service.hod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HODReportService {
    List<Map<String, Object>> getLeaveReport(LocalDate startDate, LocalDate endDate);
    
    List<Map<String, Object>> getODReport(LocalDate startDate, LocalDate endDate);
    
    List<Map<String, Object>> getOutpassReport(LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getDepartmentPerformanceReport();
    
    byte[] exportLeaveReport(LocalDate startDate, LocalDate endDate);
}
