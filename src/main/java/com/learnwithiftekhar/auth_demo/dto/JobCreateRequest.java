package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobCreateRequest {

    private String title;
    private String description;
    private String location;
    private String employmentType;
    private Integer minSalary;
    private Integer maxSalary;
}

