package com.learnit.servicesImpl;

import java.text.DecimalFormat;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learnit.exceptions.ApiException;
import com.learnit.models.User;
import com.learnit.payloads.ApiResponse;
import com.learnit.payloads.AuthUserDto;
import com.learnit.payloads.ChangePassword;
import com.learnit.payloads.ForgotPassword;
import com.learnit.payloads.JwtAuthRequest;
import com.learnit.payloads.JwtAuthResponse;
import com.learnit.repository.UserRepository;
import com.learnit.security.JwtTokenHelper;
import com.learnit.services.EmailService;
import com.learnit.services.UserService;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired private UserRepository userRepository;
	@Autowired private ModelMapper modelMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired ImageProcessing imageProcessing;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired private EmailService emailService;
	
	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public AuthUserDto registerUser(AuthUserDto authUser) {
		User oldUser = this.userRepository.findByEmail(authUser.getEmail());
		if(oldUser !=null) // if user already exist
			return null;
		User user = this.modelMapper.map(authUser, User.class);
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		User registerUser = this.userRepository.save(user);
		
		// ***********
		String subject="Welcome to LearnIT - Get Ready to Learn and Create!";
		String body="<html>\r\n"
				+ "<body>\r\n"
				+ "    <p>\r\n"
				+ "        <strong>Thank You!</strong> You've successfully registered with LearnIT, your one-stop platform for learning and teaching.\r\n"
				+ "    </p>\r\n"
				+ "    <p>\r\n"
				+ "        With LearnIT, the world of knowledge is at your fingertips. Whether you're looking to upskill, pursue a new passion, or share your expertise with others, our platform has it all.\r\n"
				+ "    </p>\r\n"
				+ "    <h3>Here's a quick guide to get you started:</h3>\r\n"
				+ "    <ul>\r\n"
				+ "        <li><strong>Browse Courses:</strong> Discover a wide range of courses created by experts from various fields. Find topics that spark your curiosity and align with your goals.</li>\r\n"
				+ "        <li><strong>Enroll and Learn:</strong> Once you've found a course that interests you, simply enroll, and let the learning begin! Enjoy the flexibility of learning at your own pace.</li>\r\n"
				+ "        <li><strong>Create Your Course:</strong> Are you passionate about something? Share your knowledge by creating your own course. Inspire and educate learners from around the globe.</li>\r\n"
				+ "    </ul><br><br><br>\r\n"
				
				+"<p>If you need any assistance or have any questions, feel free to reach out to our support team at <a href=\"mailto:support@learnit.com\">support@learnit.com</a>.</p>\r\n"
				+ "\r\n"
				+ "    <p>Welcome to LearnIT! Let's embark on this learning journey together and make the most of this incredible platform.</p>\r\n"
				+ "\r\n"
				+ "    <p>Best regards,</p>\r\n"
				+ "    <p>The LearnIT Team</p>"
				+ "</body>\r\n"
				+ "</html>";
		
		this.emailService.sendMail(registerUser.getEmail(), subject, body);
		
		return this.modelMapper.map(registerUser, AuthUserDto.class);
	}
	@Override
	public JwtAuthResponse loginUser(JwtAuthRequest request) throws Exception {
		this.authenticate(request.getEmail(), request.getPassword());
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getEmail());
		String token = this.jwtTokenHelper.generateToken(userDetails);

		JwtAuthResponse response = new JwtAuthResponse();
		response.setToken(token);
		response.setUser(this.modelMapper.map((User) userDetails, AuthUserDto.class));
		return response;
	}
	
	
	private void authenticate(String username, String password) throws Exception {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);

		try {

			this.authenticationManager.authenticate(authenticationToken);

		} catch (BadCredentialsException e) {
			throw new ApiException("Wrong username or password !!");
		}

	}
	@Override
	public boolean changePassword(ChangePassword obj,String userName) {
		User user = this.userRepository.findByEmail(userName);
		
		if(this.passwordEncoder.matches(obj.getOldPassword(),user.getPassword())) {
			user.setPassword(this.passwordEncoder.encode(obj.getNewPassword()));
			this.userRepository.save(user);
			return true;
		}
			
		return false;
	}
	@Override
	public AuthUserDto uploadProfilePicture(MultipartFile image, String email) {
		User user = this.userRepository.findByEmail(email);
		try {
			if(user.getProfilePicture()!=null) {
				String oldImage=user.getProfilePicture().substring(user.getProfilePicture().lastIndexOf("/")+1);
				this.imageProcessing.deleteFileFromS3(oldImage);
			}
			String uploadImageName = this.imageProcessing.uploadToS3(image);
			String profilePicture=this.imageProcessing.getS3VideoUrl(uploadImageName);
			user.setProfilePicture(profilePicture);
		}catch(Exception e) {
			user.setProfilePicture(null);
		}
		User savedUser = this.userRepository.save(user);
		AuthUserDto userDto = this.modelMapper.map(savedUser, AuthUserDto.class);
		return userDto;
	}
	@Override
	public String sendForgotPasswordOtp(String email) {
		try {
			User user = this.userRepository.findByEmail(email);
			String otp=new DecimalFormat("000000").format(new Random().nextInt(999999));
			user.setForgotPasswordOtp(otp);
			this.userRepository.save(user);
			// ***********
			String subject="Reset Your Password - OTP Verification Required!";
			String body="<html>\r\n"
					+ "<head>\r\n"
					+ "    <meta charset=\"UTF-8\">\r\n"
					+ "    <title>Forgot Password - OTP Verification</title>\r\n"
					+ "</head>\r\n"
					+ "<body>\r\n"
					+ "    <h1>Forgot Password - OTP Verification</h1>\r\n"
					+ "    <p>Hello "+user.getName()+",</p>\r\n"
					+ "    <p>We received a request to reset your password. To continue with the password reset process, please enter the OTP (One-Time Password) below:</p>\r\n"
					+ "    <p style=\"font-size: 24px; font-weight: bold;\">"+otp+"</p>\r\n"
					+ "    <p style=\"font-style: italic;\">Please note that this OTP is valid for a single use and will expire after a short period of time for security reasons.</p>\r\n"
					+ "    <p>If you did not request a password reset, you can safely ignore this email.</p>\r\n"
					+ "    <p>Best regards,</p>\r\n"
					+ "    <p>The LearnIT Team</p>\r\n"
					+ "</body>\r\n"
					+ "</html>";
			
			this.emailService.sendMail(email, subject, body);
			
			return otp;
		}catch (Exception e) {
			return "Email does not exist";
		}
	}
	@Override
	public boolean forgotPassword(ForgotPassword forgotPassword) {
		User user=null;
		try {
			user = this.userRepository.findByEmail(forgotPassword.getEmail());
			if(user.getForgotPasswordOtp().equals(forgotPassword.getOtp())){
				user.setPassword(this.passwordEncoder.encode(forgotPassword.getPassword()));
				user.setForgotPasswordOtp(null);
				this.userRepository.save(user);
				return true;
			}
			
		}catch (Exception e) {
			return false;
		}
		return false;
	}

}
