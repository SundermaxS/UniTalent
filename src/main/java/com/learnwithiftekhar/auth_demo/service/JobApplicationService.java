package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.dto.JobApplicationCreateRequest;
import com.learnwithiftekhar.auth_demo.dto.JobApplicationResponse;
import com.learnwithiftekhar.auth_demo.entity.*;
import com.learnwithiftekhar.auth_demo.entity.JobApplication.ApplicationStatus;
import com.learnwithiftekhar.auth_demo.mapper.JobApplicationMapper;
import com.learnwithiftekhar.auth_demo.repository.JobApplicationRepository;
import com.learnwithiftekhar.auth_demo.repository.JobRepository;
import com.learnwithiftekhar.auth_demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final JobApplicationMapper jobApplicationMapper;

    // студент откликается на вакансию
    public JobApplicationResponse apply(JobApplicationCreateRequest dto, UserDetails principal) {
        String email = principal.getUsername();

        User candidate = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new IllegalStateException("Job not found"));

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .resumeUrl(null) // позже подставим URL из S3
                .coverLetter(dto.getCoverLetter())
                .createdAt(LocalDateTime.now())
                .status(ApplicationStatus.PENDING)
                .build();

        jobApplicationRepository.save(application);

        return jobApplicationMapper.toDto(application);
    }

    // студент смотрит свои отклики
    public List<JobApplicationResponse> getMyApplications(UserDetails principal) {
        String email = principal.getUsername();

        User candidate = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return jobApplicationRepository.findByCandidate(candidate)
                .stream()
                .map(jobApplicationMapper::toDto)
                .toList();
    }

    // работодатель смотрит отклики на свои вакансии
    public List<JobApplicationResponse> getApplicationsForMyJobs(UserDetails principal) {
        String email = principal.getUsername();

        User employer = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Company company = companyRepository.findByUser(employer)
                .orElseThrow(() -> new IllegalStateException("Company not found for this user"));

        List<Job> myJobs = jobRepository.findByCompany(company);

        return jobApplicationRepository.findByJobIn(myJobs)
                .stream()
                .map(jobApplicationMapper::toDto)
                .toList();
    }
}
