package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Repository
public class CouponDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<Coupon> findByCode(String code) {
        String sql = "SELECT *, is_active AS active FROM coupons WHERE code = ?";
        List<Coupon> coupons = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Coupon.class), code);
        return coupons.stream().findFirst();
    }

    public List<Coupon> findAllActive() {
        String sql = "SELECT *, is_active AS active FROM coupons WHERE is_active = 1 AND (valid_to IS NULL OR valid_to >= GETDATE()) AND (valid_from IS NULL OR valid_from <= GETDATE())";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Coupon.class));
    }
}
