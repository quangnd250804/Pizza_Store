package fa.training.pizza_store_api.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import fa.training.pizza_store_api.model.Combo;
import fa.training.pizza_store_api.model.ComboDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ComboDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Combo> findAllAvailableCombos() {
        String sql = "SELECT *, is_available AS available, is_deleted AS deleted FROM combos WHERE is_available = 1 AND is_deleted = 0";
        List<Combo> combos = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Combo.class));

        String detailSql = "SELECT cd.*, p.name as productName, p.image_url as productImageUrl " +
                "FROM combo_details cd " +
                "JOIN products p ON cd.product_id = p.id " +
                "WHERE cd.combo_id = ?";
        for (Combo combo : combos) {
            List<ComboDetail> details = jdbcTemplate.query(detailSql, new BeanPropertyRowMapper<>(ComboDetail.class), combo.getId());
            combo.setDetails(details);
        }
        return combos;
    }

    public List<Combo> findAllCombos() {
        String sql = "SELECT *, is_available AS available, is_deleted AS deleted FROM combos";
        List<Combo> combos = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Combo.class));

        String detailSql = "SELECT cd.*, p.name as productName, p.image_url as productImageUrl " +
                "FROM combo_details cd " +
                "JOIN products p ON cd.product_id = p.id " +
                "WHERE cd.combo_id = ?";
        for (Combo combo : combos) {
            List<ComboDetail> details = jdbcTemplate.query(detailSql, new BeanPropertyRowMapper<>(ComboDetail.class), combo.getId());
            combo.setDetails(details);
        }
        return combos;
    }

    @Transactional
    public void saveWithDetails(Combo combo) {
        // 1. Chèn bảng combos gốc
        String sqlCombo = "INSERT INTO combos (name, description, image_url, price, is_available) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlCombo, combo.getName(), combo.getDescription(), combo.getImageUrl(), combo.getPrice(),
                combo.isAvailable());

        int generatedComboId = jdbcTemplate.queryForObject("SELECT @@IDENTITY", Integer.class);

        // 2. Chèn các món ăn đi kèm vào bảng combo_details
        if (combo.getDetails() != null) {
            String sqlDetail = "INSERT INTO combo_details (combo_id, product_id, quantity) VALUES (?, ?, ?)";
            for (ComboDetail detail : combo.getDetails()) {
                jdbcTemplate.update(sqlDetail, generatedComboId, detail.getProductId(), detail.getQuantity());
            }
        }
    }

    public void updateWithDetails(Combo combo) {
        // 1. Cập nhật bảng combos gốc
        String sqlCombo = "UPDATE combos SET name = ?, description = ?, image_url = ?, price = ?, is_available = ? WHERE id = ? AND is_deleted = 0";
        jdbcTemplate.update(sqlCombo, combo.getName(), combo.getDescription(), combo.getImageUrl(), combo.getPrice(),
                combo.isAvailable(), combo.getId());

        // 2. Xóa các món ăn cũ trong bảng combo_details
        String sqlDeleteDetails = "DELETE FROM combo_details WHERE combo_id = ?";
        jdbcTemplate.update(sqlDeleteDetails, combo.getId());

        // 3. Chèn lại các món ăn mới vào bảng combo_details
        if (combo.getDetails() != null) {
            String sqlDetail = "INSERT INTO combo_details (combo_id, product_id, quantity) VALUES (?, ?, ?)";
            for (ComboDetail detail : combo.getDetails()) {
                jdbcTemplate.update(sqlDetail, combo.getId(), detail.getProductId(), detail.getQuantity());
            }
        }
    }

    public int softDelete(int id) {
        String sql = "UPDATE combos SET is_deleted = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public Combo findById(int id) {
        String sql = "SELECT *, is_available AS available, is_deleted AS deleted FROM combos WHERE id = ? AND is_deleted = 0";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Combo.class), id);
        } catch (Exception e) {
            return null;
        }
    }
}
