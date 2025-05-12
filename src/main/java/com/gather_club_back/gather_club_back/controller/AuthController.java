package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.dto.AuthRequest;
import com.gather_club_back.gather_club_back.dto.JwtAuthenticationResponse;
import com.gather_club_back.gather_club_back.dto.RegisterRequest;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.enums.Role;
import com.gather_club_back.gather_club_back.jwt.JwtTokenProvider;
import com.gather_club_back.gather_club_back.mapper.UserMapper;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        log.info("Login attempt for: {}", authRequest.getUsernameOrEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsernameOrEmail(),
                        authRequest.getPasswordHash()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Валидация входных данных
        if (registerRequest.getPasswordHash() == null || registerRequest.getPasswordHash().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User()
                .setUsername(registerRequest.getUsername())
                .setEmail(registerRequest.getEmail())
                .setPasswordHash(passwordEncoder.encode(registerRequest.getPasswordHash())) // Теперь password не null
                .setRole(Role.ROLE_USER)
                .setCreatedAt(LocalDateTime.now())
                .setIsVerified(false);

        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }
}
