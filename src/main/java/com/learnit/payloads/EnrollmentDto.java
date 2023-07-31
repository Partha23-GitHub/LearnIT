package com.learnit.payloads;

import java.math.BigDecimal;
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
public class EnrollmentDto {
	private Long id;
	
	private String payment_id;
	@NotNull
	@NotBlank
	@NotEmpty
    private UserDto user;
	@NotNull
	 @NotBlank
	 @NotEmpty
    private CourseDto course;
	
	@NotBlank @NotNull
    private BigDecimal amount;
    
    @NotBlank @NotNull
    private String uploadDateAndTime;
}
