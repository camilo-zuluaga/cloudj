package com.cloudj.backend.controller;

import com.cloudj.backend.dto.request.CustomUserDetails;
import com.cloudj.backend.dto.request.LoginRequest;
import com.cloudj.backend.dto.request.RegisterRequest;
import com.cloudj.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(user, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    HttpServletResponse response) {
        authService.logout(customUserDetails.getUsername(), response);
        return ResponseEntity.ok("Logout completed");
    }
}