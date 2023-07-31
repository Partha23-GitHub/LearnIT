package com.learnit.servicesImpl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.learnit.exceptions.ResourseNotFoundException;
import com.learnit.models.Course;
import com.learnit.models.User;
import com.learnit.repository.CourseRepository;
import com.learnit.repository.UserRepository;
import com.learnit.services.EmailService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class Razorpay {
	@Value("${razorpay.api_key}")
	private String razorpay_key;
	
	@Value("${razorpay.api_secret}")
	private String razorpay_secret;
	
	@Autowired private UserRepository userRepository;
	@Autowired private CourseRepository courseRepository;
	@Autowired private EmailService emailService;
	
	public boolean enrollUserInCourse(String email,long courseId) {
		User user = this.userRepository.findByEmail(email);
		Course course = this.courseRepository.findById(courseId).orElseThrow(()->new ResourseNotFoundException("No Course Found"));
		
		boolean added = user.getEnrolledCourses().add(course);
		this.userRepository.save(user);
		course.getEnrolledUsers().add(user);
		this.courseRepository.save(course);
		if(!added)
			return false;
		// ***********
				String subject="Course Enrollment Confirmation!";
				String body="<html>\r\n"
						+ "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 0;\">\r\n"
						+ "\r\n"
						+ "    <div style=\"background-color: #f3f3f3; padding: 20px;\">\r\n"
						+ "        <h2 style=\"color: #007bff;\">Course Enrollment Confirmation</h2>\r\n"
						+ "        <p>Hello "+user.getName()+",</p>\r\n"
						+ "        <p>Thank you for enrolling in the course <b>"+course.getTitle()+"</b>.</p>\r\n"
						+ "        <p>You are now a part of an exciting learning journey with our instructor <b>"+course.getInstructor().getName()+"</b>.</p>\r\n"
						+ "        <p>We hope you enjoy the course and find it valuable for your learning goals.</p>\r\n"
						+ "        <p>If you have any questions or need assistance, feel free to reach out to our support team.</p>\r\n"
						+ "        <p>Best regards,<br>\r\n"
						+ "        The LearnIT Team</p>\r\n"
						+ "    </div>\r\n"
						+ "\r\n"
						+ "</body>\r\n"
						+ "</html>\r\n";
				
				this.emailService.sendMail(user.getEmail(), subject, body);
		
		return true;
	}
	
	public String createOrder(Double coursePrice) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpay_key, razorpay_secret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", coursePrice*100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());

            
            Order order = razorpayClient.orders.create(orderRequest);
            return order.toString();
            
        } catch (RazorpayException e) {
        	
            return null;
        }
    }
}
