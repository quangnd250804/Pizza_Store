package fa.training.pizza_store_api.dto;

import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDateTime;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    @NotBlank(message = "Password không được để trống")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", message = "Password phải chứa ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt")
    private String password;
    @NotBlank(message = "Full name không được để trống")
    private String fullName;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Phone number không được để trống")
    private String phoneNumber;
    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDateTime dob;
}
