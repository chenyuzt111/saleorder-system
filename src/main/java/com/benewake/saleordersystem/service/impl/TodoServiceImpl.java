package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.*;
import com.benewake.saleordersystem.mapper.TodoTaskMapper;
import com.benewake.saleordersystem.service.CustomerService;
import com.benewake.saleordersystem.service.ItemService;
import com.benewake.saleordersystem.service.TodoService;
import com.benewake.saleordersystem.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoTaskMapper todoTaskMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    /**
     * 获取当前用户的待处理监控消息，根据用户的角色和权限过滤数据
     *
     * @return 包含待办任务信息的列表或提示消息
     */
    @Override
    public List<String> getFilteredOrders() {
        // 更新订单信息表中的 daydiff 字段
        updateDayDiffForAllOrders();

        // 获取当前用户信息
        User currentUser = hostHolder.getUser();
        Long userId = currentUser.getId();

        // 获取当前用户的useryc、userxd、userpr值
        UserTypeValues userTypeValues = todoTaskMapper.getUserTypeValues(userId);
        Long useryc = userTypeValues.getUseryc();
        Long userxd = userTypeValues.getUserxd();
        Long userpr = userTypeValues.getUserpr();

        // 调用Mapper层执行数据库查询，获取所有订单
        List<Inquiry> allOrders = todoTaskMapper.selectFilteredOrders(userId, useryc, userxd, userpr);

        // 创建空列表以存储客户和物料信息
        List<Customer> customers = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        // 声明列表以存储拼接后的客户和物料信息
        List<String> customerItemInfoList = new ArrayList<>();

        // 遍历订单列表，提取客户ID和物料ID，然后使用对应的方法获取客户和物料信息
        for (Inquiry order : allOrders) {
            Long customerId = order.getCustomerId(); // 获取客户ID
            Long itemId = order.getItemId(); // 获取物料ID
            Integer orderType = order.getInquiryType(); // 获取订单类型
            String orderCode = order.getInquiryCode(); // 获取订单编号

            // 使用你的findCustomerById方法获取客户信息
            Customer customer = customerService.findCustomerById(customerId);

            // 使用你的findItemById方法获取物料信息
            Item item = itemService.findItemById(itemId);

            // 提取客户姓名和物料名称
            String customerName = customer.getFName();
            String itemName = item.getItemName();

            // 根据订单类型值拼接相应的文字
            String orderTypeText = getOrderTypeText(orderType);

            // 拼接订单信息
            String customerItemInfo = getOrderInfo(orderCode, orderTypeText, itemName, customerName);

            customerItemInfoList.add(customerItemInfo);
        }

        return customerItemInfoList;
    }

    // 辅助方法，根据订单类型返回相应的文字
    private String getOrderTypeText(Integer orderType) {
        if (orderType == 4) {
            return "YC";
        } else if (orderType == 5) {
            return "XD";
        } else if (orderType == 2) {
            return "PR";
        }
        return "";
    }

    // 辅助方法，拼接订单信息
    private String getOrderInfo(String orderCode, String orderTypeText, String itemName, String customerName) {
        return "单据编号:"+orderCode + " 单据类型:" + orderTypeText + " 物料名称:" + itemName + " 客户名称:" + customerName + " 请及时更新需求!";
    }

    /**
     * 更新所有订单的 daydiff 字段
     */
    public void updateDayDiffForAllOrders() {
        // 获取当前时间
        Date currentDate = new Date();

        // 获取订单信息表中所有的订单信息
        List<Inquiry> allInquiries = todoTaskMapper.getAllInquiries();

        for (Inquiry inquiry : allInquiries) {
            // 获取订单的 expected_time
            Date expectedTime = inquiry.getExpectedTime();

            // 计算工作日天数
            int workdays = workdays(currentDate, expectedTime);

            // 更新订单信息表中的 daydiff 字段
            todoTaskMapper.updateDayDiffForOrder(inquiry.getInquiryId(), workdays);
        }
    }

    /**
     * 计算两个日期之间的工作日天数
     */
    public int workdays(Date startDate, Date endDate) {
        return todoTaskMapper.getWorkdays(startDate, endDate);
    }
}
