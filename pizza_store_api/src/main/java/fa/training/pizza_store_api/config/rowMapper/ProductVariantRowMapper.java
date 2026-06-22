package fa.training.pizza_store_api.config.rowMapper;

import fa.training.pizza_store_api.model.ProductVariant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class ProductVariantRowMapper implements RowMapper<ProductVariant> {
    @Override
    public ProductVariant mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
        ProductVariant productVariant = new ProductVariant();
        productVariant.setId(rs.getInt("id"));
        productVariant.setProductId(rs.getInt("product_id"));
        productVariant.setSizeId(rs.getInt("size_id"));
        productVariant.setPrice(rs.getBigDecimal("price"));
        productVariant.setSizeName(rs.getString("sizeName"));
        productVariant.setSizeCode(rs.getString("sizeCode"));
        return productVariant;
    }
}
