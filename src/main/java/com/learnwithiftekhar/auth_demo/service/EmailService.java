package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.Job;
import com.learnwithiftekhar.auth_demo.entity.User;
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

    @Async
    public void sendJobInvitationNotification(String to, Company company, Job job, User student) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("alser5846@gmail.com");
            message.setSubject("UniTalent – Job invitation");

            String body = """
                Hello %s %s!

                You have been invited to apply for a job on UniTalent.

                Company: %s
                BIN: %s

                Job Title: %s
                Location: %s
                Employment Type: %s
                Salary: %s - %s

                Please login to your UniTalent account to accept or reject this invitation.

                """
                    .formatted(
                            student.getFirstName(),
                            student.getLastName(),
                            company.getCompanyName(),
                            company.getBin(),
                            job.getTitle(),
                            job.getLocation() != null ? job.getLocation() : "-",
                            job.getEmploymentType() != null ? job.getEmploymentType() : "-",
                            job.getMinSalary() != null ? job.getMinSalary() : "-",
                            job.getMaxSalary() != null ? job.getMaxSalary() : "-"
                    );

            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalIdentifierException("Failed to send job invitation email");
        }
    }

}
