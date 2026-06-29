package fa.training.pizza_store_api.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import fa.training.pizza_store_api.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Lấy danh mục chưa bị xóa và đang hoạt động (cho client)
    public List<Category> findAllActive() {
        String sql = "SELECT *, is_active AS active, is_deleted AS deleted FROM categories WHERE is_deleted = 0 AND is_active = 1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Category.class));
    }

    // Lấy danh mục (cho admin)
    public List<Category> findAll() {
        String sql = "SELECT *, is_active AS active, is_deleted AS deleted FROM categories";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Category.class));
    }

    // Add category
    public int save(Category category) {
        String sql = "INSERT INTO  categories (name, code, image_url, description, is_active) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql, category.getName(), category.getCode(), category.getImageUrl(),
                category.getDescription(), category.isActive());
    }

    // Update category
    public int update(Category category) {
        String sql = "UPDATE categories SET name = ?, code = ?, image_url = ?, description = ?, is_active = ? WHERE id = ? AND is_deleted = 0";
        return jdbcTemplate.update(sql, category.getName(), category.getCode(), category.getImageUrl(),
                category.getDescription(), category.isActive(), category.getId());
    }

    // Soft delete category
    public int delete(int id) {
        String sql = "UPDATE categories SET is_deleted = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
