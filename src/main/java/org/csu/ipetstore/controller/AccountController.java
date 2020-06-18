package org.csu.ipetstore.controller;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.Cart;
import org.csu.ipetstore.service.AccountService;
import org.csu.ipetstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Hydra
 * @date 2020/3/25
 */

@Controller
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private CartService cartService;

    @GetMapping("/login")
    public String loginForm(HttpSession session){
        session.setAttribute("verification", "false");

        if (session.getAttribute("account") != null) {
            logger.warn("已经登录过，切换账号请先注销");
            return "redirect:/profile";
        }

        logger.info("进入登录界面");
        return "account/login";
    }

    //在REST风格中，登陆创建了session资源，故使用Post
    @ResponseBody
    @PostMapping("/login")
    public String login(HttpSession session, Account account){

        //调试
        logger.debug(account.getPassword());

        //检测是否已经登录
        if (session.getAttribute("account") != null) {
            logger.warn("已经登录 切换用户请先注销");
            return "alreadyLogin";
        }

        //检测是否通过验证
        String test = (String) session.getAttribute("verification");
        session.setAttribute("verification", "false");
        if (test == null || !test.equals("true")){
            logger.warn("请先填写验证码");
            return "false";
        }

        String dispatcher = (String) session.getAttribute("dispatcher");
        session.setAttribute("dispatcher", "main");
        if (StringUtils.isEmpty(dispatcher)){
            dispatcher = "main";
        }

        Account confirmAccount = accountService.getAccount(account);
        if (confirmAccount != null) {
            session.setAttribute("account", confirmAccount);
            logger.info("用户 " + account.getUsername() +" 登录成功");

            Cart historyCart = cartService.getCartByAccount(account);
            Cart currentCart = (Cart) session.getAttribute("cart");
            logger.debug("购物车取出成功");

            if(!historyCart.isEmpty()){
                if(currentCart==null || currentCart.isEmpty()){
                    session.setAttribute("cart", historyCart);
                    logger.info("当前购物车为空");

                    return String.join("|", Arrays.asList(
                            "history-cart-true-current-empty", dispatcher));
                } else{
                    logger.info("当前购物车不为空");

                    return String.join("|", Arrays.asList(
                            "history-cart-true-current-notEmpty", dispatcher));
                }
            } else {
                logger.info("无历史购物车");

                return String.join("|", Arrays.asList(
                        "history-cart-false", dispatcher));
            }
        } else {
            logger.info("登录失败");

            return String.join("|", Arrays.asList(
                    "false", dispatcher));
        }
    }

    //TODO: 添加真正的验证码功能
    @ResponseBody
    @PostMapping("/verification")
    public void verify(HttpSession session){
        session.setAttribute("verification", "true");
        logger.info("用户通过验证");
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Map<String, Object> map){
        Account account = (Account) session.getAttribute("account");
        if (account == null){
            return "redirect:/login";
        }
        map.put("account", account);
        return "account/profile";
    }

    @PutMapping("/profile")
    public String confirmEdit(HttpSession session, Account account){
        Account preAccount = (Account) session.getAttribute("account");

        if (preAccount == null) {
            logger.warn("用户session已过期 请重新登录");

            return "redirect:/login";
        }

        //检测是否通过验证
        String test = (String) session.getAttribute("verification");
        session.setAttribute("verification", "false");
        if (test == null || !test.equals("true")){
            logger.warn("请先填写验证码");

            return "redirect:/profile";
        }

        String password = account.getPassword();
        String repeatedPassword = account.getRepeatedPassword();
        boolean hasChangedPassword = false;
        if (!StringUtils.isEmpty(password) && password.equals(repeatedPassword)) {
            account.setPassword(password);
            hasChangedPassword = true;
        }

        //如果不同需要从数据库中取出banner图片资源位置
        String preCategory = preAccount.getFavouriteCategoryId();
        String newCategory = account.getFavouriteCategoryId();
        if(!newCategory.equals(preCategory)){
            account.setBannerName(accountService.getBannerName(newCategory));
            logger.info("重新设置横幅");
        }

        accountService.updateAccount(account);
        session.setAttribute("account", account);
        logger.info("用户 " + account.getUsername() + " 个人信息修改成功");

        if (hasChangedPassword) {
            logger.info("更改密码后需重新登录");
            session.setAttribute("account", null);
            return "redirect:/login";
        }
        return "redirect:/profile";
    }

    @GetMapping("/signOut")
    public String signOut(HttpSession session){
        Account account = (Account) session.getAttribute("account");
        session.setAttribute("account", null);

        if(account == null){
            logger.warn("账号为空仍然发送退出登陆请求");
        }
        else{
            Cart cart = (Cart) session.getAttribute("cart");
            cartService.updateCart(cart, account);
            session.setAttribute("cart", null); //初始化购物车
            session.setAttribute("order", null); //初始化订单
            logger.info("用户" +account.getUsername() + "进行注销");
        }

        return "redirect:/main.html";
    }

    //检查用户名是否可用
    @ResponseBody
    @GetMapping("/username")
    public String checkUserName(@RequestParam("username-reg")String username){
        if(StringUtils.isEmpty(username)){
            return "empty";
        }
        else if(accountService.isUsernameExist(username)) {
            return "deny";
        }else{
            return "confirm";
        }
    }

    @ResponseBody
    @PostMapping("/register")
    public String register(HttpSession session, Account account,
                           @RequestParam("username-reg") String username,
                           @RequestParam("password-reg")String password){
        session.setAttribute("account", null);

        account.setUsername(username);
        account.setPassword(password);
        logger.debug(account.toString());
        if (accountService.insertAccount(account)) {
            logger.info("用户 " + account.getUsername() + " 注册成功");

            return "true";
        } else {
            logger.info("注册失败");

            return "false";
        }
    }

    //用历史购物车覆盖当前购物车
    @GetMapping("/historicalCart")
    public void historicalCart(HttpSession session, String handleCart){
        Account account = (Account) session.getAttribute("account");

        if("true".equals(handleCart)){
            Cart cart = cartService.getCartByAccount(account);
            session.setAttribute("cart", cart);
            logger.info("用历史购物车覆盖当前购物车");
        }
    }
}
