package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.dto.*;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.service.CompanyService;
import com.learnwithiftekhar.auth_demo.service.EmployerService;
import com.learnwithiftekhar.auth_demo.service.JwtService;
import com.learnwithiftekhar.auth_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> registerEmployer(@RequestBody EmployerRegisterRequest request) {
        companyService.registerEmployer(request);
        return ResponseEntity.ok(
                "Ваш запрос работодателя отправлен. После одобрения админом ваша роль станет EMPLOYER."
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            String jwt = jwtService.generateToken(request.getEmail());

            return ResponseEntity.ok(new AuthResponse(jwt, request.getEmail()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
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

    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<Page<StudentSummaryResponse>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String programClass,
            @RequestParam(required = false) Double gpaMin,
            @RequestParam(required = false) Double gpaMax
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                employerService.getStudentsFiltered(programClass, gpaMin, gpaMax, pageable)
        );
    }

}
