package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.entity.Customer;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.service.CustomerService;
import com.benewake.saleordersystem.service.CustomerTypeService;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.benewake.saleordersystem.utils.BenewakeConstants.USER_TYPE_ADMIN;

/**
 * @author Lcs
 * @since 2023年07月12 11:15
 * 描 述： TODO
 */
@Api(tags = "客户管理接口")
@Controller
@ResponseBody
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerTypeService customerTypeService;

    @Autowired
    private HostHolder hostHolder;

    @ApiOperation("客户类型模糊匹配")
    @PostMapping("/typeList")
    public Result getCustomerTypeLikeList(@RequestBody Map<String,Object> param){
        String type = (String) param.get("customerType");
        if (type == null) {
            type = "";
        }
        return Result.success(customerService.getCustomerTypeLikeList(type));
    }

    @ApiOperation("客户名称模糊匹配")
    @PostMapping("/likeList")
    public Result<List<Customer>> getCustomerLikeList(@RequestBody Map<String,Object> param){
        String customerName = (String) param.get("customerName");
        return Result.success(customerService.getCustomerLikeList(customerName));
    }

    @ApiOperation("根据物料id和客户id获得客户类型")
    @PostMapping("/type")
    public Result getCustomerType(@RequestBody Map<String,Object> param){
        try{
            Long itemId = Long.parseLong((String) param.get("itemId"));
            Long customerId = Long.parseLong((String) param.get("customerId"));
            String type = customerTypeService.getCustomerTypeByRule(customerId,itemId);
            if(StringUtils.isEmpty(type)){
                return Result.fail().message("无匹配类型，请飞书联系管理员！");
            }
            return Result.success(customerTypeService.getCustomerTypeByRule(customerId,itemId),null);
        }catch (Exception e) {
            return Result.fail("输入格式有误！",null);
        }
    }



    @ApiOperation("添加客户")
    @PostMapping("/add")
    public Result<Object> addCustomer(@RequestBody Customer customer) {
        User user = hostHolder.getUser();  // 假设您有一个用户对象来表示当前登录用户

        // 检查客户ID是否为空或为 ""
        if (customer.getFCustId() == null) {
            return Result.fail().message("请填写客户ID！");
        }

        // 检查客户名称是否为空或为 ""
        if (StringUtils.isBlank(customer.getFName())) {
            return Result.fail().message("请填写客户名称！");
        }

        if (user != null && user.getUserType().equals(USER_TYPE_ADMIN)) {
            // 检查客户ID是否已经存在
            Customer existingCustomerById = customerService.findCustomerById(customer.getFCustId());
            if (existingCustomerById != null) {
                return Result.fail().message("客户ID已经存在！");
            }

            // 检查客户名称是否已经存在
            Customer existingCustomerByName = customerService.findCustomerByName(customer.getFName());
            if (existingCustomerByName != null) {
                return Result.fail().message("客户名称已经存在！");
            }


            // 执行添加客户的操作
            Customer createdCustomer = customerService.createCustomer(customer);
            return Result.success(createdCustomer);
        } else {
            return Result.fail().message("只有管理员用户可以使用此功能！");
        }
    }


    @ApiOperation("根据客户ID列表批量删除客户")
    @DeleteMapping("/delete")
    public Result<Object> deleteCustomersByIds(@RequestBody List<Long> ids) {
        User user = hostHolder.getUser();  // 假设您有一个用户对象来表示当前登录用户

        if (user != null && user.getUserType().equals(USER_TYPE_ADMIN)) {
            boolean deleted = customerService.deleteCustomersByIds(ids);
            if (deleted) {
                return Result.success("客户删除成功！");
            } else {
                return Result.fail().message("客户不存在或删除失败！");
            }
        } else {
            return Result.fail().message("只有管理员用户可以使用此功能！");
        }
    }

    @ApiOperation("根据客户ID列表单个删除客户")
    @DeleteMapping("/delete/{customerId}")
    public Result<Object> deleteSingleCustomer(@PathVariable Long customerId) {
        User user = hostHolder.getUser();  // 假设您有一个用户对象来表示当前登录用户

        if (user != null && user.getUserType().equals(USER_TYPE_ADMIN)) {
            // 检查是否存在该客户
            Customer existingCustomer = customerService.findCustomerById(customerId);
            if (existingCustomer == null) {
                return Result.fail().message("客户不存在！");
            }

            // 执行删除客户的操作
            boolean deleted = customerService.deleteCustomer(customerId);
            if (deleted) {
                return Result.success().message("删除成功！");
            } else {
                return Result.fail().message("删除失败！");
            }
        } else {
            return Result.fail().message("只有管理员用户可以使用此功能！");
        }

    }

    @ApiOperation("更新客户信息")
    @PutMapping("/update/{customerId}")
    public Result<Object> updateCustomer(@PathVariable Long customerId, @RequestBody Customer updatedCustomer) {
        User user = hostHolder.getUser();  // 假设您有一个用户对象来表示当前登录用户

        if (user != null && user.getUserType().equals(USER_TYPE_ADMIN)) {
            // 检查是否存在该客户
            Customer existingCustomer = customerService.findCustomerById(customerId);
            if (existingCustomer == null) {
                return Result.fail().message("客户不存在！");
            }

            // 更新客户信息
            updatedCustomer.setFCustId(customerId);  // 设置要更新的客户ID
            boolean updated = customerService.updateCustomer(updatedCustomer);
            if (updated) {
                return Result.success().message("客户信息更新成功！");
            } else {
                return Result.fail().message("客户信息更新失败！");
            }
        } else {
            return Result.fail().message("只有管理员用户可以使用此功能！");
        }
    }
}
