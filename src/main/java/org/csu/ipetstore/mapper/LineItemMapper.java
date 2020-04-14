package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.LineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineItemMapper {
    // 根据订单ID得到订单中的商品项
    List<LineItem> getLineItemsByOrderId(int orderId);

    // 插入商品项
    void insertLineItem(LineItem lineItem);

    void deleteLineItemsByOrderId(int orderId);
}
