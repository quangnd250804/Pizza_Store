package fa.training.pizza_store_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer comboId;
    private Integer quantity;
    private Integer sizeId; // S, M, L
    private BigDecimal price;

    private List<OrderToppingDetail> toppingDetails;
}
