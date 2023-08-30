package com.benewake.saleordersystem.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Lcs
 * 描 述： 日志记录
 * @since 2023年06月29 15:11
 */

//用于生成日志记录器 log，你可以通过 log 对象来记录日志
@Slf4j
//声明这是一个切面
@Aspect
//将LosAspect类注册为spring容器的一个组件
@Component
public class LogAspect {
    /**
     * ..表示包及子包 该方法代表controller层所有方法
     */
    //这是一个切入点表达式，切入点表达式拦截“com.benewake.saleordersystem.controller”包下的所有方法
    @Pointcut("execution(public * com.benewake.saleordersystem.controller.*.*(..))")
    public void controllerMethod(){}


    //这是一个环绕通知，表达当满足切入点条件时，在方法前后都会执行切面逻辑
    @Around("controllerMethod()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        // before
        // 用户XXX，在XXX（时间），访问了[xxx方法]
        //获取请求属性对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //如果为空返回空值
        if(attributes == null) {
            return null;
        }
        //从请求属性对象中获取http请求对象
        HttpServletRequest request = attributes.getRequest();
        //接下来联系的if语句用于获取客户端的真实IP地址，尝试从不同的头部字段和属性中获取
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip != null && ip.length() > 15){
            if(ip.indexOf(",")>0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        //获取当前时间，并格式化为字符串
        String now = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(new Date());
        //获取被拦截方法所属的类名
        String target = joinPoint.getSignature().getDeclaringTypeName();
        //使用日志记录器log记录一条日志信息，用户ip，访问时间，类名，方法名，转化为列表记录在日志中
        log.info(String.format("用户[%s],在[%s],访问了[%s]的[%s]方法，参数为[%s].",
                ip,now,target,joinPoint.getSignature().getName(),Arrays.asList(joinPoint.getArgs())));


        //执行被拦截的方法并获取其返回值，将返回值存储在result对象中
        Object result = joinPoint.proceed();

        // after
        //log.info("返回结果："+JSON.toJSONString(result));


        //返回方法的返回值，完成整个切面的操作
        return result;

    }



}
