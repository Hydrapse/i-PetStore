package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.config.AlipayConfig;
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
import org.springframework.scheduling.annotation.Async;
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

        int preLineNum = order.getLineItems().size(); //用户一开始的商品种类数量
        for (int i = 0; i < preLineNum; i++) {
            LineItem lineItem = order.getLineItems().get(i);
            String itemId = lineItem.getItemId();
            Integer decrement = lineItem.getQuantity();

            //检查并修改库存
            int qty = itemMapper.getInventoryQuantity(itemId);
            if(qty == 0){
                lineItem.setQuantity(0);
                errorList.add(lineItem);
                order.getLineItems().remove(i); //从列表中删除
                logger.warn("货物 " + itemId + " 库存为空，该物品购买失败");
                break;
            }
            else if(qty < decrement){
                lineItem.setQuantity(qty);
                decrement = qty;
                errorList.add(lineItem);
                logger.warn("货物 " + itemId + " 库存不足，购买数量调整为现有库存");
            }

            Map<String, Object> param = new HashMap<>(2);
            param.put("itemId", itemId);
            param.put("increment", decrement);
            itemMapper.updateInventoryQuantity(param);

        }

        int afterLineNum = order.getLineItems().size(); //经过库存检验后的种类数量
        if(afterLineNum <= 0){
            logger.warn("订单购物项为空，取消插入订单");
            return errorList;
        }

        orderMapper.insertOrder(order);
        logger.info("插入完成");
        order.setStatus("P"); //默认状态：待支付
        orderMapper.insertOrderStatus(order, afterLineNum); //订单有4个阶段 现在插入的是第1个阶段

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

    @Override
    @Transactional //事务 注意要按相同顺序执行，避免死锁
    @Async //异步执行
    public void checkPaymentSuccess(int orderId) {
        StringBuffer temp = new StringBuffer(AlipayConfig.TIMEOUT_EXPRESS);
        int timeOutMinutes = Integer.parseInt(temp.substring(0, temp.length()-1));
        try {
            //在截止时间到的时候 查看支付是否成功
            Thread.sleep(timeOutMinutes * 6000); //毫秒为单位
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Order order = getOrder(orderId);
        if("P".equals(order.getStatus())){ //检查订单状态是否仍为待支付
            logger.warn("订单 " + orderId + " 逾期未支付，订单取消");

            //把减少的库存都加回去
            List<LineItem> lineItems = order.getLineItems();
            for (int i = 0; i < lineItems.size(); i++) {
                LineItem lineItem = lineItems.get(i);
                String itemId = lineItem.getItemId();
                Integer increment = Math.abs(lineItem.getQuantity()) * (-1); //负数形式代表添加
                Map<String, Object> param = new HashMap<>(2);
                param.put("itemId", itemId);
                param.put("increment", increment);
                itemMapper.updateInventoryQuantity(param);
            }

            //先锁表 ORDERS 和 ORDERSTATUS, 再锁表 LINEITEM 若顺序相反可能会导致死锁
            orderMapper.deleteOrderByOrderId(orderId);
            lineItemMapper.deleteLineItemsByOrderId(orderId);

            return;
        }

        logger.info("订单 " + orderId + " 已支付，验证通过");
    }

    @Override
    public void setOrderStatus(Order order) {
        orderMapper.setOrderStatus(order);
    }
}

