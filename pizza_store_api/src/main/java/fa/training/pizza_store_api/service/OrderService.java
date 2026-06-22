package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.OrderDao;
import fa.training.pizza_store_api.dao.ProductDao;
import fa.training.pizza_store_api.dao.ToppingDao;
import fa.training.pizza_store_api.dto.OrderDetailRequest;
import fa.training.pizza_store_api.dto.OrderDetailResponse;
import fa.training.pizza_store_api.dto.OrderRequest;
import fa.training.pizza_store_api.dto.OrderResponse;
import fa.training.pizza_store_api.dto.OrderToppingResponse;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Order;
import fa.training.pizza_store_api.model.OrderDetail;
import fa.training.pizza_store_api.model.OrderToppingDetail;
import fa.training.pizza_store_api.model.Topping;
import fa.training.pizza_store_api.dao.ComboDao;
import fa.training.pizza_store_api.model.Combo;
import fa.training.pizza_store_api.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ComboDao comboDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ToppingDao toppingDao;

    @Autowired
    private CouponService couponService;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());
        
        // Trạng thái đơn hàng: PENDING (Chờ duyệt), CONFIRMED (Đã xác nhận), SHIPPING (Đang giao), DELIVERED (Đã giao), CANCELLED (Đã hủy)
        order.setStatus(OrderStatus.PENDING);
        
        // Trạng thái thanh toán: UNPAID (Chưa thanh toán), PAID (Đã thanh toán)
        order.setPaymentStatus("UNPAID");

        List<OrderDetail> orderDetails = new ArrayList<>();

        if (request.getOrderDetails() == null || request.getOrderDetails().isEmpty()) {
            throw new AppException(400, "Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
            BigDecimal totalItemPrice = BigDecimal.ZERO;
            OrderDetail detail = new OrderDetail();
            detail.setQuantity(detailRequest.getQuantity());

            if (detailRequest.getProductId() != null) {
                BigDecimal productPrice = productDao.findVariantPrice(detailRequest.getProductId(), detailRequest.getSizeId());
                if (productPrice == null) {
                    throw new AppException(400, "Sản phẩm hoặc kích thước không hợp lệ: Sản phẩm ID " + detailRequest.getProductId() + " - Kích thước ID " + detailRequest.getSizeId());
                }

                totalItemPrice = productPrice;
                detail.setProductId(detailRequest.getProductId());
                detail.setSizeId(detailRequest.getSizeId());
                detail.setPrice(productPrice); // Giá sản phẩm chưa bao gồm topping

                List<OrderToppingDetail> toppingDetails = new ArrayList<>();
                if (detailRequest.getToppingIds() != null) {
                    for (Integer toppingId : detailRequest.getToppingIds()) {
                        Topping topping = toppingDao.findById(toppingId);
                        if (topping == null || !topping.isAvailable()) {
                            throw new AppException(400, "Topping không hợp lệ hoặc đã hết: ID " + toppingId);
                        }
                        OrderToppingDetail toppingDetail = new OrderToppingDetail();
                        toppingDetail.setToppingId(toppingId);
                        toppingDetail.setPrice(topping.getPrice()); // Giá topping tại thời điểm mua
                        
                        totalItemPrice = totalItemPrice.add(topping.getPrice());
                        toppingDetails.add(toppingDetail);
                    }
                }
                detail.setToppingDetails(toppingDetails);
            } else if (detailRequest.getComboId() != null) {
                Combo combo = comboDao.findById(detailRequest.getComboId());
                if (combo == null || !combo.isAvailable()) {
                    throw new AppException(400, "Combo không hợp lệ hoặc đã hết: ID " + detailRequest.getComboId());
                }
                totalItemPrice = combo.getPrice();
                detail.setComboId(detailRequest.getComboId());
                detail.setPrice(combo.getPrice());
            } else {
                throw new AppException(400, "Chi tiết đơn hàng phải có sản phẩm hoặc combo");
            }
            
            // Tính tổng tiền của item này nhân với số lượng
            totalOrderPrice = totalOrderPrice.add(totalItemPrice.multiply(new BigDecimal(detailRequest.getQuantity())));
            
            orderDetails.add(detail);
        }

        // Apply coupon if exists
        BigDecimal discountApplied = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            try {
                fa.training.pizza_store_api.model.Coupon coupon = couponService.validateCoupon(request.getCouponCode(), totalOrderPrice);
                order.setCouponId(coupon.getId());
                
                if ("AMOUNT".equalsIgnoreCase(coupon.getDiscountType())) {
                    discountApplied = coupon.getDiscountValue();
                } else if ("PERCENT".equalsIgnoreCase(coupon.getDiscountType())) {
                    discountApplied = totalOrderPrice.multiply(coupon.getDiscountValue()).divide(new BigDecimal(100));
                }
                
                if (discountApplied.compareTo(totalOrderPrice) > 0) {
                    discountApplied = totalOrderPrice; // Không giảm giá quá giá trị đơn hàng
                }
            } catch (RuntimeException e) {
                throw new AppException(400, e.getMessage());
            }
        }

        order.setDiscountApplied(discountApplied);
        order.setTotalPrice(totalOrderPrice.subtract(discountApplied));

        // 1. Lưu Order
        order = orderDao.createOrder(order);

        // 2. Lưu OrderDetails
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(order.getId());
            orderDao.saveOrderDetail(detail);

            // 3. Lưu OrderToppingDetails
            if (detail.getToppingDetails() != null) {
                for (OrderToppingDetail toppingDetail : detail.getToppingDetails()) {
                    toppingDetail.setOrderDetailId(detail.getId());
                    orderDao.saveOrderToppingDetail(toppingDetail);
                }
            }
        }
        
        return order.getId();
    }

    public PageResponse<OrderResponse> getOrderHistory(Integer userId, String status, int page, int limit) {
        int totalElements = orderDao.countByUserIdAndStatus(userId, status);
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        
        List<Order> orders = orderDao.findByUserIdAndStatus(userId, status, page, limit);
        List<OrderResponse> orderResponses = new ArrayList<>();
        
        for (Order order : orders) {
            orderResponses.add(mapToOrderResponse(order, false)); // Don't need details for list view
        }
        
        return PageResponse.<OrderResponse>builder()
                .content(orderResponses)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    public OrderResponse getOrderById(Integer orderId, Integer userId) {
        Order order = orderDao.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new AppException(404, "Không tìm thấy đơn hàng");
        }
        return mapToOrderResponse(order, true);
    }

    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = orderDao.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new AppException(404, "Không tìm thấy đơn hàng");
        }
        
        if (order.getStatus() == OrderStatus.PENDING || 
            order.getStatus() == OrderStatus.CONFIRMED || 
            order.getStatus() == OrderStatus.PREPARING) {
            orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED.name());
        } else {
            throw new AppException(400, "Không thể hủy đơn hàng ở trạng thái này");
        }
    }

    private OrderResponse mapToOrderResponse(Order order, boolean includeDetails) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setNote(order.getNote());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setCouponId(order.getCouponId());
        response.setDiscountApplied(order.getDiscountApplied());

        if (includeDetails) {
            List<OrderDetailResponse> detailResponses = orderDao.findOrderDetails(order.getId());
            for (OrderDetailResponse detail : detailResponses) {
                List<OrderToppingResponse> toppings = orderDao.findToppingsByOrderDetailId(detail.getId());
                detail.setToppingDetails(toppings);
            }
            response.setOrderDetails(detailResponses);
        }

        return response;
    }
}
