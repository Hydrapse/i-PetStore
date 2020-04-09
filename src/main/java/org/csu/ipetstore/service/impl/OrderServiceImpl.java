package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.domain.LineItem;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.domain.Sequence;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.mapper.LineItemMapper;
import org.csu.ipetstore.mapper.OrderMapper;
import org.csu.ipetstore.mapper.SequenceMapper;
import org.csu.ipetstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private LineItemMapper lineItemMapper;

    @Autowired
    private SequenceMapper sequenceMapper;


    //向数据库插入订单, 开启事务管理
    //隔离级别：一个事务只能读取另一个事务已经提交的数据
    //传播级别：若当前存在事务则加入，否则创建
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<LineItem> insertOrder(Order order) {
        List<LineItem> errorList = new ArrayList<>(); //塞入库存不足的订单项

        //设置订单日期, ID
        order.setOrderDate(new Date());
        order.setOrderId(getNextId("ordernum"));

        for (int i = 0; i < order.getLineItems().size(); i++) {
            LineItem lineItem = order.getLineItems().get(i);
            String itemId = lineItem.getItemId();
            Integer increment = new Integer(lineItem.getQuantity());

            //检查并修改库存
            int qty = itemMapper.getInventoryQuantity(itemId);
            if(qty == 0){
                lineItem.setQuantity(0);
                errorList.add(lineItem);
                order.getLineItems().remove(i); //从列表中删除
                logger.warn("货物 " + itemId + " 库存为空，该物品购买失败");
                break;
            }
            else if(qty < increment){
                lineItem.setQuantity(qty);
                increment = qty;
                errorList.add(lineItem);
                logger.warn("货物 " + itemId + " 库存不足，购买数量调整为现有库存");
            }

            Map<String, Object> param = new HashMap<>(2);
            param.put("itemId", itemId);
            param.put("increment", increment);
            itemMapper.updateInventoryQuantity(param);

        }

        orderMapper.insertOrder(order);
        orderMapper.insertOrderStatus(order);
        for (int i = 0; i < order.getLineItems().size(); i++) {
            LineItem lineItem = order.getLineItems().get(i);
            lineItem.setOrderId(order.getOrderId());
            lineItemMapper.insertLineItem(lineItem);
        }
        logger.info("订单 " + order.getOrderId() + " 添加成功");

        return errorList;
    }

    @Override
    public Order getOrder(int orderId) {
        Order order = orderMapper.getOrder(orderId);
        if(order == null){
            return order;
        }
        order.setLineItems(lineItemMapper.getLineItemsByOrderId(orderId));

        for (int i = 0; i < order.getLineItems().size(); i++) {
            LineItem lineItem = order.getLineItems().get(i);
            Item item = itemMapper.getItem(lineItem.getItemId());
            //item中的quantity代表库存量
            item.setQuantity(itemMapper.getInventoryQuantity(lineItem.getItemId()));
            lineItem.setItem(item);
        }

        return order;
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        return orderMapper.getOrdersByUsername(username);
    }

    @Override
    public int getNextId(String name) {
        Sequence sequence = new Sequence(name, -1);
        sequence = sequenceMapper.getSequence(sequence);
        if (sequence == null) {
            throw new RuntimeException("Error: A null sequence was returned from the database (could not get next " + name
                    + " sequence).");
        }
        Sequence parameterObject = new Sequence(name, sequence.getNextId() + 1);
        if(sequenceMapper.updateSequence(parameterObject)){
            return parameterObject.getNextId();
        }else {
            throw new RuntimeException("Can't updateSequence!");
        }
    }
}
