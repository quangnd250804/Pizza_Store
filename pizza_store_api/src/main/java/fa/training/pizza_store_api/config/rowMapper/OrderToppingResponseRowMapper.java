package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.dto.OrderToppingResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderToppingResponseRowMapper implements RowMapper<OrderToppingResponse> {
    @Override
    public OrderToppingResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderToppingResponse topping = new OrderToppingResponse();
        topping.setToppingId(rs.getInt("topping_id"));
        topping.setName(rs.getString("name"));
        topping.setPrice(rs.getBigDecimal("price"));
        return topping;
    }
}
