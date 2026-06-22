package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.model.ComboDetail;
import org.springframework.jdbc.core.RowMapper;

public class ComboDetailRowMapper implements RowMapper<ComboDetail> {
    @Override
    public ComboDetail mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ComboDetail comboDetail = new ComboDetail();
        comboDetail.setId(rs.getInt("id"));
        comboDetail.setComboId(rs.getInt("combo_id"));
        comboDetail.setProductId(rs.getInt("product_id"));
        comboDetail.setQuantity(rs.getInt("quantity"));
        comboDetail.setProductName(rs.getString("productName"));
        comboDetail.setProductImageUrl(rs.getString("productImageUrl"));
        return comboDetail;
    }
}
