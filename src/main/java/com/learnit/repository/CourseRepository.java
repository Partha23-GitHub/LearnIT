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
	
//	@Query("SELECT c FROM Course c where c.id = :userId")
//	List<Course> findEnrolledCoursesByUserId(@Param("userId") Long userId);
//	
//	@Query("SELECT c FROM Course c WHERE c.instructor_id = :instructorId")
//	List<Course> findCoursesByInstructorId(@Param("instructorId") Long instructorId);




}
