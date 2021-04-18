package com.realgo.goyy.user.controller;

import com.realgo.common.service.config.AuthContextHolder;
import com.realgo.common.utils.result.Result;
import com.realgo.goyy.user.config.IpUtil;
import com.realgo.goyy.user.service.UserInfoService;
import com.realgo.goyy.vo.user.LoginVo;
import com.realgo.goyy.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    // 登录
    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {

        loginVo.setIp(IpUtil.getIpAddr(request));
        Map<String, Object> info = userInfoService.login(loginVo);
        return Result.ok();
    }

    // 用户认证接口
    @PostMapping("/auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        // 传递两个参数，第一个用户ID，第二个认证数据VO对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();
    }
}
