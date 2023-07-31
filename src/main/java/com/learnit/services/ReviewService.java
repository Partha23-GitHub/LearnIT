package com.learnit.services;

import java.util.List;

import com.learnit.payloads.PaginatedReviewList;
import com.learnit.payloads.ReviewDto;

public interface ReviewService {
	
	ReviewDto addReview(String comment,Long userId,Long CourseId);
	
	void deleteReview(Long reviewId);
	
	ReviewDto updateReview(String comment,Long reviewId);
	
	List<ReviewDto> topReviews(Long courseId);
	
	PaginatedReviewList allReviews(Long courseId,Integer pageNumber,Integer pageSize,String sortBy,String sortDirection);
}
