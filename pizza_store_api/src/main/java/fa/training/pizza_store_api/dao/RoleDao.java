package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Role> getRolesByUserId(int userId) {
        String sql = "SELECT r.* FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";

        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Role.class), userId);
    }

    //Tìm kiếm role id theo role name
    public Integer findRoleIdByRoleName(String roleName) {
        String sql = "SELECT id FROM roles WHERE role_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, roleName);
    }

    public List<Role> getAllRoles() {
        String sql = "SELECT * FROM roles";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Role.class));
    }
}
