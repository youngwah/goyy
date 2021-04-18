package com.realgo.goyy.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realgo.goyy.model.user.UserInfo;
import com.realgo.goyy.vo.user.LoginVo;
import com.realgo.goyy.vo.user.UserAuthVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> login(LoginVo loginVo);

    void userAuth(Long userId, UserAuthVo userAuthVo);
}
