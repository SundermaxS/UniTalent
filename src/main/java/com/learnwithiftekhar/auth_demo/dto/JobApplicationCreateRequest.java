package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;


@Data
public class JobApplicationCreateRequest {
    private Long jobId;
    private String coverLetter;
    // resumeUrl пока не передаём — в будущем подтянем из S3
}

