package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.model.Role;
import fa.training.pizza_store_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleDao roleDao;

    public Optional<User> getUserById(Integer id) {
        String sql = "SELECT *, is_active AS active, is_deleted AS deleted FROM users WHERE id = ?";
        try{
            User u = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            if(u != null){
                List<Role> roles = roleDao.getRolesByUserId(u.getId());
                u.setRoles(new HashSet<>(roles));
            }

            return Optional.ofNullable(u);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT *, is_active AS active, is_deleted AS deleted FROM users WHERE username = ? AND is_active = 1 AND is_deleted = 0";
        try{
            User u = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
            if(u != null){
                List<Role> roles = roleDao.getRolesByUserId(u.getId());
                u.setRoles(new HashSet<>(roles));
            }

            return Optional.ofNullable(u);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    //Kiểm tra xem username đã tồn tại chưa
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username) > 0;
    }

    //Kiem tra xem email đã tồn tại chưa
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, email) > 0;
    }

    //Lưu thông tin đăng ký user
    @org.springframework.transaction.annotation.Transactional
    public void save(User user, String roleName) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone_number, dob) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDob()
        );

        String getUserIdSql = "SELECT id FROM users WHERE username = ?";
        Integer userId = jdbcTemplate.queryForObject(getUserIdSql, Integer.class, user.getUsername());

        Integer roleId = roleDao.findRoleIdByRoleName(roleName);
        if(roleId == null){
            throw new RuntimeException("Không tồn tại role: " + roleName + "trong hệ thống");
        }

        String insertUserRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        jdbcTemplate.update(insertUserRoleSql, userId, roleId);
    }
    public List<User> findAll(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT *, is_active AS active, is_deleted AS deleted FROM users " +
                "ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), offset, limit);
        for (User u : users) {
            List<Role> roles = roleDao.getRolesByUserId(u.getId());
            u.setRoles(new HashSet<>(roles));
        }
        return users;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void updateStatus(int id, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        jdbcTemplate.update(sql, isActive ? 1 : 0, id);
    }

    @Transactional
    public void updateRoles(int userId, List<Integer> roleIds) {
        // Delete all old roles
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, userId);

        // Insert new roles
        String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        for (Integer roleId : roleIds) {
            jdbcTemplate.update(insertSql, userId, roleId);
        }
    }
}
