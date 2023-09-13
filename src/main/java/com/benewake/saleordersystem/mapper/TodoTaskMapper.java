package com.benewake.saleordersystem.mapper;

import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.TodoTask;
import com.benewake.saleordersystem.entity.UserTypeValues;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface TodoTaskMapper {
    //清空fim_todotask_table表中数据
    @Delete("DELETE FROM fim_todotask_table")
    void truncateTodoTaskTable();

    // 插入待办任务数据
    @Insert("INSERT INTO fim_todotask_table (inquiry_id, salesman_id, inquiry_code, inquiry_type, item_name, customer_name, message) " +
            "VALUES (#{inquiryId}, #{salesmanId}, #{inquiryCode}, #{inquiryType}, #{itemName}, #{customerName}, #{message})")
    void insertTodoTask(TodoTask todoTask);


    // 查询所有待办任务
    @Select("SELECT * FROM fim_todotask_table")
    List<TodoTask> selectTodoTask();

    // 查询销售员的待办任务
    @Select("SELECT * FROM fim_todotask_table WHERE salesman_id = #{userId}")
    List<TodoTask> smselectTodoTask(@Param("userId") Long userId);

    // 根据inquiryId查询对应的订单信息
    @Select("SELECT * FROM fim_inquiry_table WHERE inquiry_id = #{inquiryId}")
    Inquiry selectInquiryById(@Param("inquiryId") Long inquiryId);

    // 查询所有待办任务的订单
    @Select("SELECT * FROM fim_inquiry_table WHERE " +
            "((inquiry_type = 4 AND daydiff < (SELECT FIM_user_YC FROM fim_users_table WHERE FIM_user_id = salesman_id)) " +
            "OR (inquiry_type = 5 AND daydiff < (SELECT FIM_user_XD FROM fim_users_table WHERE FIM_user_id = salesman_id)) " +
            "OR (inquiry_type = 2 AND daydiff < (SELECT FIM_user_PR FROM fim_users_table WHERE FIM_user_id = salesman_id)))")
    List<Inquiry> selectAllPendingTasks();

    // 获取订单信息表中所有的订单信息
    @Select("SELECT * FROM fim_inquiry_table")
    List<Inquiry> getAllInquiries();

    // 更新订单信息表中的 daydiff 字段
    @Update("UPDATE fim_inquiry_table\n" +
            "    SET daydiff = #{workdays}\n" +
            "    WHERE inquiry_id = #{inquiryId}")
    void updateDayDiffForOrder(@Param("inquiryId") Long inquiryId, @Param("workdays") int workdays);

    // 查询两个日期之间的工作日总数
    @Select("SELECT COUNT(*) \n" +
            "FROM fim_work_day \n" +
            "WHERE date BETWEEN #{startDate} AND #{endDate} \n" +
            "      AND people_num != 0;")
    int getWorkdays(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    // 查询PO订单是否延期
    @Select(
            "SELECT *" +
                    "FROM fim_inquiry_table  " +
                    "WHERE " +
                    "   inquiry_type = 1 " +
                    "   AND arranged_time > expected_time"
    )
    List<Inquiry> POOrders();
}
