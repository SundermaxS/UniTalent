package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

@Data
public class EmployerRegisterRequest {

    private String firstName;
    private String lastName;
    private String username;

    private String email;
    private String password;

    private String phoneNumber;

    private String bin;
    private String companyName;
    private String website;
    private String description;
}
