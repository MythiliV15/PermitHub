package com.permithub.repository;

import com.permithub.entity.LeaveCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveCategoryRepository extends JpaRepository<LeaveCategory, Long> {
    List<LeaveCategory> findByDepartmentIdAndIsActiveTrue(Long departmentId);
}
