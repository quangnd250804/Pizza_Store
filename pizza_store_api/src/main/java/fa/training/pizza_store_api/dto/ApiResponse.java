package fa.training.pizza_store_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)// Chỉ bao gồm các trường không null trong JSON
public class ApiResponse<T> {
    private int code;
    private String message;
    private T result;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .result(result)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
