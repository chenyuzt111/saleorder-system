package com.benewake.saleordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.saleordersystem.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Update("UPDATE fim_users_table SET FIM_user_YC = #{ycValue}, FIM_user_XD = #{xdValue}, FIM_user_PR = #{prValue} WHERE FIM_user_id = #{userId}")
    int updateUserValues(@Param("userId") Long id, @Param("ycValue") String ycvalue,
                          @Param("xdValue") String xdvalue, @Param("prValue") String prvalue);

    @Insert("INSERT INTO customer_type_dic (customer_type) VALUES (#{customerType})")
    void insertCustomerType(@Param("customerType") String customerType);

    @Delete("DELETE FROM customer_type_dic WHERE customer_type = #{customerType}")
    void deleteCustomerType(@Param("customerType") String customerType);


    @Insert("INSERT INTO inquiry_type_dic (inquiry_type_name) VALUES (#{typeName})")
    int insertInquiryType(@Param("typeName") String typeName);

    @Delete("DELETE FROM inquiry_type_dic WHERE inquiry_type_name = #{typeName}")
    int deleteInquiryType(@Param("typeName") String typeName);

    @Insert("INSERT INTO item_type_dic (item_type_name) VALUES (#{itemTypeName})")
    int insertItemType(@Param("itemTypeName") String itemTypeName);

    @Delete("DELETE FROM item_type_dic WHERE item_type_name = #{itemTypeName}")
    int deleteItemType(@Param("itemTypeName") String itemTypeName);

    @Insert("INSERT INTO fim_customer_table (customer_name) VALUES (#{customerName})")
    int insertCustomerName(@Param("customerName") String customerName);

    @Delete("DELETE FROM fim_customer_table WHERE customer_name = #{customerName}")
    int deleteCustomerName(@Param("customerName") String customerName);

    @Update("UPDATE fim_customer_table SET customer_name = #{newCustomerName} WHERE customer_name = #{oldCustomerName}")
    int updateCustomerName(@Param("oldCustomerName") String oldCustomerName, @Param("newCustomerName") String newCustomerName);

    @Insert("INSERT INTO fim_customer_type_table (customer_id, item_id, customer_type) VALUES (#{customerId}, #{itemId}, #{customerType})")
    int insertCustomerItem(@Param("customerId") int customerId, @Param("itemId") int itemId, @Param("customerType") String customerType);

    @Delete("DELETE FROM fim_customer_type_table WHERE customer_id = #{customerId} AND item_id = #{itemId} AND customer_type = #{customerType}")
    int deleteCustomerItem(
            @Param("customerId") int customerId,
            @Param("itemId") int itemId,
            @Param("customerType") String customerType
    );

    @Insert("INSERT INTO fim_past_customer_rename_table (customer_name_old, customer_name_new) " +
            "VALUES (#{customerNameOld}, #{customerNameNew})")
    int insertCustomerRename(@Param("customerNameOld") String customerNameOld,
                             @Param("customerNameNew") String customerNameNew);


    @Delete("DELETE FROM fim_past_customer_rename_table WHERE customer_name_old = #{customerNameOld}")
    int deleteCustomerRenameByOldName(@Param("customerNameOld") String customerNameOld);

    @Update("UPDATE fim_past_customer_rename_table SET customer_name_new = #{customerNameNew} WHERE customer_name_old = #{customerNameOld}")
    int updateCustomerRename(@Param("customerNameOld") String customerNameOld, @Param("customerNameNew") String customerNameNew);


    @Insert("INSERT INTO fim_past_item_change_table (item_code_old, item_code_new) " +
            "VALUES (#{itemCodeOld}, #{itemCodeNew})")
    int insertItemChange(@Param("itemCodeOld") String itemCodeOld,
                         @Param("itemCodeNew") String itemCodeNew);

    @Delete("DELETE FROM fim_past_item_change_table WHERE item_code_old = #{itemCodeOld}")
    int deleteItemChangeByOldCode(@Param("itemCodeOld") String itemCodeOld);


    @Update("UPDATE fim_past_item_change_table SET item_code_new = #{itemCodeNew} WHERE item_code_old = #{itemCodeOld}")
    int updateItemChange(@Param("itemCodeOld") String itemCodeOld, @Param("itemCodeNew") String itemCodeNew);

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

    @Insert("INSERT INTO fim_past_choose_item_table (item_code, item_name, start_month) " +
            "VALUES (#{itemCode}, #{itemName}, #{startMonth})")
    int insertPastChooseItem(@Param("itemCode") String itemCode,
                             @Param("itemName") String itemName,
                             @Param("startMonth") LocalDateTime startMonth);

    @Delete("DELETE FROM fim_past_choose_item_table WHERE item_code = #{itemCode}")
    int deletePastChooseItemByItemCode(@Param("itemCode") String itemCode);




}
