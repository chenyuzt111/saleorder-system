package com.benewake.saleordersystem.mapper;

import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.UserTypeValues;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

import java.util.Date;
import java.util.List;

@Mapper
public interface TodoTaskMapper {

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

    // 获取用户的类型值
    @Select("SELECT FIM_user_YC, FIM_user_XD, FIM_user_PR FROM fim_users_table WHERE FIM_user_id = #{userId}")
    @Results({
            @Result(property = "useryc", column = "FIM_user_YC"),
            @Result(property = "userxd", column = "FIM_user_XD"),
            @Result(property = "userpr", column = "FIM_user_PR")
    })
    UserTypeValues getUserTypeValues(@Param("userId") Long userId);

    // 查询满足条件的订单信息
    @Select(
            "SELECT i.*, " +
                    "       itm.item_name AS itemName, " +
                    "       cust.customer_name AS customerName, " +
                    "       itd.inquiry_type_name AS inquiryTypeName " +
                    "FROM fim_inquiry_table i " +
                    "LEFT JOIN fim_item_table itm ON i.item_id = itm.item_id " +
                    "LEFT JOIN fim_customer_table cust ON i.customer_id = cust.customer_id " +
                    "LEFT JOIN inquiry_type_dic itd ON i.inquiry_type = itd.inquiry_type " +
                    "WHERE " +
                    "   i.created_user = #{userId} " +
                    "   AND (" +
                    "       (i.inquiry_type = 4 AND  i.daydiff < #{useryc}) " +
                    "       OR (i.inquiry_type = 5 AND  i.daydiff < #{userxd}) " +
                    "       OR (i.inquiry_type = 2 AND  i.daydiff < #{userpr})" +
                    "   )"
    )
    @Options(statementType = StatementType.CALLABLE)
    List<Inquiry> selectFilteredOrders(@Param("userId") Long userId, @Param("useryc") Long useryc, @Param("userxd") Long userxd, @Param("userpr") Long userpr);
}
