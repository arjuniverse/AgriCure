package com.agricure.service;

import com.agricure.dto.auth.AuthResponse;
import com.agricure.dto.auth.LoginRequest;
import com.agricure.dto.auth.RegisterRequest;
import com.agricure.entity.Role;
import com.agricure.entity.User;
import com.agricure.exception.DuplicateResourceException;
import com.agricure.exception.ResourceNotFoundException;
import com.agricure.repository.UserRepository;
import com.agricure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getEmail(), saved.getRole());
        return AuthResponse.bearer(token, saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return AuthResponse.bearer(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
