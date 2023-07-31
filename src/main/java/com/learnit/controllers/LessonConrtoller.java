package com.learnit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.CourseDto;
import com.learnit.payloads.LessonDto;
import com.learnit.services.LessonService;

@RestController
@RequestMapping("api/lessons")
public class LessonConrtoller {
	@Autowired private LessonService lessonService;
	
	@PostMapping("/{courseId}")
	public ResponseEntity<CourseDto>addLessons(@RequestBody List<LessonDto>lessons,@PathVariable Long courseId){
		CourseDto coursesWithLessons = this.lessonService.addLessonsToCourse(lessons, courseId);

		return new ResponseEntity<CourseDto>(coursesWithLessons,HttpStatus.CREATED);
	}
	
	@GetMapping("/{lessonId}")
	public ResponseEntity<LessonDto>getLesson(@PathVariable Long lessonId){
		LessonDto lesson = this.lessonService.getLesson(lessonId);
		return new ResponseEntity<LessonDto>(lesson,HttpStatusCode.valueOf(200));
	}
	
	@PutMapping("/{lessonId}")
	public ResponseEntity<LessonDto>updateLesson(@RequestBody LessonDto lessonDto,@PathVariable Long lessonId){
		LessonDto lesson = this.lessonService.updateLesson(lessonDto, lessonId);
		return new ResponseEntity<LessonDto>(lesson,HttpStatusCode.valueOf(200));
	}
	
	@DeleteMapping("/{lessonId}")
	public ResponseEntity<ApiResponse>deleteLesson(@PathVariable Long lessonId){
		this.lessonService.deleteLesson(lessonId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Lesson Deleted",true),HttpStatus.OK);
	}
}
