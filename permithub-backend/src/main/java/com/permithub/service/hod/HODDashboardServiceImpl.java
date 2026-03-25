package com.permithub.service.hod;

import com.permithub.dto.hod.DashboardStatsDTO;
import com.permithub.entity.*;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import com.permithub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HODDashboardServiceImpl implements HODDashboardService {

    private final FacultyProfileRepository facultyProfileRepository;
    private final FacultyRoleRepository facultyRoleRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;

    @Override
    public DashboardStatsDTO getDashboardStats(Long hodId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        if (deptId == null) {
            // Fallback: lookup department from HOD's own profile
            deptId = facultyProfileRepository.findByUserId(hodId)
                    .map(FacultyProfile::getDepartmentId)
                    .orElse(null);
        }
        
        if (deptId == null) {
            log.warn("Department ID is null and fallback failed for HOD ID: {}", hodId);
            return createEmptyStats();
        }
        
        final Long finalDeptId = deptId;
        Department department = departmentRepository.findById(finalDeptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found for ID: " + finalDeptId));
        
        long pendingLeave = leaveRequestRepository.countByDepartmentIdAndStatus(deptId, "PENDING");
        long pendingOD = odRequestRepository.countByDepartmentIdAndStatus(deptId, "PENDING");
        long pendingOutpass = outpassRequestRepository.countByDepartmentIdAndStatus(deptId, "PENDING");

        List<FacultyProfile> facultyList = facultyProfileRepository.findByDepartmentId(deptId);
        long facultyCount = (facultyList != null) ? facultyList.size() : 0;

        return DashboardStatsDTO.builder()
                .departmentId(deptId)
                .departmentName(department.getName())
                .departmentCode(department.getCode())
                .totalStudents(studentProfileRepository.countByDepartmentId(deptId))
                .totalFaculty(facultyCount)
                .totalMentors(getCountByRole(deptId, "MENTOR"))
                .totalClassAdvisors(getCountByRole(deptId, "CLASS_ADVISOR"))
                .totalEventCoordinators(getCountByRole(deptId, "EVENT_COORDINATOR"))
                .pendingLeaveApprovals(pendingLeave)
                .pendingODApprovals(pendingOD)
                .pendingOutpassApprovals(pendingOutpass)
                .totalPendingApprovals(pendingLeave + pendingOD + pendingOutpass)
                .hostelersCount(studentProfileRepository.findByDepartmentId(deptId).stream().filter(s -> Boolean.TRUE.equals(s.getIsHosteler())).count())
                .dayScholarsCount(studentProfileRepository.findByDepartmentId(deptId).stream().filter(s -> Boolean.FALSE.equals(s.getIsHosteler())).count())
                .build();
    }

    private DashboardStatsDTO createEmptyStats() {
        return DashboardStatsDTO.builder()
                .departmentName("Loading...")
                .totalStudents(0L)
                .totalFaculty(0L)
                .totalPendingApprovals(0L)
                .build();
    }

    private long getCountByRole(Long deptId, String roleName) {
        List<FacultyProfile> faculty = facultyProfileRepository.findByDepartmentId(deptId);
        if (faculty.isEmpty()) return 0;
        List<Long> facultyIds = faculty.stream().map(FacultyProfile::getId).collect(Collectors.toList());
        return facultyRoleRepository.findAll().stream()
                .filter(fr -> facultyIds.contains(fr.getFacultyId()) && fr.getRoleName().equals(roleName) && fr.getIsActive())
                .count();
    }

    @Override
    public Page<Object> getRecentActivities(Long hodId, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Object getDepartmentOverview(Long hodId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Department department = departmentRepository.findById(deptId).get();
        Map<String, Object> overview = new HashMap<>();
        overview.put("department", department);
        overview.put("totalStudents", studentProfileRepository.countByDepartmentId(deptId));
        overview.put("totalFaculty", facultyProfileRepository.findByDepartmentId(deptId).size());
        return overview;
    }

    @Override
    public Map<String, Long> getYearWiseStudentDistribution(Long hodId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return studentProfileRepository.findByDepartmentId(deptId).stream()
                .collect(Collectors.groupingBy(s -> "Year " + s.getYear(), Collectors.counting()));
    }

    @Override
    public Map<String, Long> getSectionWiseStudentDistribution(Long hodId, Integer year) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        return studentProfileRepository.findByDepartmentId(deptId).stream()
                .filter(s -> year == null || s.getYear().equals(year))
                .collect(Collectors.groupingBy(s -> s.getSection() != null ? s.getSection() : "No Section", Collectors.counting()));
    }

    @Override
    public Map<String, Long> getPendingApprovalsCount(Long hodId) {
        DashboardStatsDTO stats = getDashboardStats(hodId);
        Map<String, Long> pending = new HashMap<>();
        pending.put("LEAVE", stats.getPendingLeaveApprovals());
        pending.put("OD", stats.getPendingODApprovals());
        pending.put("OUTPASS", stats.getPendingOutpassApprovals());
        return pending;
    }

    @Override
    public Map<String, Long> getFacultyRoleDistribution(Long hodId) {
        Long deptId = SecurityUtils.getCurrentUserDepartmentId();
        Map<String, Long> dist = new HashMap<>();
        dist.put("MENTOR", getCountByRole(deptId, "MENTOR"));
        dist.put("CLASS_ADVISOR", getCountByRole(deptId, "CLASS_ADVISOR"));
        dist.put("EVENT_COORDINATOR", getCountByRole(deptId, "EVENT_COORDINATOR"));
        return dist;
    }
}