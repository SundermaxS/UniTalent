package com.learnwithiftekhar.auth_demo.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String email;
    private String password;
}
