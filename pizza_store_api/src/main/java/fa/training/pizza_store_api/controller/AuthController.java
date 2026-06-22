package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.config.JwtTokenProvider;
import fa.training.pizza_store_api.dao.RefreshTokenDao;
import fa.training.pizza_store_api.dao.UserDao;
import fa.training.pizza_store_api.dto.*;
import fa.training.pizza_store_api.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenDao refreshTokenDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Valid
    public ApiResponse<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Received registration request for username: {}", registerRequest.getUsername());
        String message = authService.register(registerRequest);

        log.info("User {} registered successfully", registerRequest.getUsername());
        return ApiResponse.success(message);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for username: {}", loginRequest.getUsername());
        AuthResponse authResponse = authService.login(loginRequest);
        log.info("User {} authenticated successfully", loginRequest.getUsername());
        return ApiResponse.success(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request){
        String requestRefreshToken = request.getRefreshToken();
        log.info("Received refresh token request for username: {}", requestRefreshToken);

        return refreshTokenDao.findByToken(requestRefreshToken)
                .map(tokenMap -> {
                    // Kiểm tra hết hạn
                    Timestamp expiryDate = (Timestamp) tokenMap.get("expiry_date");
                    if (expiryDate.toInstant().isBefore(Instant.now())) {
                        return ResponseEntity.status(401).body(ApiResponse.builder().code(401).message("Refresh token đã hết hạn. Vui lòng đăng nhập lại!").build());
                    }

                    // Lấy thông tin User để sinh AccessToken mới
                    int userId = (int) tokenMap.get("user_id");
                    // Giả định bạn có hàm lấy username từ userId hoặc viết 1 query nhỏ
                    String username = userDao.getUserById(userId)
                            .map(user -> user.getUsername())
                            .orElseThrow(() -> new IllegalStateException("User không tồn tại!"));

                    // Sinh AccessToken mới (Hạn ngắn 15 phút)
                    String newAccessToken = jwtTokenProvider.generateTokenFromUsername(username);

                    return ResponseEntity.ok(ApiResponse.success(new AuthResponse(newAccessToken, requestRefreshToken)));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(ApiResponse.builder().code(401).message("Refresh token không tồn tại!").build()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request){
        log.info("Received logout request for refresh token: {}", request.getRefreshToken());
        boolean isLoggedOut = authService.logout(request.getRefreshToken());

        if (isLoggedOut) {
            return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công"));
        } else {
            return ResponseEntity.status(400).body(ApiResponse.builder()
                    .code(400)
                    .message("Đăng xuất thất bại! Refresh token không tồn tại hoặc đã bị xóa.")
                    .build());
        }
    }
}
