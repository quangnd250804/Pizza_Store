package fa.training.pizza_store_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
    public String name;

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(max = 10,message = "Mã danh mục không được vượt quá 10 ký tự")
    public String code;

    public String imageUrl;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    public String description;

    @JsonProperty("isActive")
    public boolean isActive = true;
}
