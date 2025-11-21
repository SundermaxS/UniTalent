package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.dto.JobCreateRequest;
import com.learnwithiftekhar.auth_demo.dto.JobResponse;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.Job;
import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.mapper.JobMapper;
import com.learnwithiftekhar.auth_demo.repository.CompanyRepository;
import com.learnwithiftekhar.auth_demo.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final JobMapper jobMapper;

    public JobResponse createJob(JobCreateRequest dto, UserDetails principal) {
        String email = principal.getUsername();

        User user = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Company not found for this user"));

        Job job = jobMapper.toEntity(dto, company);
        jobRepository.save(job);

        return jobMapper.toDto(job);
    }

    public List<JobResponse> getAllActiveJobs() {
        return jobRepository.findByActiveTrue()
                .stream()
                .map(jobMapper::toDto)
                .toList();
    }

    public List<JobResponse> getMyJobs(UserDetails principal) {
        String email = principal.getUsername();

        User user = userService.findUser(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Company not found for this user"));

        return jobRepository.findByCompany(company)
                .stream()
                .map(jobMapper::toDto)
                .toList();
    }
}
