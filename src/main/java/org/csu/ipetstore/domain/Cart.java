package org.csu.ipetstore.domain;

//import com.ibatis.common.util.PaginatedArrayList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class Cart implements Serializable {

    private static final long serialVersionUID = 8329559983943337176L;
    private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new HashMap<>());
    private final List<CartItem> itemList = new ArrayList<>();


    public void setCartByCartItemList(List<CartItem> cartItemList){

        //必须是新初始化的对象才能调用
        if(!itemList.isEmpty() || cartItemList == null){
            System.out.println("ERROR:CART");
            return;
        }
        for(CartItem cartItem : cartItemList){
            itemList.add(cartItem);
            itemMap.put(cartItem.getItem().getItemId(), cartItem);
        }
    }

    public List<CartItem> getCartItemList() {
        return itemList;
    }

    public int getNumberOfItems() {
        return itemList.size();
    }

    public Iterator<CartItem> getAllCartItems() {
        return itemList.iterator();
    }

    public boolean containsItemId(String itemId) {
        return itemMap.containsKey(itemId);
    }

    public void addItem(Item item, boolean isInStock) {
        CartItem cartItem = itemMap.get(item.getItemId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(0);
            cartItem.setInStock(isInStock);
            itemMap.put(item.getItemId(), cartItem);
            itemList.add(cartItem);
        }
        cartItem.incrementQuantity();
    }

    public Item removeItemById(String itemId) {
        CartItem cartItem = itemMap.remove(itemId);
        if (cartItem == null) {
            return null;
        } else {
            itemList.remove(cartItem);
            return cartItem.getItem();
        }
    }

    public void incrementQuantityByItemId(String itemId) {
        CartItem cartItem = itemMap.get(itemId);
        cartItem.incrementQuantity();
    }

    public void setQuantityByItemId(String itemId, int quantity) {
        CartItem cartItem = itemMap.get(itemId);
        cartItem.setQuantity(quantity);
    }

    public BigDecimal getSubTotal() {
        BigDecimal subTotal = new BigDecimal("0");
        Iterator<CartItem> items = getAllCartItems();
        while (items.hasNext()) {
            CartItem cartItem = items.next();
            Item item = cartItem.getItem();
            BigDecimal listPrice = item.getListPrice();
            BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(listPrice.multiply(quantity));
        }
        return subTotal;
    }

    public CartItem getCartItemByItemId(String itemId) {
        CartItem cartItem = null;
        for (CartItem c : itemList) {
            if (itemId.equals(c.getItem().getItemId())) {
                cartItem = c;
                break;
            }
        }
        return cartItem;
    }

    public boolean isEmpty(){
        return itemList.isEmpty();
    }
}
