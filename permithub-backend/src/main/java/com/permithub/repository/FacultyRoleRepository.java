package com.permithub.repository;

import com.permithub.entity.FacultyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRoleRepository extends JpaRepository<FacultyRole, Long> {
    
    List<FacultyRole> findByFacultyId(Long facultyId);
    
    List<FacultyRole> findByFacultyIdAndIsActiveTrue(Long facultyId);
    
    Optional<FacultyRole> findByFacultyIdAndRoleName(Long facultyId, String roleName);
    
    @Modifying
    @Query("UPDATE FacultyRole fr SET fr.isActive = false WHERE fr.facultyId = :facultyId")
    void deactivateAllForFaculty(Long facultyId);
}
