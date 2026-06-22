package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setAvailable(rs.getBoolean("is_available"));
        product.setDeleted(rs.getBoolean("is_deleted"));
        product.setCreateAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);

        // Map category_name nếu có
        try {
            product.setCategoryName(rs.getString("category_name"));
        } catch (SQLException e) {
            // Column doesn't exist, skip it
            product.setCategoryName(null);
        }

        return product;
    }
}

