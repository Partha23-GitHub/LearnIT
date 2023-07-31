package com.learnit.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourseNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	String message;
	 public ResourseNotFoundException(String message) {
		super(message);
		this.message=message;
	}
}
