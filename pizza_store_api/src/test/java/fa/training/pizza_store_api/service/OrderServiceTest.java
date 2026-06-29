package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.OrderDao;
import fa.training.pizza_store_api.dto.OrderResponse;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.enums.OrderStatus;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderDao orderDao;

    @Test
    void getOrderByIdAdmin_Success() {
        Order order = new Order();
        order.setId(1);
        order.setCustomerName("Test Customer");

        when(orderDao.findById(1)).thenReturn(order);
        when(orderDao.findOrderDetails(1)).thenReturn(Collections.emptyList()); // Mock for mapToOrderResponse includeDetails=true

        OrderResponse response = orderService.getOrderByIdAdmin(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Customer", response.getCustomerName());
        verify(orderDao, times(1)).findById(1);
        verify(orderDao, times(1)).findOrderDetails(1);
    }

    @Test
    void getOrderByIdAdmin_NotFound() {
        when(orderDao.findById(999)).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> orderService.getOrderByIdAdmin(999));
        assertEquals(404, exception.getCode());
        assertEquals("Không tìm thấy đơn hàng", exception.getMessage());
        
        verify(orderDao, times(1)).findById(999);
    }

    @Test
    void getAllOrdersAdmin_Success() {
        Order order1 = new Order();
        order1.setId(1);
        
        Order order2 = new Order();
        order2.setId(2);

        when(orderDao.countAllOrdersAdmin("John", "PENDING", "COD", null, null, null))
                .thenReturn(2);
        when(orderDao.findAllOrdersAdmin("John", "PENDING", "COD", null, null, null, "createdAt", "DESC", 1, 10))
                .thenReturn(Arrays.asList(order1, order2));

        PageResponse<OrderResponse> response = orderService.getAllOrdersAdmin("John", "PENDING", "COD", null, null, null, "createdAt", "DESC", 1, 10);

        assertEquals(2, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getCurrentPage());
        assertEquals(2, response.getContent().size());
        
        verify(orderDao, times(1)).countAllOrdersAdmin("John", "PENDING", "COD", null, null, null);
        verify(orderDao, times(1)).findAllOrdersAdmin("John", "PENDING", "COD", null, null, null, "createdAt", "DESC", 1, 10);
    }

    @Test
    void updateOrderStatusAdmin_Success() {
        Order order = new Order();
        order.setId(1);
        order.setStatus(OrderStatus.PENDING);

        when(orderDao.findById(1)).thenReturn(order);

        orderService.updateOrderStatusAdmin(1, "CONFIRMED");

        verify(orderDao, times(1)).updateOrderStatus(1, "CONFIRMED");
    }

    @Test
    void updateOrderStatusAdmin_NotFound() {
        when(orderDao.findById(999)).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> orderService.updateOrderStatusAdmin(999, "CONFIRMED"));
        assertEquals(404, exception.getCode());
        assertEquals("Không tìm thấy đơn hàng", exception.getMessage());
        
        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }

    @Test
    void updateOrderStatusAdmin_InvalidStatus() {
        Order order = new Order();
        order.setId(1);
        order.setStatus(OrderStatus.PENDING);

        when(orderDao.findById(1)).thenReturn(order);

        AppException exception = assertThrows(AppException.class, () -> orderService.updateOrderStatusAdmin(1, "INVALID_STATUS"));
        assertEquals(400, exception.getCode());
        assertEquals("Trạng thái đơn hàng không hợp lệ", exception.getMessage());
        
        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }
}
