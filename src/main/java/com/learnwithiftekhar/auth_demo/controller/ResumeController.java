package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import com.learnwithiftekhar.auth_demo.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @PostMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadMyResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal
    ) {
        try {
            User currentUser;
            if(userRepository.findByEmail(principal.getUsername()).isPresent()) {
                currentUser = userRepository.findByEmail(principal.getUsername()).get();
            }else{
                return ResponseEntity.notFound().build();
            }

            if (currentUser.getResumePublicId() != null) {
                cloudinaryService.deleteFile(currentUser.getResumePublicId());
                currentUser.setResumePublicId(null);
                currentUser.setResumeUrl(null);
            }

            Map result = cloudinaryService.uploadResume(file);
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            currentUser.setResumeUrl(url);
            currentUser.setResumePublicId(publicId);
            userRepository.save(currentUser);

            return ResponseEntity.ok(Map.of(
                    "message", "Резюме успешно загружено",
                    "resumeUrl", url
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки резюме");
        }
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMyResume(
            @RequestParam("file") MultipartFile file,
            UserDetails principal
    ) {
        return uploadMyResume(file, principal);
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMyResume(@AuthenticationPrincipal UserDetails principal) {
        try {
            User currentUser;
            if(userRepository.findByEmail(principal.getUsername()).isPresent()) {
                currentUser = userRepository.findByEmail(principal.getUsername()).get();
            }else{
                return ResponseEntity.notFound().build();
            }

            if (currentUser.getResumePublicId() == null) {
                return ResponseEntity.badRequest().body("У вас ещё нет загруженного резюме");
            }

            cloudinaryService.deleteFile(currentUser.getResumePublicId());

            currentUser.setResumePublicId(null);
            currentUser.setResumeUrl(null);
            userRepository.save(currentUser);

            return ResponseEntity.ok("Ваше резюме удалено");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка при удалении резюме");
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public ResponseEntity<?> getUserResume(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getResumeUrl() == null) {
            return ResponseEntity.badRequest().body("У этого пользователя ещё нет резюме");
        }

        return ResponseEntity.ok(Map.of(
                "resumeUrl", user.getResumeUrl()
        ));

    }
}
