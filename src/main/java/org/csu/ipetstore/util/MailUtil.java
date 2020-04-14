package org.csu.ipetstore.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
public class MailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    //这一步是获取application.properties中设置的发件人邮箱地址
    @Value("${spring.mail.username}")
    private String username;

    //发送邮件(接收三个前台传过来的参数:addressee 收件人地址、theme 邮件主题、text 邮件正文)
    //@RequestMapping("sendMail")
    public void sendMail(String addressee,String theme,String text) {
        //发邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //发件人邮件地址(上面获取到的，也可以直接填写,string类型)
        message.setFrom(username);
        //收件人邮件地址
        message.setTo(addressee);
        //邮件主题
        message.setSubject(theme);
        //邮件正文
        message.setText(text);
        javaMailSender.send(message);
    }
}
