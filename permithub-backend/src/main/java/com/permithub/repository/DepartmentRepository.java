package com.permithub.repository;

import com.permithub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByCode(String code);
    
    Optional<Department> findByName(String name);
    
    Boolean existsByCode(String code);
    
    Boolean existsByName(String name);
    
    @Modifying
    @Transactional
    @Query("UPDATE Department d SET d.hod = null WHERE d.hod.id = :hodId")
    void removeHod(@Param("hodId") Long hodId);
    
    @Query("SELECT d FROM Department d WHERE d.hod.id = :hodId")
    Optional<Department> findByHodId(@Param("hodId") Long hodId);
}