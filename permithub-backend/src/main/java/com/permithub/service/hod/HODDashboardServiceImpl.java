package com.permithub.service.hod;

import com.permithub.dto.hod.DashboardStatsDTO;
import com.permithub.dto.hod.RecentActivityDTO;
import com.permithub.entity.*;
import com.permithub.exception.ResourceNotFoundException;
import com.permithub.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HODDashboardServiceImpl implements HODDashboardService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ODRequestRepository odRequestRepository;
    private final OutpassRequestRepository outpassRequestRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public DashboardStatsDTO getDashboardStats(Long hodId) {
        log.info("Fetching dashboard stats for HOD ID: {}", hodId);
        
        // Get HOD's department
        Faculty hod = getHodFaculty(hodId);
        Department department = hod.getDepartment();
        
        if (department == null) {
            throw new ResourceNotFoundException("Department not found for HOD");
        }
        
        Long deptId = department.getId();
        
        // Build dashboard stats
        return DashboardStatsDTO.builder()
                .departmentId(deptId)
                .departmentName(department.getName())
                .departmentCode(department.getCode())
                .totalStudents(studentRepository.countActiveByDepartment(deptId))
                .totalFaculty(facultyRepository.countActiveByDepartment(deptId))
                .totalMentors(getFacultyCountByRole(deptId, Role.FACULTY_MENTOR))
                .totalClassAdvisors(getFacultyCountByRole(deptId, Role.FACULTY_CLASS_ADVISOR))
                .totalEventCoordinators(getFacultyCountByRole(deptId, Role.FACULTY_EVENT_COORDINATOR))
                .pendingLeaveApprovals(leaveRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId))
                .pendingODApprovals(odRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId))
                .pendingOutpassApprovals(outpassRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_WARDEN, deptId))
                .totalPendingApprovals(calculateTotalPendingApprovals(deptId))
                .yearWiseStudentDistribution(getYearWiseStudentDistribution(hodId))
                .sectionWiseStudentDistribution(getSectionWiseStudentDistribution(hodId, null))
                .hostelersCount(getHostelersCount(deptId))
                .dayScholarsCount(getDayScholarsCount(deptId))
                .recentActivities(getRecentActivitiesList(deptId))
                .leaveRequestsByMonth(getLeaveRequestsByMonth(deptId))
                .odRequestsByType(getODRequestsByType(deptId))
                .approvalsByStatus(getApprovalsByStatus(deptId))
                .build();
    }

    @Override
    public Page<Object> getRecentActivities(Long hodId, Pageable pageable) {
        log.info("Fetching recent activities for HOD ID: {}", hodId);
        
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Object> activities = new ArrayList<>();
        
        // Get recent leave requests
        List<LeaveRequest> recentLeaves = leaveRequestRepository.findRecentByDepartment(deptId, pageable);
        activities.addAll(recentLeaves.stream()
                .map(lr -> createActivityDTO("LEAVE", 
                        lr.getStudent().getFullName() + " applied for leave",
                        lr.getStudent().getFullName(),
                        lr.getStudent().getRegisterNumber(),
                        lr.getStatus().toString(),
                        lr.getAppliedDate()))
                .collect(Collectors.toList()));
        
        // Get recent OD requests
        List<ODRequest> recentODs = odRequestRepository.findRecentByDepartment(deptId, pageable);
        activities.addAll(recentODs.stream()
                .map(od -> createActivityDTO("OD",
                        od.getStudent().getFullName() + " applied for OD - " + od.getEventName(),
                        od.getStudent().getFullName(),
                        od.getStudent().getRegisterNumber(),
                        od.getStatus().toString(),
                        od.getAppliedDate()))
                .collect(Collectors.toList()));
        
        // Get recent outpass requests
        // Note: You'll need to add findRecentByDepartment method to OutpassRequestRepository
        // For now, we'll skip or implement later
        
        // Sort by date descending
        activities.sort((a1, a2) -> {
            LocalDateTime d1 = ((RecentActivityDTO) a1).getTimestamp() != null ? 
                    LocalDateTime.parse(((RecentActivityDTO) a1).getTimestamp(), DATE_FORMATTER) : LocalDateTime.MIN;
            LocalDateTime d2 = ((RecentActivityDTO) a2).getTimestamp() != null ? 
                    LocalDateTime.parse(((RecentActivityDTO) a2).getTimestamp(), DATE_FORMATTER) : LocalDateTime.MIN;
            return d2.compareTo(d1);
        });
        
        return new PageImpl<>(activities.stream().limit(pageable.getPageSize()).collect(Collectors.toList()));
    }

    @Override
    public Object getDepartmentOverview(Long hodId) {
        Faculty hod = getHodFaculty(hodId);
        Department department = hod.getDepartment();
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("department", department);
        overview.put("totalStudents", studentRepository.countActiveByDepartment(department.getId()));
        overview.put("totalFaculty", facultyRepository.countActiveByDepartment(department.getId()));
        overview.put("studentDistribution", getYearWiseStudentDistribution(hodId));
        overview.put("facultyList", facultyRepository.findByDepartmentId(department.getId()));
        
        return overview;
    }

    @Override
    public Map<String, Long> getYearWiseStudentDistribution(Long hodId) {
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Object[]> distribution = studentRepository.getStudentDistribution(deptId);
        
        return distribution.stream()
                .collect(Collectors.groupingBy(
                        obj -> "Year " + obj[0],
                        Collectors.summingLong(obj -> ((Number) obj[2]).longValue())
                ));
    }

    @Override
    public Map<String, Long> getSectionWiseStudentDistribution(Long hodId, Integer year) {
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        List<Student> students;
        if (year != null) {
            students = studentRepository.findByDepartmentId(deptId).stream()
                    .filter(s -> s.getYear().equals(year) && s.getIsActive())
                    .collect(Collectors.toList());
        } else {
            students = studentRepository.findByDepartmentId(deptId).stream()
                    .filter(s -> s.getIsActive())
                    .collect(Collectors.toList());
        }
        
        return students.stream()
                .collect(Collectors.groupingBy(
                        Student::getSection,
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getPendingApprovalsCount(Long hodId) {
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Map<String, Long> pendingCounts = new HashMap<>();
        pendingCounts.put("LEAVE", leaveRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId));
        pendingCounts.put("OD", odRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId));
        pendingCounts.put("OUTPASS", outpassRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_WARDEN, deptId));
        
        return pendingCounts;
    }

    @Override
    public Map<String, Long> getFacultyRoleDistribution(Long hodId) {
        Faculty hod = getHodFaculty(hodId);
        Long deptId = hod.getDepartment().getId();
        
        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("MENTORS", getFacultyCountByRole(deptId, Role.FACULTY_MENTOR));
        roleDistribution.put("CLASS_ADVISORS", getFacultyCountByRole(deptId, Role.FACULTY_CLASS_ADVISOR));
        roleDistribution.put("EVENT_COORDINATORS", getFacultyCountByRole(deptId, Role.FACULTY_EVENT_COORDINATOR));
        
        return roleDistribution;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Faculty getHodFaculty(Long hodId) {
        return facultyRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with ID: " + hodId));
    }

    private long getFacultyCountByRole(Long deptId, Role role) {
        return facultyRepository.findByRoleAndDepartment(role, deptId).size();
    }

    private long calculateTotalPendingApprovals(Long deptId) {
        return leaveRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId)
                + odRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_CLASS_ADVISOR, deptId)
                + outpassRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_WARDEN, deptId);
    }

    private long getHostelersCount(Long deptId) {
        return studentRepository.findByDepartmentId(deptId).stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsHosteler()) && s.getIsActive())
                .count();
    }

    private long getDayScholarsCount(Long deptId) {
        return studentRepository.findByDepartmentId(deptId).stream()
                .filter(s -> Boolean.FALSE.equals(s.getIsHosteler()) && s.getIsActive())
                .count();
    }

    private List<RecentActivityDTO> getRecentActivitiesList(Long deptId) {
        List<RecentActivityDTO> activities = new ArrayList<>();
        
        // Get last 10 activities from each type
        Pageable limit = org.springframework.data.domain.PageRequest.of(0, 5);
        
        leaveRequestRepository.findRecentByDepartment(deptId, limit)
                .forEach(lr -> activities.add(createActivityDTO("LEAVE",
                        lr.getStudent().getFullName() + " applied for leave",
                        lr.getStudent().getFullName(),
                        lr.getStudent().getRegisterNumber(),
                        lr.getStatus().toString(),
                        lr.getAppliedDate())));
        
        odRequestRepository.findRecentByDepartment(deptId, limit)
                .forEach(od -> activities.add(createActivityDTO("OD",
                        od.getStudent().getFullName() + " applied for OD - " + od.getEventName(),
                        od.getStudent().getFullName(),
                        od.getStudent().getRegisterNumber(),
                        od.getStatus().toString(),
                        od.getAppliedDate())));
        
        // Sort and limit to 10
        activities.sort((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()));
        
        return activities.stream().limit(10).collect(Collectors.toList());
    }

    private RecentActivityDTO createActivityDTO(String type, String description, String studentName,
                                                String registerNumber, String status, LocalDateTime timestamp) {
        return RecentActivityDTO.builder()
                .type(type)
                .description(description)
                .studentName(studentName)
                .registerNumber(registerNumber)
                .status(status)
                .timestamp(timestamp != null ? timestamp.format(DATE_FORMATTER) : null)
                .build();
    }

    private Map<String, Long> getLeaveRequestsByMonth(Long deptId) {
        // This would require a custom query grouping by month
        // For now, return empty map
        return new HashMap<>();
    }

    private Map<String, Long> getODRequestsByType(Long deptId) {
        // This would require a custom query grouping by event type
        // For now, return empty map
        return new HashMap<>();
    }

    private Map<String, Long> getApprovalsByStatus(Long deptId) {
        Map<String, Long> approvals = new HashMap<>();
        
        approvals.put("PENDING", calculateTotalPendingApprovals(deptId));
        approvals.put("APPROVED", 
                leaveRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_HOD, deptId)
                + odRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_HOD, deptId)
                + outpassRequestRepository.countByStatusAndDepartment(RequestStatus.APPROVED_BY_HOD, deptId));
        approvals.put("REJECTED",
                leaveRequestRepository.countByStatusAndDepartment(RequestStatus.REJECTED_BY_HOD, deptId)
                + odRequestRepository.countByStatusAndDepartment(RequestStatus.REJECTED_BY_HOD, deptId)
                + outpassRequestRepository.countByStatusAndDepartment(RequestStatus.REJECTED_BY_HOD, deptId));
        
        return approvals;
    }
}