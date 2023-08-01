package com.learnit.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learnit.exceptions.ResourseNotFoundException;
import com.learnit.models.Course;
import com.learnit.models.User;
import com.learnit.payloads.CourseDto;
import com.learnit.payloads.CourseDtoWithoutLessons;
import com.learnit.payloads.PagenatedCoursesList;
import com.learnit.payloads.ReviewDto;
import com.learnit.repository.CourseRepository;
import com.learnit.repository.UserRepository;
import com.learnit.services.CourseService;
import com.learnit.services.LessonService;
import com.learnit.services.ReviewService;

@Service
public class CourseServiceImpl implements CourseService {
	
	@Autowired private ModelMapper modelMapper;
	@Autowired private CourseRepository courseRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private ReviewService reviewService;
	@Autowired private LessonService lessonService;
	@Autowired ImageProcessing imageProcessing;
	
	
	@Override
	public CourseDto createCourse(CourseDto courseDto,Long userId) {
		
		courseDto.setRating(0.0);
		courseDto.setNumberOfRatings(0);
		Course course = this.modelMapper.map(courseDto, Course.class);
		course.setInstructor(this.userRepository.findById(userId).get());
		Course savedCourse = this.courseRepository.save(course);
		
		CourseDto courseDto1 = this.modelMapper.map(savedCourse, CourseDto.class);
		courseDto1.setNumberOfEnrollments((long)savedCourse.getEnrolledUsers().size());
		return courseDto1;
	}
	@Override
	public CourseDto getCourse(Long courseId) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Course Not Found"));
		List<ReviewDto> topReviews = this.reviewService.topReviews(courseId);
		CourseDto courseDto = this.modelMapper.map(course, CourseDto.class);
		courseDto.setReviews(topReviews);
		courseDto.setLessons(this.lessonService.getAllLessons(courseId));
		courseDto.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
		return courseDto;
	}

	@Override
	public CourseDto updateCourse(CourseDto courseDto, Long courseId) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Course Not Found"));
		
		course.setCategory(courseDto.getCategory());
		course.setCourseDuration(courseDto.getCourseDuration());
		course.setCourseLanguage(courseDto.getCourseLanguage());
		course.setDescription(courseDto.getDescription());
		course.setPrice(courseDto.getPrice());
		course.setTitle(courseDto.getTitle());
		
		Course updatedCourse = this.courseRepository.save(course);
		
		CourseDto courseDto1 = this.modelMapper.map(course, CourseDto.class);
		courseDto1.setNumberOfEnrollments((long)updatedCourse.getEnrolledUsers().size());
		return courseDto1;
	}

	@Override
	public boolean deleteCourse(Long courseId,Long userId) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Course Not Found"));
		User user = this.userRepository.findById(userId).get();
		if(course.getInstructor().equals(user)) {
			if(course.getThumbnail()!=null) {
				String oldImage=course.getThumbnail().substring(course.getThumbnail().lastIndexOf("/")+1);
				this.imageProcessing.deleteFileFromS3(oldImage);
			}
			this.courseRepository.delete(course);
			return true;
		}
		return false;
	}
	
	@Override
	public PagenatedCoursesList getAllCourse(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
		//for pagination
				Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
				
				Pageable p=PageRequest.of(pageNumber, pageSize,sort);
				Page<Course>pagecourse= this.courseRepository.findAll(p);
				List<Course> allCourses = pagecourse.getContent();
				
				List<CourseDtoWithoutLessons> allCourseDtos = allCourses.stream()
						.map(course->{
						CourseDtoWithoutLessons courseDtoList = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
						courseDtoList.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
						return courseDtoList;
						}).collect(Collectors.toList());
				//setting post response for returning
				PagenatedCoursesList courseResponse=new PagenatedCoursesList();
				
				courseResponse.setCourses(allCourseDtos);
				courseResponse.setPageNumber(pagecourse.getNumber());
				courseResponse.setPageSize(pagecourse.getSize());
				courseResponse.setTotalElements((int)pagecourse.getTotalElements());
				courseResponse.setTotalPages(pagecourse.getTotalPages());
				courseResponse.setLastPage(pagecourse.isLast());
				
				return courseResponse;
	}
	
	@Override
	public PagenatedCoursesList getCoursesByCategory(String category, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDirection) {
		// Create a Sort object based on the sorting parameters
		Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();

	    // Create a PageRequest object with the desired pagination parameters
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

	    // Call the repository method to retrieve the paginated results
		Page<Course> pageCourse = this.courseRepository.findByCategoryIgnoreCase(category.toLowerCase(), pageable);
	    
		List<Course> allCourses = pageCourse.getContent();
		
		List<CourseDtoWithoutLessons> allCourseDtos = allCourses.stream().map(course->{
			CourseDtoWithoutLessons courseDtoList = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
			courseDtoList.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
			return courseDtoList;
			}).collect(Collectors.toList());
		//setting post response for returning
		PagenatedCoursesList courseResponse=new PagenatedCoursesList();
		
		courseResponse.setCourses(allCourseDtos);
		courseResponse.setPageNumber(pageCourse.getNumber());
		courseResponse.setPageSize(pageCourse.getSize());
		courseResponse.setTotalElements((int)pageCourse.getTotalElements());
		courseResponse.setTotalPages(pageCourse.getTotalPages());
		courseResponse.setLastPage(pageCourse.isLast());
		
		return courseResponse;
	}

	
	@Override
	public PagenatedCoursesList searhCourseByTitle(String title,Integer pageNumber,Integer pageSize,String sortBy,String sortDirection) {
		Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
		
		Pageable p=PageRequest.of(pageNumber, pageSize,sort);
		Page<Course>pagecourse=this.courseRepository.findByTitleContainingIgnoreCase(title,p);
		List<Course> allCourses = pagecourse.getContent();
		
		List<CourseDtoWithoutLessons> allCourseDtos = allCourses.stream().map(course->{
			CourseDtoWithoutLessons courseDtoList = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
			courseDtoList.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
			return courseDtoList;
			}).collect(Collectors.toList());
		//setting post response for returning
		PagenatedCoursesList courseResponse=new PagenatedCoursesList();
		
		courseResponse.setCourses(allCourseDtos);
		courseResponse.setPageNumber(pagecourse.getNumber());
		courseResponse.setPageSize(pagecourse.getSize());
		courseResponse.setTotalElements((int)pagecourse.getTotalElements());
		courseResponse.setTotalPages(pagecourse.getTotalPages());
		courseResponse.setLastPage(pagecourse.isLast());
		
		return courseResponse;
	}
	
	@Override
	public CourseDto uploadThumbnail(MultipartFile file, Long courseId) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Course Not Found"));
		try {
			if(course.getThumbnail()!=null) {
				String oldImage=course.getThumbnail().substring(course.getThumbnail().lastIndexOf("/")+1);
				this.imageProcessing.deleteFileFromS3(oldImage);
			}
			String uploadImageName = this.imageProcessing.uploadToS3(file);
			String thumbnailUrl=this.imageProcessing.getS3VideoUrl(uploadImageName);
			course.setThumbnail(thumbnailUrl);
		}catch(Exception e) {
			course.setThumbnail(null);
		}
		Course savedCourse = this.courseRepository.save(course);
		CourseDto courseDto = this.modelMapper.map(savedCourse, CourseDto.class);
		courseDto.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
		return courseDto;
	}
	
	@Override
	public Double updateRating(Long courseId, Double rating) {
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("Course Not Found"));
		
		Double existingRating=course.getRating();
		int numberOfRatings = course.getNumberOfRatings();
		
		Double newRating= Math.round((((existingRating*numberOfRatings)+rating)/++numberOfRatings) * 10.0) / 10.0;
		
		course.setRating(newRating);
		course.setNumberOfRatings(numberOfRatings);
		
		Course savedCourse = this.courseRepository.save(course);
		
		return savedCourse.getRating();
	}
	@Override
	public PagenatedCoursesList getUserEnrollments(String email,Integer pageNumber, Integer pageSize, String sortBy,String sortDirection) {
		Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
		
		Pageable p=PageRequest.of(pageNumber, pageSize,sort);
		Page<Course>pagecourse= this.courseRepository.findAllByEnrolledUsersEmail(email, p);
		List<Course> allCourses = pagecourse.getContent();
		
		List<CourseDtoWithoutLessons> allCourseDtos = allCourses.stream()
				.map(course->{
				CourseDtoWithoutLessons courseDtoList = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
				courseDtoList.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
				return courseDtoList;
				}).collect(Collectors.toList());
		//setting post response for returning
		PagenatedCoursesList courseResponse=new PagenatedCoursesList();
		
		courseResponse.setCourses(allCourseDtos);
		courseResponse.setPageNumber(pagecourse.getNumber());
		courseResponse.setPageSize(pagecourse.getSize());
		courseResponse.setTotalElements((int)pagecourse.getTotalElements());
		courseResponse.setTotalPages(pagecourse.getTotalPages());
		courseResponse.setLastPage(pagecourse.isLast());
		
		return courseResponse;
	}
	@Override
	public PagenatedCoursesList getCreatedCourse(String email,Integer pageNumber, Integer pageSize, String sortBy,String sortDirection) {
		Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
		
		Pageable p=PageRequest.of(pageNumber, pageSize,sort);
		Page<Course>pagecourse= this.courseRepository.findAllByInstructorEmail(email, p);
		List<Course> allCourses = pagecourse.getContent();
		
		List<CourseDtoWithoutLessons> allCourseDtos = allCourses.stream()
				.map(course->{
				CourseDtoWithoutLessons courseDtoList = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
				courseDtoList.setNumberOfEnrollments((long)course.getEnrolledUsers().size());
				return courseDtoList;
				}).collect(Collectors.toList());
		//setting post response for returning
		PagenatedCoursesList courseResponse=new PagenatedCoursesList();
		
		courseResponse.setCourses(allCourseDtos);
		courseResponse.setPageNumber(pagecourse.getNumber());
		courseResponse.setPageSize(pagecourse.getSize());
		courseResponse.setTotalElements((int)pagecourse.getTotalElements());
		courseResponse.setTotalPages(pagecourse.getTotalPages());
		courseResponse.setLastPage(pagecourse.isLast());
		
		return courseResponse;
	}

}
