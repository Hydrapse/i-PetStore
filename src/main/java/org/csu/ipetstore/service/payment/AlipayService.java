package org.csu.ipetstore.service.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.csu.ipetstore.config.AlipayConfig;
import org.csu.ipetstore.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hydra
 * @date 2020/4/10
 */

@Service
public class AlipayService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AlipayClient alipayClient;

    public String tradePagePayForm(Order order){

        // 创建Alipay支付请求对象
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setNotifyUrl(AlipayConfig.NOTIFY_URL);
        alipayRequest.setReturnUrl(AlipayConfig.NETURL + "/order/" + order.getOrderId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("out_trade_no", order.getOrderId());
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");
        params.put("total_amount", //USD转换为CNY, 精度设置为2位小数
                order.getTotalPrice().multiply(AlipayConfig.USD_TO_CNY)
                     .setScale(2, RoundingMode.FLOOR));
        params.put("subject", "宠物订单");
//        params.put("body", "美味的烤鹦鹉");
        params.put("store_id", "DL_001");
        params.put("goods_type", "1"); //实体
        params.put("qr_pay_mode", "2"); //跳转模式
        params.put("integration_type", "PCWEB"); //请求后页面集成方式
        params.put("timeout_express", AlipayConfig.TIMEOUT_EXPRESS); //就尼玛离谱
//        params.put("time_expire", "2020-04-11 10:05:01");

        alipayRequest.setBizContent(JSONObject.toJSONString(params));
        String webForm = "";// 输出页面的表单
        try {
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            webForm = alipayResponse.getBody();
            logger.warn("支付请求发送成功");
        } catch (Exception e) {
            logger.info("支付请求出错", e);
        }
        return webForm;
    }

}
