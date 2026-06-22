package fa.training.pizza_store_api.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ComboDetailRequest {
    @NotNull(message = "ID sản phẩm không được trống")
    private Integer productId;

    @NotNull(message = "Số lượng sản phẩm không được trống")
    @Min(value = 1, message = "Số lượng sản phẩm trong combo ít nhất phải bằng 1")
    private Integer quantity;
}
