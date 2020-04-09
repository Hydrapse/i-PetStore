package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.CartItem;
import org.csu.ipetstore.domain.Item;

import java.util.List;

public interface CartService {
    void updateCart(Cart cart, Account account);

    Cart getCartByAccount(Account account);

    Item getItemByProductName(String productName);

    void checkInventoryByCartItemList(List<CartItem> cartItemList);
}
