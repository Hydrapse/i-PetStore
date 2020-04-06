package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.CartItem;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.mapper.CartItemMapper;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void updateCart(Cart cart, Account account) {
        try{
            cartItemMapper.deleteCartItemList(account);
            if (cart == null || cart.isEmpty()){
                //logger.info("用户 " + account.getUsername() + " 删除购物车");
            }else{
                List<CartItem>cartItemList = cart.getCartItemList();
                cartItemMapper.insertCartItemList(cartItemList,account.getUsername());
                //logger.info("用户 " + account.getUsername() + " 更新购物车");
            }
        }catch (SQLException e){
            //logger.error("用户 " + account.getUsername() +" 更新购物车时出现了错误");
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
                //logger.info("用户 " + account.getUsername() + " 没有历史购物车记录");
                return cart;
            }
            cart.setCartByCartItemList(cartItemList);
        }catch (SQLException e) {
            //logger.error("用户 " + account.getUsername() + " 获取购物车时出现了错误");
            e.printStackTrace();
        }
        return cart;
    }

    @Override
    public Item getItemByProductName(String productName) {
        if(productName == null || productName.equals("")) {
            return null;
        }
        Item rtn = itemMapper.getItemByProductName(productName);
        return rtn;
    }
}
