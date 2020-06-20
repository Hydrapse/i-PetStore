package org.csu.ipetstore.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.csu.ipetstore.domain.result.CommonResult;
import org.csu.ipetstore.domain.result.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hydra
 * @date 2020/6/20
 * @description: iPetstore权限控制器
 */

@Api(tags = "实验二权限控制器")
@RestController
@RequestMapping("/authentic")
public class AuthenticController {

    /**
     * 如果用户未登录, 访问所有后台接口均被拦截, 跳转至此处
     */
    @ApiOperation("未认证跳转接口")
    @GetMapping("/unverified")
    public CommonResult login(){
        return CommonResult.failure(ResultCode.USER_NOT_LOGGED_IN, "未进行身份认证");
    }

    /**
     * SpringSecurity会拦截, 并处理登录信息
     */
    @ApiOperation("后台登录URL")
    @PostMapping("/login")
    public CommonResult login(String username, String password){
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        return CommonResult.success("/authentic/login 处理后台登录信息");
    }

    /**
     * SpringSecurity会拦截, 并将cookie对应session从内存登出
     */
    @ApiOperation("后台退出登录URL")
    @RequestMapping("/logout")
    public CommonResult logout(){
        return CommonResult.success("/authentic/logout 对应cookie登出");
    }
}
