package fa.training.pizza_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Integer userId;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String note;
    private String paymentMethod;
    private String couponCode;

    private List<OrderDetailRequest> orderDetails;
}
