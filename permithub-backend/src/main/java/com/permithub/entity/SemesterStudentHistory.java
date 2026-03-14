package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "semester_student_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterStudentHistory extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false, length = 10)
    private String section;
    
    @Column(name = "leave_balance", nullable = false)
    private Integer leaveBalance;
    
    @Column(name = "attendance_percentage", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;
    
    @ManyToOne
    @JoinColumn(name = "promoted_from_semester_id")
    private Semester promotedFromSemester;
    
    @ManyToOne
    @JoinColumn(name = "promoted_by")
    private User promotedBy;
    
    @Column(name = "promoted_date", nullable = false)
    private LocalDateTime promotedDate;
    
    @Column(name = "promotion_remarks", length = 500)
    private String promotionRemarks;
    
    @Column(name = "is_passed")
    @Builder.Default
    private Boolean isPassed = true;
    
    @Column(name = "is_arrear")
    @Builder.Default
    private Boolean isArrear = false;
    
    @Column(name = "arrears_count")
    @Builder.Default
    private Integer arrearsCount = 0;
    
    // Academic performance
    @Column(name = "sgpa", precision = 3, scale = 2)
    private BigDecimal sgpa;
    
    @Column(name = "cgpa", precision = 3, scale = 2)
    private BigDecimal cgpa;
    
    @Column(name = "total_marks")
    private Integer totalMarks;
    
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
    
    @Column(name = "rank_in_class")
    private Integer rankInClass;
    
    // Backlog details
    @Column(name = "backlog_subjects", columnDefinition = "TEXT")
    private String backlogSubjects; // JSON array of subjects
    
    @Column(name = "backlog_cleared")
    @Builder.Default
    private Boolean backlogCleared = false;
    
    @Column(name = "backlog_cleared_date")
    private LocalDateTime backlogClearedDate;
    
    // Attendance details
    @Column(name = "working_days")
    private Integer workingDays;
    
    @Column(name = "days_present")
    private Integer daysPresent;
    
    @Column(name = "days_absent")
    private Integer daysAbsent;
    
    @Column(name = "leaves_taken")
    private Integer leavesTaken;
    
    @Column(name = "ods_taken")
    private Integer odsTaken;
    
    // Fee details
    @Column(name = "tuition_fee_paid")
    @Builder.Default
    private Boolean tuitionFeePaid = false;
    
    @Column(name = "exam_fee_paid")
    @Builder.Default
    private Boolean examFeePaid = false;
    
    @Column(name = "library_fee_paid")
    @Builder.Default
    private Boolean libraryFeePaid = false;
    
    @Column(name = "hostel_fee_paid")
    @Builder.Default
    private Boolean hostelFeePaid = false;
    
    @Column(name = "transport_fee_paid")
    @Builder.Default
    private Boolean transportFeePaid = false;
    
    @Column(name = "total_fee_paid", precision = 10, scale = 2)
    private BigDecimal totalFeePaid;
    
    @Column(name = "fee_due", precision = 10, scale = 2)
    private BigDecimal feeDue;
    
    // Helper methods
    public boolean isEligibleForPromotion() {
        return isPassed && !isArrear && attendancePercentage != null && 
               attendancePercentage.compareTo(new BigDecimal("75")) >= 0;
    }
    
    public boolean hasBacklogs() {
        return arrearsCount != null && arrearsCount > 0;
    }
    
    public void calculateAttendance() {
        if (workingDays != null && workingDays > 0) {
            if (daysPresent != null) {
                this.attendancePercentage = BigDecimal.valueOf((daysPresent * 100.0) / workingDays)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                this.daysAbsent = workingDays - daysPresent;
            }
        }
    }
    
    public void updateFromPromotion(Semester newSemester, User promotedByUser) {
        this.promotedToSemester(newSemester, promotedByUser);
    }
    
    private void promotedToSemester(Semester newSemester, User promotedByUser) {
        this.promotedFromSemester = this.semester;
        this.promotedBy = promotedByUser;
        this.promotedDate = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (promotedDate == null) {
            promotedDate = LocalDateTime.now();
        }
        if (leaveBalance == null) {
            leaveBalance = 20;
        }
        if (arrearsCount == null) {
            arrearsCount = 0;
        }
        calculateAttendance();
    }
}