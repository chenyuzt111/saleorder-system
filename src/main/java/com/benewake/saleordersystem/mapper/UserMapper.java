package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.basedata.InquiryTypeDic;
import com.benewake.saleordersystem.entity.basedata.*;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Update("UPDATE fim_users_table SET FIM_user_YC = #{ycValue}, FIM_user_XD = #{xdValue}, FIM_user_PR = #{prValue} WHERE FIM_user_id = #{userId}")
    int updateUserValues(@Param("userId") Long id, @Param("ycValue") String ycvalue,
                          @Param("xdValue") String xdvalue, @Param("prValue") String prvalue);

    @Insert("INSERT INTO fim_customer_type_dic (customer_type) VALUES (#{customerType})")
    void insertCustomerType(@Param("customerType") String customerType);

    @Delete("DELETE FROM fim_customer_type_dic WHERE customer_type = #{customerType}")
    void deleteCustomerType(@Param("customerType") String customerType);

    @Select("<script>"
            + "select *"
            + "from fim_customer_type_dic"
            + "</script>")
    List<CustomerTypeDic> selectCustomerType();


    @Insert("INSERT INTO fim_inquiry_type_dic (inquiry_type_name) VALUES (#{typeName})")
    int insertInquiryType(@Param("typeName") String typeName);

    @Delete("DELETE FROM inquiry_type_dic WHERE inquiry_type_name = #{typeName}")
    int deleteInquiryType(@Param("typeName") String typeName);

    @Select("<script>"
            + "select *"
            + "from fim_inquiry_type_dic"
            + " order by inquiry_type "
            + "</script>")
    List<InquiryTypeDic> selectInquiryTypeDic();

    @Insert("INSERT INTO fim_item_type_dic (item_type_name) VALUES (#{itemTypeName})")
    int insertItemType(@Param("itemTypeName") String itemTypeName);

    @Delete("DELETE FROM fim_item_type_dic WHERE item_type_name = #{itemTypeName}")
    int deleteItemType(@Param("itemTypeName") String itemTypeName);

    @Select("<script>"
            + "select *"
            + "from fim_item_type_dic"
            +" order by item_type"
            + "</script>")
    List<ItemTypeDic> selectItemTypeDic();

    @Insert("INSERT INTO fim_customer_table (customer_name) VALUES (#{customerName})")
    int insertCustomerName(@Param("customerName") String customerName);

    @Delete("DELETE FROM fim_customer_table WHERE customer_name = #{customerName}")
    int deleteCustomerName(@Param("customerName") String customerName);

    @Update("UPDATE fim_customer_table SET customer_name = #{newCustomerName} WHERE customer_name = #{oldCustomerName}")
    int updateCustomerName(@Param("oldCustomerName") String oldCustomerName, @Param("newCustomerName") String newCustomerName);

    @Select("<script>"
            + "select *"
            + "from fim_customer_table"
            +" order by customer_id"
            + "</script>")
    List<FimCustomerTable> selectFimCustomerTable();

    @Insert("INSERT INTO fim_customer_type_table (customer_id, item_id, customer_type) VALUES (#{customerId}, #{itemId}, #{customerType})")
    int insertCustomerItem(@Param("customerId") int customerId, @Param("itemId") int itemId, @Param("customerType") String customerType);

    @Delete("DELETE FROM fim_customer_type_table WHERE customer_id = #{customerId} AND item_id = #{itemId} AND customer_type = #{customerType}")
    int deleteCustomerItem(
            @Param("customerId") int customerId,
            @Param("itemId") int itemId,
            @Param("customerType") String customerType
    );

    @Select("<script>"
            + "select *"
            + "from fim_customer_type_table"
            +" order by customer_id"
            + "</script>")
    List<FimCustomerTypeTable> selectFimCustomerTypeTable();

    @Insert("INSERT INTO fim_past_customer_rename_table (customer_name_old, customer_name_new) " +
            "VALUES (#{customerNameOld}, #{customerNameNew})")
    int insertCustomerRename(@Param("customerNameOld") String customerNameOld,
                             @Param("customerNameNew") String customerNameNew);


    @Delete("DELETE FROM fim_past_customer_rename_table WHERE customer_name_old = #{customerNameOld}")
    int deleteCustomerRenameByOldName(@Param("customerNameOld") String customerNameOld);

    @Update("UPDATE fim_past_customer_rename_table SET customer_name_new = #{customerNameNew} WHERE customer_name_old = #{customerNameOld}")
    int updateCustomerRename(@Param("customerNameOld") String customerNameOld, @Param("customerNameNew") String customerNameNew);

    @Select("<script>"
            + "select *"
            + "from fim_past_customer_rename_table"
            + "</script>")
    List<FimPastCustomerRenameTable> selectFimPastCustomerRenameTable();

    @Insert("INSERT INTO fim_past_item_change_table (item_code_old, item_code_new) " +
            "VALUES (#{itemCodeOld}, #{itemCodeNew})")
    int insertItemChange(@Param("itemCodeOld") String itemCodeOld,
                         @Param("itemCodeNew") String itemCodeNew);

    @Delete("DELETE FROM fim_past_item_change_table WHERE item_code_old = #{itemCodeOld}")
    int deleteItemChangeByOldCode(@Param("itemCodeOld") String itemCodeOld);


    @Update("UPDATE fim_past_item_change_table SET item_code_new = #{itemCodeNew} WHERE item_code_old = #{itemCodeOld}")
    int updateItemChange(@Param("itemCodeOld") String itemCodeOld, @Param("itemCodeNew") String itemCodeNew);

    @Select("<script>"
            + "select *"
            + "from fim_past_item_change_table"
            + "</script>")
    List<FimPastItemChangeTable> selectFimPastItemChangeTable();


    @Insert("INSERT INTO fim_past_salesman_changing_table (salesman_name_old, salesman_name_new) " +
            "VALUES (#{salesmanNameOld}, #{salesmanNameNew})")
    int insertSalesmanChanging(@Param("salesmanNameOld") String salesmanNameOld,
                               @Param("salesmanNameNew") String salesmanNameNew);

    @Delete("DELETE FROM fim_past_salesman_changing_table WHERE salesman_name_old = #{salesmanNameOld}")
    int deleteSalesmanChangingByOldName(@Param("salesmanNameOld") String salesmanNameOld);

    @Update("UPDATE fim_past_salesman_changing_table SET salesman_name_new = #{salesmanNameNew} " +
            "WHERE salesman_name_old = #{salesmanNameOld}")
    int updateSalesmanChanging(@Param("salesmanNameOld") String salesmanNameOld,
                               @Param("salesmanNameNew") String salesmanNameNew);

    @Select("<script>"
            + "select *"
            + "from fim_past_salesman_changing_table"
            + "</script>")
    List<FimPastSalesmanChangingTable> selectFimPastSalesmanChangingTable();


    @Insert("INSERT INTO fim_past_customized_item_changing_table (customer_name, item_code_old, item_code_new) " +
            "VALUES (#{customerName}, #{itemCodeOld}, #{itemCodeNew})")
    int insertCustomizedItemChange(@Param("customerName") String customerName,
                                   @Param("itemCodeOld") String itemCodeOld,
                                   @Param("itemCodeNew") String itemCodeNew);

    @Delete("DELETE FROM fim_past_customized_item_changing_table " +
            "WHERE customer_name = #{customerName} " +
            "AND item_code_old = #{itemCodeOld} " +
            "AND item_code_new = #{itemCodeNew}")
    int deleteCustomizedItemChange(@Param("customerName") String customerName,
                                   @Param("itemCodeOld") String itemCodeOld,
                                   @Param("itemCodeNew") String itemCodeNew);


    @Select("<script>"
            + "select *"
            + "from fim_past_customized_item_changing_table"
            + "</script>")
    List<FimPastCustomizedItemChangingTable> selectFimPastCustomizedItemChangingTable();

    @Insert("INSERT INTO fim_past_choose_item_table (item_code, item_name, start_month) " +
            "VALUES (#{itemCode}, #{itemName}, #{startMonth})")
    int insertPastChooseItem(@Param("itemCode") String itemCode,
                             @Param("itemName") String itemName,
                             @Param("startMonth") LocalDateTime startMonth);

    @Delete("DELETE FROM fim_past_choose_item_table WHERE item_code = #{itemCode}")
    int deletePastChooseItemByItemCode(@Param("itemCode") String itemCode);

    @Select("<script>"
            + "select *"
            + "from fim_past_choose_item_table"
            + "</script>")
    List<FimPastChooseItemTable> selectFimPastChooseItemTable();



}
