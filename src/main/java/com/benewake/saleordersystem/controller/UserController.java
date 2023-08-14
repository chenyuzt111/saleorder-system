package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.annotation.LoginRequired;
import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年07月07 15:29
 * 描 述： TODO
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController implements BenewakeConstants {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @ApiOperation("更新用户密码")
    @PostMapping("/updatePwd")
    @LoginRequired
    public Result updatePassword(HttpServletRequest request, Map<String,Object> param){
        String oldPassword = (String) param.get("oldPassword");
        String newPassword = (String) param.get("newPassword");
        String rePassword = (String) param.get("rePassword");
        Long userId = Long.parseLong((String)param.get("userid"));
        // 判断空值
        if(StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(rePassword)){
            return Result.fail().message("密码不能为空");
        }
        // 判断新密码两次输入是否一致
        if(!newPassword.equals(rePassword)){
            return Result.fail().message("两次输入的密码不一致");
        }
        // 判断新密码是否符合条件

        // 判断旧密码是否正确(管理员不需要)
        User user = hostHolder.getUser();
        // 普通用户
        if(user.getUserType().equals(USER_TYPE_SALESMAN) || user.getUserType().equals(USER_TYPE_VISITOR)){
            String password = CommonUtils.md5(oldPassword+user.getSalt());
            if(!password.equals(user.getPassword())){
                return Result.fail().message("旧密码错误,请输入正确的密码!");
            }
            // 修改密码
            userService.updatePassword(user.getId(),newPassword);
            // 设置ticket过期 则重新登录
            String ticket = CookieUtil.getValue(request,"ticket");
            userService.logout(ticket);
            return Result.success().message("修改成功,请重新登录!");
        }else if(user.getUserType().equals(USER_TYPE_ADMIN) || user.getUserType().equals(USER_TYPE_SYSTEM)){
            // 空值处理
            if(userId == null){
                return Result.fail().message("要修改的用户id为空");
            }
            // 管理员修改用户密码
            int res = userService.updatePassword(userId,newPassword);
            if(res != -1){
                return Result.success().message("修改成功！");
            }else{
                return Result.fail().message("用户id不存在！");
            }
        }

        return Result.fail().message("无效用户！");
    }

    /**
     * 根据用户姓名模糊匹配
     * @return
     */
    @ApiOperation("根据姓名模糊匹配用户")
    @PostMapping("/likeList")
    public Result<List<User>> getUserLikeList(@RequestBody Map<String,Object> param){
        String username = (String) param.get("username");
        Long userType = param.get("userType")==null?null:Long.parseLong((String) param.get("userType"));
        return Result.success(userService.getUsernameLikeList(username,userType));
    }

}
