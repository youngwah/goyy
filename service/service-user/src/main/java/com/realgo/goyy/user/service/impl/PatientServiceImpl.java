package com.realgo.goyy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realgo.goyy.model.user.Patient;
import com.realgo.goyy.user.mapper.PatientMapper;
import com.realgo.goyy.user.service.PatientService;

import java.util.List;

public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Override
    public List<Patient> findAll(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Patient> patients = baseMapper.selectList(queryWrapper);


        return patients;
    }
}
