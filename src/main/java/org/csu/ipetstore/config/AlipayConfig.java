package org.csu.ipetstore.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * @author Hydra
 * @date 2020/4/9
 */

@Configuration
//@ConfigurationProperties(prefix= "alipay")
public class AlipayConfig {

    //汇率 为了方便设为全局变量(主要是沙箱接口有问题)
    public static final BigDecimal USD_TO_CNY = new BigDecimal("7.0503");
    public static final BigDecimal CNY_TO_USD = new BigDecimal("0.14183");

    //内网穿透，每次测试前都需要检查网址是否改变
    public static final String NETURL = "http://ey5paa.natappfree.cc";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，其实就是你的一个支付完成后返回的页面URL
    public static final String NOTIFY_URL = NETURL + "/alipay/notify";

    //页面同步跳转至订单页面，无需全剧配置

    //订单最长未支付时间
    public static final String TIMEOUT_EXPRESS = "30m";

    //支付宝沙箱网关
    public static final String URL = "https://openapi.alipaydev.com/gateway.do";
    // 商户APP_ID
    public static final String APP_ID = "2016102300747347";
    // 商户RSA 私钥
    public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAi5ehCh+yW6IMKdnr9D97mwn9PpZubG8jE9WGC0SkZNCDqhowXCbghwXTXjYKCyihIpJcVS9Yf8hnFVA4YeEodhSUI5DQGhHZ8C5yWyFp8EgYZjiDJphue93nG77jxbZLzbAN4ZXHF5AzR2G6cCqybMfAGlN1Vgt85AHGrOYqp7pZREG3f+MG184gX1CC7BMO8t6DivAjxWnyeMQcqQ39uxmRAb3ZMiwgpRm83oTRd+xwa32LSo9kGVCJWaWAm7C/58+xkmOangxbcw6G1BymRdA8UH9PUN768IysCQLHkcYJ7DQ7fQ6Sq9dfMUcc4hfISHzJYnMJHQVSTOEcwKiJAgMBAAECggEASWeuefWbGes3Pz3hV8Q5Q41a5JIs1ZI/WHciS0UWeTn6CIgcDd3D8ItpXCipn3Cbn7rHY64SK8Iji7EjcStXpI5H0CYy+jNGJPkdK/m0Jmg1hB/MYaugJ1bPxWDdiIaCtimHefNLn6hLEFGhX2uNy5IPIRyoPq58GLPwPWcDJLU9sjog9jDh7chZ31Ne4vb5FWJLyLvu4nZoUD8qEc125cP0u0Twb4ZAcUYG/TuaRamLlYAAkoUBUbLtc4F6T64Npa1s5VFJe9dQxP/pZt3cjMDgViQzVbuqjSk35iioCND2AYH9eUVXvQcCl9Vr/hkUJIJeCh65xE4kbzAYpkywgQKBgQDoA1NTD5ygfK+m+oTag8gxfgVfOXdkoflhA65G7DwsVigrlp7x2ie12Q7W3aRbIZMkj/NF/g24PiT9jHdePmsK4mSl5PqXilQJJyuxNIt88TGYSMVRO8zV9cv94u5uO4HS7+qLD3jCfPpax/CEHQnlvc7YBlCDu4y00lECz7TLsQKBgQDUc61goXRqdkwEmeYT8ASuXMqbfIgBrpm1RspNJTNH24id7DAp1gIcnDcff+a2icUGgtWUQ4VdQ1u6DKSo7veS0gkyj/Gy6HRC6VoZwrZDfFd5LZpkAJySZFeVPJz7Iw9Q8PXgwS6dG4QEpqm8O0q8FMxNzEYEYQWn7+rnBDFYWQKBgCN8nroNoT9K0vMPTlK95VjslZXDDGfv/lzAW1+tsmZ1px9DonwLihdeY86piAIIWKE3A1apTR/pIW9GlNttYKIBjrNxGoMjPUdJHn20M8Gggp1wQ/3wz5vKRLFIjH9/ypOg7pbmfuoAg6pu+nK6nlDtfz27eYUsopDluk5bxqOBAoGBAKCUzve7EcNmpw7TRJTNVrx0ZrAf8K1Uy9m5KDAnbhtiVGEJpV355J67dO7aPgi7GbFSMx1d6/ASecKmy87k0TyohUsygf44tafduby+8AUSdwug7JGp5K7CKXEj34PzfCAyAadgXVti8OaTEri0GEkRb1tei43DyAKn1WXIRYQBAoGBAJFtDqsZQ2+UKdkuE462C3L6F7agfYoDSYtIHShCVnxmfqOXqDngp+ayRtJUSP7bSCOcEHeNo6UZuX2E55s9ro03A53Jpm+YUukhylL/75f02knl5jSX7p+ZeGCe9Da70Opu6zQz8a5U1hhRs67dG+GwLccQo1CEeQK+OMKf9+bU";
    // 请求方式 json
    public static final String FORMAT = "json";
    // 编码格式，目前只支持UTF-8
    public static final String CHARSET = "UTF-8";
    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmA9KBxrnuaFf4z695cO8eL+H/Xdx5R2gQCuv3CTxQOHgefyFNMDLrsZqcI9t2RCQNOE+R8enHPxC0TqwTDF83U3ceUiWWkanetducK/rEbUI35VsyAIqE1DsfSeRCIRJnOStiLjBxyklqjukUlgOaTqC0i8P9hr58WJ+daJ3ECAxK3xMCMeLAVkC84anG6l0XGpcOKBqim2QdDYAnriE/wWAkuIIyzs+GSm8zalox+2uXfz4HZMQvakCZvPsVKNAUhrD6G/RayJJmtDasei8HsU8TV6C8Q2YCpRdIQRbo0sffrNNa91G+S3UKvXD5lJ/oggSoOfSaQwr8eX5tr/sBQIDAQAB";
    // 签名方式
    public static final String SIGN_TYPE = "RSA2";

    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }
}
