package com.benewake.saleordersystem.utils.api;

import com.alibaba.fastjson2.JSONObject;
import com.benewake.saleordersystem.entity.sfexpress.Route;
import com.benewake.saleordersystem.entity.sfexpress.SF_SEARCH_RESULT;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lcs
 * @since 2023年07月07 10:05
 * 描 述： TODO
 */
public class SFUtils {

    /**
     * 解析路由查询结果 获得运送路径列表
     * @param result
     * @return
     */
    //接收一个路由信息，根据这个路由信息返回一个route列表
    public static List<Route> parseToRoute(SF_SEARCH_RESULT result){
        List<Route> routes = new ArrayList<>();
        if(result == null) return routes;
        //这里解析查询结果的 apiResultData 字段，并获取其中的 msgData 字段的字符串值。
        String msgData = JSONObject.parseObject(result.getApiResultData()).getString("msgData");
        //如果msgdate为空，直接返回空的列表
        if(StringUtils.isBlank(msgData)) return routes;
        //这里解析 msgData 字符串，获取其中的 routeResps 数组的第一个元素，并将其转换为字符串形式。
        String routeResps = JSONObject.parseObject(msgData).getJSONArray("routeResps").get(0).toString();
        if(StringUtils.isBlank(routeResps)) return routes;
        //这里将解析后的 routeResps 字符串转换为 JSON 对象，并从中获取名为 "routes" 的数组，然后将其转换为 List<Route> 类型的运送路径列表
        routes = JSONObject.parseObject(routeResps).getList("routes", Route.class);
        return routes;
    }

    /**
     * 解析并只获取最新消息
     * @param result
     * @return
     */
    public static Route getLastestRemark(SF_SEARCH_RESULT result){
        //通过parseToRoute将接收到的地址转化为运送状态信息
        List<Route> routes = parseToRoute(result);
        //如果接受到的信息为空就返回一个新建状态列表
        if(routes==null || routes.size()==0) return new Route();
        //检查最后一个路由信息的操作码为是否等于字符串8000
        if("8000".equals(routes.get(routes.size()-1).getOpCode())){
            //如果最后一个路由信息的操作码是8000，就返回倒数第二个路由信息，也就是倒数第一个不是8000的路由信息
            return routes.get(routes.size() - 2);
        }else{
            //不等于8000，直接返回最后一个路由信息
            return routes.get(routes.size() - 1);
        }
    }


}
