package com.benewake.saleordersystem.service.impl;

import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.UserTypeValues;
import com.benewake.saleordersystem.mapper.TodoTaskMapper;
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

    /**
     * 获取当前用户的待处理监控消息，根据用户的角色和权限过滤数据
     *
     * @return 包含待办任务信息的列表或提示消息
     */
    @Override
    public List<Inquiry> getFilteredOrders() {
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

        // 调用Mapper层执行数据库查询，获取所有订单
        List<Inquiry> filteredOrders = new ArrayList<>();
        for (Inquiry order : allOrders) {
            Inquiry filteredOrder = new Inquiry();
            filteredOrder.setInquiryCode(order.getInquiryCode());
            filteredOrder.setItemName(order.getItemName());
            filteredOrder.setCustomerName(order.getCustomerName());
            filteredOrder.setInquiryTypeName(order.getInquiryTypeName());
            filteredOrder.setMessage("请及时更新需求！");
            filteredOrders.add(filteredOrder);
        }

        return filteredOrders;
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
