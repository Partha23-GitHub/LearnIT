package com.learnit.services;

import java.util.List;

import com.learnit.payloads.CourseDto;
import com.learnit.payloads.LessonDto;

public interface LessonService {
	CourseDto addLessonsToCourse(List<LessonDto> lessonDto,Long courseId);
	
	LessonDto getLesson(Long lessonId);
	
	LessonDto updateLesson(LessonDto lessonDto,Long lessonId);
	
	void deleteLesson(Long lessonId);
	
	List<LessonDto> getAllLessons(Long courseId);
	
}
