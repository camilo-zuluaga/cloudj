package com.cloudj.backend.service;

import com.cloudj.backend.auth.util.JwtUtil;
import com.cloudj.backend.auth.util.TokenWithExpiration;
import com.cloudj.backend.domain.RefreshToken;
import com.cloudj.backend.domain.User;
import com.cloudj.backend.dto.request.CustomUserDetails;
import com.cloudj.backend.dto.request.LoginRequest;
import com.cloudj.backend.dto.request.RegisterRequest;
import com.cloudj.backend.dto.response.AuthResponse;
import com.cloudj.backend.dto.response.MessageResponse;
import com.cloudj.backend.exceptions.AuthException;
import com.cloudj.backend.repository.RefreshTokenRepository;
import com.cloudj.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CookieService cookieService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("john", "password", "john@email.com");
        testUser = User.builder()
                .id(1L)
                .email("john@email.com")
                .username("john")
                .password("encoded")
                .build();
    }

    @Test
    void givenValidUserDetails_whenRegistering_thenRegisterUserAndReturnMessage() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        MessageResponse<String> response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.message()).contains("New user registered successfully");
    }

    @Test
    void givenDuplicateUsername_whenRegistering_thenThrowAuthException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AuthException.class);
    }

    @Test
    void givenValidCredentials_whenLoggingIn_thenLogTheUser() {
        LoginRequest loginRequest = new LoginRequest("john", "password");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(userDetails.getUser()).thenReturn(testUser);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("jwt-token");

        TokenWithExpiration tokenWithExpiration = new TokenWithExpiration("refresh-token",
                new Date(System.currentTimeMillis() + 10000));
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn(tokenWithExpiration);

        AuthResponse authResponse = authService.login(loginRequest, response);

        assertThat(authResponse).isNotNull();
        assertEquals("jwt-token", authResponse.accessToken());
        assertEquals("john", authResponse.username());
        verify(cookieService).addHttpOnlyCookie(eq("refreshToken"), eq("refresh-token"), anyInt(), eq(response));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void givenInvalidCredentials_whenLoggingIn_thenThrowAuthException() {
        LoginRequest loginRequest = new LoginRequest("john", "wrong");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> authService.login(loginRequest, response))
                .isInstanceOf(AuthException.class);
    }

    @Test
    void testLogout() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.logout("john", response);

        verify(cookieService).deleteCookie(eq("refreshToken"), eq(response));
        verify(refreshTokenRepository).revokeAllByUsername("john");
    }
}