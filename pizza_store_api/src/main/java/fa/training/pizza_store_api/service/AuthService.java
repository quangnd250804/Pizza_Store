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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String register(RegisterRequest registerRequest) {
        if (userDao.existsByUsername(registerRequest.getUsername())) {
            log.error("Registration failed: Username {} already exists", registerRequest.getUsername());
            throw new AppException(400, "Tên đăng nhập đã tồn tại trong hệ thống");
        }

        if (userDao.existsByEmail(registerRequest.getEmail())) {
            log.error("Registration failed: Email {} already exists", registerRequest.getEmail());
            throw new AppException(400, "Email đã tồn tại trong hệ thống");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setDob(registerRequest.getDob());
        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());

        log.info("Registering new user with username: {}", registerRequest.getUsername());

        //Mặc định đăng ký từ web sẽ có role là CUSTOMER, nếu muốn đăng ký nhân viên thì phải có api riêng và chỉ admin mới được phép gọi
        userDao.save(user, "CUSTOMER");
        return "Đăng ký thành công";
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt  = jwtTokenProvider.generateToken(authentication);
            log.info("JWT: {}", jwt);

            // 1. Sinh Refresh Token
            String refreshToken = UUID.randomUUID().toString();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("Refresh token: {}", refreshToken);

            // 2. Lưu vào DB với hạn dùng 7 ngày
            Instant expiryDate = Instant.now().plusMillis(jwtTokenProvider.getRefreshExpiration());
            refreshTokenDao.save(userDetails.getUser().getId(), refreshToken, expiryDate);

            return new AuthResponse(jwt, refreshToken);
        } catch (BadCredentialsException e) {
            // Bắtlỗi nhập sai tài khoản hoặc mật khẩu
            log.error("Authentication failed for username: {}", loginRequest.getUsername(), e);
            throw new AppException(401, "Tên đăng nhập hoặc mật khẩu không chính xác!");

        } catch (Exception e) {
            // Bắt các lỗi hệ thống khác (Lỗi DB, lỗi NullPointer, kết nối mạng...)
            log.error("An error occurred during authentication for username: {}", loginRequest.getUsername(), e);
            throw new AppException(500, "Hệ thống đăng nhập đang gặp sự cố kỹ thuật. Vui lòng thử lại sau!");
        }
    }

    public boolean logout(String token) {
        int rowDelete = refreshTokenDao.deleteByToken(token);

        return rowDelete > 0;
    }
}
