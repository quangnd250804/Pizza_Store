package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.model.Combo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComboRowMapper implements RowMapper<Combo> {
    public Combo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Combo combo = new Combo();
        combo.setId(rs.getInt("id"));
        combo.setName(rs.getString("name"));
        combo.setDescription(rs.getString("description"));
        combo.setImageUrl(rs.getString("image_url"));
        combo.setPrice(rs.getBigDecimal("price"));
        combo.setAvailable(rs.getBoolean("is_available"));
        combo.setDeleted(rs.getBoolean("is_deleted"));
        combo.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return combo;
    }
}
