package fa.training.pizza_store_api.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariant {
    private int id;
    private int productId;
    private int sizeId;
    private BigDecimal price;

    // Thêm các trường mở rộng để JOIN lấy thông tin hiển thị ra Frontend
    private String sizeName;
    private String sizeCode;
}
