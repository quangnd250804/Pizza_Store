package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.RoleDao;
import fa.training.pizza_store_api.dao.UserDao;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.UserCreateRequest;
import fa.training.pizza_store_api.dto.UserResponse;
import fa.training.pizza_store_api.enums.RoleEnum;
import fa.training.pizza_store_api.model.Role;
import fa.training.pizza_store_api.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getAllUsers_Success() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");

        when(userDao.findAll(1, 10)).thenReturn(Arrays.asList(user1, user2));
        when(userDao.countAll()).thenReturn(2);

        PageResponse<UserResponse> response = userService.getAllUsers(1, 10);

        assertEquals(2, response.getContent().size());
        assertEquals(1, response.getCurrentPage());
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElements());
        assertEquals("user1", response.getContent().get(0).getUsername());
        
        verify(userDao, times(1)).findAll(1, 10);
        verify(userDao, times(1)).countAll();
    }

    @Test
    void createStaff_Success_DefaultRole() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("staff1");
        request.setPassword("password");
        request.setEmail("staff1@test.com");
        request.setFullName("Staff One");
        request.setPhoneNumber("1234567890");
        request.setDob(LocalDateTime.of(2000, 1, 1, 0, 0));

        when(userDao.existsByUsername("staff1")).thenReturn(false);
        when(userDao.existsByEmail("staff1@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.createStaff(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).save(userCaptor.capture(), eq(RoleEnum.KITCHEN.getRoleName()));

        User savedUser = userCaptor.getValue();
        assertEquals("staff1", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("staff1@test.com", savedUser.getEmail());
        assertEquals("Staff One", savedUser.getFullName());
        assertEquals("1234567890", savedUser.getPhoneNumber());
        assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), savedUser.getDob());
    }

    @Test
    void createStaff_Success_CustomRole() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("staff2");
        request.setPassword("password");
        request.setEmail("staff2@test.com");
        request.setRoleName(RoleEnum.CASHIER.getRoleName());

        when(userDao.existsByUsername("staff2")).thenReturn(false);
        when(userDao.existsByEmail("staff2@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.createStaff(request);

        verify(userDao, times(1)).save(any(User.class), eq(RoleEnum.CASHIER.getRoleName()));
    }

    @Test
    void createStaff_UsernameExists_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("staff1");

        when(userDao.existsByUsername("staff1")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createStaff(request));
        assertEquals("Tên đăng nhập đã tồn tại", exception.getMessage());
        
        verify(userDao, never()).save(any(User.class), anyString());
    }

    @Test
    void createStaff_EmailExists_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("staff1");
        request.setEmail("staff1@test.com");

        when(userDao.existsByUsername("staff1")).thenReturn(false);
        when(userDao.existsByEmail("staff1@test.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createStaff(request));
        assertEquals("Email đã tồn tại", exception.getMessage());
        
        verify(userDao, never()).save(any(User.class), anyString());
    }

    @Test
    void updateUserStatus_Success() {
        userService.updateUserStatus(1, true);
        verify(userDao, times(1)).updateStatus(1, true);
    }

    @Test
    void updateUserRoles_Success() {
        List<Integer> roles = Arrays.asList(1, 2);
        userService.updateUserRoles(1, roles);
        verify(userDao, times(1)).updateRoles(1, roles);
    }

    @Test
    void getAllRoles_Success() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("ROLE_ADMIN");

        when(roleDao.getAllRoles()).thenReturn(Collections.singletonList(role));

        List<Role> roles = userService.getAllRoles();

        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0).getRoleName());
        verify(roleDao, times(1)).getAllRoles();
    }
}
