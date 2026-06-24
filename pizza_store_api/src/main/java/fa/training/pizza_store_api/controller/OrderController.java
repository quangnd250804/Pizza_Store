package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.config.CustomUserDetails;
import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.OrderRequest;
import fa.training.pizza_store_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<Integer> createOrder(@RequestBody OrderRequest orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            orderRequest.setUserId(userDetails.getUser().getId());
        }
        Integer orderId = orderService.createOrder(orderRequest);
        return ApiResponse.success(orderId);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        throw new fa.training.pizza_store_api.exception.AppException(401, "Unauthorized");
    }

    @GetMapping("/history")
    public ApiResponse<fa.training.pizza_store_api.dto.PageResponse<fa.training.pizza_store_api.dto.OrderResponse>> getOrderHistory(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Integer userId = getCurrentUserId();
        return ApiResponse.success(orderService.getOrderHistory(userId, status, page, limit));
    }

    @GetMapping("/{id}")
    public ApiResponse<fa.training.pizza_store_api.dto.OrderResponse> getOrderById(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        return ApiResponse.success(orderService.getOrderById(id, userId));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        orderService.cancelOrder(id, userId);
        return ApiResponse.success("Đã hủy đơn hàng thành công");
    }

    @GetMapping("/admin")
    public ApiResponse<fa.training.pizza_store_api.dto.PageResponse<fa.training.pizza_store_api.dto.OrderResponse>> getAllOrdersAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(orderService.getAllOrdersAdmin(search, status, paymentMethod, startDate, endDate, sortBy, sortDirection, page, limit));
    }

    @GetMapping("/admin/{id}")
    public ApiResponse<fa.training.pizza_store_api.dto.OrderResponse> getOrderByIdAdmin(@PathVariable Integer id) {
        return ApiResponse.success(orderService.getOrderByIdAdmin(id));
    }

    @PutMapping("/admin/{id}/status")
    public ApiResponse<String> updateOrderStatusAdmin(@PathVariable Integer id, @RequestParam String status) {
        orderService.updateOrderStatusAdmin(id, status);
        return ApiResponse.success("Cập nhật trạng thái đơn hàng thành công");
    }
}
