package com.benewake.saleordersystem.service;


import com.benewake.saleordersystem.entity.LoginTicket;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.basedata.*;
import com.benewake.saleordersystem.excel.model.SalesmanChangingTableModel;
import com.benewake.saleordersystem.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 */
public interface UserService  {

    /**
     * 添加新的用户
     * @param user
     * @return
     */
    Result<Map<String,Object>> addUser(User user);

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    Map<String,Object> login(String username,String password);

    /**
     * 退出登录状态
     * @param ticket
     * @return
     */
    Map<String,Object> logout(String ticket);

    /**
     * 获取登录凭证
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 根据用户Id查找用户
     * @param id
     * @return
     */
    User findUserById(Long id);

    /**
     * 修改用户类型
     * @param id
     * @param type
     * @return
     */
    int updateUserType(Long id,Long type);

    /**
     * 更新用户名
     * @param id
     * @param username
     * @return
     */
    int updateUsername(Long id,String username);

    /**
     * 更新用户密码
     * @param id
     * @param password
     * @return
     */
    int updatePassword(Long id,String password);

    /**
     * 根据姓名模糊匹配用户
     * @param username
     * @return
     */
    List<User> getUsernameLikeList(String username,Long userType);

    /**
     * 根据姓名查找对应的销售员用户对象
     * @param salesmanName
     * @return
     */
    User findSalesmanByName(String salesmanName);


    /**
     * 更新用户YC,XD,PR值
     * @param id
     * @param ycvalue
     * @param xdvalue
     * @param prvalue
     * @return
     */
    int updateUserValues(Long id, String ycvalue, String xdvalue, String prvalue);



    void addCustomerType(String customerType);

    void deleteCustomerType(String customerType);

    List<CustomerTypeDic> getCustomerTypes();

    int addInquiryType(String typeName);

    int deleteInquiryType(String typeName);

    List<InquiryTypeDic> selectInquiryTypeDic();

    int insertItemType(String itemTypeName);

    int deleteItemType(String itemTypeName);

    List<ItemTypeDic> selectItemTypeDic();

    int addCustomerName(String  customerName);

    int deleteCustomerName(String customerName);

    int updateCustomerName(String oldCustomerName, String newCustomerName);

    List<FimCustomerTable> selectFimCustomerTable();
    int insertCustomerItem(int customerId, int itemId, String customerType);

    int deleteCustomerItem(int customerId, int itemId, String customerType);

    List<FimCustomerTypeTable> selectFimCustomerTypeTable();

    int addCustomerRename(String customerNameOld, String customerNameNew);

    int deleteCustomerRenameByOldName(String customerNameOld);

    int updateCustomerRename(String customerNameOld, String customerNameNew);

    List<FimPastCustomerRenameTable> selectFimPastCustomerRenameTable();

    int addItemChange(String itemCodeOld, String itemCodeNew);

    int deleteItemChangeByOldCode(String itemCodeOld);

    int updateItemChange(String itemCodeOld, String itemCodeNew);

    List<FimPastItemChangeTable> selectFimPastItemChangeTable();

    int addSalesmanChanging(String salesmanNameOld, String salesmanNameNew);

    int deleteSalesmanChangingByOldName(String salesmanNameOld);

    int updateSalesmanChanging(String salesmanNameOld, String salesmanNameNew);

    List<FimPastSalesmanChangingTable> selectFimPastSalesmanChangingTable();

    Map<String,Object> checkAddSalesmanChangingTableByExcel(SalesmanChangingTableModel salesmanChangingTableModel, int rowIndex);
    Map<String, Object> saveDataByExcel(MultipartFile file);
    Result addOrdersSalesmanChangingTableByExcel(MultipartFile file);

    int addCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew);

    int deleteCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew);

    List<FimPastCustomizedItemChangingTable> selectFimPastCustomizedItemChangingTable();

    int insertPastChooseItem(String itemCode, String itemName, LocalDateTime startMonth);

    int deletePastChooseItemByItemCode(String itemCode);

    List<FimPastChooseItemTable> selectFimPastChooseItemTable();

    int insertFimItemTable(String itemCode, String itemName, int itemType,int quantitative);

    int deleteFimItemTable(int itemId);

    List<FimItemTable> selectFimItemTable();

    int updateFimItemTable(int itemId,String itemCode, String itemName, int itemType,int quantitative);
}
