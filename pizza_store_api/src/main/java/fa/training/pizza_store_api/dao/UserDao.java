package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.model.Role;
import fa.training.pizza_store_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        String sql = "SELECT * FROM users WHERE id = ?";
        try{
            User u = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setActive(rs.getBoolean("is_active"));
                user.setDeleted(rs.getBoolean("is_deleted"));
                user.setDob(rs.getTimestamp("dob")!= null ? rs.getTimestamp("dob").toLocalDateTime() : null);
                user.setCreatedAt(rs.getTimestamp("created_at")!= null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setPhoneNumber(rs.getString("phone_number"));
                return user;
            }, id);
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
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = 1 AND is_deleted = 0";
        try{
            User u = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setActive(rs.getBoolean("is_active"));
                user.setDeleted(rs.getBoolean("is_deleted"));
                user.setDob(rs.getTimestamp("dob")!= null ? rs.getTimestamp("dob").toLocalDateTime() : null);
                user.setCreatedAt(rs.getTimestamp("created_at")!= null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setPhoneNumber(rs.getString("phone_number"));
                return user;
            }, username);
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
}
