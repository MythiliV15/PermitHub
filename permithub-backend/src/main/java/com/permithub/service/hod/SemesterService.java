package com.permithub.service.hod;

import com.permithub.dto.hod.SemesterDTO;
import com.permithub.dto.hod.SemesterPromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SemesterService {
    
    SemesterDTO createSemester(SemesterDTO semesterDTO);
    SemesterDTO updateSemester(Long semesterId, SemesterDTO semesterDTO);
    Page<SemesterDTO> getAllSemesters(Pageable pageable);
    SemesterDTO getSemesterById(Long semesterId);
    SemesterDTO getActiveSemester();
    SemesterDTO activateSemester(Long semesterId);
    SemesterDTO deactivateSemester(Long semesterId);
    Map<String, Object> promoteStudents(SemesterPromotionDTO promotionDTO);
    int resetLeaveBalance(Integer newBalance);
    List<Map<String, Object>> getPromotionEligibility(Integer year, String section);
    SemesterDTO setDefaultLeaveLimit(Long semesterId, Integer leaveLimit);
    Map<String, Object> getSemesterStatistics();
}