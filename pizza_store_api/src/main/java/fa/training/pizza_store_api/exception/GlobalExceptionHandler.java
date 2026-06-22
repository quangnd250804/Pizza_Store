package fa.training.pizza_store_api.exception;

import fa.training.pizza_store_api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //1.Bắt các lỗi do logic nghiệp vụ chủ động ném ra
    @ExceptionHandler({AppException.class})
    public ResponseEntity<ApiResponse<Object>> handleAppExeption(AppException e){
        ApiResponse<Object> respone = ApiResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respone);
    }

    //2.Bắt các lỗi Validation dữ liệu đầu vào @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e){
        String message = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : "Lỗi xác thực dữ liệu";
        ApiResponse<Object> response = ApiResponse.builder()
                .code(400)
                .message(message)
                .timestamp(LocalDateTime.now()).
                build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //3.Bắt các lỗi không xác định khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e){
        ApiResponse<Object> response = ApiResponse.builder()
                .code(500)
                .message("Hệ thống gặp sự cố: " + e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
