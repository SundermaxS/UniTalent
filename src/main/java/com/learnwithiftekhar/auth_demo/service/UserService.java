package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.entity.Role;
import com.learnwithiftekhar.auth_demo.entity.Token;
import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.mapper.CompanyMapper;
import com.learnwithiftekhar.auth_demo.mapper.UserMapper;
import com.learnwithiftekhar.auth_demo.repository.CompanyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    // новый метод:
    public User registerEmployer(EmployerRegisterRequest dto) {
        // email уникален
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(existing -> {
                    throw new IllegalStateException("User with this email already exists");
                });

        // BIN уникален
        companyRepository.findByBin(dto.getBin())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Company with this BIN already exists");
                });

        // маппим DTO -> User
        User user = userMapper.fromEmployerRegister(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);
        user.setLocked(false);
        user.setRole(Role.USER); // позже можно разделить роли, если захочешь

        userRepository.save(user);

        // маппим DTO -> Company
        Company company = companyMapper.toEntity(dto, user);
        companyRepository.save(company);

        // отправляем токен на почту
        String token = UUID.randomUUID().toString();
        Token confirmationToken = new Token(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        tokenService.save(confirmationToken);
        emailService.sendSimpleMail(user.getEmail(), token);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Создаём Spring Security User, привязывая enabled/locked
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),                    // username = email
                user.getPassword(),                 // already encoded
                user.isEnabled(),                   // enabled
                true,                               // accountNonExpired
                true,                               // credentialsNonExpired
                !user.isLocked(),                   // accountNonLocked
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public Optional<User> findUser(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

    public User registerUser(User user) {
        // check if user with username or email already exist
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalStateException("User already exists");
                });

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setLocked(false);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        Token confirmationToken = new Token(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        tokenService.save(confirmationToken);
        emailService.sendSimpleMail(user.getEmail(), token);
        System.out.println(token);

        return user;
    }


    @Transactional
    public boolean confirmToken(String token) {
        Token confirmationToken = tokenService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if(confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("User already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if(expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }else{
            confirmationToken.setConfirmedAt(LocalDateTime.now());
            tokenService.save(confirmationToken);
            enableUser(confirmationToken.getUser());
            return true;
        }
    }

    private void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
}