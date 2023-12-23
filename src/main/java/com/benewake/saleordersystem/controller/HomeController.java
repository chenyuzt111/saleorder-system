package com.benewake.saleordersystem.controller;

import com.benewake.saleordersystem.entity.User;
import com.benewake.saleordersystem.service.UserService;
import com.benewake.saleordersystem.utils.BenewakeConstants;
import com.benewake.saleordersystem.utils.HostHolder;
import com.benewake.saleordersystem.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年06月30 11:49
 * 描 述： TODO
 */
@Api(tags = "主页相关接口")
@RestController
@ResponseBody
public class HomeController implements BenewakeConstants {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 用户登录
     *
     * @param response
     * @return 登录信息
     */
    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletResponse response) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (hostHolder.getUser() != null) {
            // 当前已存在登录用户
            Map<String, Object> map = new HashMap<>(1);
            return Result.fail(202, "当前已有账号登录，请先退出当前账号！", null);
        }

        // 尝试登录
        Map<String, Object> map = userService.login(username, password);
        if (map.containsKey("ticket")) {
            //验证成功 设置Cookie并返回成功信息
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge((int) DEFAULT_EXPIRED_SECONDS);
            response.addCookie(cookie);
            return Result.success(200, "success", map);
        } else {
            // 验证失败 返回失败信息
            return Result.fail(400, (String) map.get("error"), null);
        }
    }

    /**
     * 用户登出
     *
     * @param ticket
     * @return
     */
    @ApiOperation("用户登出接口")
    @GetMapping("/logout")
    public Result<Map<String, Object>> logout(@CookieValue("ticket") String ticket) {
        return Result.success(200, (String) userService.logout(ticket).get("ticketMessage"), null);
    }


    @GetMapping("/callback")
    public Result handleCallback(@RequestParam("code") String authorizationCode,HttpServletResponse response) throws IOException {
        // 在这里处理授权码，可以存储或使用它来获取访问令牌
        System.out.println("Authorization Code: " + authorizationCode);
        String appAccessToken = userService.getAppAccessToken();
        // 这里可以跳转到其他页面或返回响应
        String userAccessToken = userService.getUserAccessToken(authorizationCode,appAccessToken);
        String username =userService.userInfo(userAccessToken);

        Map<String, Object> map = userService.feishulogin(username);
        if (map.containsKey("ticket")) {
            //验证成功 设置Cookie并返回成功信息
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge((int) DEFAULT_EXPIRED_SECONDS);
            response.addCookie(cookie);
            return Result.success(200,"success",map);
        }else{
            // 验证失败 返回失败信息
            return Result.fail(400, (String) map.get("error"),null);
        }
    }

}
