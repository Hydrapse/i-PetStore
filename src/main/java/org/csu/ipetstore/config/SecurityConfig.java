package org.csu.ipetstore.config;

import com.alibaba.fastjson.JSON;
import org.csu.ipetstore.domain.result.CommonResult;
import org.csu.ipetstore.domain.result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;

/**
 * @author Hydra
 * @date 2020/6/20
 * @description: Spring Security 配置类
 */


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *  springboot2.x引入的security版本是5.x的，这个版本需要提供一个PasswordEncoder实例，不然就会报错
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable() //关闭csrf跨域访问验证
        .authorizeRequests()
            .antMatchers("/manager/**").authenticated()
            .anyRequest().permitAll()
            .and()
        .formLogin()
            .loginPage("/authentic/unverified")
            .loginProcessingUrl("/authentic/login") //登录处理接口
            .permitAll()
            .successHandler((req, resp, authentication) -> {
                resp.setContentType("application/json;charset=utf-8");
                String json = JSON.toJSONString(CommonResult.success("认证通过 登录成功"));
                PrintWriter out = resp.getWriter();
                out.write(json);
                out.flush();
            })
            .failureHandler((req, resp, exception) -> {
                resp.setContentType("application/json;charset=utf-8");
                String json = JSON.toJSONString(CommonResult.failure(
                        ResultCode.USER_ACCOUNT_ERROR, "账户密码错误"));
                PrintWriter out = resp.getWriter();
                out.write(json);
                out.flush();
            })
            .and()
        .logout()
            .logoutUrl("/authentic/logout")
            .permitAll();
    }

    /**
     * 设置全局登录账号密码
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                PasswordEncoder passwordEncoder) throws Exception {
        //密码进行bcrypt加密
        String pwd = "123";
        String cryptPwd = passwordEncoder.encode(pwd);
        System.out.println(cryptPwd);
        auth
            .inMemoryAuthentication()
            .withUser("admin").password(cryptPwd).roles("USER");
    }
}