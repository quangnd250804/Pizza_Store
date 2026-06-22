package fa.training.pizza_store_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {
    private Integer id;
    private String code;
    private String description;
    private String discountType; // "PERCENT" or "AMOUNT"
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    @JsonProperty("is_active")
    private Boolean isActive;
    private LocalDateTime createdAt;
}
