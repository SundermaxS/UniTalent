package com.learnwithiftekhar.auth_demo.service;

import com.learnwithiftekhar.auth_demo.dto.UserRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Role;
import com.learnwithiftekhar.auth_demo.entity.Token;
import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                !user.isLocked(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public Optional<User> findUser(String email){
        return userRepository.findByEmail(email);
    }

    // 🔹 РЕГИСТРАЦИЯ через DTO
    public User registerUser(UserRegisterRequest request) {
        // Проверка: email должен быть уникальным
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalStateException("User with this email already exists");
                });

        // Собираем пользователя из DTO
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)     // пока одна роль, потом можно разделить STUDENT/EMPLOYER
                .enabled(false)
                .locked(false)
                .build();

        userRepository.save(user);

        // Генерируем токен подтверждения и отправляем письмо
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

    @Transactional
    public boolean confirmToken(String token) {
        Token confirmationToken = tokenService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("User already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        } else {
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