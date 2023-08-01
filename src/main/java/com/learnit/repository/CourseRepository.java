package com.learnit.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learnit.models.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	
	@Query("SELECT c FROM Course c WHERE LOWER(c.category) LIKE %:category%")
    Page<Course> findByCategoryIgnoreCase(@Param("category") String category, Pageable pageable);
	
	Page<Course> findByTitleContainingIgnoreCase(String title,Pageable pageable);
	
	// Get all enrolled courses for a user by email with pagination
    Page<Course> findAllByEnrolledUsersEmail(String email, Pageable pageable);

    // Get all created courses for a user by email with pagination
    Page<Course> findAllByInstructorEmail(String email, Pageable pageable);




}
