package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<String> registerEmployer(@RequestBody EmployerRegisterRequest request) {
        companyService.registerEmployer(request);
        return ResponseEntity.ok(
                "Ваш запрос работодателя отправлен. После одобрения админом ваша роль станет EMPLOYER."
        );
    }

    public record PendingEmployerDto(
            Long userId,
            Long employerId,
            String companyName,
            String bin,
            String email,
            String phoneNumber,
            boolean approved
    ) {}

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PendingEmployerDto>> getPendingEmployers() {
        List<Company> pending = companyService.getPendingEmployers();

        List<PendingEmployerDto> dtos = pending.stream()
                .map(e -> new PendingEmployerDto(
                        e.getUser().getId(),
                        e.getId(),
                        e.getCompanyName(),
                        e.getBin(),
                        e.getUser().getEmail(),
                        e.getUser().getPhoneNumber(),
                        e.isApproved()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveEmployer(@PathVariable Long userId) {
        companyService.approveEmployer(userId);
        return ResponseEntity.ok("Работодатель одобрен, роль пользователя изменена на EMPLOYER.");
    }
}
