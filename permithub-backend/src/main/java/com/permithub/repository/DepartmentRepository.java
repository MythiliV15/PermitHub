package com.permithub.repository;

import com.permithub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByCode(String code);
    
    Optional<Department> findByName(String name);
    
    Boolean existsByCode(String code);
    
    Boolean existsByName(String name);
    
    @Query("SELECT d FROM Department d WHERE d.id = (SELECT fp.departmentId FROM FacultyProfile fp WHERE fp.userId = :hodId)")
    Optional<Department> findByHodId(@Param("hodId") Long hodId);
}