package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.UserCreateRequest;
import fa.training.pizza_store_api.dto.UserResponse;
import fa.training.pizza_store_api.dto.UserRoleUpdateRequest;
import fa.training.pizza_store_api.dto.UserStatusUpdateRequest;
import fa.training.pizza_store_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ApiResponse.success(userService.getAllUsers(page, limit));
    }

    @PostMapping("/staff")
    public ApiResponse<String> createStaff(@Valid @RequestBody UserCreateRequest request) {
        userService.createStaff(request);
        return ApiResponse.success("Tạo tài khoản nhân viên thành công");
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> updateUserStatus(@PathVariable int id, @RequestBody UserStatusUpdateRequest request) {
        userService.updateUserStatus(id, request.isActive());
        return ApiResponse.success("Cập nhật trạng thái tài khoản thành công");
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<String> updateUserRoles(@PathVariable int id, @Valid @RequestBody UserRoleUpdateRequest request) {
        userService.updateUserRoles(id, request.getRoleIds());
        return ApiResponse.success("Cập nhật quyền thành công");
    }

    @GetMapping("/roles")
    public ApiResponse<Object> getAllRoles() {
        return ApiResponse.success(userService.getAllRoles());
    }
}
