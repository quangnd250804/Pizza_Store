package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.enums.OrderStatus;
import fa.training.pizza_store_api.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setNote(rs.getString("note"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setPaymentMethod(rs.getString("payment_method"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        int couponId = rs.getInt("coupon_id");
        if (!rs.wasNull()) {
            order.setCouponId(couponId);
        }
        
        order.setDiscountApplied(rs.getBigDecimal("discount_applied"));
        
        return order;
    }
}
