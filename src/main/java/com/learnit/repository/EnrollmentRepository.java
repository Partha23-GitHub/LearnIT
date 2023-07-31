package com.learnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnit.models.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
