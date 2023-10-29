package com.benewake.saleordersystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.benewake.saleordersystem.entity.Delivery;
import com.benewake.saleordersystem.entity.sfexpress.Route;
import com.benewake.saleordersystem.entity.sfexpress.SF_SEARCH_RESULT;
import com.benewake.saleordersystem.service.KingDeeService;
import com.benewake.saleordersystem.service.SFExpressService;
import com.benewake.saleordersystem.utils.api.SFUtils;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import com.sf.csim.express.service.IServiceCodeStandard;
import com.sf.csim.express.service.code.ExpressServiceCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Lcs
 * @since
 * 描述：
 **/
@Service
public class SFExpressServiceImpl implements SFExpressService {

    @Autowired
    private KingDeeService kingDeeService;

    //顾客编码
    private static final String CLIENT_CODE = "LCSZ82CQOO";
    // 生产环境校验码
    private static final String CHECK_WORD = "x6x14lUFjJqDCfb1mJ1H2Lp33R1ajCZ1";
    //生产的地址
    private static final String CALL_URL_PRO = "https://bspgw.sf-express.com/std/service";



    //查询指定单号和电话号码的订单路由信息
    @Override
    public SF_SEARCH_RESULT findRoutesByCode(String code, String tel) {
        try {
            //这里使用了一个枚举类型，表示顺丰提供的不同的服务接口，"EXP_RECE_SEARCH_ROUTES"是该枚举的一个值对应于查询订单路由的接口
            IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_SEARCH_ROUTES;

            Map<String, String> params = new HashMap<>();

            //获取时间戳，作为请求的一个参数
            String timeStamp = String.valueOf(System.currentTimeMillis());

            //构建json格式的消息数据，包含了查询所需要的订单号和电话号码
            String msgData = "{\"trackingType\": \"1\",\"trackingNumber\": [\"" + code + "\"],\"checkPhoneNo\": \"" + tel + "\"}";
            // 顾客编码
            params.put("partnerID", CLIENT_CODE);
            params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
            // 接口服务码
            params.put("serviceCode", standardService.getCode());
            //时间戳
            params.put("timestamp", timeStamp);
            //消息数据
            params.put("msgData", msgData);
            //msgDigest 是使用 CallExpressServiceTools.getMsgDigest 方法计算的消息摘要，用于校验数据的完整性和安全性。
            params.put("msgDigest", CallExpressServiceTools.getMsgDigest(msgData, timeStamp, CHECK_WORD));
            //向指定url地址发送请求，传递参数映射
            String result = HttpClientUtil.post(CALL_URL_PRO, params);
            //解析返回结果利用JSON.parseObject方法，将返回的json数据格式解析成SF_SEARCH_RESULT类的对象
            SF_SEARCH_RESULT routes = JSON.parseObject(result, SF_SEARCH_RESULT.class);
            return routes;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    //用于查询指定订单号和电话号码的派送预计时间信息
    @Override
    public SF_SEARCH_RESULT searchPromitm(String code, String tel) {
        try {
            //预计派送时间
            //枚举类型，EXP_RECE_SEARCH_PROMITM指定了查询派送预计时间的接口
            IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_SEARCH_PROMITM;

            CallExpressServiceTools tools = CallExpressServiceTools.getInstance();

            // set common header

            Map<String, String> params = new HashMap<>();
            //获取时间戳
            String timeStamp = String.valueOf(System.currentTimeMillis());
            //构建消息数据
            String msgData = "{\n" +
                    "    \"searchNo\": \"" + code + "\",\n" +
                    "    \"checkType\": 1,\n" +
                    "    \"checkNos\": [\"" + tel + "\"]\n" +
                    "}";
            // 顾客编码
            params.put("partnerID", CLIENT_CODE);
            params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
            // 接口服务码
            params.put("serviceCode", standardService.getCode());
            params.put("timestamp", timeStamp);
            params.put("msgData", msgData);
            params.put("msgDigest", CallExpressServiceTools.getMsgDigest(msgData, timeStamp, CHECK_WORD));
            //发送http请求，传递参数映射
            String result = HttpClientUtil.post(CALL_URL_PRO, params);
            return JSON.parseObject(result, SF_SEARCH_RESULT.class);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    //用于获取指定提货单号的最新订单信息
    //接受的是物流单的信息，返回的是route类型
    /*@Override
    public Route getLastestRouteByFCarriageNO(Delivery delivery){
        //SaleOut saleOut = kingDeeService.selectFCarriageNO(fCarriageNO);
        if(delivery != null){
            //如果传入的物流单不为空，就判断物流单中的电话号码是否为空
            if(StringUtils.isBlank(delivery.getDeliveryPhone())) {
                return null;
            } else{
                *//*不为空的话，如果电话号码不为空，会执行这个分支，调用 SFUtils.getLastestRemark 方法，
                传递了查询订单路由信息的调用，并返回其结果。这个方法的作用是从查询结果中获取最新的路由备注信息。*//*
                return SFUtils.getLastestRemark(findRoutesByCode(delivery.getDeliveryCode(),delivery.getDeliveryPhone()));
            }
        }else{
            return null;
        }
    }*/

    public List<Route> getLastestRoutesByDelivery(Delivery delivery) {
        List<Route> latestRoutes = new ArrayList<>();

        if (delivery != null) {
            String deliveryCodes = delivery.getDeliveryCode(); // 获取包含多个顺丰单号的字符串

            if (!StringUtils.isBlank(deliveryCodes)) {
                String[] deliveryCodeArray = deliveryCodes.split("\\s+"); // 使用正则表达式来根据空格拆分单号
                for (String deliveryCode : deliveryCodeArray) {
                    Route latestRoute = new Route();
                    boolean integrity = checkStringLength(deliveryCode);
                    if(integrity) {
                        // 对每个单号调用 getLastestRouteByFCarriageNO 方法，获取最新路由信息
                        latestRoute = SFUtils.getLastestRemark(findRoutesByCode(deliveryCode, delivery.getDeliveryPhone()));
                        if (latestRoute.getOpCode()==null){
                            latestRoute.setRemark("顺丰号正确，手机号异常请检查");
                            latestRoute.setOpCode("32");
                        }

                    }else {
                        String errorMessage = "顺丰号 " + delivery.getDeliveryCode() + " 数字有遗漏，请用户及时修改！";
                        latestRoute.setRemark(errorMessage);
                        latestRoute.setOpCode("32");
                    }
                    if(latestRoute!=null){
                        latestRoutes.add(latestRoute);
                    }
                }
            }
        }

        return latestRoutes;
    }


    public boolean checkStringLength(String input) {
        if (input != null && input.length() == 15) {
            return true;
        } else {
            return false;
        }
    }
}
