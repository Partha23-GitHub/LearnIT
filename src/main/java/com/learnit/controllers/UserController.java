package com.learnit.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.AuthUserDto;
import com.learnit.payloads.ChangePassword;
import com.learnit.payloads.ForgotPassword;
import com.learnit.payloads.JwtAuthRequest;
import com.learnit.payloads.JwtAuthResponse;
import com.learnit.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	@Autowired ModelMapper modelMapper;

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody JwtAuthRequest request) throws Exception {
		JwtAuthResponse loggedinUser = this.userService.loginUser(request);
		return new ResponseEntity<JwtAuthResponse>(loggedinUser, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody AuthUserDto authDto) throws Exception {
		AuthUserDto registerUser = this.userService.registerUser(authDto);
		if(registerUser !=null)
			return new ResponseEntity<AuthUserDto>(registerUser, HttpStatus.CREATED);
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Email already exist",false), HttpStatus.FORBIDDEN);
		
	}
	
	@PostMapping("/changepassword")
	public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		
		boolean result = this.userService.changePassword(changePassword,userName);
		
		if(result)
			return new ResponseEntity<ApiResponse>(new ApiResponse("Password changed successfully",true), HttpStatus.OK);
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Wrong password ! Try againg",false), HttpStatus.OK);
	}
	
	@PostMapping("/send-otp")
	public ResponseEntity<?> sendOtpForForgotPassword(@Param("email")String email) throws Exception {
		String otp = this.userService.sendForgotPasswordOtp(email);
		
		if(otp.length()==6) {
			Map<String, String>response=new HashMap<>();
			response.put("otp", otp);
			response.put("message", "OTP has been sent to "+email);
			return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
		}
		return new ResponseEntity<ApiResponse>(new ApiResponse("Email does not exist! put a valid email",false), HttpStatus.OK);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> changePasswordForForgotPassword(@RequestBody ForgotPassword forgotPassword) throws Exception {
		boolean isChanged = this.userService.forgotPassword(forgotPassword);
		if(isChanged)
			return new ResponseEntity<ApiResponse>(new ApiResponse("Password changed successfully!! please login",true), HttpStatus.OK);
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Something went wrong! try again",false), HttpStatus.OK);
	}
	
	
	//post image upload
	@PatchMapping("/image/upload")
	public ResponseEntity<AuthUserDto>uploadPostImage(@RequestParam MultipartFile image) throws IOException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		AuthUserDto updatedUser = this.userService.uploadProfilePicture(image, userName);
		return new ResponseEntity<AuthUserDto>(updatedUser,HttpStatus.OK);
	}
}
	
