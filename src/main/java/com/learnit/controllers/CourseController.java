package com.learnit.controllers;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.CourseDto;
import com.learnit.payloads.CourseDtoWithoutLessons;
import com.learnit.payloads.PagenatedCoursesList;
import com.learnit.repository.CourseRepository;
import com.learnit.repository.UserRepository;
import com.learnit.services.CourseService;
import com.learnit.servicesImpl.Razorpay;
import com.learnit.constants.CourseConstants;
import com.learnit.models.Course;
import com.learnit.models.User;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
	@Autowired private ModelMapper modelMapper;
	@Autowired private CourseService courseService;
	@Autowired private UserRepository userRepository;
	@Autowired private CourseRepository courseRepository;
	@Autowired private Razorpay razorpay;
	
	//create the course
	@PostMapping("")
	public ResponseEntity<CourseDto>createCourse(@RequestBody CourseDto course){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = this.userRepository.findByEmail(auth.getName()).getId();
		
		CourseDto createdCourse = this.courseService.createCourse(course,userId);
		 return new ResponseEntity<CourseDto>(createdCourse,HttpStatus.CREATED);
	}
	
	//update the course
	@PatchMapping("/{courseId}")
	public ResponseEntity<CourseDto>updateCourse(@RequestBody CourseDto course,@PathVariable Long courseId){
		 CourseDto updatedCourse = this.courseService.updateCourse(course,courseId);
		 return new ResponseEntity<CourseDto>(updatedCourse,HttpStatus.OK);
	}
	
	//get All course
		@GetMapping("")
		public ResponseEntity<PagenatedCoursesList>getAllCourses(@RequestParam(value="pageNumber",defaultValue=CourseConstants.PAGE_NUMBER,required = false)Integer pageNumber,
				@RequestParam(value="pageSize",defaultValue=CourseConstants.PAGE_SIZE,required = false)Integer pageSize,
				@RequestParam(value="sortBy",defaultValue =CourseConstants.SORT_BY,required = false)String sortBy,
				@RequestParam(value="sortDirection",defaultValue =CourseConstants.SORT_DIR,required = false)String sortDirection){
				
			PagenatedCoursesList allCourses = this.courseService.getAllCourse(pageNumber, pageSize, sortBy, sortDirection);
			return new ResponseEntity<PagenatedCoursesList>(allCourses,HttpStatus.OK);
		}
	
	//get a course
	@GetMapping("/get/{courseId}")
	public ResponseEntity<?>getACourse(@PathVariable Long courseId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = this.userRepository.findByEmail(auth.getName()).getId();
		
		 CourseDto courseDto = this.courseService.getCourse(courseId);
		 
		 Course course = this.courseRepository.findById(courseId).get();
		 User accessedUser = this.userRepository.findById(userId).get();
		 
		 if(courseDto.getInstructor().getId()== accessedUser.getId() || accessedUser.getEnrolledCourses().contains(course))
			 return new ResponseEntity<CourseDto>(courseDto,HttpStatus.OK);
		 
		 CourseDtoWithoutLessons courseDtoWithoutLessons = this.modelMapper.map(course, CourseDtoWithoutLessons.class);
		 return new ResponseEntity<CourseDtoWithoutLessons>(courseDtoWithoutLessons,HttpStatus.OK);
		 
	}
		
	//delete a course
	@DeleteMapping("/{courseId}")
	public ResponseEntity<ApiResponse>deleteACourse(@PathVariable Long courseId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = this.userRepository.findByEmail(auth.getName()).getId();
		
		boolean response = this.courseService.deleteCourse(courseId,userId);
		if(response)
			return new ResponseEntity<ApiResponse>(new ApiResponse("Course deleted",true),HttpStatus.OK);
		
		 return new ResponseEntity<ApiResponse>(new ApiResponse("Access Denied",false),HttpStatus.FORBIDDEN);
	}
	
	//get courses by category
	@GetMapping("/category")
	public ResponseEntity<PagenatedCoursesList>getCoursesByCategory(@RequestParam String keyword,@RequestParam(value="pageNumber",defaultValue=CourseConstants.PAGE_NUMBER,required = false)Integer pageNumber,
				@RequestParam(value="pageSize",defaultValue=CourseConstants.PAGE_SIZE,required = false)Integer pageSize,
				@RequestParam(value="sortBy",defaultValue =CourseConstants.SORT_BY,required = false)String sortBy,
				@RequestParam(value="sortDirection",defaultValue =CourseConstants.SORT_DIR,required = false)String sortDirection){
		
		PagenatedCoursesList coursesByCategory = this.courseService.getCoursesByCategory(keyword,pageNumber, pageSize, sortBy, sortDirection);
		return new ResponseEntity<PagenatedCoursesList>(coursesByCategory,HttpStatus.OK);
	}
	//search courses by title
	@GetMapping("/title")
	public ResponseEntity<PagenatedCoursesList>searchCoursesByTitle(@RequestParam String keyword,@RequestParam(value="pageNumber",defaultValue=CourseConstants.PAGE_NUMBER,required = false)Integer pageNumber,
					@RequestParam(value="pageSize",defaultValue=CourseConstants.PAGE_SIZE,required = false)Integer pageSize,
					@RequestParam(value="sortBy",defaultValue =CourseConstants.SORT_BY,required = false)String sortBy,
					@RequestParam(value="sortDirection",defaultValue =CourseConstants.SORT_DIR,required = false)String sortDirection){
					
		PagenatedCoursesList searchedCourses = this.courseService.searhCourseByTitle(keyword,pageNumber, pageSize, sortBy, sortDirection);
		return new ResponseEntity<PagenatedCoursesList>(searchedCourses,HttpStatus.OK);
	}
	
	//get all enrolled courses
	@GetMapping("/enrolled-courses")
	public ResponseEntity<PagenatedCoursesList>getEnrolledCourses(@RequestParam(value="pageNumber",defaultValue=CourseConstants.PAGE_NUMBER,required = false)Integer pageNumber,
					@RequestParam(value="pageSize",defaultValue=CourseConstants.PAGE_SIZE,required = false)Integer pageSize,
					@RequestParam(value="sortBy",defaultValue =CourseConstants.SORT_BY,required = false)String sortBy,
					@RequestParam(value="sortDirection",defaultValue =CourseConstants.SORT_DIR,required = false)String sortDirection){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		PagenatedCoursesList searchedCourses = this.courseService.getUserEnrollments(auth.getName(), pageNumber, pageSize, sortBy, sortDirection);
		return new ResponseEntity<PagenatedCoursesList>(searchedCourses,HttpStatus.OK);
	}
	
	//get all created courses
		@GetMapping("/created-courses")
		public ResponseEntity<PagenatedCoursesList>getcreatedCourses(@RequestParam(value="pageNumber",defaultValue=CourseConstants.PAGE_NUMBER,required = false)Integer pageNumber,
						@RequestParam(value="pageSize",defaultValue=CourseConstants.PAGE_SIZE,required = false)Integer pageSize,
						@RequestParam(value="sortBy",defaultValue =CourseConstants.SORT_BY,required = false)String sortBy,
						@RequestParam(value="sortDirection",defaultValue =CourseConstants.SORT_DIR,required = false)String sortDirection){
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			PagenatedCoursesList searchedCourses = this.courseService.getCreatedCourse(auth.getName(), pageNumber, pageSize, sortBy, sortDirection);
			return new ResponseEntity<PagenatedCoursesList>(searchedCourses,HttpStatus.OK);
		}
	
	//add humbnail
	@PutMapping("/thumbnail/{courseId}")
	public ResponseEntity<CourseDto>uploadThumbnail(@RequestParam MultipartFile file, @PathVariable Long courseId){
		CourseDto uploadThumbnailedCourse = this.courseService.uploadThumbnail(file, courseId);
		return new ResponseEntity<CourseDto>(uploadThumbnailedCourse,HttpStatus.OK);
	}
		
	@PatchMapping("/rating/{courseId}")
	public ResponseEntity<Map<String,Double>> updateRating(@PathVariable Long courseId,@RequestParam Double rating) {
		Double updatedRating = this.courseService.updateRating(courseId, rating);
		
		return new ResponseEntity<Map<String,Double>>(Map.of("rating",updatedRating),HttpStatus.OK);
	}
	
	@PostMapping("/create-order")
	public ResponseEntity<?> createOrder(@Param("amount") Double amount) {
		String createOrder = this.razorpay.createOrder(amount);
		if(createOrder!=null)
			return new ResponseEntity<Object>(createOrder,HttpStatus.CREATED);
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Something went Wrong",false),HttpStatus.OK);
	}
	
	@PostMapping("/do-enroll")
	public ResponseEntity<ApiResponse> enrollIntoCourses(@Param("courseId")Long courseId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean isEnrolled = this.razorpay.enrollUserInCourse(auth.getName(),courseId);
		if(isEnrolled)
			return new ResponseEntity<ApiResponse>(new ApiResponse("Successfully Enrolled!",true),HttpStatus.OK);
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Already Enrolled!",false),HttpStatus.OK);
	}
	
}
