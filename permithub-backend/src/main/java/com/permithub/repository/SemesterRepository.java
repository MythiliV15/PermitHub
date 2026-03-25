package com.permithub.repository;

import com.permithub.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    List<Semester> findByDepartmentId(Long departmentId);
    
    Optional<Semester> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Semester s SET s.isActive = false WHERE s.departmentId = :deptId")
    void deactivateAllByDepartment(@Param("deptId") Long deptId);
}