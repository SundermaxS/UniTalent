package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;

    private Long candidateId;
    private String candidateEmail;

    private String resumeUrl;
    private String coverLetter;

    private String status;
    private LocalDateTime createdAt;
}
