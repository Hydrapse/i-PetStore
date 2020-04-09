package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.CartItem;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.mapper.CartItemMapper;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void updateCart(Cart cart, Account account) {
        try{
            cartItemMapper.deleteCartItemList(account);
            if (cart == null || cart.isEmpty()){
                logger.info("用户 " + account.getUsername() + " 删除购物车");
            }else{
                List<CartItem>cartItemList = cart.getCartItemList();
                cartItemMapper.insertCartItemList(cartItemList,account.getUsername());
                logger.info("用户 " + account.getUsername() + " 更新购物车");
            }
        }catch (SQLException e){
            logger.error("用户 " + account.getUsername() +" 更新购物车时出现了错误");
            e.printStackTrace();
        }
    }

    @Override
    public Cart getCartByAccount(Account account) {
        Cart cart = new Cart();
        try{
            List<CartItem> cartItemList = cartItemMapper.getCartItemList(account);
            if(cartItemList.isEmpty()){
                System.out.println("is empty");
                logger.info("用户 " + account.getUsername() + " 没有历史购物车记录");
                return cart;
            }
            checkInventoryByCartItemList(cartItemList);
            cart.setCartByCartItemList(cartItemList);
        }catch (SQLException e) {
            logger.error("用户 " + account.getUsername() + " 获取购物车时出现了错误");
            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public Item getItemByProductName(String productName) {
        if(productName == null || productName.equals("")) {
            return null;
        }
        //TODO: 购物车查询列表，存在一个productName对应多个item
        Item rtn = null;
        List<Item> itemList = itemMapper.getItemListByProductName(productName);
        if(!itemList.isEmpty()){
            rtn = itemList.get(0);
        }
        return rtn;
    }

    @Override
    public void checkInventoryByCartItemList(List<CartItem> cartItemList) {
        for(CartItem cartItem : cartItemList){
            String itemId = cartItem.getItem().getItemId();
            int qty = itemMapper.getInventoryQuantity(itemId);
            cartItem.getItem().setQuantity(qty); //重新设置库存, 这个代表的是这一时刻的单项总库存

            if(qty == 0){
                cartItem.setInStock(false);
                cartItem.setQuantity(Math.abs(cartItem.getQuantity())); //这个设置的是购物车单项购买个数
                logger.info("货物 " + itemId +" 库存为空");
            }
            else if(Math.abs(cartItem.getQuantity()) > qty){

                //用负数代表缺货的商品，其绝对值为所能购买的个数
                //注意！负数代表有货物，但是并没有所需要的那么多，并不是完全没货
                cartItem.setInStock(true);
                cartItem.setQuantity(qty * -1);
                logger.info("货物 " + itemId +" 库存不足, 剩余: " + qty);
            }
            else{
                cartItem.setInStock(true);
                logger.info("货物 " + itemId +" 库存充足");
            }
        }
    }
}
