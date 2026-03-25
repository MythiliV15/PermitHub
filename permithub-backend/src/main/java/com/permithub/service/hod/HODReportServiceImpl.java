package com.permithub.service.hod;

import com.permithub.exception.BadRequestException;
import com.permithub.repository.*;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HODReportServiceImpl implements HODReportService {

    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;

    @Override
    public List<Map<String, Object>> getLeaveReport(LocalDate startDate, LocalDate endDate) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Generating leave report for department ID: {} from {} to {}", deptId, startDate, endDate);
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getODReport(LocalDate startDate, LocalDate endDate) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Generating OD report for department ID: {} from {} to {}", deptId, startDate, endDate);
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getOutpassReport(LocalDate startDate, LocalDate endDate) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Generating outpass report for department ID: {} from {} to {}", deptId, startDate, endDate);
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getDepartmentPerformanceReport() {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Generating department performance report for department ID: {}", deptId);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalStudents", (long) studentProfileRepository.findByDepartmentId(deptId).size());
        report.put("totalFaculty", (long) facultyProfileRepository.findByDepartmentId(deptId).size());
        return report;
    }

    @Override
    public byte[] exportLeaveReport(LocalDate startDate, LocalDate endDate) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            throw new BadRequestException("No department associated with current user");
        }
        
        log.info("Exporting leave report for department ID: {} from {} to {}", deptId, startDate, endDate);
        return new byte[0];
    }
}
