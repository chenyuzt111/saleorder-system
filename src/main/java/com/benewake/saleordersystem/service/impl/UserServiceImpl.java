package com.benewake.saleordersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benewake.saleordersystem.entity.LoginTicket;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.mapper.LoginTicketMapper;
import com.benewake.saleordersystem.mapper.UserMapper;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.benewake.saleordersystem.utils.CommonUtils;
import com.benewake.saleordersystem.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public int addInquiryType(String typeName) {
        return userMapper.insertInquiryType(typeName);
    }
    @Override
    public int deleteInquiryType(String typeName) {
        return userMapper.deleteInquiryType(typeName);
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

    @Override
    public int insertCustomerItem(int customerId, int itemId, String customerType) {
        return userMapper.insertCustomerItem(customerId, itemId, customerType);
    }

    @Override
    public int deleteCustomerItem(int customerId, int itemId, String customerType) {
        return userMapper.deleteCustomerItem(customerId, itemId, customerType);
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


    public int addCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew) {
        return userMapper.insertCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
    }

    public int deleteCustomizedItemChange(String customerName, String itemCodeOld, String itemCodeNew) {
        return userMapper.deleteCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
    }

    public int insertPastChooseItem(String itemCode, String itemName, LocalDateTime startMonth) {
        return userMapper.insertPastChooseItem(itemCode, itemName, startMonth);
    }

    public int deletePastChooseItemByItemCode(String itemCode) {
        return userMapper.deletePastChooseItemByItemCode(itemCode);
    }

}
