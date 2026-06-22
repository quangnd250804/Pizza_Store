package fa.training.pizza_store_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Topping {
    private int id;
    private String name;
    private BigDecimal price;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private LocalDateTime createdAt;
}