package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.service.JwtService;
import com.learnwithiftekhar.auth_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @PostMapping("/register")
    /**TODO
     * здесь есть недочет если пользователь введет в джэйсон enable true то сможет с любой почты регаться
     *  написал возможный вариант решения
     */
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered. Please check your email to confirm.");
    }

    @GetMapping("/confirmToken")
    public ResponseEntity<String> confirmToken(@RequestParam("token") String token) {
        boolean result = userService.confirmToken(token);
        if (result) return ResponseEntity.ok("Email confirmed successfully!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            String jwt = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(jwt); // Возвращаем access token
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        //JWT stateless из за этого "логаут" обычно реализуется на клиенте поэтому не надо писать в спрингсекюрити путь для логаута это обычно для сессий.
        return ResponseEntity.ok("Logged out (just remove token on client side)");
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test JWT");
    }
}