package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.CouponDao;
import fa.training.pizza_store_api.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    public List<Coupon> getActiveCoupons() {
        return couponDao.findAllActive();
    }

    public Coupon validateCoupon(String code, BigDecimal cartTotal) {
        Optional<Coupon> optionalCoupon = couponDao.findByCode(code);
        if (!optionalCoupon.isPresent()) {
            throw new RuntimeException("Mã giảm giá không tồn tại");
        }

        Coupon coupon = optionalCoupon.get();

        if (!coupon.getIsActive()) {
            throw new RuntimeException("Mã giảm giá đã ngừng hoạt động");
        }

        if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá chưa đến ngày sử dụng");
        }

        if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        if (coupon.getMinOrderValue() != null && cartTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue() + "đ để áp dụng mã này");
        }

        return coupon;
    }
}
