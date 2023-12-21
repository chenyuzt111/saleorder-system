package com.benewake.saleordersystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benewake.saleordersystem.entity.LoginTicket;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.basedata.*;
import com.benewake.saleordersystem.excel.*;
import com.benewake.saleordersystem.excel.model.*;
import com.benewake.saleordersystem.mapper.LoginTicketMapper;
import com.benewake.saleordersystem.mapper.UserMapper;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.benewake.saleordersystem.utils.CommonUtils;
import com.benewake.saleordersystem.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Lcs
 * @since 2023年06月30 13:42
 * 描 述：TODO
 */
@Service
public class UserServiceImpl  implements UserService, BenewakeConstants {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Override
    public Result addUser(User user) {
        Map<String,Object> map = new HashMap<>();
        // 空处理 程序错误 抛出异常
        if(null == user){
            return Result.fail().message("用户信息不能为空！");
        }
        // 内容缺失检查
        if(StringUtils.isBlank(user.getUsername())){
            return Result.fail().message("用户名不能为空！");
        }
        if(StringUtils.isBlank(user.getPassword())){
            return Result.fail().message("密码不能为空!");
        }
        if(user.getUserType()==null){
            return Result.fail().message("用户类型不能为空！");
        }

        // 验证用户名是否唯一
        QueryWrapper<User> queryWrap = new QueryWrapper<>();
        queryWrap.eq("FIM_user_name",user.getUsername());
        User u = userMapper.selectOne(queryWrap);
        if(u != null){
            return Result.fail().message("用户已存在！");
        }else{
            // 加密
            user.setSalt(CommonUtils.generateUUID().substring(0, 5));
            user.setPassword(CommonUtils.md5(user.getPassword() + user.getSalt()));

            // 设置默认参数
            if(user.getUserConllection()==null){
                user.setUserConllection(0L);
            }

            // 存入数据库
            userMapper.insert(user);
            return Result.success(map);
        }
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("error", "未注册无法登录，注册请飞书联系管理员！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("error", "密码不能为空！");
            return map;
        }
        // 根据用户名查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("FIM_user_name",username);
        User u = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (null == u) {
            map.put("error", "未注册无法登录，注册请飞书联系管理员!");
            return map;
        }
        if(u.getUserType().equals(USER_TYPE_INVALID)){
            map.put("error","用户无效，请飞书联系管理员！");
            return map;
        }
        // 验证密码
        password = CommonUtils.md5(password + u.getSalt());
        if (!password.equals(u.getPassword())) {
            map.put("error", "密码错误！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(CommonUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + DEFAULT_EXPIRED_SECONDS* 1000L));
        // 持久化
        loginTicketMapper.insert(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        map.put("userId",u.getId());
        map.put("username",u.getUsername());
        map.put("userType",u.getUserType());
        map.put("collection",u.getUserConllection());
        return map;

    }

    @Override
    public Map<String, Object> logout(String ticket) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(ticket)){
            map.put("ticketMessage","ticket不存在或已失效");
            return map;
        }
        // 条件查询
        QueryWrapper<LoginTicket> qw = new QueryWrapper<>();
        qw.eq("FIM_ticket",ticket);
        LoginTicket loginTicket = loginTicketMapper.selectOne(qw);
        if(null == loginTicket || loginTicket.getStatus() != 0 || !loginTicket.getExpired().after(new Date())){
            map.put("ticketMessage","ticket不存在或已失效");
            return map;
        }
        loginTicket.setStatus(1);
        loginTicketMapper.updateById(loginTicket);
        map.put("ticketMessage","退出成功！");
        return map;
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        QueryWrapper<LoginTicket> qw = new QueryWrapper<>();
        qw.eq("FIM_ticket",ticket);
        return loginTicketMapper.selectOne(qw);
    }

    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public int updateUserType(Long id, Long type) {
        User u = userMapper.selectById(id);
        u.setUserType(type);
        return userMapper.updateById(u);
    }

    @Override
    public int updateUsername(Long id, String username) {
        User u = userMapper.selectById(id);
        u.setUsername(username);
        return userMapper.updateById(u);
    }

    @Override
    public int updatePassword(Long id, String password) {
        User u = userMapper.selectById(id);
        if(u ==  null) {
            return -1;
        }
        // 加密存储
        u.setPassword(CommonUtils.md5(password+u.getSalt()));
        return userMapper.updateById(u);
    }

    @Override
    public List<User> getUsernameLikeList(String username,Long userType) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId,User::getUsername)
                .like(StringUtils.isNotBlank(username),User::getUsername,username)
                .eq(userType!=null,User::getUserType,userType);
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public User findSalesmanByName(String salesmanName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId)
                .eq(User::getUsername,salesmanName)
                .eq(User::getUserType,USER_TYPE_SALESMAN);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public int updateUserValues(Long id, String ycvalue, String xdvalue, String prvalue) {

        int rowsAffected = userMapper.updateUserValues(id, ycvalue, xdvalue, prvalue);
        if (rowsAffected > 0) {
            return 1; // 更新成功
        } else {
            return 0; // 更新失败
        }
    }

    @Override
    public void addCustomerType(String customerType) {
        userMapper.insertCustomerType(customerType);
    }
    @Override
    public void deleteCustomerType(String customerType) {
        userMapper.deleteCustomerType(customerType);
    }
    @Override
    public List<CustomerTypeDic> getCustomerTypes() {
        return userMapper.selectCustomerType();
    }
    @Override
    public int addInquiryType(String typeName) {
        return userMapper.insertInquiryType(typeName);
    }
    @Override
    public int deleteInquiryType(String typeName) {
        return userMapper.deleteInquiryType(typeName);
    }

    @Override
    public List<InquiryTypeDic> selectInquiryTypeDic() {
        return userMapper.selectInquiryTypeDic();
    }

    @Override
    public int insertItemType(String itemTypeName) {
        return userMapper.insertItemType(itemTypeName);
    }
    @Override
    public int deleteItemType(String itemTypeName) {
        return userMapper.deleteItemType(itemTypeName);
    }

    @Override
    public List<ItemTypeDic> selectItemTypeDic() {
        return userMapper.selectItemTypeDic();
    }

    @Override
    public int addCustomerName(String  customerName) {
        return userMapper.insertCustomerName(customerName);
    }

    @Override
    public int deleteCustomerName(String customerName) {
        return userMapper.deleteCustomerName(customerName);
    }

    @Override
    public int updateCustomerName(String oldCustomerName, String newCustomerName) {
        return userMapper.updateCustomerName(oldCustomerName,newCustomerName);
    }

    public Map<String,Object> checkCustomerNameByExcel(CustomerModel customerModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimCustomerTable fimCustomerTable=new FimCustomerTable();
        List<FimCustomerTable> a = selectFimCustomerTable();
        for (FimCustomerTable fimCustomerTable1:a) {
            if (Objects.equals(customerModel.getCustomerName(), fimCustomerTable1.getCustomerName())) {
                map.put("error", "第" + rowIndex + "行的客户已存在替换，请核对！");
                return map;
            }
        }
        fimCustomerTable.setCustomerName(customerModel.getCustomerName());



        map.put("fimCustomerTable",fimCustomerTable);
        return map;
    };

    public Result addCustomerNameByExcel(MultipartFile file) {
        List<FimCustomerTable> existList = userMapper.selectFimCustomerTable();
        CustomerListener listener = new CustomerListener(this, existList);
        Map<String, Object> map = new HashMap<>(16);

        try {
            EasyExcel.read(file.getInputStream(), CustomerModel.class, listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查map中是否包含"error"键，表示处理失败
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            return Result.success().message((String) map.get("success"));
        }
    }
    @Override
    public List<FimCustomerTable> selectFimCustomerTable() {
        return userMapper.selectFimCustomerTable();
    }
    @Override
    public int insertCustomerItem(int customerId, int itemId, String customerType) {
        return userMapper.insertCustomerItem(customerId, itemId, customerType);
    }

    @Override
    public int deleteCustomerItem(int customerId, int itemId, String customerType) {
        return userMapper.deleteCustomerItem(customerId, itemId, customerType);
    }

    @Override
    public List<FimCustomerTypeTable> selectFimCustomerTypeTable() {
        return userMapper.selectFimCustomerTypeTable();
    }
    @Override
    public int addCustomerRename(String customerNameOld, String customerNameNew) {
        return userMapper.insertCustomerRename(customerNameOld, customerNameNew);
    }


    @Override
    public int deleteCustomerRenameByOldName(String customerNameOld) {
        return userMapper.deleteCustomerRenameByOldName(customerNameOld);
    }

    public int updateCustomerRename(String customerNameOld, String customerNameNew) {
        return userMapper.updateCustomerRename(customerNameOld, customerNameNew);
    }

    public Map<String,Object> checkCustomerRenameByExcel(CustomerRenameModel customerRenameModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimPastCustomerRenameTable fimPastCustomerRenameTable=new FimPastCustomerRenameTable();
        List<FimPastCustomerRenameTable> a = selectFimPastCustomerRenameTable();
        for (FimPastCustomerRenameTable fimPastCustomerRenameTable1:a) {
            if (Objects.equals(customerRenameModel.getCustomerNameOld(), fimPastCustomerRenameTable1.getCustomerNameOld())) {
                map.put("error", "第" + rowIndex + "行的客户已存在替换，请核对！");
                return map;
            }
        }
        fimPastCustomerRenameTable.setCustomerNameOld(customerRenameModel.getCustomerNameOld());
        fimPastCustomerRenameTable.setCustomerNameNew(customerRenameModel.getCustomerNameNew());


        map.put("fimPastCustomerRenameTable",fimPastCustomerRenameTable);
        return map;
    };

    public Result addCustomerRenameByExcel(MultipartFile file) {
        List<FimPastCustomerRenameTable> existList = userMapper.selectFimPastCustomerRenameTable();
        CustomerRenameListener listener = new CustomerRenameListener(this, existList);
        Map<String, Object> map = new HashMap<>(16);

        try {
            EasyExcel.read(file.getInputStream(), CustomerRenameModel.class, listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查map中是否包含"error"键，表示处理失败
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            return Result.success().message((String) map.get("success"));
        }
    }

    @Override
    public List<FimPastCustomerRenameTable> selectFimPastCustomerRenameTable() {
        return userMapper.selectFimPastCustomerRenameTable();
    }
    @Override
    public int addItemChange(String itemCodeOld, String itemCodeNew) {
        return userMapper.insertItemChange(itemCodeOld, itemCodeNew);
    }

    @Override
    public int deleteItemChangeByOldCode(String itemCodeOld) {
        return userMapper.deleteItemChangeByOldCode(itemCodeOld);
    }

    @Override
    public int updateItemChange(String itemCodeOld, String itemCodeNew) {
        return userMapper.updateItemChange(itemCodeOld, itemCodeNew);
    }

    public Map<String,Object> checkItemChangeByExcel(ItemChangeModel itemChangeModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimPastItemChangeTable fimPastItemChangeTable=new FimPastItemChangeTable();
        List<FimPastItemChangeTable> a = selectFimPastItemChangeTable();
        for (FimPastItemChangeTable fimPastItemChangeTable1:a) {
            if (itemChangeModel.getItemCodeOld()== fimPastItemChangeTable1.getItemCodeOld() ) {
                map.put("error", "第" + rowIndex + "行的物料编码已存在，请核对！");
                return map;
            }
        }
        fimPastItemChangeTable.setItemCodeOld(itemChangeModel.getItemCodeOld());
        fimPastItemChangeTable.setItemCodeNew(itemChangeModel.getItemCodeNew());


        map.put("fimPastItemChangeTable",fimPastItemChangeTable);
        return map;
    };

    public Result addItemChangeByExcel(MultipartFile file) {
        List<FimPastItemChangeTable> existList = userMapper.selectFimPastItemChangeTable();
        ItemChangeListener listener = new ItemChangeListener(this, existList);
        Map<String, Object> map = new HashMap<>(16);

        try {
            EasyExcel.read(file.getInputStream(), ItemChangeModel.class, listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查map中是否包含"error"键，表示处理失败
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            return Result.success().message((String) map.get("success"));
        }
    }
    @Override
    public List<FimPastItemChangeTable> selectFimPastItemChangeTable() {
        return userMapper.selectFimPastItemChangeTable();
    }

    @Override
    public int addSalesmanChanging(String salesmanNameOld, String salesmanNameNew) {
        return userMapper.insertSalesmanChanging(salesmanNameOld, salesmanNameNew);
    }

    @Override
    public int deleteSalesmanChangingByOldName(String salesmanNameOld) {
        return userMapper.deleteSalesmanChangingByOldName(salesmanNameOld);
    }

    @Override
    public int updateSalesmanChanging(String salesmanNameOld, String salesmanNameNew) {
        return userMapper.updateSalesmanChanging(salesmanNameOld, salesmanNameNew);
    }
    @Override
    public List<FimPastSalesmanChangingTable> selectFimPastSalesmanChangingTable() {
        return userMapper.selectFimPastSalesmanChangingTable();
    }

    public Map<String,Object> checkAddSalesmanChangingTableByExcel(SalesmanChangingTableModel salesmanChangingTableModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimPastSalesmanChangingTable fimPastSalesmanChangingTable=new FimPastSalesmanChangingTable();
        List<FimPastSalesmanChangingTable> a = selectFimPastSalesmanChangingTable();
        for (FimPastSalesmanChangingTable fimPastSalesmanChangingTable1:a) {
            if (salesmanChangingTableModel.getSalesman_name_old() == fimPastSalesmanChangingTable1.getSalesmanNameOld()) {
                map.put("error", "第" + rowIndex + "行的销售员名称已存在，请核对");
                return map;
            }
        }
        fimPastSalesmanChangingTable.setSalesmanNameNew(salesmanChangingTableModel.getSalesman_name_new());
        fimPastSalesmanChangingTable.setSalesmanNameOld(salesmanChangingTableModel.getSalesman_name_old());

        map.put("fimPastSalesmanChangingTable",fimPastSalesmanChangingTable);
        return map;
    };

//    public Map<String, Object> saveDataByExcel(MultipartFile file) {
//        List<FimPastSalesmanChangingTable> existList = userMapper.selectFimPastSalesmanChangingTable();
//        SalesmanChangingTableListener listener = new SalesmanChangingTableListener(this,existList);
//        Map<String,Object> map;
//        try{
//            EasyExcel.read(file.getInputStream(), SalesmanChangingTableModel.class,listener).sheet().headRowNumber(1).doRead();
//            map = listener.getMap();
//        }catch (Exception e) {
//            map = listener.getMap();
//            e.printStackTrace();
//        }
//        return map;
//    }
public Result addOrdersSalesmanChangingTableByExcel(MultipartFile file) {
    List<FimPastSalesmanChangingTable> existList = userMapper.selectFimPastSalesmanChangingTable();
    SalesmanChangingTableListener listener = new SalesmanChangingTableListener(this, existList);
    Map<String, Object> map = new HashMap<>(16);

    try {
        EasyExcel.read(file.getInputStream(), SalesmanChangingTableModel.class, listener).sheet().headRowNumber(1).doRead();
        map = listener.getMap();
    } catch (Exception e) {
        e.printStackTrace();
    }

    // 检查map中是否包含"error"键，表示处理失败
    if (map.containsKey("error")) {
        return Result.fail().message((String) map.get("error"));
    } else {
        return Result.success().message((String) map.get("success"));
    }
}


    public int addCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew) {
        return userMapper.insertCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
    }

    public int deleteCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew) {
        return userMapper.deleteCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
    }

    public Map<String,Object> checkCustomizedItemChangeByExcel(CustomizedItemChangeModel customizedItemChangeModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimPastCustomizedItemChangingTable fimPastCustomizedItemChangingTable=new FimPastCustomizedItemChangingTable();
        List<FimPastCustomizedItemChangingTable> a = selectFimPastCustomizedItemChangingTable();
        for (FimPastCustomizedItemChangingTable fimPastCustomizedItemChangingTable1:a) {
            if (customizedItemChangeModel.getCustomerName()== fimPastCustomizedItemChangingTable1.getCustomerName() && customizedItemChangeModel.getItemCodeOld()==fimPastCustomizedItemChangingTable1.getItemCodeOld()) {
                map.put("error", "第" + rowIndex + "行的客户的该编码已有替换物料编码，请核对");
                return map;
            }
        }
        fimPastCustomizedItemChangingTable.setCustomerName(customizedItemChangeModel.getCustomerName());
        fimPastCustomizedItemChangingTable.setItemCodeNew(customizedItemChangeModel.getCustomerName());
        fimPastCustomizedItemChangingTable.setItemCodeOld(customizedItemChangeModel.getItemCodeOld());

        map.put("fimPastCustomizedItemChangingTable",fimPastCustomizedItemChangingTable);
        return map;
    };

    public Result addCustomizedItemChangeByExcel(MultipartFile file) {
        List<FimPastCustomizedItemChangingTable> existList = userMapper.selectFimPastCustomizedItemChangingTable();
        CustomizedItemChangeListener listener = new CustomizedItemChangeListener(this, existList);
        Map<String, Object> map = new HashMap<>(16);

        try {
            EasyExcel.read(file.getInputStream(), CustomizedItemChangeModel.class, listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查map中是否包含"error"键，表示处理失败
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            return Result.success().message((String) map.get("success"));
        }
    }
    @Override
    public List<FimPastCustomizedItemChangingTable> selectFimPastCustomizedItemChangingTable() {
        return userMapper.selectFimPastCustomizedItemChangingTable();
    }

    public int insertPastChooseItem(String itemCode, String itemName, Date startMonth) {
        return userMapper.insertPastChooseItem(itemCode, itemName, startMonth);
    }

    public int deletePastChooseItemByItemCode(String itemCode) {
        return userMapper.deletePastChooseItemByItemCode(itemCode);
    }

    public Map<String,Object> checkChooseItemByExcel(ChooseItemModel chooseItemModel, int rowIndex) {
        Map<String, Object> map = new HashMap<>();
        FimPastChooseItemTable fimPastChooseItemTable=new FimPastChooseItemTable();
        List<FimPastChooseItemTable> a = selectFimPastChooseItemTable();
        for (FimPastChooseItemTable fimPastChooseItemTable1:a) {
            if (chooseItemModel.getItemCode()== fimPastChooseItemTable1.getItemCode() ) {
                map.put("error", "第" + rowIndex + "行的物料编码已存在，请核对！");
                return map;
            }
        }
        fimPastChooseItemTable.setItemCode(chooseItemModel.getItemCode());
        fimPastChooseItemTable.setItemName(chooseItemModel.getItemName());
        fimPastChooseItemTable.setStartMonth(chooseItemModel.getStartMonth());


        map.put("fimPastChooseItemTable",fimPastChooseItemTable);
        return map;
    };

    public Result addChooseItemByExcel(MultipartFile file) {
        List<FimPastChooseItemTable> existList = userMapper.selectFimPastChooseItemTable();
        ChooseItemListener listener = new ChooseItemListener(this, existList);
        Map<String, Object> map = new HashMap<>(16);

        try {
            EasyExcel.read(file.getInputStream(), ChooseItemModel.class, listener).sheet().headRowNumber(1).doRead();
            map = listener.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查map中是否包含"error"键，表示处理失败
        if (map.containsKey("error")) {
            return Result.fail().message((String) map.get("error"));
        } else {
            return Result.success().message((String) map.get("success"));
        }
    }

    @Override
    public List<FimPastChooseItemTable> selectFimPastChooseItemTable() {
        return userMapper.selectFimPastChooseItemTable();
    }

    @Override
    public int insertFimItemTable(String itemCode, String itemName, int itemType,int quantitative) {
        return userMapper.insertFimItemTable( itemCode,itemName,itemType,quantitative);
    }

    @Override
    public int updateFimItemTable(int itemId,String itemCode, String itemName, int itemType,int quantitative) {
        return userMapper.updateFimItemTable(itemId, itemCode,itemName,itemType,quantitative);
    }


    public int deleteFimItemTable(int itemId) {
        return userMapper.deleteFimItemTable(itemId);
    }

    @Override
    public List<FimItemTable> selectFimItemTable() {
        return userMapper.selectFimItemTable();
    }
}
