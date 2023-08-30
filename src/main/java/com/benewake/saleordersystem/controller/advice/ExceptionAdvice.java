package com.benewake.saleordersystem.controller.advice;

import com.benewake.saleordersystem.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lcs
 * 描述：全局捕获异常并返回相应提示
 */
//针对@Controller注解的控制器进行全局异常处理，拦截控制器的异常
@ControllerAdvice(annotations = Controller.class)
@Slf4j
//控制器增强类的名字
public class ExceptionAdvice{

    //异常处理方法的注解，该方法会处理所有类型的Exception异常
    @ExceptionHandler(value = Exception.class)
    //方法的返回值会被序列化为json并返回给客户端
    @ResponseBody
    public Result<String> handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常："+e.getMessage());
        for(StackTraceElement element : e.getStackTrace()){
            log.error(element.toString());
        }
        return Result.fail(1,"请求失败，服务器异常！",e.getMessage());
    }
}
