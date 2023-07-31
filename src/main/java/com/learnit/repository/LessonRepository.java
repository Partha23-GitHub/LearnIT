package com.learnit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learnit.models.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long>{
	 @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId")
	    List<Lesson> getAllLessons(@Param("courseId") Long courseId);
}
