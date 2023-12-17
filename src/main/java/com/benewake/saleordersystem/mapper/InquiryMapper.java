package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.saleordersystem.entity.Inquiry;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Lcs
 */
@Mapper
public interface InquiryMapper extends BaseMapper<Inquiry> {

    /**
     * 批量添加询单信息
     * @param lists
     * @return
     */
    @Insert("<script> " +
            "Insert into fim_inquiry_table(inquiry_code,state," +
            "created_user,salesman_id,item_id,customer_id,sale_num,expected_time,arranged_time,inquiry_type,remark) " +
            "values " +
            "<foreach collection='lists' item='list' separator=','> " +
            "(" +
            "#{list.inquiryCode},#{list.state},#{list.createdUser}," +
            "#{list.salesmanId},#{list.itemId},#{list.customerId},#{list.saleNum}," +
            "#{list.expectedTime},#{list.arrangedTime},#{list.inquiryType},#{list.remark} " +
            ")" +
            "</foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty = "inquiryId")
    int insertInquiries(@Param("lists") List<Inquiry> lists);

    /**
     * 更新state 和 arrangeTIme
     * @param success
     * @return
     */
    @Update("<script>" +
            "<foreach collection='list' item='item' index='index' separator=';'>" +
            "update fim_inquiry_table " +
            "<set>" +
            "state = #{item.state} " +
            "<if test='item.arrangedTime!=null'> " +
            ",arranged_time = #{item.arrangedTime} " +
            "</if> " +
            "</set> " +
            "where inquiry_id = #{item.inquiryId} " +
            "</foreach> " +
            "</script>")
    Integer ipdateByInquiry(@Param("list") List<Inquiry> success);

    @Select("<script>" +
            "select inquiry_type_name from fim_inquiry_type_dic " +
            "where inquiry_type_name like #{type}" +
            "</script>")
    List<String> getInquiryTypeList(@Param("type") String s);

    @Select("<script>" +
            "select state from fim_inquiry_table " +
            "group by state order by state asc " +
            "</script>")
    List<String> getStateList();

    @Update("UPDATE fim_inquiry_table SET allow_inquiry = 1 WHERE inquiry_id = #{inquiryId}")
    void updateInquiryAllowInquiry(@Param("inquiryId") Long inquiryId);


    @Select("SELECT * FROM fim_inquiry_table WHERE inquiry_code = #{inquiryCode} AND state>=0")
    Inquiry getInquiriesByCode(@Param("inquiryCode") String inquiryCode);


    @Update("UPDATE fim_inquiry_table " +
            "SET inquiry_type = 1 " +
            "WHERE inquiry_code = #{inquiryCode} AND state >= 0")
    void updateInquiryTypeByCode(@Param("inquiryCode") String inquiryCode);

    @Select("select inquiry_code" +
            " from fim_inquiry_table " +
            "where inquiry_id = #{inquiryId}")
    String selectcodeByid(@Param("inquiryId") int inquiryId);


    @Update("update fim_inquiry_table " +
            "set state = 0 " +
            "where inquiry_code = #{inquiryCode} and state = -2")
    int restoreOrder(@Param("inquiryCode") String inquiryCode);
}
