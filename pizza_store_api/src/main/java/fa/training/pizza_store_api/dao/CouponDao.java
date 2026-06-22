package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Coupon> rowMapper = new RowMapper<Coupon>() {
        @Override
        public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Coupon.builder()
                    .id(rs.getInt("id"))
                    .code(rs.getString("code"))
                    .description(rs.getString("description"))
                    .discountType(rs.getString("discount_type"))
                    .discountValue(rs.getBigDecimal("discount_value"))
                    .minOrderValue(rs.getBigDecimal("min_order_value"))
                    .validFrom(rs.getTimestamp("valid_from") != null ? rs.getTimestamp("valid_from").toLocalDateTime() : null)
                    .validTo(rs.getTimestamp("valid_to") != null ? rs.getTimestamp("valid_to").toLocalDateTime() : null)
                    .isActive(rs.getBoolean("is_active"))
                    .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                    .build();
        }
    };

    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code = ?";
        List<Coupon> coupons = jdbcTemplate.query(sql, rowMapper, code);
        return coupons.stream().findFirst();
    }

    public List<Coupon> findAllActive() {
        String sql = "SELECT * FROM coupons WHERE is_active = 1 AND (valid_to IS NULL OR valid_to >= GETDATE()) AND (valid_from IS NULL OR valid_from <= GETDATE())";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
