package fa.training.pizza_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequest {
    private Integer productId;
    private Integer comboId;
    private Integer sizeId;
    private Integer quantity;

    private List<Integer> toppingIds;
}
