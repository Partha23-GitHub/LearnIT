package com.learnit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learnit.constants.ReviewConstants;
import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.PaginatedReviewList;
import com.learnit.payloads.ReviewDto;
import com.learnit.repository.UserRepository;
import com.learnit.services.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	@Autowired private ReviewService reviewService;
	@Autowired private UserRepository userRepository;
	
	@PostMapping("")
	public ResponseEntity<ReviewDto>addReview(@RequestParam String review,@RequestHeader Long courseId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = this.userRepository.findByEmail(auth.getName()).getId();
		
		ReviewDto addedReview = this.reviewService.addReview(review, userId, courseId);
		return new ResponseEntity<ReviewDto>(addedReview,HttpStatus.CREATED);
	}
	@PatchMapping("")
	public ResponseEntity<ReviewDto>updateReview(@RequestParam String review,@RequestHeader Long reviewId){
		
		ReviewDto updatedReview = this.reviewService.updateReview(review, reviewId);
		return new ResponseEntity<ReviewDto>(updatedReview,HttpStatus.CREATED);
	}
	
	@DeleteMapping("")
	public ResponseEntity<ApiResponse>deleteReview(@RequestParam Long reviewId){
		
		this.reviewService.deleteReview(reviewId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Review deleted,",true),HttpStatus.OK);
	}
	
	@GetMapping("")
	public ResponseEntity<PaginatedReviewList>getReviews(@RequestHeader Long courseId,@RequestParam(value="pageNumber",defaultValue=ReviewConstants.PAGE_NUMBER,required = false)Integer pageNumber,
			@RequestParam(value="pageSize",defaultValue=ReviewConstants.PAGE_SIZE,required = false)Integer pageSize,
			@RequestParam(value="sortBy",defaultValue =ReviewConstants.SORT_BY,required = false)String sortBy,
			@RequestParam(value="sortDirection",defaultValue =ReviewConstants.SORT_DIR,required = false)String sortDirection){
		
		PaginatedReviewList allReviews = this.reviewService.allReviews(courseId, pageNumber, pageSize, sortBy, sortDirection);
		return new ResponseEntity<PaginatedReviewList>(allReviews,HttpStatus.OK);
	}
}
