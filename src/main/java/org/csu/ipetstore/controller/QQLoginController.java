package org.csu.ipetstore.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.PageFans;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.PageFansBean;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.javabeans.weibo.Company;
import com.qq.connect.oauth.Oauth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
public class QQLoginController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/qqlogin")
    public String qqLogin(HttpServletRequest request) {
        try {
            return  "redirect:" + new Oauth().getAuthorizeURL(request);
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping("qq/index.html")
    public String Login(HttpServletRequest request, Model model){

        String accessToken   = null,
                openID        = null;
        UserInfoBean userInfoBean = null;
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

            long tokenExpireIn = 0L;

            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                System.out.println("欢迎你，代号为 " + openID + " 的用户!");
                request.getSession().setAttribute("demo_openid", openID);
                System.out.println("<a href=" + "/shuoshuoDemo.html" +  " target=\"_blank\">去看看发表说说的demo吧</a>");
                // 利用获取到的accessToken 去获取当前用户的openid --------- end


                System.out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- start </p>");
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                userInfoBean = qzoneUserInfo.getUserInfo();
                System.out.println("<br/>");
                if (userInfoBean.getRet() == 0) {
                    System.out.println(userInfoBean.getNickname() + "<br/>");
                    System.out.println(userInfoBean.getGender() + "<br/>");
                    System.out.println("黄钻等级： " + userInfoBean.getLevel() + "<br/>");
                    System.out.println("会员 : " + userInfoBean.isVip() + "<br/>");
                    System.out.println("黄钻会员： " + userInfoBean.isYellowYearVip() + "<br/>");
                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
                } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }
                System.out.println("<p> end -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- end </p>");



                System.out.println("<p> start ----------------------------------- 验证当前用户是否为认证空间的粉丝------------------------------------------------ start <p>");
                PageFans pageFansObj = new PageFans(accessToken, openID);
                PageFansBean pageFansBean = pageFansObj.checkPageFans("97700000");
                if (pageFansBean.getRet() == 0) {
                    System.out.println("<p>验证您" + (pageFansBean.isFans() ? "是" : "不是")  + "QQ空间97700000官方认证空间的粉丝</p>");
                } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + pageFansBean.getMsg());
                }
                System.out.println("<p> end ----------------------------------- 验证当前用户是否为认证空间的粉丝------------------------------------------------ end <p>");



                System.out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在微博的昵称等信息 ---------------------------- start </p>");
                com.qq.connect.api.weibo.UserInfo weiboUserInfo = new com.qq.connect.api.weibo.UserInfo(accessToken, openID);
                com.qq.connect.javabeans.weibo.UserInfoBean weiboUserInfoBean = weiboUserInfo.getUserInfo();
                if (weiboUserInfoBean.getRet() == 0) {
                    //获取用户的微博头像----------------------start
                    System.out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
                    System.out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
                    System.out.println("<image src=" + weiboUserInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
                    //获取用户的微博头像 ---------------------end

                    //获取用户的生日信息 --------------------start
                    System.out.println("<p>尊敬的用户，你的生日是： " + weiboUserInfoBean.getBirthday().getYear()
                            +  "年" + weiboUserInfoBean.getBirthday().getMonth() + "月" +
                            weiboUserInfoBean.getBirthday().getDay() + "日");
                    //获取用户的生日信息 --------------------end

                    StringBuffer sb = new StringBuffer();
                    sb.append("<p>所在地:" + weiboUserInfoBean.getCountryCode() + "-" + weiboUserInfoBean.getProvinceCode() + "-" + weiboUserInfoBean.getCityCode()
                            + weiboUserInfoBean.getLocation());

                    //获取用户的公司信息---------------------------start
                    ArrayList<Company> companies = weiboUserInfoBean.getCompanies();
                    if (companies.size() > 0) {
                        //有公司信息
                        for (int i=0, j=companies.size(); i<j; i++) {
                            sb.append("<p>曾服役过的公司：公司ID-" + companies.get(i).getID() + " 名称-" +
                                    companies.get(i).getCompanyName() + " 部门名称-" + companies.get(i).getDepartmentName() + " 开始工作年-" +
                                    companies.get(i).getBeginYear() + " 结束工作年-" + companies.get(i).getEndYear());
                        }
                    } else {
                        //没有公司信息
                    }
                    //获取用户的公司信息---------------------------end



                } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + weiboUserInfoBean.getMsg());
                }

                System.out.println("<p> end -----------------------------------利用获取到的accessToken,openid 去获取用户在微博的昵称等信息 ---------------------------- end </p>");



            }
        } catch (QQConnectException e) {
        }


        model.addAttribute("openId",openID);
        model.addAttribute("imgSrc",userInfoBean.getAvatar().getAvatarURL100());
        return "success";
    }
}
