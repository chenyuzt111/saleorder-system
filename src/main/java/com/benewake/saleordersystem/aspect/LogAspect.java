package com.benewake.saleordersystem.aspect;

import com.alibaba.fastjson.JSON;
import com.benewake.saleordersystem.controller.FimOperLogController;
import com.benewake.saleordersystem.entity.FimOperLog;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.VO.StartInquiryVo;
import com.benewake.saleordersystem.mapper.InquiryMapper;
import com.benewake.saleordersystem.mapper.TodoTaskMapper;
import com.benewake.saleordersystem.service.FimOperLogService;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.logging.log4j.message.ParameterizedMessage.format;

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


    @Autowired
    private HostHolder hostHolder;


    @Autowired
    private InquiryMapper inquiryMapper;
    @Autowired
    private FimOperLogService fimOperLogService;



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
        log.info(String.format("用户[%s],ip地址[%s],在[%s],访问了[%s]的[%s]方法，参数为[%s].",hostHolder.getUser(),
                ip,now,target,joinPoint.getSignature().getName(),Arrays.asList(joinPoint.getArgs())));

        Object[] args = joinPoint.getArgs();

        //执行被拦截的方法并获取其返回值，将返回值存储在result对象中
        Object result = joinPoint.proceed();

        String methodName = joinPoint.getSignature().getName();
        log.info("返回结果："+ JSON.toJSONString(result));
        if(!"login".equals(methodName)) {
            FimOperLog fimOperLog = new FimOperLog();
            fimOperLog.setOperIp(ip);
            fimOperLog.setOperName(hostHolder.getUser().getUsername());
            fimOperLog.setCreateTime(new Date());
            fimOperLog.setStatus(0);
            fimOperLog.setDeleted(0);


            if ("addInquiries".equals(methodName)) {
                int startInquiry = 0; // 从args中获取startInquiry的值
                String inquiryCode = null;
                int inquiryId = 0;

                for (Object arg : args) {
                    if (arg instanceof StartInquiryVo) {
                        StartInquiryVo startInquiryVo = (StartInquiryVo) arg;
                        startInquiry = startInquiryVo.getStartInquiry();
                        if (startInquiryVo.getInquiryList() != null && !startInquiryVo.getInquiryList().isEmpty()) {
                            // 如果inquiryList不为空，可以从第一个Inquiry对象获取inquiryCode
                            Inquiry firstInquiry = startInquiryVo.getInquiryList().get(0);
                            inquiryCode = firstInquiry.getInquiryCode();
                            if(firstInquiry.getInquiryId()!=null) {
                                inquiryId = Math.toIntExact(firstInquiry.getInquiryId());
                            }
                        }

                    }
                }
                if (startInquiry == 0) {
                    fimOperLog.setTitle("新增订单");
                    fimOperLog.setErrorMsg(inquiryCode);
                    // 调用FimOperLogController中的save方法
                    save(fimOperLog, request);
                } else if (startInquiry == 1) {
                    inquiryCode = inquiryMapper.selectcodeByid(inquiryId);
                    fimOperLog.setTitle("询单");
                    fimOperLog.setErrorMsg(inquiryCode);
                    // 调用FimOperLogController中的save方法
                    save(fimOperLog, request);
                }
            } else if ("updateInquired".equals(methodName)) {
                String inquiryCode = null;
                for (Object arg : args) {

                    Inquiry inquiry = (Inquiry) arg;
                    inquiryCode = inquiry.getInquiryCode();
                }
                fimOperLog.setErrorMsg(inquiryCode);
                fimOperLog.setTitle("修改订单");
// 调用FimOperLogController中的save方法
                save(fimOperLog, request);
            } else if ("updateInquiryAllowInquiry".equals(methodName)) {
                List<Long> idList = new ArrayList<>();
                for (Object arg : args) {

                    ArrayList<?> arrayList = (ArrayList<?>) arg; // 将arg强制转换为ArrayList
                    for (Object item : arrayList) {
                        if (item instanceof Long) {
                            Long id = (Long) item;
                            idList.add(id); // 将Long值添加到列表中
                        }
                    }

                }
                StringBuilder errorMsgBuilder = new StringBuilder();
                // 构建查询语句，根据id查询单据编号
                for (Long id : idList) {
                    String inquiryCode = inquiryMapper.selectcodeByid(Math.toIntExact(id));
                    errorMsgBuilder.append(inquiryCode).append(" ");

                }
                fimOperLog.setErrorMsg(errorMsgBuilder.toString().trim());
                fimOperLog.setTitle("允许询单");
                save(fimOperLog, request);
            } else if ("deleteOrder".equals(methodName)) {
                for (Object arg : args) {
                    if (arg instanceof LinkedHashMap) {
                        LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) arg;

                        // 假设 orderId 是键的名称，你可以根据实际情况修改
                        Object orderId = map.get("orderId");
                        if (orderId != null) {
                            int inquiryId = Integer.parseInt(orderId.toString()); // 将orderId转化为int
                            String inquiryCode = inquiryMapper.selectcodeByid(inquiryId);

                            fimOperLog.setErrorMsg(inquiryCode);
                        }
                    }
                }
                fimOperLog.setTitle("删除订单");
                // 调用FimOperLogController中的save方法
                save(fimOperLog, request);
            } else if ("addOrdersByExcel".equals(methodName)) {
                fimOperLog.setTitle("导入excel");
                // 调用FimOperLogController中的save方法
                save(fimOperLog, request);
            } else {
                fimOperLog.setTitle(methodName);
                // 调用FimOperLogController中的save方法
            }

            //返回方法的返回值，完成整个切面的操作

        }
        return result;
    }


    public void save(@RequestBody FimOperLog fimOperLog, HttpServletRequest request){
        String ip = IpUtil.getIpAddress(request);
        fimOperLog.setOperIp(ip);
        fimOperLog.setOperName(hostHolder.getUser().getUsername());
        fimOperLogService.save(fimOperLog);

    }



}
