package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.annotation.AdminRequired;
import com.benewake.saleordersystem.annotation.LoginRequired;
import com.benewake.saleordersystem.entity.Customer;
import com.benewake.saleordersystem.entity.Item;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.entity.basedata.*;
import com.benewake.saleordersystem.service.CustomerService;
import com.benewake.saleordersystem.service.ItemService;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_ADMIN;
import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_SYSTEM;

/**
 * @author Lcs
 * @since 2023年07月06 15:34
 * 描 述： TODO
 */
@Api(tags = "管理员管理")
@ResponseBody
@RequestMapping("/admin")
@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加新用户
     * @return
     */
    @ApiOperation("添加新用户接口")
    @AdminRequired
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        return userService.addUser(user);
    }

    @ApiOperation("管理员修改用户YC、XD、PR值")
    @PostMapping("/updateUserValues")
    @LoginRequired
    public Result updateUserValues(@RequestBody Map<String, Object> param) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("只有管理员用户才可以修改用户值！");
        }

        Long id = Long.parseLong((String)param.get("userid"));
        String ycvalue = ((String)param.get("ycValue"));
        String xdvalue = ((String)param.get("xdValue"));
        String prvalue = ((String)param.get("prValue"));

        if ( StringUtils.isEmpty(ycvalue) || StringUtils.isEmpty(xdvalue) || StringUtils.isEmpty(prvalue)) {
            return Result.fail().message("数据不能为空！");
        }

        // 执行更新操作
        int result = userService.updateUserValues(id, ycvalue, xdvalue, prvalue);

        if (result > 0) {
            return Result.success().message("用户值更新成功！");
        } else {
            return Result.fail().message("用户值更新失败！");
        }
    }


    /**
     * 管理员添加客户类型字典
     *
     * @param customerType 客户类型
     * @return 操作结果
     */
    @ApiOperation("1管理员添加客户类型字典")
    @PostMapping("/addCustomerType")
    public Result addCustomerType(@RequestParam String customerType) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(customerType)) {
            return Result.message("请填写完整信息");
        }

        try {
            userService.addCustomerType(customerType);
            return Result.message("添加成功！");
        } catch (Exception e) {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员删除客户类型字典
     *
     * @param customerType 客户类型
     * @return 操作结果
     */
    @ApiOperation("2管理员删除客户类型字典")
    @DeleteMapping("/deleteCustomerType")
    public Result deleteCustomerType(@RequestParam String customerType) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(customerType)) {
            return Result.message("请填写完整信息");
        }

        try {
            userService.deleteCustomerType(customerType);
            return Result.message("删除成功！");
        } catch (Exception e) {
            return Result.message("删除失败！");
        }
    }

    @GetMapping("/getCustomerTypes")
    public List<CustomerTypeDic> getCustomerTypes() {
        return userService.getCustomerTypes(); // 调用Service层方法获取客户类型列表
    }


    /**
     * 管理员增加订单类型字典
     *
     * @param typeName 订单类型名称
     * @return 操作结果
     */
    @ApiOperation("3管理员增加订单类型字典")
    @PostMapping("/addInquiryType")
    public Result addInquiryType(@RequestParam String typeName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }


        if (StringUtils.isBlank(typeName)) {
            return Result.message("请填写完整信息");
        }
        int rowsAffected = userService.addInquiryType(typeName);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }


    /**
     * 管理员删除订单类型字典
     *
     * @param typeName 订单类型名称
     * @return 操作结果
     */
    @ApiOperation("4管理员删除订单类型字典")
    @PostMapping("/deleteInquiryType")
    public Result deleteInquiryType(@RequestParam String typeName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }


        if (StringUtils.isBlank(typeName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteInquiryType(typeName);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    @GetMapping("/selectInquiryTypeDic")
    public List<InquiryTypeDic> selectInquiryTypeDic() {
        return userService.selectInquiryTypeDic(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员增加产品类型字典
     *
     * @param itemTypeName 产品类型名称
     * @return 操作结果
     */
    @ApiOperation("5管理员增加产品类型字典")
    @PostMapping("/insertItemType")
    public Result insertItemType(@RequestParam String itemTypeName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(itemTypeName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.insertItemType(itemTypeName);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员删除产品类型字典
     *
     * @param itemTypeName 产品类型名称
     * @return 操作结果
     */
    @ApiOperation("6管理员删除产品类型字典")
    @PostMapping("/deleteItemType")
    public Result deleteItemType(@RequestParam String itemTypeName) {


        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(itemTypeName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteItemType(itemTypeName);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    @GetMapping("/selectItemTypeDic")
    public List<ItemTypeDic> selectItemTypeDic() {
        return userService.selectItemTypeDic(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员新增客户名称
     *
     * @param addCustomerName 新客户名称
     * @return 操作结果
     */
    @ApiOperation("7管理员新增客户名称")
    @PostMapping("/addCustomerName")
    public Result addCustomerName(@RequestParam String addCustomerName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(addCustomerName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.addCustomerName(addCustomerName);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }



    /**
     * 管理员删除客户名称
     *
     * @param addCustomerName 客户名称
     * @return 操作结果
     */
    @ApiOperation("8管理员删除客户名称")
    @PostMapping("/deleteCustomerName")
    public Result deleteCustomerName(@RequestParam String addCustomerName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(addCustomerName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteCustomerName(addCustomerName);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    /**
     * 管理员修改客户名称
     *
     * @param oldCustomerName 旧客户名称
     * @param newCustomerName 新客户名称
     * @return 操作结果
     */
    @ApiOperation("9管理员修改客户名称")
    @PostMapping("/updateCustomerName")
    public Result updateCustomerName(@RequestParam String oldCustomerName, String newCustomerName) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(oldCustomerName, newCustomerName)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.updateCustomerName(oldCustomerName, newCustomerName);
        if (rowsAffected > 0) {
            return Result.message("修改成功！");
        } else {
            return Result.message("修改失败！");
        }
    }

    @GetMapping("/selectFimCustomerTable")
    public List<FimCustomerTable> selectFimCustomerTable() {
        return userService.selectFimCustomerTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员添加客户类型管理
     *
     * @param customerName 客户名称
     * @param itemCode     产品编码
     * @param customerType 客户类型
     * @return 操作结果
     */
    @ApiOperation("10管理员添加客户类型管理")
    @PostMapping("/insertCustomerItem")
    public Result insertCustomerItem(@RequestParam String customerName, String itemCode, String customerType) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerName, itemCode, customerType)) {
            return Result.message("请填写完整信息");
        }

        Customer customerByName = customerService.findCustomerByName(customerName);
        Item item = itemService.findItemByCode(itemCode);
        int rowsAffected = userService.insertCustomerItem(Math.toIntExact(customerByName.getFCustId()), Math.toIntExact(item.getId()), customerType);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员删除客户类型管理
     *
     * @param customerName 客户名称
     * @param itemCode     产品编码
     * @param customerType 客户类型
     * @return 操作结果
     */
    @ApiOperation("11管理员删除客户类型管理")
    @PostMapping("/deleteCustomerItem")
    public Result deleteCustomerItem(@RequestParam String customerName, String itemCode, String customerType) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerName, itemCode, customerType)) {
            return Result.message("请填写完整信息");
        }

        Customer customerByName = customerService.findCustomerByName(customerName);
        Item item = itemService.findItemByCode(itemCode);
        int rowsAffected = userService.deleteCustomerItem(Math.toIntExact(customerByName.getFCustId()), Math.toIntExact(item.getId()), customerType);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    @GetMapping("/selectFimCustomerTypeTable")
    public List<FimCustomerTypeTable> selectFimCustomerTypeTable() {
        return userService.selectFimCustomerTypeTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员添加客户名称替换
     *
     * @param customerNameOld 旧客户名称
     * @param customerNameNew 新客户名称
     * @return 操作结果
     */
    @ApiOperation("12管理员添加客户名称替换")
    @PostMapping("/addCustomerRename")
    public Result addCustomerRename(@RequestParam String customerNameOld, String customerNameNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerNameOld, customerNameNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.addCustomerRename(customerNameOld, customerNameNew);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员根据客户旧名称删除记录
     *
     * @param customerNameOld 旧客户名称
     * @return 操作结果
     */
    @ApiOperation("13管理员根据客户旧名称删除记录")
    @PostMapping("/deleteCustomerRenameByOldName}")
    public Result deleteCustomerRenameByOldName(@RequestParam String customerNameOld) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(customerNameOld)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteCustomerRenameByOldName(customerNameOld);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    /**
     * 管理员根据客户旧名称，修改新名称
     *
     * @param customerNameOld 旧客户名称
     * @param customerNameNew 新客户名称
     * @return 操作结果
     */
    @ApiOperation("14管理员根据客户旧名称，修改新名称")
    @PutMapping("/updateCustomerRename")
    public Result updateCustomerRename(@RequestParam String customerNameOld, String customerNameNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerNameOld, customerNameNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.updateCustomerRename(customerNameOld, customerNameNew);
        if (rowsAffected > 0) {
            return Result.message("修改成功！");
        } else {
            return Result.message("修改失败！");
        }
    }
    @GetMapping("/selectFimPastCustomerRenameTable")
    public List<FimPastCustomerRenameTable> selectFimPastCustomerRenameTable() {
        return userService.selectFimPastCustomerRenameTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员添加物料替换
     *
     * @param itemCodeOld 旧物料编码
     * @param itemCodeNew 新物料编码
     * @return 操作结果
     */
    @ApiOperation("15管理员添加物料替换")
    @PostMapping("/addItemChange")
    public Result addItemChange(@RequestParam String itemCodeOld, String itemCodeNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(itemCodeOld, itemCodeNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.addItemChange(itemCodeOld, itemCodeNew);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员根据旧物料删除记录
     *
     * @param itemCodeOld 旧物料编码
     * @return 操作结果
     */
    @ApiOperation("16管理员根据旧物料删除记录")
    @PostMapping("/deleteItemChangeByOldCode")
    public Result deleteItemChangeByOldCode(@RequestParam String itemCodeOld) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(itemCodeOld)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteItemChangeByOldCode(itemCodeOld);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    /**
     * 管理员根据旧物料找到记录找修改新物料
     *
     * @param itemCodeOld 旧物料编码
     * @param itemCodeNew 新物料编码
     * @return 操作结果
     */
    @ApiOperation("17管理员根据旧物料找到记录找修改新物料")
    @PutMapping("/updateItemChange")
    public Result updateItemChange(@RequestParam String itemCodeOld, String itemCodeNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(itemCodeOld, itemCodeNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.updateItemChange(itemCodeOld, itemCodeNew);
        if (rowsAffected > 0) {
            return Result.message("修改成功！");
        } else {
            return Result.message("修改失败！");
        }
    }

    @GetMapping("/selectFimPastItemChangeTable")
    public List<FimPastItemChangeTable> selectFimPastItemChangeTable() {
        return userService.selectFimPastItemChangeTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员添加销售员替换
     *
     * @param salesmanNameOld 旧销售员名称
     * @param salesmanNameNew 新销售员名称
     * @return 操作结果
     */
    @ApiOperation("18管理员添加销售员替换")
    @PostMapping("/addSalesmanChange")
    public Result addSalesmanChange(@RequestParam String salesmanNameOld, String salesmanNameNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(salesmanNameOld)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.addSalesmanChanging(salesmanNameOld, salesmanNameNew);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员根据旧销售员删除记录
     *
     * @param salesmanNameOld 旧销售员名称
     * @return 操作结果
     */
    @ApiOperation("19管理员根据旧销售员删除记录")
    @PostMapping("/deleteSalesmanChangeByOldName")
    public Result deleteSalesmanChangeByOldName(@RequestParam String salesmanNameOld) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(salesmanNameOld)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteSalesmanChangingByOldName(salesmanNameOld);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }

    /**
     * 管理员根据旧销售员找到记录找修改新销售员
     *
     * @param salesmanNameOld 旧销售员名称
     * @param salesmanNameNew 新销售员名称
     * @return 操作结果
     */
    @ApiOperation("20管理员根据旧销售员找到记录找修改新销售员")
    @PutMapping("/updateSalesmanChange")
    public Result updateSalesmanChange(@RequestParam String salesmanNameOld, String salesmanNameNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(salesmanNameOld, salesmanNameNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.updateSalesmanChanging(salesmanNameOld, salesmanNameNew);
        if (rowsAffected > 0) {
            return Result.message("修改成功！");
        } else {
            return Result.message("修改失败！");
        }
    }

    @GetMapping("/selectFimPastSalesmanChangingTable")
    public List<FimPastSalesmanChangingTable> selectFimPastSalesmanChangingTable() {
        return userService.selectFimPastSalesmanChangingTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 管理员添加定制物料替换表
     *
     * @param customerName 客户名称
     * @param itemCodeOld  旧物料编码
     * @param itemCodeNew  新物料编码
     * @return 操作结果
     */
    @ApiOperation("21管理员添加定制物料替换表")
    @PostMapping("/addCustomizedItemChange")
    public Result addCustomizedItemChange(
            @RequestParam("customerName") String customerName,
            @RequestParam("itemCodeOld") String itemCodeOld,
            @RequestParam("itemCodeNew") String itemCodeNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerName, itemCodeOld, itemCodeNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.addCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 管理员根据名字，旧物料编码，新物料编码删除物料替换表
     *
     * @param customerName 客户名称
     * @param itemCodeOld  旧物料编码
     * @param itemCodeNew  新物料编码
     * @return 操作结果
     */
    @ApiOperation("22管理员根据名字，旧物料编码，新物料编码删除物料替换表")
    @DeleteMapping("/deleteCustomizedItemChange")
    public Result deleteCustomizedItemChange(
            @RequestParam("customerName") String customerName,
            @RequestParam("itemCodeOld") String itemCodeOld,
            @RequestParam("itemCodeNew") String itemCodeNew) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(customerName, itemCodeOld, itemCodeNew)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deleteCustomizedItemChange(customerName, itemCodeOld, itemCodeNew);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }


    @GetMapping("/selectFimPastCustomizedItemChangingTable")
    public List<FimPastCustomizedItemChangingTable> selectFimPastCustomizedItemChangingTable() {
        return userService.selectFimPastCustomizedItemChangingTable(); // 调用Service层方法获取客户类型列表
    }

    /**
     * 添加筛选物料表
     * @param itemCode    物料编码
     * @param itemName    物料名称
     * @param startMonth  开始月份
     * @return            操作结果
     */
    @ApiOperation("23添加筛选物料表")
    @PostMapping("/addPastChooseItem")
    public Result addPastChooseItem(
            @RequestParam String itemCode,
            @RequestParam String itemName,
            @RequestParam("startMonth")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startMonth) {
        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isAnyBlank(itemCode,itemName)|| startMonth == null) {
            return Result.message("请填写完整信息");
        }


        int rowsAffected = userService.insertPastChooseItem(itemCode, itemName, startMonth);
        if (rowsAffected > 0) {
            return Result.message("添加成功！");
        } else {
            return Result.message("添加失败！");
        }
    }

    /**
     * 根据物料编码删除筛选物料表
     * @param itemCode  物料编码
     * @return          操作结果
     */
    @ApiOperation("24根据物料编码删除筛选物料表")
    @DeleteMapping("/deletePastChooseItemByItemCode")
    public Result deletePastChooseItemByItemCode(@RequestParam String itemCode) {

        User currentUser = hostHolder.getUser(); // 获取当前登录用户信息

        if (!currentUser.getUserType().equals(USER_TYPE_ADMIN) && !currentUser.getUserType().equals(USER_TYPE_SYSTEM)) {
            return Result.fail().message("没有管理员权限");
        }

        if (StringUtils.isBlank(itemCode)) {
            return Result.message("请填写完整信息");
        }

        int rowsAffected = userService.deletePastChooseItemByItemCode(itemCode);
        if (rowsAffected > 0) {
            return Result.message("删除成功！");
        } else {
            return Result.message("删除失败！");
        }
    }


    @GetMapping("/selectFimPastChooseItemTable")
    public List<FimPastChooseItemTable> selectFimPastChooseItemTable() {
        return userService.selectFimPastChooseItemTable(); // 调用Service层方法获取客户类型列表
    }

}
