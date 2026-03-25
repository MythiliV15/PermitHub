package com.permithub.repository;

import com.permithub.entity.GoogleSheetsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GoogleSheetsConfigRepository extends JpaRepository<GoogleSheetsConfig, Long> {
    Optional<GoogleSheetsConfig> findByDepartmentIdAndConfigName(Long departmentId, String configName);
}
