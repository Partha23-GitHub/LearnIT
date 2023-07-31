package com.learnit.payloads;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedReviewList {
	private Integer pageNumber;
	private Integer pageSize;
	private List<ReviewDto>reviews;
	private Integer totalElements;
	private Integer totalPages;
	private boolean isLastPage;
}
