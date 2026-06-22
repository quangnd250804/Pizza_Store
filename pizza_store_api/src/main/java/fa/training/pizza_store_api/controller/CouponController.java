package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.model.Coupon;
import fa.training.pizza_store_api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Coupon>>> getActiveCoupons() {
        List<Coupon> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Coupon>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal total) {
        try {
            Coupon coupon = couponService.validateCoupon(code, total);
            return ResponseEntity.ok(ApiResponse.success(coupon));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Coupon>builder()
                    .code(400)
                    .message(e.getMessage())
                    .build());
        }
    }
}
