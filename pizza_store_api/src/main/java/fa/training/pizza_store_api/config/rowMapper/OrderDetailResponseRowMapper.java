package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.dto.OrderDetailResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailResponseRowMapper implements RowMapper<OrderDetailResponse> {
    @Override
    public OrderDetailResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderDetailResponse detail = new OrderDetailResponse();
        detail.setId(rs.getInt("id"));
        detail.setOrderId(rs.getInt("order_id"));
        
        int productId = rs.getInt("product_id");
        if (!rs.wasNull()) {
            detail.setProductId(productId);
            detail.setProductName(rs.getString("product_name"));
            detail.setProductImageUrl(rs.getString("product_image"));
            
            detail.setSizeId(rs.getInt("size_id"));
            detail.setSizeName(rs.getString("size_name"));
        }
        
        int comboId = rs.getInt("combo_id");
        if (!rs.wasNull()) {
            detail.setComboId(comboId);
            detail.setComboName(rs.getString("combo_name"));
            detail.setComboImageUrl(rs.getString("combo_image"));
        }
        
        detail.setQuantity(rs.getInt("quantity"));
        detail.setPrice(rs.getBigDecimal("price"));
        
        return detail;
    }
}
