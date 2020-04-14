package org.csu.ipetstore.service.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/*
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.50</version>
</dependency>
<dependency>
	<groupId>com.alipay.sdk</groupId>
	<artifactId>alipay-sdk-java</artifactId>
	<version>3.3.49.ALL</version>
</dependency>
*/
public class PayService {
    private static final Logger logger = LoggerFactory.getLogger(PayService.class);

    public Object alipayTradePagePayService(){
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		//请确认是否在工程中引入了fastjson
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("out_trade_no", "12345");
		params.put("product_code", "FAST_INSTANT_TRADE_PAY");
		params.put("total_amount", "100.00");
		params.put("subject", "iphone");
		params.put("body", "手机");
		params.put("store_id", "DL_001");
		params.put("goods_type", "1");
		params.put("qr_pay_mode", "2"); //跳转模式
 		params.put("integration_type", "PCWEB"); //请求后页面集成方式
		params.put("timeout_express", "30m"); //就尼玛离谱
		//params.put("time_expire", "2020-04-11 10:05:01");

//		params.put("goods_detail", ""); //需填写
//		params.put("passback_params", "业务参数"); //公共回传参数
//		params.put("extend_params", "业务参数"); //业务扩展参数
//		params.put("promo_params", "业务参数");
//		params.put("royalty_info", "业务参数");
//		params.put("sub_merchant", "业务参数");
//		params.put("merchant_order_no", "业务参数"); // 商户原始订单号
//		params.put("enable_pay_channels", "业务参数"); //指定支付渠道
//		params.put("disable_pay_channels", "业务参数"); //禁用支付渠道
//		params.put("qrcode_width", "业务参数");
//		params.put("settle_info", "业务参数"); //描述结算信息
//		params.put("invoice_info", "业务参数"); //开票信息
//		params.put("agreement_sign_params", "业务参数"); //签约参数
//		params.put("request_from_url", "https://www.hydrapse.com"); //请求来源地址？好像web端无用，应该是支付失败退回去
//		params.put("business_params", "业务参数"); //业务信息
//		params.put("ext_user_info", "业务参数"); //外部制定买家

	//设置业务参数，bizContent为发送的请求信息，开发者需要根据实际情况填充此类
	Object obj = JSONObject.toJSON(params);
	alipayRequest.setBizContent(obj.toString());
	//sdk请求客户端，已将配置信息初始化
    AlipayClient alipayClient = getAlipayClient();
	AlipayTradePagePayResponse alipayResponse = null;
	try {
		//因为是接口服务，使用exexcute方法获取到返回值
		alipayResponse = alipayClient.pageExecute(alipayRequest);

		if(alipayResponse.isSuccess()){
			logger.info("调用成功");
			//TODO 实际业务处理，开发者编写。可以通过alipayResponse.getXXX的形式获取到返回值
		} else {
			logger.info("调用失败");
		}
		return alipayResponse;				
		} catch (AlipayApiException e) {
		    if(e.getCause() instanceof java.security.spec.InvalidKeySpecException){
		        return "商户私钥格式不正确，请确认配置文件alipay_openapi_sanbox.properties中是否配置正确";
		    }
		}
		return alipayResponse;				
    }

    public static AlipayClient getAlipayClient() {
//    		ResourceBundle bundle = ResourceBundle.getBundle("alipay_openapi_sanbox");
    		// 网关
    		String URL = "https://openapi.alipaydev.com/gateway.do";
    		// 商户APP_ID
    		String APP_ID = "2016102300747347";
    		// 商户RSA 私钥
    		String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAi5ehCh+yW6IMKdnr9D97mwn9PpZubG8jE9WGC0SkZNCDqhowXCbghwXTXjYKCyihIpJcVS9Yf8hnFVA4YeEodhSUI5DQGhHZ8C5yWyFp8EgYZjiDJphue93nG77jxbZLzbAN4ZXHF5AzR2G6cCqybMfAGlN1Vgt85AHGrOYqp7pZREG3f+MG184gX1CC7BMO8t6DivAjxWnyeMQcqQ39uxmRAb3ZMiwgpRm83oTRd+xwa32LSo9kGVCJWaWAm7C/58+xkmOangxbcw6G1BymRdA8UH9PUN768IysCQLHkcYJ7DQ7fQ6Sq9dfMUcc4hfISHzJYnMJHQVSTOEcwKiJAgMBAAECggEASWeuefWbGes3Pz3hV8Q5Q41a5JIs1ZI/WHciS0UWeTn6CIgcDd3D8ItpXCipn3Cbn7rHY64SK8Iji7EjcStXpI5H0CYy+jNGJPkdK/m0Jmg1hB/MYaugJ1bPxWDdiIaCtimHefNLn6hLEFGhX2uNy5IPIRyoPq58GLPwPWcDJLU9sjog9jDh7chZ31Ne4vb5FWJLyLvu4nZoUD8qEc125cP0u0Twb4ZAcUYG/TuaRamLlYAAkoUBUbLtc4F6T64Npa1s5VFJe9dQxP/pZt3cjMDgViQzVbuqjSk35iioCND2AYH9eUVXvQcCl9Vr/hkUJIJeCh65xE4kbzAYpkywgQKBgQDoA1NTD5ygfK+m+oTag8gxfgVfOXdkoflhA65G7DwsVigrlp7x2ie12Q7W3aRbIZMkj/NF/g24PiT9jHdePmsK4mSl5PqXilQJJyuxNIt88TGYSMVRO8zV9cv94u5uO4HS7+qLD3jCfPpax/CEHQnlvc7YBlCDu4y00lECz7TLsQKBgQDUc61goXRqdkwEmeYT8ASuXMqbfIgBrpm1RspNJTNH24id7DAp1gIcnDcff+a2icUGgtWUQ4VdQ1u6DKSo7veS0gkyj/Gy6HRC6VoZwrZDfFd5LZpkAJySZFeVPJz7Iw9Q8PXgwS6dG4QEpqm8O0q8FMxNzEYEYQWn7+rnBDFYWQKBgCN8nroNoT9K0vMPTlK95VjslZXDDGfv/lzAW1+tsmZ1px9DonwLihdeY86piAIIWKE3A1apTR/pIW9GlNttYKIBjrNxGoMjPUdJHn20M8Gggp1wQ/3wz5vKRLFIjH9/ypOg7pbmfuoAg6pu+nK6nlDtfz27eYUsopDluk5bxqOBAoGBAKCUzve7EcNmpw7TRJTNVrx0ZrAf8K1Uy9m5KDAnbhtiVGEJpV355J67dO7aPgi7GbFSMx1d6/ASecKmy87k0TyohUsygf44tafduby+8AUSdwug7JGp5K7CKXEj34PzfCAyAadgXVti8OaTEri0GEkRb1tei43DyAKn1WXIRYQBAoGBAJFtDqsZQ2+UKdkuE462C3L6F7agfYoDSYtIHShCVnxmfqOXqDngp+ayRtJUSP7bSCOcEHeNo6UZuX2E55s9ro03A53Jpm+YUukhylL/75f02knl5jSX7p+ZeGCe9Da70Opu6zQz8a5U1hhRs67dG+GwLccQo1CEeQK+OMKf9+bU";
    		// 请求方式 json
    		String FORMAT = "json";
    		// 编码格式，目前只支持UTF-8
    		String CHARSET = "UTF-8";
    		// 支付宝公钥
    		String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmA9KBxrnuaFf4z695cO8eL+H/Xdx5R2gQCuv3CTxQOHgefyFNMDLrsZqcI9t2RCQNOE+R8enHPxC0TqwTDF83U3ceUiWWkanetducK/rEbUI35VsyAIqE1DsfSeRCIRJnOStiLjBxyklqjukUlgOaTqC0i8P9hr58WJ+daJ3ECAxK3xMCMeLAVkC84anG6l0XGpcOKBqim2QdDYAnriE/wWAkuIIyzs+GSm8zalox+2uXfz4HZMQvakCZvPsVKNAUhrD6G/RayJJmtDasei8HsU8TV6C8Q2YCpRdIQRbo0sffrNNa91G+S3UKvXD5lJ/oggSoOfSaQwr8eX5tr/sBQIDAQAB";
    		// 签名方式
    		String SIGN_TYPE = "RSA2";
    		return new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    	}}