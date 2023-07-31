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

import com.learnit.exceptions.ApiException;
import com.learnit.exceptions.ResourseNotFoundException;
import com.learnit.models.Course;
import com.learnit.models.Review;
import com.learnit.models.User;
import com.learnit.payloads.PaginatedReviewList;
import com.learnit.payloads.ReviewDto;
import com.learnit.repository.CourseRepository;
import com.learnit.repository.ReviewRepository;
import com.learnit.repository.UserRepository;
import com.learnit.services.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Autowired UserRepository userRepository;
	@Autowired CourseRepository courseRepository;
	@Autowired ModelMapper modelMapper;
	@Autowired ReviewRepository reviewRepository;
	
	@Override
	public ReviewDto addReview(String comment, Long userId, Long CourseId) {
		User user=this.userRepository.findById(userId).orElseThrow(()->new ResourseNotFoundException("Something went wrong, Try Again!!"));
		Course course=this.courseRepository.findById(CourseId).orElseThrow(()->new ResourseNotFoundException("Something went wrong, Try Again!!"));
		
		
		Review review=new Review();
		review.setComment(comment);
		review.setCourse(course);
		review.setUser(user);
		
		Review savedReview = this.reviewRepository.save(review);
		
		course.getReviews().add(savedReview);
		
		this.courseRepository.save(course);
		
		return this.modelMapper.map(savedReview, ReviewDto.class);
	}

	@Override
	public void deleteReview(Long reviewId) {
		
		Review review = this.reviewRepository.findById(reviewId).orElseThrow(()->new ResourseNotFoundException("Something went wrong,review not found"));
		User user = review.getUser();
		
		if(review.getUser()==user) {
			Course course = review.getCourse();
			review.setUser(null);
			course.getReviews().remove(review);
			this.courseRepository.save(course);
		}else {
			throw new ApiException("Review cannot be deleted");
		}
	}

	@Override
	public List<ReviewDto> topReviews(Long courseId) {
		Course course = this.courseRepository.findById(courseId).get();
		List<Review> topReviews = course.getReviews().stream().limit(5).collect(Collectors.toList());
		
		List<ReviewDto> topReviewsDtos = topReviews.stream().map(review-> this.modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList());
		return topReviewsDtos;
	}

	@Override
	public PaginatedReviewList allReviews(Long courseId,Integer pageNumber,Integer pageSize,String sortBy,String sortDirection) {
		Sort sort=sortDirection.equalsIgnoreCase("descending")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
		
		Pageable p=PageRequest.of(pageNumber, pageSize,sort);
		Page<Review> pageOfReviews = this.reviewRepository.findAll(p);
		List<Review> reviews = pageOfReviews.getContent();
		
		List<ReviewDto> reviewDtos = reviews.stream().map(review->this.modelMapper.map(review, ReviewDto.class)).collect(Collectors.toList());
		
		PaginatedReviewList reviewResponse=new PaginatedReviewList();
		
		reviewResponse.setLastPage(pageOfReviews.isLast());
		reviewResponse.setTotalPages(pageOfReviews.getTotalPages());
		reviewResponse.setPageSize(pageOfReviews.getSize());
		reviewResponse.setPageNumber(pageOfReviews.getNumber());
		reviewResponse.setTotalElements((int)pageOfReviews.getTotalElements());
		reviewResponse.setReviews(reviewDtos);
		return reviewResponse;
	}

	@Override
	public ReviewDto updateReview(String comment, Long reviewId) {
		Review review = this.reviewRepository.findById(reviewId).orElseThrow(()->new ResourseNotFoundException("Something went wrong,review not found"));
		review.setComment(comment);
		Review updatedReview = this.reviewRepository.save(review);
		return this.modelMapper.map(updatedReview, ReviewDto.class);
	}
}
