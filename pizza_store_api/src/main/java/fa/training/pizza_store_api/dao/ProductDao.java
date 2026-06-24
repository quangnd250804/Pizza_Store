package fa.training.pizza_store_api.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import fa.training.pizza_store_api.model.Product;
import fa.training.pizza_store_api.model.ProductVariant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
public class ProductDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int countActive(String categoryCode) {
        String sql = "SELECT COUNT(*) FROM products p " +
                "JOIN categories c ON p.category_id = c.id " +
                "WHERE p.is_deleted = 0 AND p.is_available = 1";
        if (categoryCode != null && !categoryCode.isEmpty()) {
            sql += " AND c.code = '" + categoryCode + "'";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // Lấy tất cả sản phẩm kèm biến thể kích thước (Dành cho trang chủ Menu khách
    // hàng)
    public List<Product> findAllActive(String categoryCode, int page, int limit) {
        String sql = "SELECT p.*, p.is_available AS available, p.is_deleted AS deleted, c.name AS category_name " +
                "FROM products p " +
                "JOIN categories c ON p.category_id = c.id " +
                "WHERE p.is_deleted = 0 AND p.is_available = 1";

        if (categoryCode != null && !categoryCode.isEmpty()) {
            sql += " AND c.code = '" + categoryCode + "'";
        }

        int offset = (page - 1) * limit;
        sql += " ORDER BY p.id OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

        List<Product> products = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));

        // Nap thêm biến thể kích thước + giá tiền cho từng sản phẩm
        String variantSql = "SELECT pv.*, s.name as sizeName, s.code as sizeCode " +
                "FROM product_variants pv " +
                "JOIN sizes s ON pv.size_id = s.id " +
                "WHERE pv.product_id = ?";
        for (Product product : products) {
            List<ProductVariant> variants = jdbcTemplate.query(variantSql, new BeanPropertyRowMapper<>(ProductVariant.class),
                    product.getId());
            product.setVariants(variants);
        }
        return products;
    }

    public int countAll(String categoryCode) {
        String sql = "SELECT COUNT(*) FROM products p " +
                "JOIN categories c ON p.category_id = c.id ";
        if (categoryCode != null && !categoryCode.isEmpty()) {
            sql += "WHERE c.code = '" + categoryCode + "'";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // Lấy tất cả sản phẩm kèm biến thể kích thước (Dành cho admin quản lý)
    public List<Product> findAll(String categoryCode, int page, int limit) {
        String sql = "SELECT p.*, p.is_available AS available, p.is_deleted AS deleted, c.name AS category_name " +
                "FROM products p " +
                "JOIN categories c ON p.category_id = c.id ";

        if (categoryCode != null && !categoryCode.isEmpty()) {
            sql += "WHERE c.code = '" + categoryCode + "'";
        }

        int offset = (page - 1) * limit;
        sql += " ORDER BY p.id OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

        List<Product> products = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));

        // Nap thêm biến thể kích thước + giá tiền cho từng sản phẩm
        String variantSql = "SELECT pv.*, s.name as sizeName, s.code as sizeCode " +
                "FROM product_variants pv " +
                "JOIN sizes s ON pv.size_id = s.id " +
                "WHERE pv.product_id = ?";
        for (Product product : products) {
            List<ProductVariant> variants = jdbcTemplate.query(variantSql, new BeanPropertyRowMapper<>(ProductVariant.class),
                    product.getId());
            product.setVariants(variants);
        }

        return products;
    }

    // Thêm mới sản phẩm bao gồm cả kích thước (Dùng Transaction để đảm bảo tính
    // toàn vẹn)
    @Transactional
    public void saveWithVariants(Product product) {
        // Thêm sản phẩm
        String sql = "INSERT INTO products (category_id, name, description, image_url, is_available) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, product.getCategoryId(), product.getName(), product.getDescription(),
                product.getImageUrl(), product.isAvailable());

        // Lấy ID sản phẩm vừa thêm
        Integer productId = jdbcTemplate.queryForObject("SELECT @@IDENTITY", Integer.class);

        // Thêm các biến thể kích thước
        if (product.getVariants() != null) {
            String variantSql = "INSERT INTO product_variants (product_id, size_id, price) VALUES (?,?,?)";
            for (ProductVariant variant : product.getVariants()) {
                jdbcTemplate.update(variantSql, productId, variant.getSizeId(), variant.getPrice());
            }
        }
    }

    // Cập nhật sản phẩm và biến thể kích thước (Dùng Transaction để đảm bảo tính
    // toàn vẹn)
    @Transactional
    public void updateWithVariants(Product product) {
        // Cập nhật thông tin sản phẩm
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, image_url = ?, is_available = ? WHERE id = ? AND is_deleted = 0";
        jdbcTemplate.update(sql, product.getCategoryId(), product.getName(), product.getDescription(),
                product.getImageUrl(), product.isAvailable(), product.getId());

        // Xóa các biến thể cũ của sản phẩm
        String deleteVariantSql = "DELETE FROM product_variants WHERE product_id = ?";
        jdbcTemplate.update(deleteVariantSql, product.getId());

        // Thêm lại các biến thể mới
        if (product.getVariants() != null) {
            String variantSql = "INSERT INTO product_variants (product_id, size_id, price) VALUES (?,?,?)";
            for (ProductVariant variant : product.getVariants()) {
                jdbcTemplate.update(variantSql, product.getId(), variant.getSizeId(), variant.getPrice());
            }
        }
    }

    // Xóa mềm sản phẩm
    public int softDelete(int id) {
        String sql = "UPDATE products SET is_deleted = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public java.math.BigDecimal findVariantPrice(int productId, int sizeId) {
        String sql = "SELECT price FROM product_variants WHERE product_id = ? AND size_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, java.math.BigDecimal.class, productId, sizeId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
}
