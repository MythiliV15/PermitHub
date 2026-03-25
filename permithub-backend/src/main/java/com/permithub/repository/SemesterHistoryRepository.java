package com.permithub.repository;

import com.permithub.entity.SemesterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SemesterHistoryRepository extends JpaRepository<SemesterHistory, Long> {
    List<SemesterHistory> findByStudentIdOrderBySemesterIdDesc(Long studentId);
}
