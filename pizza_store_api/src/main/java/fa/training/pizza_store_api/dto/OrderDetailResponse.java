package fa.training.pizza_store_api.dto;

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
public class OrderDetailResponse {
    private Integer id;
    private Integer orderId;
    
    // For Product
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer sizeId;
    private String sizeName;
    
    // For Combo
    private Integer comboId;
    private String comboName;
    private String comboImageUrl;
    
    private Integer quantity;
    private BigDecimal price; // Price per item (including size variant price, but NOT toppings)

    private List<OrderToppingResponse> toppingDetails;
}
