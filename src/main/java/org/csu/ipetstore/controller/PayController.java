package org.csu.ipetstore.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.csu.ipetstore.config.AlipayConfig;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.service.AlipayService;
import org.csu.ipetstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Hydra
 * @date 2020/4/10
 */

@RestController
public class PayController {
    Logger logger = LoggerFactory.getLogger("PayController.class");

    @Autowired
    AlipayService aliPayService;

    @Autowired
    OrderService orderService;

    //提交支付请求必须保证'订单不为空'且状态为'待支付'
    @GetMapping("/alipay/easy_pay/{oid}")
    public String alipay(@PathVariable("oid")int oid,
                         HttpSession session,
                         HttpServletResponse response){
        Order order = orderService.getOrder(oid); //TODO：这个应该在缓存中取出
        if(order == null || !"P".equals(order.getStatus())){
            logger.warn("订单为空或订单状态不为待支付，请按照正常流程下单");
            try {
                response.sendRedirect("/main.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "订单为空或订单状态不为待支付，请按照正常流程下单";
        }

        //先获取请求体，再将order滞空，方便下一个订单的进行
        String body = aliPayService.tradePagePayForm(order);

        //正常将订单信息提交给支付宝
        return body;
    }

    //异步是post
    @PostMapping("/alipay/notify")
    public String notifyAlipay(HttpServletRequest request) {
        logger.info("----notify-----");

        //官方推荐代码
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            /*try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            params.put(name, valueStr);
        }
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
        } catch (AlipayApiException e) {
            logger.warn("异步验签出现异常");
            e.printStackTrace();
        }

	/* 实际验证过程建议商户务必添加以下校验：
	1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
	2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
	3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
	4、验证app_id是否为该商户本身。
	*/
	    logger.info(params.get("trade_status"));

        if(signVerified) { //验证成功
            //总金额
            BigDecimal total_amount = new BigDecimal(params.get("total_amount"));

            //交易状态
            String trade_status = params.get("trade_status");

            //商户订单号
            String out_trade_no = params.get("out_trade_no");
            logger.info("订单 " + out_trade_no + " 验签成功");

            Order order = orderService.getOrder(Integer.parseInt(out_trade_no));
            if(order == null){
                if(total_amount.intValue() > 0){
                    logger.error("注意：订单 " + out_trade_no + " 已过期或被取消，然而却有钱款 " +
                            total_amount + " 到账, 应查看日志记录联系顾客");
                }
                return "success"; //支付完成，异常于平台内处理
            }

            //订单中的总金额换算成人民币
            BigDecimal order_total = order.getTotalPrice().multiply(AlipayConfig.USD_TO_CNY).setScale(2, RoundingMode.FLOOR);

            //若两个数不相等则不为0
            if(total_amount.compareTo(order_total) != 0){
                logger.error("订单 " + out_trade_no + " 预期支付价格与实际支付价格不相等。预期支付价格为 " +
                        order_total + " 实际支付价格为 " + total_amount + " 请核实是否使用优惠券");
                return "amount_not_match"; //返回值除了success外无意义，仅代表不接受此次交易
            }

            //TODO: 实验二时进行完善
            if(trade_status.equals("TRADE_SUCCESS")){
                //支付成功，可以退款
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                String order_status = order.getStatus();
                if(order_status.equals("P")){ //该订单未做过处理，仍为待支付状态
                    order.setStatus("S"); //状态设置为支付成功
                    orderService.setOrderStatus(order);
                    logger.info("设置订单 " + out_trade_no + " 状态为支付成功");
                }
                else{ //该订单已处理过
                    logger.info("订单 " + out_trade_no + " 已经被处理过, 状态为 " + order_status);
                }
            }else if (trade_status.equals("TRADE_FINISHED")){
                //付款完成后，支付宝系统发送该交易状态通知
                logger.info("订单 " + out_trade_no + "交易完成，不可退款");
            }

            return "success";
        }
        else { //验证失败
            logger.warn("异步返回消息验签未通过");
            return "fail";
        }
    }

}
