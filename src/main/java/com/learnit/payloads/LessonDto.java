package com.learnit.payloads;


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
public class LessonDto {
	private Long id;
	  
	 @NotNull
	 @NotBlank
	 @NotEmpty
	 private String title;
	 
	 @NotNull
	 @NotBlank
	 @NotEmpty
	 private String youtubeUrl;
}
