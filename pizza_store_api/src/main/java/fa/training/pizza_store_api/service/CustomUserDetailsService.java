package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.config.CustomUserDetails;
import fa.training.pizza_store_api.dao.UserDao;
import fa.training.pizza_store_api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println(">>> LỖI: Không tìm thấy username này trong DB!");
                    return new UsernameNotFoundException("Không tìm thấy người dùng");
                });

        log.info("User found: {}", user.getUsername());
        log.info("User roles: {}", user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toList()));

        return new CustomUserDetails(user);
    }
}
