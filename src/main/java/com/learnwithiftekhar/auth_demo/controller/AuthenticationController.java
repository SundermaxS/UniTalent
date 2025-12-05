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

    @PostMapping("/login")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        userService.registerUser(request);
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out (remove token on client side)");
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test JWT");
    }
}