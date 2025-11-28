package com.learnwithiftekhar.auth_demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeStorageService {

    //TODO
    String uploadResume(Long userId, MultipartFile file);
}