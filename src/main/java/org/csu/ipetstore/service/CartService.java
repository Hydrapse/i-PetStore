package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.Item;

public interface CartService {
    void updateCart(Cart cart, Account account);

    Cart getCartByAccount(Account account);

    Item getItemByProductName(String productName);
}
