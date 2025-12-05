package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.dto.JobApplicationResponse;
import com.learnwithiftekhar.auth_demo.dto.StudentSummaryResponse;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.service.CompanyService;
import com.learnwithiftekhar.auth_demo.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final CompanyService companyService;
    private final EmployerService employerService;

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

    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<Page<StudentSummaryResponse>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(employerService.getStudents(pageable));
    }

    // ✅ пригласить студента на конкретную вакансию работодателя
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @PostMapping("/jobs/{jobId}/invite/{studentId}")
    public ResponseEntity<JobApplicationResponse> inviteStudent(
            @PathVariable Long jobId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(
                employerService.inviteStudentToJob(jobId, studentId, principal)
        );
    }
}
