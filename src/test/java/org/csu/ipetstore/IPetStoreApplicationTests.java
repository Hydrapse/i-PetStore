package org.csu.ipetstore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.csu.ipetstore.domain.Order;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.mapper.LineItemMapper;
import org.csu.ipetstore.mapper.OrderMapper;
import org.csu.ipetstore.service.ManagerService;
import org.csu.ipetstore.service.payment.PayService;
import org.csu.ipetstore.util.OSSClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class IPetStoreApplicationTests {

    @Autowired
    ManagerService managerService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    LineItemMapper lineItemMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    OSSClientUtil ossClientUtil;

    @Test
    void deleteTest(){
        Object o = managerService.deleteProduct("AV-CB-01");
        System.out.println(JSON.toJSONString(o));
    }

    @Test
    void ossTest(){
        String str = "https://i-petstore.oss-cn-shenzhen.aliyuncs.com/images/product/bird1.gif?Expires=1592374052&OSSAccessKeyId=TMP.3KhkiNRJw2wAqdq41zgkR6d2FoRz7biojNfszCXPXzaKt3vgcJ859JpRzhv7ve6uDH4rxq3UJovQmy28tdTBGEHHfkNCPr&Signature=%2F%2BSWbe6R2DVai4glzhbxigNVVck%3D";
        System.out.println(ossClientUtil.getFileName(str));
    }

    @Test
    void test(){
        StringBuffer temp = new StringBuffer("30m");
        int timeOutMinutes = Integer.parseInt(temp.substring(0, temp.length()-1));
        System.out.println(timeOutMinutes);
    }

    @Test
    void contextLoads() {
        List<Order> list = orderMapper.getOrdersByItemId("EST-20", Order.SUCCEED);
        System.out.println(list.size());
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
