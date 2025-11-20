package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

@Data
public class EmployerRegisterRequest {

    // контактное лицо
    private String firstName;
    private String lastName;
    private String username;

    private String email;
    private String password;

    private String phoneNumber; // на будущее, если пригодится

    // данные компании
    private String bin;
    private String companyName;
    private String website;
    private String description;
}
