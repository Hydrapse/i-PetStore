package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Order;

import java.util.List;

public interface OrderService {
    // 插入订单，生成新订单
    void insertOrder(Order order);

    // 取得订单
    Order getOrder(int orderId);

    // 通过用户名查看订单
    List<Order> getOrdersByUsername(String username);

    // 生成订单序列
    int getNextId(String name) ;
}
