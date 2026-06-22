package fa.training.pizza_store_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ComboRequest {
    @NotBlank(message = "Tên combo không được để trống")
    private String name;

    private String description;
    private String imageUrl;

    @NotNull(message = "Giá combo không được để trống")
    @Min(value = 0, message = "Giá combo không được âm")
    private BigDecimal price;

    @JsonProperty("isAvailable")
    private boolean isAvailable = true;

    @NotEmpty(message = "Combo phải chứa ít nhất một sản phẩm bên trong")
    private List<ComboDetailRequest> items;
}
