package com.learnwithiftekhar.auth_demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeStorageService {

    /**
     * Сохранить резюме и вернуть публичный URL.
     * Сейчас можно сделать заглушку, потом подключить S3.
     */
    String uploadResume(Long userId, MultipartFile file);
}