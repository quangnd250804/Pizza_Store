package fa.training.pizza_store_api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Repository
public class RefreshTokenDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(int userId, String token, Instant expiryDate){
        // Xóa các token cũ của user này trước khi lưu token mới (Tránh rác DB)
        deleteByUserId(userId);

        String sql = "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, token, Timestamp.from(expiryDate));
    }

    public Optional<Map<String, Object>> findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, token);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteByUserId(int userId) {
        jdbcTemplate.update("DELETE FROM refresh_tokens WHERE user_id = ?", userId);
    }

    public int deleteByToken(String token) {
        return jdbcTemplate.update("DELETE FROM refresh_tokens WHERE token = ?", token);
    }
}
