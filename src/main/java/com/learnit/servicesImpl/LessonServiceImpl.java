package com.learnit.servicesImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnit.exceptions.ResourseNotFoundException;
import com.learnit.models.Course;
import com.learnit.models.Lesson;
import com.learnit.payloads.CourseDto;
import com.learnit.payloads.LessonDto;
import com.learnit.repository.CourseRepository;
import com.learnit.repository.LessonRepository;
import com.learnit.services.LessonService;

@Service
public class LessonServiceImpl implements LessonService {
	@Autowired private CourseRepository courseRepository;
	@Autowired private LessonRepository lessonRepository;
	@Autowired private ModelMapper modelMapper;
	
	@Override
	public CourseDto addLessonsToCourse(List<LessonDto>lessons,Long courseId) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Please try Again !! Course not Created"));
		
		List<Lesson> allLessons = lessons.stream()
				.map(lessonDto->{
					Lesson lesson=new Lesson();
					lesson.setTitle(lessonDto.getTitle());
					lesson.setYoutubeUrl(lessonDto.getYoutubeUrl());
					lesson.setCourse(course);
					return lesson;
				}).collect(Collectors.toList());
						
		course.getLessons().addAll(allLessons);
		
		this.courseRepository.save(course);
		//fetch the updated course for returning
		Course courseWithLessons = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Please try Again !! Course not Created"));
		return this.modelMapper.map(courseWithLessons,CourseDto.class);
	}

	@Override
	public LessonDto getLesson(Long lessonId) {
		Lesson lesson = this.lessonRepository.findById(lessonId).orElseThrow(()->new ResourseNotFoundException("Lesson Not Found"));
		return this.modelMapper.map(lesson, LessonDto.class);
	}

	@Override
	public LessonDto updateLesson(LessonDto lessonDto, Long lessonId) {
		Lesson lesson = this.lessonRepository.findById(lessonId).orElseThrow(()->new ResourseNotFoundException("Lesson Not Found"));
		lesson.setTitle(lessonDto.getTitle());
		lesson.setYoutubeUrl(lessonDto.getYoutubeUrl());
		this.lessonRepository.save(lesson);
		//not fetching again
		return this.modelMapper.map(lesson, LessonDto.class);
	}

	@Override
	public void deleteLesson(Long lessonId) {
		Lesson lesson = this.lessonRepository.findById(lessonId).orElseThrow(()->new ResourseNotFoundException("Lesson Not Found"));
		this.lessonRepository.delete(lesson);
	}

	@Override
	public List<LessonDto> getAllLessons(Long courseId) {
		
		List<Lesson> allLessons = this.lessonRepository.getAllLessons(courseId);
		
		Comparator<Lesson> idComparator = Comparator.comparing(Lesson::getId);

		// Sort the list using the comparator and collect the sorted elements into a new list
		List<Lesson> sortedLessons = allLessons.stream()
		        .sorted(idComparator)
		        .collect(Collectors.toList());
		
		List<LessonDto> allLessonsDtos = sortedLessons.stream().map(lesson->this.modelMapper.map(lesson, LessonDto.class)).collect(Collectors.toList());

		return allLessonsDtos;
	}

}
