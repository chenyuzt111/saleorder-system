package com.benewake.saleordersystem.controller.intercepter;

import com.benewake.saleordersystem.annotation.AdminRequired;
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
 * @since 2023年07月06 15:35
 * 描 述： TODO
 * 管理员权限拦截器
 */


//这个注解表示将该类注册为spring容器中的一个组件，以便于被自动扫描和管理
@Component
//拦截器的名字
public class AdminRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    //在这里重写HandlerInterceptor接口中的prehandle接口，用于请求处理之前拦截和处理
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截的为方法时（判断拦截器是否为HandlerMethod 类型）
        if(handler instanceof HandlerMethod){
            //如果是的话，获取该方法的method方法
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            //尝试获取该方法是否标记了AdminRequired注解
            AdminRequired adminRequired = method.getAnnotation(AdminRequired.class);
            // 有标记 且此时处于未登录状态，或者当前用户类型不是1或5
            if(null != adminRequired && (hostHolder.getUser() == null ||
                    hostHolder.getUser().getUserType()!=1 && hostHolder.getUser().getUserType()!=5) ){
                // 提示账号未登录
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(CommonUtils.getJSONString(1,"您的权限不够！"));
                writer.close();
                return false;
            }
        }

        return true;
    }
}
