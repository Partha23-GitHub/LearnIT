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
public class AuthUserDto {
	private Long id;
	
    @NotNull
	@NotBlank
	@NotEmpty
    private String name;
    
    @NotNull
	@NotBlank
	@NotEmpty
    private String email;
    
    @NotNull
	@NotBlank
	@NotEmpty
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String profilePicture;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role="USER";
}
