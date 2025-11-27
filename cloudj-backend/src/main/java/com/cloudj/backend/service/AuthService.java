package com.cloudj.backend.service;

import com.cloudj.backend.auth.security.TokenWithExpiration;
import com.cloudj.backend.auth.security.util.JwtUtil;
import com.cloudj.backend.domain.RefreshToken;
import com.cloudj.backend.domain.User;
import com.cloudj.backend.dto.out.AuthResponse;
import com.cloudj.backend.dto.out.MessageResponse;
import com.cloudj.backend.dto.request.CustomUserDetails;
import com.cloudj.backend.dto.request.LoginRequest;
import com.cloudj.backend.dto.request.RefreshTokenRequest;
import com.cloudj.backend.dto.request.RegisterRequest;
import com.cloudj.backend.exceptions.AuthException;
import com.cloudj.backend.exceptions.JWTException;
import com.cloudj.backend.repository.RefreshTokenRepository;
import com.cloudj.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public MessageResponse register(RegisterRequest registerRequest) {

        userRepository.findByUsername(registerRequest.username())
                .ifPresent(user -> {
                    throw new AuthException("Username %s already in use".formatted(registerRequest.username()));
                });

        User user = User.builder()
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
//                .roles(Set.of(new Role("Test")))
                .build();

        userRepository.save(user);

        return new MessageResponse("New user registered successfully", LocalDateTime.now());
    }

    public AuthResponse login(LoginRequest authRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(authentication);
            TokenWithExpiration refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken.token())
                    .expiryDate(refreshToken.expirationDate())
                    .user(user.getUser())
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            return new AuthResponse(jwt, refreshToken.token());
        } catch (Exception e) {
            throw new AuthException("Username or password is not correct");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String token = refreshTokenRequest.refreshToken();

        if (!jwtUtil.validateToken(token)) {
            throw new JWTException("Invalid JWT refresh token");
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> !refreshToken.isRevoked())
                .filter(refreshToken -> refreshToken.getExpiryDate().after(new Date()))
                .orElseThrow(() -> new JWTException("Invalid JWT token"));

        User user = refreshTokenEntity.getUser();
        Authentication authentication = createAuthentication(user);
        String newAccessToken = jwtUtil.generateToken(authentication);

        return new AuthResponse(newAccessToken, token);
    }

    private Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user,
                null,
                user.getAuthorities());
    }

    public void logout(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JWTException("Missing or Invalid authorization header");
        }

        String jwt = authHeader.substring(7);

        if (!jwtUtil.validateToken(jwt)) {
            throw new JWTException("Invalid JWT token");
        }

        String username = jwtUtil.getUsernameFromToken(jwt);
        refreshTokenRepository.revokeAllByUsername(username); // well, logs out of every device
    }
}