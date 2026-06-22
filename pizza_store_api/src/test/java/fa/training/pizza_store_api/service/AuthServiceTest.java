package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.config.CustomUserDetails;
import fa.training.pizza_store_api.config.JwtTokenProvider;
import fa.training.pizza_store_api.dao.RefreshTokenDao;
import fa.training.pizza_store_api.dao.UserDao;
import fa.training.pizza_store_api.dto.AuthResponse;
import fa.training.pizza_store_api.dto.LoginRequest;
import fa.training.pizza_store_api.dto.RegisterRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserDao userDao;
    @Mock
    private RefreshTokenDao refreshTokenDao;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void regitserSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@gmail.com");
        registerRequest.setPassword("Quang123@");
        registerRequest.setFullName("Nguyen Van A");
        registerRequest.setDob(java.time.LocalDateTime.parse("1990-01-01T00:00:00"));
        registerRequest.setPhoneNumber("0123456789");

        when(userDao.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userDao.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        String result = authService.register(registerRequest);
        assertEquals("Đăng ký thành công", result);
        verify(userDao).save(any(User.class), eq("CUSTOMER"));
    }

    @Test
    void registerUsernameAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@gmail.com");
        registerRequest.setPassword("Quang123@");
        registerRequest.setFullName("Nguyen Van A");
        registerRequest.setDob(java.time.LocalDateTime.parse("1990-01-01T00:00:00"));
        registerRequest.setPhoneNumber("0123456789");

        when(userDao.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.register(registerRequest));
        assertEquals("Tên đăng nhập đã tồn tại trong hệ thống", exception.getMessage());
        verify(userDao, never()).save(any(), any());
    }

    @Test
    void registerEmailAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@gmail.com");
        registerRequest.setPassword("Quang123@");
        registerRequest.setFullName("Nguyen Van A");
        registerRequest.setDob(java.time.LocalDateTime.parse("1990-01-01T00:00:00"));
        registerRequest.setPhoneNumber("0123456789");

        when(userDao.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userDao.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.register(registerRequest));
        assertEquals("Email đã tồn tại trong hệ thống", exception.getMessage());
        verify(userDao, never()).save(any(), any());
    }

    @Test
    void loginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Quang123@");

        User user = new User();
        user.setId(1);
        user.setUsername("admin");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(jwtTokenProvider.getRefreshExpiration()).thenReturn(7L * 24 * 60 * 60 * 1000);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());

        verify(authenticationManager).authenticate(any());
        verify(jwtTokenProvider).generateToken(authentication);
        verify(refreshTokenDao).save(eq(1), anyString(), any(Instant.class));
    }

    @Test
    void loginBadCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AppException exception = assertThrows(AppException.class, () -> authService.login(loginRequest));
        assertEquals("Tên đăng nhập hoặc mật khẩu không chính xác!", exception.getMessage());

        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenDao, never()).save(anyInt(), anyString(), any());
    }

    @Test
    void loginSystemError() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Quang123@");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("System error"));

        AppException exception = assertThrows(AppException.class, () -> authService.login(loginRequest));
        assertEquals("Hệ thống đăng nhập đang gặp sự cố kỹ thuật. Vui lòng thử lại sau!", exception.getMessage());

        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenDao, never()).save(anyInt(), anyString(), any());
    }

    @Test
    void logoutSuccess() {
        when(refreshTokenDao.deleteByToken("token-123")).thenReturn(1);

        boolean result = authService.logout("token-123");

        assertTrue(result);
        verify(refreshTokenDao).deleteByToken("token-123");
    }

    @Test
    void logoutFailed() {
        when(refreshTokenDao.deleteByToken("token-123")).thenReturn(0);

        boolean result = authService.logout("token-123");

        assertFalse(result);
        verify(refreshTokenDao).deleteByToken("token-123");
    }
}