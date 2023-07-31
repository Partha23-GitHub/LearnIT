package com.learnit.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.learnit.payloads.ApiResponse;

import io.jsonwebtoken.SignatureException;


@RestControllerAdvice
public class GlobalExceptionHandler{
	
	//wrong Http Method handlers
		@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
		public static ResponseEntity<Map<String,String>>httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
			Map<String,String>httpMethodErrorMap=new HashMap<>();
			httpMethodErrorMap.put("error", ex.getMessage());
			return new ResponseEntity<Map<String,String>>(httpMethodErrorMap,HttpStatus.BAD_REQUEST);
		}
	
	//Resource Not found Exception Handler
	@ExceptionHandler(ResourseNotFoundException.class)
	public static ResponseEntity<ApiResponse>resourceNotFoundExceptionHandler(ResourseNotFoundException ex){
		ApiResponse apiResponse=new ApiResponse(ex.getMessage(),false);
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.NOT_FOUND);
	}
	
	//Add & Update user validate Exception Handler
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public static ResponseEntity<Map<String,String>>handleMethodArgsNotValidException(MethodArgumentNotValidException ex){
		Map<String,String>validFeildResponseMap=new HashMap<>();
		
		//bind all exceptions and traverse then extract the field and default message the put it into response map
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName=((FieldError)error).getField();
			String meaage=error.getDefaultMessage();
			validFeildResponseMap.put(fieldName,meaage);
		});
		return new ResponseEntity<Map<String,String>>(validFeildResponseMap,HttpStatus.BAD_REQUEST);
	}
	
	//wrong end point handlers
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public static ResponseEntity<Map<String,String>>methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
		Map<String,String>urlErrorMap=new HashMap<>();
		urlErrorMap.put("error", ex.getMessage());
		return new ResponseEntity<Map<String,String>>(urlErrorMap,HttpStatus.BAD_REQUEST);
	}
	// missing body exception
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public static ResponseEntity<Map<String,String>>httpMessageNotReadableException(HttpMessageNotReadableException ex){
		Map<String,String>httpMessageNotReadableException=new HashMap<>();
		httpMessageNotReadableException.put("error", "Request body is missing");
		return new ResponseEntity<Map<String,String>>(httpMessageNotReadableException,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public static ResponseEntity<Map<String,String>>httpMessageNotQuniueKeyException(SQLIntegrityConstraintViolationException ex){
		Map<String,String>httpMessageNotQuniueKeyException=new HashMap<>();
		httpMessageNotQuniueKeyException.put("error", "email already exist");
		return new ResponseEntity<Map<String,String>>(httpMessageNotQuniueKeyException,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApiException.class)
	public static ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public static ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public static ResponseEntity<ApiResponse> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(Exception.class)
	public static ResponseEntity<ApiResponse> jwtException(Exception ex) {
		ApiResponse apiResponse = new ApiResponse("Jwt Token is not valid", false);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(SignatureException.class)
	public static ResponseEntity<ApiResponse> signatureException(SignatureException ex) {
		ApiResponse apiResponse = new ApiResponse("Jwt Token is not valid", false);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
}
