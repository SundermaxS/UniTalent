package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

@Data
public class StudentSummaryResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private String programClass;
    private String gpa;
    private String skills;

    private String resumeUrl;
}
