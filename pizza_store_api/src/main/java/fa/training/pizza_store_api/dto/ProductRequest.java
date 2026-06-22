package fa.training.pizza_store_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProductRequest {
    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Integer categoryId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm không vượt quá 200 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả không vượt quá 500 ký tự")
    private String description;

    private String imageUrl;

    @JsonProperty("isAvailable")
    private boolean isAvailable = true;

    @NotEmpty(message = "Sản phẩm phải cấu hình ít nhất một kích thước và giá tiền")
    private List<ProductVariantRequest> variants;
}