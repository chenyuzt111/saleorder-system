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
            "SELECT *" +
                    "FROM fim_inquiry_table  " +
                    "WHERE " +
                    "   created_user = #{userId} " +
                    "   AND (" +
                    "       (inquiry_type = 4 AND  daydiff < #{useryc}) " +
                    "       OR (inquiry_type = 5 AND  daydiff < #{userxd}) " +
                    "       OR (inquiry_type = 2 AND  daydiff < #{userpr})" +
                    "   )"
    )
    List<Inquiry> selectFilteredOrders(@Param("userId") Long userId, @Param("useryc") Long useryc, @Param("userxd") Long userxd, @Param("userpr") Long userpr);
}
