package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {
    // 根据用户名得到订单
    List<Order> getOrdersByUsername(String username);

    // 根据订单ID得到订单
    Order getOrder(int orderId);

    // 插入新订单
    void insertOrder(Order order);

    // 插入新订单状态
    void insertOrderStatus(@Param("order")Order order, @Param("lineNum")int lineNum);

    void deleteOrderByOrderId(int orderId);

    void setOrderStatus(Order order);
}
