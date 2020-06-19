package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.LineItem;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.domain.request.OrderRequest;
import org.csu.ipetstore.domain.request.PageRequest;
import org.csu.ipetstore.domain.result.PageResult;

import java.util.List;

public interface OrderService {
    // 插入订单，生成新订单
    List<LineItem> insertOrder(Order order);

    // 取得订单
    Order getOrder(int orderId);

    // 通过用户名查看订单
    List<Order> getOrdersByUsername(String username);

    // 生成订单序列
    int getNextId(String name);

    //异步任务，在订单完成之后检查是否支付成功，若不成功删除订单
    void checkPaymentSuccess(int orderId);

    void setOrderStatus(Order order);

    //根据参数分页查询订单
    PageResult findOrderPage(OrderRequest orderRequest, PageRequest pageRequest);
}
