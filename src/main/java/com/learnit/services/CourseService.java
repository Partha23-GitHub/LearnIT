package com.learnit.services;

import org.springframework.web.multipart.MultipartFile;

import com.learnit.payloads.CourseDto;
import com.learnit.payloads.PagenatedCoursesList;

public interface CourseService {
	CourseDto createCourse(CourseDto courseDto,Long userId);
	
	CourseDto getCourse(Long courseId);
	
	CourseDto updateCourse(CourseDto courseDto,Long courseId);
	
	boolean deleteCourse(Long courseId,Long UserId);
	
	PagenatedCoursesList getAllCourse(Integer pageNumber,Integer pageSize,String sortBy,String sortDirection);
	
	PagenatedCoursesList getCoursesByCategory(String category,Integer pageNumber,Integer pageSize,String sortBy,String sortDirection);
	
	PagenatedCoursesList searhCourseByTitle(String title,Integer pageNumber,Integer pageSize,String sortBy,String sortDirection);
	
	CourseDto uploadThumbnail(MultipartFile file,Long courseId);
	
	Double updateRating(Long courseId, Double rating);
	
	PagenatedCoursesList getUserEnrollments(String email,Integer pageNumber, Integer pageSize, String sortBy,String sortDirection);
	
	PagenatedCoursesList getCreatedCourse(String email,Integer pageNumber, Integer pageSize, String sortBy,String sortDirection);
}
