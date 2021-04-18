package com.realgo.goyy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realgo.common.service.config.JwtHelper;
import com.realgo.goyy.enums.AuthStatusEnum;
import com.realgo.goyy.model.user.UserInfo;
import com.realgo.goyy.user.mapper.UserInfoMapper;
import com.realgo.goyy.user.service.UserInfoService;
import com.realgo.goyy.vo.user.LoginVo;
import com.realgo.goyy.vo.user.UserAuthVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl  extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Override
    public Map<String, Object> login(LoginVo loginVo) {

        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 校验参数
        if (!StringUtils.hasLength(phone) || !StringUtils.hasLength(code)) {
            return null;
        }

        // 校验验证码

        // 检查手机号是否已经注册
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (null == userInfo ) {    // 第一次登录，注册新用户
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setName("");
            userInfo.setStatus(1);
            this.save(userInfo);
        }
        // 校验是否被禁用
        if (userInfo.getStatus() == 0) {
            return null;
        }
        // 记录登录
        // 返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (!StringUtils.hasLength(name)) {
            name = userInfo.getNickName();
        }
        if (!StringUtils.hasLength(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        // JWT生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", "token");
        return map;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        // 根据用户ID查询用户
        UserInfo userInfo = baseMapper.selectById(userId);
        // 设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        // 更新信息
        baseMapper.updateById(userInfo);
    }
}
