package com.learnwithiftekhar.auth_demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StubResumeStorageService implements ResumeStorageService {

    @Override
    public String uploadResume(Long userId, MultipartFile file) {
        // TODO: заменить на реальную S3-реализацию
        // временно возвращаем просто фиктивный URL
        return null; // или "https://unitalent.local/resumes/" + userId + "/" + file.getOriginalFilename();
    }
}
