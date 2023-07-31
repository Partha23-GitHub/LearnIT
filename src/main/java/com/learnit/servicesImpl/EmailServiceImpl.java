package com.learnit.servicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.learnit.services.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;
	
	@Override
	@Async
	public void sendMail(String to, String subject, String body) {
		
		try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail,"LearnIT");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body,true);
            
            // Set the content type to "text/html"
            mimeMessageHelper.getMimeMessage().setContent(mimeMessage.getContent(), "text/html; charset=utf-8");
            
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("Some Error");
            e.printStackTrace();
        }
	}

}
