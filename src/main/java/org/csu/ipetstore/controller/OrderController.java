package org.csu.ipetstore.controller;

import com.alibaba.fastjson.JSON;
import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.domain.LineItem;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Hydra
 * @date 2020/4/5
 */

@Controller
public class OrderController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderService orderService;

    //个人信息填写页面
    @GetMapping("/order/accountInfo")
    public String viewAccountInfo(@RequestParam(value = "status", defaultValue = "default")String status,
                                HttpSession session,
                                Model model){
        Order order = (Order)session.getAttribute("order");
        Account account = (Account) session.getAttribute("account");
        Cart cart = (Cart)session.getAttribute("cart");

        if(account == null){
            session.setAttribute("dispatcher", "checkout");
            logger.info("用户结账前需先登陆");

            return "redirect:/login";
        }
        else if(cart == null || cart.isEmpty()){
            logger.warn("购物车为空不能结账");

            return "redirect:/cart";
        }
        else {
            //重新创建订单
            if("default".equals(status) || order == null){
                order = new Order();
                order.initOrder(account, cart);
                session.setAttribute("order", order);
                logger.info("创建新订单");
            }else{
                logger.info("继续未完成订单");
            }
            model.addAttribute("order", order);

            //如果选择继续先前的操作，则什么也不变
            return "order/accountInfo";
        }
    }

    @PostMapping("/order/accountInfo")
    public String confirmAccountInfo(@ModelAttribute Order order,
                                     Boolean isShip,
                                     HttpSession session){
        //TODO:检测库存状况
        Account account = (Account) session.getAttribute("account");
        Cart cart = (Cart)session.getAttribute("cart");


        order.initOrder(account, cart);

        session.setAttribute("order", order);
        logger.info("订单：确认用户信息");

        if(isShip == null || isShip == false){
            return "redirect:/order/lineItems?isShip=false";
        }
        return "redirect:/order/lineItems";
    }

    //购物列表确认页面
    @GetMapping("/order/lineItems")
    public String viewLineItems(@RequestParam(value = "isShip", defaultValue = "true")String isShip,
                                HttpSession session,
                                Model model){
        if(session.getAttribute("account") == null){
            logger.info("用户结账前需先登陆");
            return "redirect:/login";
        }

        Order order = (Order) session.getAttribute("order");

        if(order.getLineItems().size() <= 0){
            logger.warn("未确认购物车列表，返回购物车");
            return "redirect:/cart";
        }

        model.addAttribute("order", order);
        model.addAttribute("isShip", isShip);

        return "order/lineItems";
    }

    @ResponseBody
    @PostMapping("/order/lineItems")
    public String confirmOrder(HttpSession session){
        Order order = (Order) session.getAttribute("order");
        if(order == null){
            logger.warn("订单信息为空，返回个人信息填写页面");
            return "/order/accountInfo|";
        }
        if(order.getLineItems().size() <= 0){
            logger.warn("未确认购物车列表，返回购物车");
            return "/cart|";
        }

        List<LineItem> errorList = orderService.insertOrder(order);

        //异步执行，检查在指定时间内是否完成支付，若未完成，删除订单
        logger.debug("开始异步执行");
        orderService.checkPaymentSuccess(order.getOrderId());
        logger.debug("结束异步执行");

        //跳转到支付宝支付
        String strRedirect = "/alipay/easy_pay/" + order.getOrderId() + "|";

        //清空session中 购物车、订单
        session.setAttribute("cart", new Cart());
        session.setAttribute("order", null);

        if(errorList.isEmpty()){
            return strRedirect;
        }
        else if(order.getLineItems().size() <= 0){
            logger.warn("全部购买项均无货");
            strRedirect = "/main.html|";
            return strRedirect + JSON.toJSONString(errorList);
        }
        else{
            logger.warn("部分货物购买失败");
            return strRedirect + JSON.toJSONString(errorList);
        }
    }

    @GetMapping("/order/{oid}")
    public String viewOrder(@PathVariable("oid")String oid, HttpSession session, Model model){
        Order reviewOrder = orderService.getOrder(Integer.parseInt(oid));

        if(reviewOrder == null){
            //TODO:反馈信息给顾客
            logger.error("试图访问不存在订单");
            return "redirect:/orders";
        }
        model.addAttribute("order", reviewOrder);
        logger.info("查看历史订单 " + reviewOrder.getOrderId());

        return "order/reviewHistoryOrder";
    }

    @GetMapping("/orders")
    public String viewOrderList(HttpSession session, Model model){
        Account account = (Account) session.getAttribute("account");
        if(account == null){
            session.setAttribute("dispatcher", "checkOrderList");
            logger.warn("用户session已过期 请重新登录");
            return "account/login";
        }

        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());
        model.addAttribute("orderList", orderList);
        logger.info("查看历史订单列表");

        return "order/listOrders";
    }
}
