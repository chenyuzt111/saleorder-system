package com.benewake.saleordersystem.controller.intercepter;


import com.benewake.saleordersystem.annotation.LoginRequired;
import com.benewake.saleordersystem.utils.CommonUtils;
import com.benewake.saleordersystem.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author Lcs
 * 登陆权限拦截器
 * 这是一个 Spring Boot 拦截器，用于拦截需要登录权限的请求并进行处理。它会检查被拦截的方法上是否标注了 @LoginRequired 注解，并根据用户的登录状态判断是否放行请求。
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截的为方法时
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 有标记 且此时处于未登录状态
            if(null != loginRequired && hostHolder.getUser() == null){
                // 提示账号未登录
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(CommonUtils.getJSONString(1,"账号未登录！"));
                writer.close();
                return false;
            }
        }

        return true;
    }
}
