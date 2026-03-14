package com.permithub.repository;

import com.permithub.entity.HOD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HODRepository extends JpaRepository<HOD, Long> {
}
