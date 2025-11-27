package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.entity.Company;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendSimpleMail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("alser5846@gmail.com");
            message.setSubject("Confirm your email");
            String body = """

                    Hello from UniTalent Team!
                    Please use the following link to verify your email:

                    http://localhost:8080/api/auth/confirmToken?token=%s
                    """.formatted(token);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalIdentifierException("Failed to send email");
        }
    }

    @Async
    public void sendEmployerApprovalNotification(String to, Company employer) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("alser5846@gmail.com");
            message.setSubject("New employer registration – approval needed");

            String body = """
                    New employer has registered on UniTalent.

                    Company: %s
                    BIN: %s
                    Phone: %s
                    Website: %s

                    Contact email (user): %s

                    To approve this employer, call API:
                    POST http://localhost:8080/api/employers/%d/approve

                    Or open your admin panel and approve there.
                    """
                    .formatted(
                            employer.getCompanyName(),
                            employer.getBin(),
                            employer.getUser().getPhoneNumber(),
                            employer.getWebsite(),
                            employer.getUser().getEmail(),
                            employer.getUser().getId()
                    );

            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalIdentifierException("Failed to send employer approval email");
        }
    }
}
