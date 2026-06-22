package fa.training.pizza_store_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dob;
    private boolean isActive;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private Set<Role> roles;
}
