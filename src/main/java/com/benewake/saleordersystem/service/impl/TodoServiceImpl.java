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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.benewake.saleordersystem.utils.BenewakeConstants.*;

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
     * 获取全部待办订单，更新在数据库待办任务表中
     */
    public void selectAllPendingTasks() {
//         清空fim_todotask_table表
        todoTaskMapper.truncateTodoTaskTable();

        updateDayDiffForAllOrders();
        List<Inquiry> inquiries = todoTaskMapper.selectAllPendingTasks();
        inquiries.stream()
                .forEach(order -> {
                    Long customerId = order.getCustomerId();
                    Long itemId = order.getItemId();
                    Long salesmanId = order.getSalesmanId();
                    Integer orderType = order.getInquiryType();
                    String inquiryCode = order.getInquiryCode();
                    Long inquiryId = order.getInquiryId();

                    Customer customer = customerService.findCustomerById(customerId);
                    Item item = itemService.findItemById(itemId);

                    String customerName = customer.getFName();
                    String itemName = item.getItemName();
                    String orderTypeText = getOrderTypeText(orderType);

                    TodoTask todoTask = new TodoTask();
                    todoTask.setSalesmanId(salesmanId);
                    todoTask.setInquiryId(inquiryId);
                    todoTask.setCustomerName(customerName);
                    todoTask.setItemName(itemName);
                    todoTask.setInquiryType(orderTypeText);
                    todoTask.setInquiryCode(inquiryCode);
                    todoTask.setMessage("请及时更新需求！");

                    todoTaskMapper.insertTodoTask(todoTask);
                });

    }


    /**
     * 根据用户类型返回待办任务订单
     *
     * @return TodoTask类型数据
     */
    @Override
    public List<TodoTask> getFilteredOrders() {
        selectAllPendingTasks();
        updateDayDiffForAllOrders();
        User currentUser = hostHolder.getUser();
        Long userId = currentUser.getId();
        Long userType = currentUser.getUserType();
        List<TodoTask> filteredInquiries;

        if (userType == USER_TYPE_ADMIN) {
            filteredInquiries = todoTaskMapper.selectTodoTask();
        } else if (userType == USER_TYPE_SALESMAN) {
            filteredInquiries = todoTaskMapper.smselectTodoTask(userId);
        } else {
            filteredInquiries = Collections.emptyList();
        }

        return filteredInquiries;
    }

    private String getOrderTypeText(Integer orderType) {
        if (orderType == 4) {
            return "YC"; // YC代表什么？
        } else if (orderType == 5) {
            return "XD"; // XD代表什么？
        } else if (orderType == 2) {
            return "PR"; // PR代表什么？
        }else if (orderType == 1) {
            return "PO"; // PR代表什么？
        }
        return "";
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

    /*
     * 超链接需要用到Inquiry数据类型这个方法用于转换
     */
    @Override
    public List<Inquiry> filteredInquiries() {

        // 获取所有符合筛选条件的待办任务列表
        List<TodoTask> todoTasks = getFilteredOrders();

        List<Inquiry> filteredInquiries = new ArrayList<>();

        // 从每个待办任务中提取 inquiry_id
        for (TodoTask todoTask : todoTasks) {
            Long inquiryId = todoTask.getInquiryId();
            // 根据 inquiry_id 查询对应的订单信息
            Inquiry inquiry = todoTaskMapper.selectInquiryById(inquiryId);
            if (inquiry != null) {
                filteredInquiries.add(inquiry);
            }
        }

        return filteredInquiries;
    }

    /**
     * 获取PO订单是否延期
     */
    @Override
    public List<TodoTask> POdelays() {
        User currentUser = hostHolder.getUser();
        Long userId = currentUser.getId();
        Long userType = currentUser.getUserType();
        List<Inquiry> Podelay = todoTaskMapper.POOrders();
        List<TodoTask> Podelaylist;

        if (userType == USER_TYPE_ADMIN) {
            Podelaylist = processAdminOrders(Podelay);
        } else if (userType == USER_TYPE_SALESMAN) {
            Podelaylist = processSalesmanOrders(Podelay, userId);
        } else {
            Podelaylist = Collections.emptyList();
        }

        return Podelaylist;
    }

    /**
     * 处理管理员订单
     */
    private List<TodoTask> processAdminOrders(List<Inquiry> Podelay) {
        List<TodoTask> Podelaylist = new ArrayList<>();
        for (Inquiry order : Podelay) {
            Podelaylist.add(createPoDelayTask(order));
        }
        return Podelaylist;
    }

    /**
     * 处理销售员订单
     */
    private List<TodoTask> processSalesmanOrders(List<Inquiry> Podelay, Long userId) {
        List<TodoTask> Podelaylist = new ArrayList<>();
        for (Inquiry order : Podelay) {
            if (order.getSalesmanId().equals(userId)) {
                Podelaylist.add(createPoDelayTask(order));
            }
        }
        return Podelaylist;
    }

    /**
     * 将Inquiry转化为TodoTask类型
     */
    private TodoTask createPoDelayTask(Inquiry order) {
        // 从订单中提取所需信息
        Long customerId = order.getCustomerId(); // 客户ID
        Long itemId = order.getItemId(); // 物料ID
        Long saleNum = order.getSaleNum(); // 销售数量
        Date arrangedTime = order.getArrangedTime(); // 安排时间
        Date expectedTime = order.getExpectedTime(); // 预期完成时间

        // 计算延期天数（以毫秒为单位）
        Long timeDifferenceMillis = arrangedTime.getTime() - expectedTime.getTime();
        // 将毫秒转换为天
        Long timeDifferenceDays = timeDifferenceMillis / (24 * 60 * 60 * 1000);

        // 查询客户信息
        Customer customer = customerService.findCustomerById(customerId);

        // 查询物料信息
        Item item = itemService.findItemById(itemId);

        // 提取客户姓名和物料名称
        String customerName = customer.getFName();
        String itemName = item.getItemName();

        // 创建待办任务对象并设置属性
        TodoTask todoTask = new TodoTask();
        todoTask.setSalenum(saleNum);
        todoTask.setExpectedTime(expectedTime);
        todoTask.setDelayedTime(timeDifferenceDays);
        todoTask.setCustomerName(customerName);
        todoTask.setItemName(itemName);

        return todoTask;
    }
}
