package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private Integer minSalary;
    private Integer maxSalary;

    private String companyName;
    private String companyBin;

    private LocalDateTime createdAt;
    private Boolean active;
}
