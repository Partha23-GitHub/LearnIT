package com.learnit.services;

import com.learnit.payloads.JwtAuthRequest;
import com.learnit.payloads.JwtAuthResponse;
import com.learnit.payloads.UserDto;


import org.springframework.web.multipart.MultipartFile;

import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.AuthUserDto;
import com.learnit.payloads.ChangePassword;
import com.learnit.payloads.ForgotPassword;


public interface UserService {
	
	void logout();
	
	AuthUserDto registerUser(AuthUserDto authUser);
	JwtAuthResponse loginUser(JwtAuthRequest request)throws Exception;
	boolean changePassword(ChangePassword changePassword,String userName);
	AuthUserDto uploadProfilePicture(MultipartFile image,String email);
	
	String sendForgotPasswordOtp(String email);
	boolean forgotPassword(ForgotPassword forgotPassword);
	
}
