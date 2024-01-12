package com.benewake.saleordersystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSONObject;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String userid =getUserid(u.getUsername());

        String uselist = UserList();
        if(!uselist.contains(userid)){
            map.put("error", "您不在应用允许访问范围内，如需访问请联系管理员!");
            return map;
        }

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



    public String getAppAccessToken() {

        String appId = "cli_a5e56060a07ad00c";
        String appSecret = "hRLkCUdZX7rWvynsDCkANeYnZLJCjDMn";

        String FEISHU_ACCESS_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
        // 构造请求体
        String requestBody = "{\"app_id\": \"" + appId + "\", \"app_secret\": \"" + appSecret + "\"}";

        // 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构造 HTTP 请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 发送 POST 请求
        ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(FEISHU_ACCESS_TOKEN_URL, requestEntity, String.class);

        // 处理响应
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // 解析 JSON 响应，获取 app_access_token
            // 使用 Gson 解析 JSON
            JsonObject jsonObject = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject();
            if (jsonObject.has("app_access_token")) {
                return jsonObject.get("app_access_token").getAsString();
            }
        } else {
            // 处理请求失败的情况
            System.err.println("Failed to retrieve Feishu app access token. Status code: " + responseEntity.getStatusCode());
        }
        return null;
    }

    public String getUserAccessToken(String authorizationCode, String appAccessToken) {
        try {
            // 请求地址
            String url = "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token";

            // 请求体
            String requestBody = "{\"grant_type\":\"authorization_code\",\"code\":\"" + authorizationCode + "\"}";

            // 请求头
            String authorizationHeader = "Bearer " + appAccessToken;

            // 创建URL对象
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // 设置请求方法为POST
            con.setRequestMethod("POST");

            // 设置请求头
            con.setRequestProperty("Authorization", authorizationHeader);
            con.setRequestProperty("Content-Type", "application/json");

            // 启用输入输出
            con.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String responseBody = response.toString();

                // 使用正则表达式提取 usertoken 中的内容
                Pattern pattern = Pattern.compile("access_token\":\"([^\\\"]+)\"");
                Matcher matcher = pattern.matcher(responseBody);

                if (matcher.find()) {
                    String usertoken = matcher.group(1);

                    // 打印响应内容
                    System.out.println("HTTP Status Code: " + con.getResponseCode());
                    System.out.println("Response Body: " + responseBody);
                    System.out.println("User Token: " + usertoken);

                    // 返回 usertoken 的值
                    return usertoken;
                } else {
                    // 如果没有匹配到，可以抛出异常或者返回特殊值
                    System.out.println("No match found for usertoken in the response.");
                    return null; // or throw a custom exception
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 如果发生异常，可以返回一个特殊的值或者抛出自定义异常
                return null; // or throw a custom exception
            }} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String userInfo(String usertoken) throws IOException {
        // 请求地址
        String url = "https://open.feishu.cn/open-apis/authen/v1/user_info";



        // 请求头
        String authorizationHeader = "Bearer " + usertoken;

        // 创建URL对象
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // 设置请求方法为POST
        con.setRequestMethod("GET");

        // 设置请求头
        con.setRequestProperty("Authorization", authorizationHeader);
        con.setRequestProperty("Content-Type", "application/json");

        // 启用输入输出
        con.setDoOutput(true);

        // 获取响应
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String responseBody = response.toString();

            // 使用正则表达式提取 name 中的内容
            Pattern pattern = Pattern.compile("name\":\"([^\\\"]+)\"");
            Matcher matcher = pattern.matcher(responseBody);

            // 检查是否找到匹配项
            if (matcher.find()) {
                String name = matcher.group(1);

                // 打印响应内容
                System.out.println("HTTP Status Code: " + con.getResponseCode());
                System.out.println("Response Body: " + responseBody);
                System.out.println("Name: " + name);

                // 返回 name 的值
                return name;
            } else {
                // 如果没有匹配到，可以抛出异常或者返回特殊值
                System.out.println("No match found for name in the response.");
                return null; // or throw a custom exception
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果发生异常，可以返回一个特殊的值或者抛出自定义异常
            return null; // or throw a custom exception
        }

    }

    public void askForPermission() {
        String url = "https://open.feishu.cn/open-apis/authen/v1/authorize?app_id=cli_a5e56060a07ad00c&redirect_uri=http://localhost:8080//benewake/callback";
        try {
            // Create URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to GET
            con.setRequestMethod("GET");

            // Perform the GET request
            con.connect();

            // Check the HTTP response code (optional)
            int responseCode = con.getResponseCode();
            System.out.println("HTTP Status Code: " + responseCode);

            // Close the connection
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception as needed (e.g., logging)
        }
    }


    @Override
    public Map<String, Object> feishulogin(String username) {

        Map<String,Object> map = new HashMap<>();


        // 根据用户名查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("FIM_user_name",username);
        User u = userMapper.selectOne(queryWrapper);

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

//获取应用token
    private static String getAuthorizationHeader() {
        String apiUrl = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
        String appId = "cli_a5e56060a07ad00c";
        String appSecret = "hRLkCUdZX7rWvynsDCkANeYnZLJCjDMn";

        // 创建 HttpClient
        HttpClient httpClient = HttpClients.createDefault();

        try {
            // 创建带有 API URL 和头部的 HttpPost 请求
            HttpPost request = new HttpPost(apiUrl);

            // 设置请求头部
            request.setHeader("Content-Type", "application/json");

            // 设置请求体
            String requestBody = "{\"app_id\":\"" + appId + "\",\"app_secret\":\"" + appSecret + "\"}";
            request.setEntity(new StringEntity(requestBody));

            // 发送 POST 请求
            HttpResponse response = httpClient.execute(request);

            // 提取响应实体
            org.apache.http.HttpEntity entity = response.getEntity();

            // 解析响应并提取 tenant_access_token
            String responseString = EntityUtils.toString(entity);
            System.out.println("Response: " + responseString);

            // 解析 JSON 并提取 tenant_access_token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseString);

            if (jsonNode.has("tenant_access_token")) {
                return jsonNode.get("tenant_access_token").asText();
            } else {
                System.out.println("Response does not contain tenant_access_token.");
                return null; // 根据需求抛出异常或返回 null
            }

        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            return null; // 根据需求抛出异常或返回 null
        }
    }
//获取用户id
    public String getUserid(String username){
        try {
            // 请求地址
            String url = "https://open.feishu.cn/open-apis/contact/v3/users/batch_get_id?user_id_type=user_id";

            // 请求体
            String requestBody = "{\"emails\":[\""+removeSpacesAndDigits(convertToPinyin(username))+"@benewake.com\"],\"include_resigned\":true}";


            // 请求头
            String authorizationHeader = "Bearer " + getAuthorizationHeader();

            // 创建URL对象
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // 设置请求方法为POST
            con.setRequestMethod("POST");

            // 设置请求头
            con.setRequestProperty("Authorization", authorizationHeader);
            con.setRequestProperty("Content-Type", "application/json");

            // 启用输入输出
            con.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String responseBody = response.toString();

                // 使用正则表达式提取 usertoken 中的内容
                Pattern pattern = Pattern.compile("user_id\":\"([^\\\"]+)\"");
                Matcher matcher = pattern.matcher(responseBody);

                if (matcher.find()) {
                    String user_id = matcher.group(1);

                    System.out.println("User Token: " + user_id);

                    // 返回 usertoken 的值
                    return user_id;
                } else {
                    // 如果没有匹配到，可以抛出异常或者返回特殊值
                    System.out.println("No match found for usertoken in the response.");
                    return null; // or throw a custom exception
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 如果发生异常，可以返回一个特殊的值或者抛出自定义异常
                return null; // or throw a custom exception
            }} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String UserList(){
        try {
            // 请求地址
            String url = "https://open.feishu.cn/open-apis/application/v2/app/visibility";

            // 请求体
            String requestBody = "{\n" +
                    "    \"app_id\":\"cli_a5e56060a07ad00c\"\n" +
                    "}";


            // 请求头
            String authorizationHeader = "Bearer " + getAuthorizationHeader();

            // 创建URL对象
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // 设置请求方法为POST
            con.setRequestMethod("POST");

            // 设置请求头
            con.setRequestProperty("Authorization", authorizationHeader);
            con.setRequestProperty("Content-Type", "application/json");

            // 启用输入输出
            con.setDoOutput(true);

            // 发送请求体
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertToPinyin(String input) {
        StringBuilder pinyinBuilder = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char c : chars) {
            // 将中文字符转换为拼音，非中文字符保持不变
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                // 判断拼音是否为"lu:3"，如果是则替换为"lv"
                if ("lu:3".equals(pinyinArray[0])) {
                    pinyinBuilder.append("lv");
                } else {
                    pinyinBuilder.append(pinyinArray[0]);
                }
            } else {
                pinyinBuilder.append(c);
            }
        }


        return pinyinBuilder.toString();
    }

    private static String removeSpacesAndDigits(String input) {
        // 去除空格和数字
        return input.replaceAll("[\\s\\d]", "");
    }
}
