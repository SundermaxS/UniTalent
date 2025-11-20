package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.dto.AuthResponse;
import com.learnwithiftekhar.auth_demo.dto.LoginRequest;
import com.learnwithiftekhar.auth_demo.dto.UserRegisterRequest;
import com.learnwithiftekhar.auth_demo.service.JwtService;
import com.learnwithiftekhar.auth_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // 🔹 РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ (через DTO)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered. Please check your email to confirm.");
    }

    // 🔹 ПОДТВЕРЖДЕНИЕ EMAIL по токену
    @GetMapping("/confirmToken")
    public ResponseEntity<String> confirmToken(@RequestParam("token") String token) {
        boolean result = userService.confirmToken(token);
        if (result) {
            return ResponseEntity.ok("Email confirmed successfully!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid or expired token.");
    }

    // 🔹 ЛОГИН (DTO + JWT в ответе)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            String jwt = jwtService.generateToken(request.getEmail());

            // можно вернуть просто строку, но красивее DTO
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT stateless -> на фронте просто удалить токен из localStorage/cookie
        return ResponseEntity.ok("Logged out (remove token on client side)");
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test JWT");
    }
}