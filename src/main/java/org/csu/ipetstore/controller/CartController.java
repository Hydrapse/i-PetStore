package org.csu.ipetstore.controller;

import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.CartItem;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.service.CartService;
import org.csu.ipetstore.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * @author Hydra
 * @date 2020/4/4
 */

@Controller
public class CartController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CartService cartService;

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/cart")
    public String viewCart(HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        logger.info("访问购物车页面");

        return "cart/cart";
    }

    @ResponseBody
    @PatchMapping("/cart")
    public String updateCart(@RequestParam("type")String type,
                             @RequestParam(value = "workingItemId", defaultValue = "")String wid,
                             @RequestParam(value = "itemQuantity", defaultValue = "0")Integer quantity,
                             @RequestParam(value = "productName", defaultValue = "")String productName,
                             HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
            logger.info("创建新的购物车");
        }


        //新增单件物品至购物车
        if("add".equals(type)) {

            //通过名字查找wid(cart页面中的自动补全)
            if (!StringUtils.isEmpty(productName)) {
                Item item = cartService.getItemByProductName(productName);
                if (item == null) {
                    logger.warn("产品 " + productName + " 不存在");
                    return "ItemNotFound";
                }
                wid = item.getItemId();
            }

            int stockQuantity = catalogService.getItemQuantityById(wid);
            //TODO:当货物不足时缺货提醒
            if(stockQuantity <= 0){
                logger.info("货物id: " + wid + " 缺货, 暂时无法加入购物车" );
                return "lackItem";
            }

            if (cart.containsItemId(wid)) {
                int workingItemQuantity = cart.getCartItemByItemId(wid).getQuantity();
                //TODO:当货物不足时缺货提醒
                if(workingItemQuantity + 1 > stockQuantity){
                    logger.info("货物id: " + wid + " 缺货, 暂时无法加入购物车" );
                    return "lackItem";
                }
                else{
                    cart.incrementQuantityByItemId(wid);
                }
            }
            else {
                Item item = catalogService.getItem(wid);
                cart.addItem(item, true);
            }
            logger.info("添加item " + wid + " 至购物车");

            return "success";
        }

        //从购物车中移除某件物品
        else if("remove".equals(type)){
            if("ClearAll".equals(wid)){
                session.setAttribute("cart", null);

                return "redirect";
            }

            Item item = cart.removeItemById(wid);
            if (item == null) {
                session.setAttribute("message", "Attempted to remove null CartItem from Cart");
                logger.warn("试图将空购物车项移出购物车");

                return "error";
            } else {
                logger.info("将产品 " + wid + " 移出购物车");

                return "success";
            }
        }

        //改变购物车中某件物品的数量
        else if("update".equals(type)){
            CartItem cartItem = cart.getCartItemByItemId(wid);

            if (StringUtils.isEmpty(wid) || cartItem == null) {
                session.setAttribute("message", "Attempted to update invalid cart items");
                logger.error("试图更新不存在购物车项数据");
            }
            else {
                cart.setQuantityByItemId(wid, quantity);
                logger.info("Change item:'" + wid + "' 's quantity to " + quantity);

                return String.join("?", Arrays.asList(
                        wid,
                        String.valueOf(cartItem.getTotal()),
                        String.valueOf(cart.getSubTotal())));
            }
        }

        //type为空或者有误
        logger.error("更新购物车的type属性有误，为：" + type);

        return "error:unknownType";
    }
}
