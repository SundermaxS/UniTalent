package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.Role;
import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.repository.CompanyRepository;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService{

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final String ADMIN_EMAIL = "alser5846@gmail.com";

    @Transactional
    public void registerEmployer(EmployerRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        Company employer = Company.builder()
                .bin(request.getBin())
                .companyName(request.getCompanyName())
                .website(request.getWebsite())
                .description(request.getDescription())
                .approved(false)
                .user(user)
                .build();

        companyRepository.save(employer);

        // уведомление админу: кого надо рассмотреть
        emailService.sendEmployerApprovalNotification(ADMIN_EMAIL, employer);
    }

    @Transactional
    public void approveEmployer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company employer = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        employer.setApproved(true);
        user.setRole(Role.EMPLOYER);
    }

    public List<Company> getPendingEmployers() {
        return companyRepository.findByApprovedFalse();
    }
}

