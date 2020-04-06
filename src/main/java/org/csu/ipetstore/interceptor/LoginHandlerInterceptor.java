package org.csu.ipetstore.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Enzo Cotter on 2020/1/25.
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object loginUser = request.getSession().getAttribute("account");
        if (loginUser != null) {

            //已经登录过
            return true;
        }

        //没有登录过，跳转至登录页面
        request.setAttribute("msg", "没有权限，请先登录！");
        request.getRequestDispatcher("/index.html").forward(request, response);
        return false;
    }
}
