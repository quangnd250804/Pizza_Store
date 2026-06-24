package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.RoleDao;
import fa.training.pizza_store_api.dao.UserDao;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.UserCreateRequest;
import fa.training.pizza_store_api.dto.UserResponse;
import fa.training.pizza_store_api.model.User;
import fa.training.pizza_store_api.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fa.training.pizza_store_api.model.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PageResponse<UserResponse> getAllUsers(int page, int limit) {
        List<User> users = userDao.findAll(page, limit);
        int totalElements = userDao.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());

        return new PageResponse<>(userResponses, page, totalPages, totalElements);
    }

    public void createStaff(UserCreateRequest request) {
        if (userDao.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userDao.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDob(request.getDob());

        String roleName = request.getRoleName() != null ? request.getRoleName() : RoleEnum.KITCHEN.getRoleName();
        userDao.save(user, roleName);
    }

    public void updateUserStatus(int id, boolean isActive) {
        userDao.updateStatus(id, isActive);
    }

    public void updateUserRoles(int id, List<Integer> roleIds) {
        userDao.updateRoles(id, roleIds);
    }

    @Autowired
    private RoleDao roleDao;

    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }
}
