package fa.training.pizza_store_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {
    private int id;
    private int categoryId;
    private String name;
    private String description;
    private String imageUrl;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private LocalDateTime createAt;

    // Trường mở rộng phục vụ hiển thị
    private String categoryName;

    // Danh sách các biến thể kích thước + giá tiền của sản phẩm này
    private List<ProductVariant> variants;
}
