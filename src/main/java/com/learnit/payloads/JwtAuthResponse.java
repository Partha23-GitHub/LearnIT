package com.learnit.payloads;

import lombok.Data;

@Data
public class JwtAuthResponse {
	private String token;
	private AuthUserDto user;
}
