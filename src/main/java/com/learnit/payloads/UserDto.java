package com.learnit.payloads;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	
    @NotNull
	@NotBlank
	@NotEmpty
    private String name;
    
    @NotNull
	@NotBlank
	@NotEmpty
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;
    
    private String profilePicture;
}