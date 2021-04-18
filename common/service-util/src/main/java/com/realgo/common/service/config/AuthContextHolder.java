package com.realgo.common.service.config;

import javax.servlet.http.HttpServletRequest;

public class AuthContextHolder {

    // 获取当前用户ID
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    // 获取当前用户名称

    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
