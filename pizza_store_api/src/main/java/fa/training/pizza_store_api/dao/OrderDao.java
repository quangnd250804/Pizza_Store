package fa.training.pizza_store_api.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import fa.training.pizza_store_api.dto.OrderDetailResponse;
import fa.training.pizza_store_api.dto.OrderToppingResponse;
import fa.training.pizza_store_api.model.Order;
import fa.training.pizza_store_api.model.OrderDetail;
import fa.training.pizza_store_api.model.OrderToppingDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Order createOrder(Order order) {
        String sqlOrder = "INSERT INTO orders (user_id, customer_name, customer_phone, shipping_address, note, total_price, status, payment_status, payment_method, coupon_id, discount_applied, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getUserId());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getCustomerPhone());
            ps.setString(4, order.getShippingAddress());
            if (order.getNote() != null) {
                ps.setString(5, order.getNote());
            } else {
                ps.setNull(5, java.sql.Types.NVARCHAR);
            }
            ps.setBigDecimal(6, order.getTotalPrice());
            ps.setString(7, order.getStatus().name());
            ps.setString(8, order.getPaymentStatus());
            ps.setString(9, order.getPaymentMethod());
            if (order.getCouponId() != null) {
                ps.setInt(10, order.getCouponId());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            if (order.getDiscountApplied() != null) {
                ps.setBigDecimal(11, order.getDiscountApplied());
            } else {
                ps.setBigDecimal(11, java.math.BigDecimal.ZERO);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            order.setId(keyHolder.getKey().intValue());
        }
        return order;
    }

    public void saveOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_details (order_id, product_id, size_id, quantity, price, combo_id) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, detail.getOrderId());
            if (detail.getProductId() != null) {
                ps.setInt(2, detail.getProductId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            if (detail.getSizeId() != null) {
                ps.setInt(3, detail.getSizeId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setInt(4, detail.getQuantity());
            ps.setBigDecimal(5, detail.getPrice());
            if (detail.getComboId() != null) {
                ps.setInt(6, detail.getComboId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            detail.setId(keyHolder.getKey().intValue());
        }
    }

    public void saveOrderToppingDetail(OrderToppingDetail detail) {
        String sql = "INSERT INTO order_topping_details (order_detail_id, topping_id, price) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, detail.getOrderDetailId(), detail.getToppingId(), detail.getPrice());
    }

    public void updatePaymentStatus(Integer orderId, String status) {
        String sql = "UPDATE orders SET payment_status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    public int countByUserIdAndStatus(Integer userId, String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
        if (status != null && !status.isEmpty()) {
            sql += " AND status = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, userId, status);
        }
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    public List<Order> findByUserIdAndStatus(Integer userId, String status, int page, int limit) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        if (status != null && !status.isEmpty()) {
            sql += " AND status = '" + status + "'";
        }
        
        int offset = (page - 1) * limit;
        sql += " ORDER BY created_at DESC OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), userId);
    }

    public Order findById(Integer orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Order.class), orderId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<OrderDetailResponse> findOrderDetails(Integer orderId) {
        String sql = "SELECT od.id, od.order_id, od.product_id, od.combo_id, od.quantity, od.price, " +
                "od.size_id, s.name AS size_name, " +
                "p.name AS product_name, p.image_url AS product_image_url, " +
                "c.name AS combo_name, c.image_url AS combo_image_url " +
                "FROM order_details od " +
                "LEFT JOIN products p ON od.product_id = p.id " +
                "LEFT JOIN sizes s ON od.size_id = s.id " +
                "LEFT JOIN combos c ON od.combo_id = c.id " +
                "WHERE od.order_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderDetailResponse.class), orderId);
    }

    public List<OrderToppingResponse> findToppingsByOrderDetailId(Integer orderDetailId) {
        String sql = "SELECT otd.topping_id, t.name, otd.price " +
                "FROM order_topping_details otd " +
                "JOIN toppings t ON otd.topping_id = t.id " +
                "WHERE otd.order_detail_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderToppingResponse.class), orderDetailId);
    }

    public void updateOrderStatus(Integer orderId, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = GETDATE() WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    public int countAllOrdersAdmin(String customerName, String status, String paymentMethod, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" AND customer_name LIKE ?");
            params.add("%" + customerName + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            sql.append(" AND payment_method = ?");
            params.add(paymentMethod);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND created_at >= ?");
            params.add(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND created_at <= ?");
            params.add(endDate + " 23:59:59");
        }

        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    public List<Order> findAllOrdersAdmin(String customerName, String status, String paymentMethod, String startDate, String endDate, String sortBy, String sortDirection, int page, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" AND customer_name LIKE ?");
            params.add("%" + customerName + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            sql.append(" AND payment_method = ?");
            params.add(paymentMethod);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND created_at >= ?");
            params.add(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND created_at <= ?");
            params.add(endDate + " 23:59:59");
        }

        // Validate and apply sorting
        String orderColumn = "created_at";
        if ("totalPrice".equals(sortBy)) {
            orderColumn = "total_price";
        } else if ("createdAt".equals(sortBy)) {
            orderColumn = "created_at";
        }
        
        String direction = "ASC".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";
        sql.append(" ORDER BY ").append(orderColumn).append(" ").append(direction);
        
        int offset = (page - 1) * limit;
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Order.class), params.toArray());
    }
}
