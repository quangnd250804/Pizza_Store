package fa.training.pizza_store_api.model;

import lombok.Data;

@Data
public class ComboDetail {
    private int id;
    private int comboId;
    private int productId;
    private int quantity;

    // Trường bổ sung để hiển thị tên món ăn ra ngoài màn hình
    private String productName;
    private String productImageUrl;
}
