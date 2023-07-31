package com.learnit.payloads;

import lombok.Data;

@Data
public class RazorpayPaymentOrder {
	private final String key="rzp_test_t2XaCRKBVs2xn2"; 
	private String order_id; 
	private long amount;
	private String currency;
	private final String companyName="LearnIT";
	private final String companyLogo="https://learnit-s3.s3.eu-north-1.amazonaws.com/LearnIT_logo.png";
	private final String description = "Happy Learning from LearnIT";
}
