package org.csu.ipetstore;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.mapper.LineItemMapper;
import org.csu.ipetstore.mapper.OrderMapper;
import org.csu.ipetstore.service.payment.PayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IPetStoreApplicationTests {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    LineItemMapper lineItemMapper;

    @Test
    void test(){
        StringBuffer temp = new StringBuffer("30m");
        int timeOutMinutes = Integer.parseInt(temp.substring(0, temp.length()-1));
        System.out.println(timeOutMinutes);
    }

    @Test
    void deleteOrder(){
        int orderId = 1046;
//        orderMapper.deleteOrderByOrderId(orderId);
        lineItemMapper.deleteLineItemsByOrderId(orderId);
    }

    @Test
    void contextLoads() {
//        System.out.println(String.join("|", Arrays.asList(
//                "history-cart-true-current-empty", "222")));
        org.csu.ipetstore.domain.Order order = new Order();
        System.out.println(order.getOrderId());

    }

    @Test
    void aliPay(){
        PayService payService = new PayService();
        AlipayTradePagePayResponse alipayResponse = (AlipayTradePagePayResponse) payService.alipayTradePagePayService();
        String str = responseFormat(JSONObject.toJSONString(alipayResponse));
        System.out.println(str);
    }



    private String responseFormat(String resString){

        StringBuffer jsonForMatStr = new StringBuffer();
        int level = 0;
        for(int index=0;index<resString.length();index++)//将字符串中的字符逐个按行输出
        {
            //获取s中的每个字符
            char c = resString.charAt(index);

            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0  && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

}
