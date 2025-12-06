package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.dto.JobApplicationResponse;
import com.learnwithiftekhar.auth_demo.dto.StudentSummaryResponse;
import com.learnwithiftekhar.auth_demo.entity.*;
import com.learnwithiftekhar.auth_demo.entity.JobApplication.ApplicationStatus;
import com.learnwithiftekhar.auth_demo.mapper.JobApplicationMapper;
import com.learnwithiftekhar.auth_demo.mapper.StudentMapper;
import com.learnwithiftekhar.auth_demo.repository.CompanyRepository;
import com.learnwithiftekhar.auth_demo.repository.JobApplicationRepository;
import com.learnwithiftekhar.auth_demo.repository.JobRepository;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final UserService userService;
    private final EmailService emailService;

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    private final StudentMapper studentMapper;
    private final JobApplicationMapper jobApplicationMapper;

    public Page<StudentSummaryResponse> getStudents(Pageable pageable) {
        return userRepository.findByRole(Role.USER, pageable)
                .map(studentMapper::toDto);
    }

    public JobApplicationResponse inviteStudentToJob(Long jobId, Long studentId, UserDetails principal) {
        String email = principal.getUsername();

        User employerUser = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Company company = companyRepository.findByUser(employerUser)
                .orElseThrow(() -> new IllegalStateException("Company not found for this user"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalStateException("Job not found"));

        // ✅ Проверка что вакансия принадлежит этой компании
        if (!job.getCompany().getId().equals(company.getId())) {
            throw new IllegalStateException("You can invite only to your own jobs");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Student not found"));

        if (student.getRole() != Role.USER) {
            throw new IllegalStateException("Only students can be invited");
        }

        if (jobApplicationRepository.existsByJobAndCandidate(job, student)) {
            throw new IllegalStateException("This student already has an application for this job");
        }

        JobApplication invitation = JobApplication.builder()
                .job(job)
                .candidate(student)
                .resumeUrl(student.getResumeUrl())
                .coverLetter(null)
                .createdAt(LocalDateTime.now())
                .status(ApplicationStatus.PENDING)
                .build();

        jobApplicationRepository.save(invitation);

        emailService.sendJobInvitationNotification(
                student.getEmail(),
                company,
                job,
                student
        );

        return jobApplicationMapper.toDto(invitation);
    }

    public Page<StudentSummaryResponse> getStudentsFiltered(
            String programClass,
            Double gpaMin,
            Double gpaMax,
            Pageable pageable
    ) {
        return userRepository.findStudentsFiltered(programClass, gpaMin, gpaMax, pageable)
                .map(studentMapper::toDto);
    }

}
