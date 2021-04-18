package com.realgo.service.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.realgo.goyy.enums.DictEnum;
import com.realgo.goyy.model.hosp.Hospital;
import com.realgo.goyy.servcie.cmn.client.DictFeignClient;
import com.realgo.goyy.vo.hosp.HospitalSetQueryVo;
import com.realgo.service.hosp.repository.HospitalRepository;
import com.realgo.service.hosp.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 将Map转换成Hospital对象
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Hospital.class);
        // 在Mongodb中判断是否存在
        Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if (null != targetHospital) {
            // 修改
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            // 添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    // 分页和条件查找获取医院信息
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalSetQueryVo hospitalSetQueryVo) {
        // pageable
        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, limit, sort);

        // 转换实体
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalSetQueryVo, hospital);

        // 匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()   // 构建对象
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 模糊查询
            .withIgnoreCase(true);  // 忽略大小写

        Example<Hospital> example = Example.of(hospital, exampleMatcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        pages.getContent().stream().forEach(item ->{
            this.packHospital(item);
        });

        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital show(String id) {
        return hospitalRepository.findById(id).get();
    }

    @Override
    public Object findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosname(hosname);
    }

    @Override
    public Object item(String hoscode) {

        Map<String, Object> result = new HashMap<>();
        // 医院详情
        Hospital hospital = this.getByHoscode(hoscode);
        result.put("hospital", hospital);
        // 预约规则
        result.put("bookingRule", hospital.getBookingRule());
        return result;
    }

    // 封装数据
    private Hospital packHospital(Hospital hospital) {
        String value = hospital.getHostype();
        String hospitaltype = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), value);
        hospital.getParam().put("hostypeString", hospitaltype);
        return hospital;
    }
}
