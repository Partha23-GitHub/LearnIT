package com.learnit.payloads;

import lombok.Data;

@Data
public class ForgotPassword {
	private String email;
	private String otp;
	private String password;
}
