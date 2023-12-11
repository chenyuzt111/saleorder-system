package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.saleordersystem.entity.Delivery;
import com.benewake.saleordersystem.entity.Inquiry;
import com.benewake.saleordersystem.entity.Past.SaleOut;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Lcs
 */
@Mapper
public interface DeliveryMapper extends BaseMapper<Delivery> {

    /**
     * 批量更新运输状态信息
     * @param deliveryList
     * @return
     */
    @Update("<script>" +
            "<foreach collection='list' item='item' index='index' separator=';'>" +
            "update fim_delivery_table " +
            "<set>" +
            "delivery_state = #{item.deliveryState} " +
            "<if test='item.receiveTime!=null'> " +
            ",receive_time = #{item.receiveTime} " +
            "</if> " +
            "<if test='item.deliveryLastestState!=null'> " +
            ",delivery_latest_state = #{item.deliveryLastestState} " +
            "</if> " +
            "<if test='item.FCountry!=null'> " +
            ",country = #{item.FCountry} " +
            "</if> " +
            "<if test='item.FDeliveryIntegrity!=null'> " +
            ",delivery_code_integrity = #{item.FDeliveryIntegrity} " +
            "</if> " +
            "</set> " +
            "where inquiry_code = #{item.inquiryCode} " +
            "</foreach> " +
            "</script>")
    int updateDeliveriesState(List<Delivery> deliveryList);

    @Update("<script>" +
            "<foreach collection='list' item='item' index='index' separator=';'>" +
            "update fim_delivery_table " +
            "<set>" +
            "delivery_code = #{item.deliveryCode}, " +
            "delivery_phone = #{item.deliveryPhone} " +
            "</set> " +
            "where   #{item.inquiryCode} like inquiry_code " +
            "</foreach> " +
            "</script>")
    int updateDeliveriesCode(List<Delivery> deliveryList);


    /**
     * 批量插入数据
     * @param inquiries
     * @return
     */
    @Insert("<script>" +
            "insert into fim_delivery_table(" +
            "inquiry_code" +
            ") values " +
            "<foreach collection='lists' item='list' separator=','> " +
            "(" +
            "#{list.inquiryCode}" +
            ")" +
            "</foreach>" +
            "</script>")
    int insertLists(@Param("lists") List<Inquiry> inquiries);

    /**
     * 获取没有运输单号的单据编号列表
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select inquiry_code " +
            "from fim_delivery_table " +
            "where inquiry_code in(" +
            "select inquiry_code from fim_inquiry_table " +
            "where state >= 0)" +
            " and (" +
            "delivery_code is null or delivery_code = '' or delivery_phone is null " +
            "or delivery_phone = ''" +
            ")" +
            "</script>")
    List<Delivery> selectUnFindDeliveriesByUser1();

    @Select("<script>" +
            "select inquiry_code " +
            "from fim_delivery_table " +
            "where inquiry_code in(" +
            "select inquiry_code from fim_inquiry_table " +
            "where (created_user = #{userId} or salesman_id = #{userId}) and state >= 0" +
            ") and (" +
            "delivery_code is null or delivery_code = '' or delivery_phone is null " +
            "or delivery_phone = ''" +
            ")" +
            "</script>")
    List<Delivery> selectUnFindDeliveriesByUser2(@Param("userId") Long userId);

    /**
     * 获取用户所有未签收的运输单号和电话号码
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select inquiry_code,delivery_code,delivery_phone " +
            "from fim_delivery_table " +
            "where inquiry_code in(" +
            "select inquiry_code from fim_inquiry_table " +
            "where (created_user = #{userId} or salesman_id = #{userId}) and state >= 0 " +
            ") and " +
            "(delivery_code is not null or delivery_phone is not null) " +
            "and receive_time is null " +
            "</script>")
    List<Delivery> selectUnFinisheDeliveriesByUser1(@Param("userId")Long userId);

    @Select("<script>" +
            "select inquiry_code,delivery_code,delivery_phone " +
            "from fim_delivery_table " +
            "where inquiry_code in(" +
            "select inquiry_code from fim_inquiry_table " +
            "where state >= 0) and (delivery_code is not null or delivery_phone is not null) " +
            "and receive_time is null " +
            "</script>")
    List<Delivery> selectUnFinisheDeliveriesByUser2();

    @Select("<script>" +
            "select delivery_latest_state from fim_delivery_table " +
            "where delivery_latest_state like #{deliveryState} " +
            "</script>")
    List<String> getDeliveryStateList(@Param("deliveryState") String deliveryState);

//    遍历运输订单表将有序单号的单据编号返回
    @Select("SELECT inquiry_code FROM fim_delivery_table WHERE delivery_code is not null")
    List<String> getNonZeroDeliveryCodes();


    @Update({
            "UPDATE fim_delivery_table",
            "SET `match` = '是'",  // 使用反引号括起来
            "WHERE inquiry_code = #{inquiryCode}"
    })
    void updateDeliveryTableMatchField(@Param("inquiryCode") String inquiryCode);

}
