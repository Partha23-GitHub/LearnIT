package com.learnit.payloads;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import com.learnit.enums.Category;
import com.learnit.enums.CourseLanguage;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
	
	private Integer id;
	
	@NotNull
	@NotBlank
	@NotEmpty
	private String title;
	
	@NotNull
	@NotBlank
	@NotEmpty
	private String description;
	
	private UserDto instructor;
	
	@NotNull
	@NotBlank
	@NotEmpty
	private BigDecimal price;
	
	@NotNull
	@NotBlank
	@NotEmpty
	@Enumerated(EnumType.STRING)
	private Category category;
    
	@NotNull
	@NotBlank
	@NotEmpty
    @Enumerated(EnumType.STRING)
    private CourseLanguage courseLanguage;
    
	@NotNull
	@NotBlank
	@NotEmpty
    private String courseDuration;
	
	private Long numberOfEnrollments;
	
    private String thumbnail;
	    
	private List<LessonDto> lessons = new ArrayList<>();
	
	private Double rating;
	
	private Integer numberOfRatings;
	
    private List<ReviewDto> reviews = new ArrayList<>();

	
}
