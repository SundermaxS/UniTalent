package com.learnwithiftekhar.auth_demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnwithiftekhar.auth_demo.dto.UserRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Role;
import com.learnwithiftekhar.auth_demo.entity.User;
import com.learnwithiftekhar.auth_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

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

    public User registerUser(UserRegisterRequest request) {
        RestTemplate rest = new RestTemplate();
        Map<String, String> body = Map.of(
                "student_id", request.getEmail().substring(0, 9),
                "password", request.getPassword()
        );

        String pythonUrl = "http://localhost:5000/get-user-data";

        Map response = rest.postForObject(pythonUrl, body, Map.class);

        User student;
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            student = userRepository.findByEmail(request.getEmail()).get();
        }

        try {
            String fullName = (String) response.get("fullname");
            String[] nameParts = fullName.split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            Object scheduleObj = response.get("schedule");
            String scheduleJson = scheduleObj != null
                    ? objectMapper.writeValueAsString(scheduleObj)
                    : null;

            String email = request.getEmail();
            String safeUsername = email.length() >= 9
                    ? email.substring(0, 9)
                    : email;

            student = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .username(safeUsername)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(email)
                    .phoneNumber((String) response.get("contact_number"))
                    .schedule(scheduleJson)
                    .programClass((String) response.get("program_class"))
                    .transcript((String) response.get("transcript_print_html"))
                    .role(Role.USER)
                    .enabled(true)
                    .locked(false)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Cannot process user registration", e);
        }

        userRepository.save(student);
        return student;
    }
}