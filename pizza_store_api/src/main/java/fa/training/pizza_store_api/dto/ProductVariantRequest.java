package fa.training.pizza_store_api.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    @NotNull(message = "ID kích thước không được trống")
    private Integer sizeId;

    @NotNull(message = "Giá sản phẩm không được trống")
    @Min(value = 0, message = "Giá sản phẩm không được là số âm")
    private BigDecimal price;
}