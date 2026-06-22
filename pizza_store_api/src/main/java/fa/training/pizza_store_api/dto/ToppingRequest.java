package fa.training.pizza_store_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ToppingRequest {
    @NotBlank(message = "Tên topping không được để trống")
    @Size(max = 150, message = "Tên topping không vượt quá 150 ký tự")
    private String name;

    @NotNull(message = "Giá topping không được để trống")
    @Min(value = 0, message = "Giá topping không được là số âm")
    private BigDecimal price;

    @JsonProperty("isAvailable")
    private boolean isAvailable;
}
