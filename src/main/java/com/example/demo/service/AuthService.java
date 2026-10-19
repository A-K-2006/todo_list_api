package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService ;
    private final AuthenticationManager authenticationManager ;
    private final com.example.demo.security.CustomUserDetailsService customUserDetailsService ;

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists") ;
        }
        User user = User.builder().name(request.getName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user) ;
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail()) ;
        return new AuthResponse(jwtService.generateToken(userDetails), "User registered successfully") ;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())) ;
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail()) ;
        return new AuthResponse(jwtService.generateToken(userDetails) , "Login successfull") ;
    }
}
