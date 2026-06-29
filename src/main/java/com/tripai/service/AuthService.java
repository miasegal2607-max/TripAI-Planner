package com.tripai.service;
import com.tripai.dto.request.*;
import com.tripai.dto.response.AuthResponse;
import com.tripai.model.User;
import com.tripai.repository.UserRepository;
import com.tripai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthResponse register(RegisterRequest r) {
        if (userRepository.existsByEmail(r.getEmail())) throw new IllegalArgumentException("Email already in use");
        User u = User.builder().email(r.getEmail()).password(passwordEncoder.encode(r.getPassword())).name(r.getName()).build();
        userRepository.save(u);
        return AuthResponse.builder().token(jwtUtil.generateToken(u)).userId(u.getId()).name(u.getName()).email(u.getEmail()).build();
    }
    public AuthResponse login(LoginRequest r) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(r.getEmail(), r.getPassword()));
        User u = userRepository.findByEmail(r.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return AuthResponse.builder().token(jwtUtil.generateToken(u)).userId(u.getId()).name(u.getName()).email(u.getEmail()).build();
    }
}
