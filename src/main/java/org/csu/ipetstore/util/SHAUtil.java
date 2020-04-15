package org.csu.ipetstore.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class SHAUtil {
    public static final String KEY_SHA = "SHA";
    public static final String ra="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 字符串sha加密
     * @param s 要加密的字符串
     * @return 加密后的字符串
     */
    public static String sha(String s)
    {
        BigInteger sha =null;
        byte[] bys = s.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(bys);
            sha = new BigInteger(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha.toString(48);
    }

    /**
     * 字符串+随机盐 sha加密
     * @param s 要加密的字符串
     * @return 盐和加密后的字符串
     */
    public static Map<String,String> getResult(String s){
        Map<String,String> map=new HashMap<String,String>();
        String salt=getSalt();
        map.put("salt", salt);//盐
        map.put("password", sha(s+salt));//加密后的密码
        return map;
    }

    public static String getResult(String pwd,String salt){
        return sha(pwd + salt);
    }

    /**
     * 生成随机盐
     * @return 随机生成的盐
     */
    private static String getSalt() {
        SecureRandom random=new SecureRandom();
        int length=random.nextInt(5)+10;//盐的长度，这里是8-12可自行调整
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = ra.charAt(random.nextInt(ra.length()));
        }
        return new String(text);
    }

    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(getResult("12345678"));
    }
}
