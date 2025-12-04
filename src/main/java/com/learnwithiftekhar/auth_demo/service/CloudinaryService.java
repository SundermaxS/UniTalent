package com.learnwithiftekhar.auth_demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map uploadResume(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        long maxSize = 5L * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Резюме не должно быть больше 5 МБ");
        }

        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "unitalent/resumes",
                        "resource_type", "raw",
                        "use_filename", true,
                        "unique_filename", true
                )
        );
    }

    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "raw")
        );
    }
}
