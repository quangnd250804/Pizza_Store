package fa.training.pizza_store_api.dto;

import fa.training.pizza_store_api.model.Role;
import fa.training.pizza_store_api.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dob;
    private boolean isActive;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private Set<Role> roles;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDob(user.getDob());
        response.setActive(user.isActive());
        response.setDeleted(user.isDeleted());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(user.getRoles());
        return response;
    }
}
