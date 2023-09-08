package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.service.TodoService;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 待办事务管理控制器
 */
@Api(tags = "待办事务管理")
@RestController
@RequestMapping("/todotask")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 获取过滤后的待办任务列表
     */
    @ApiOperation("待办任务")
    @GetMapping("/filtered-orders")
    public Result<List<String>> getFilteredOrders() {
        // 调用服务方法，获取过滤后的订单列表
        List<String> filteredOrders = todoService.getFilteredOrders();


        // 判断是否有待办事务
        if (filteredOrders.isEmpty()) {
            // 没有待办事务，设置消息并返回
            return Result.fail("暂无待办事务",filteredOrders);
        } else {
            // 有待办事务，返回列表信息
            return Result.success(filteredOrders);
        }
    }

    /**
     * 获取待处理监控消息
     */
    @ApiOperation("待处理监控消息")
    @GetMapping("/PMMessages")
    public Result<String> getPMMessages() {
        List<String> filteredOrders = todoService.getFilteredOrders();

        int todoTaskCount = filteredOrders.size();
        if (todoTaskCount > 3) {
            // 待办任务数量大于3，返回提示信息
            return Result.success("已有多条待办任务，请及时更新询单表单。");
        } else {
            // 待办任务数量不大于3，返回空
            return null;
        }
    }
}

