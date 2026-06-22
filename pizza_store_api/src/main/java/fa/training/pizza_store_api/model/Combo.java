package fa.training.pizza_store_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Combo {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private LocalDateTime createdAt;

    // Danh sách các món ăn thuộc Combo này
    private List<ComboDetail> details;
}
