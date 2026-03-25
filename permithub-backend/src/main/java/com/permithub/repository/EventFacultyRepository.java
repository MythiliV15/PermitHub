package com.permithub.repository;

import com.permithub.entity.EventFaculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventFacultyRepository extends JpaRepository<EventFaculty, Long> {
    List<EventFaculty> findByEventId(Long eventId);
    List<EventFaculty> findByFacultyId(Long facultyId);
}
