package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.model.Topping;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class ToppingRowMapper implements RowMapper<Topping> {
    @Override
    public Topping mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
        Topping topping = new Topping();
        topping.setId(rs.getInt("id"));
        topping.setName(rs.getString("name"));
        topping.setPrice(rs.getBigDecimal("price"));
        topping.setAvailable(rs.getBoolean("is_available"));
        topping.setDeleted(rs.getBoolean("is_deleted"));
        topping.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return topping;
    }
}
