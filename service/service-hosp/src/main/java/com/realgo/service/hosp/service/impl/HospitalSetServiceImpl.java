package com.realgo.service.hosp.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realgo.common.utils.result.Result;
import com.realgo.common.utils.result.ResultCodeEnum;
import com.realgo.goyy.model.hosp.HospitalSet;
import com.realgo.service.hosp.mapper.HospitalSetMapper;
import com.realgo.service.hosp.service.HospitalSetService;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>  implements HospitalSetService{
    @Override
    public String getSignKey(String hoscode) {

        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if(null == hospitalSet) {
            // 医院未开通
           return  "not open";
        }
        if(hospitalSet.getStatus().intValue() == 0) {
            return "hospital lock";
        }
        return hospitalSet.getSignKey();
    }

    private HospitalSet getByHoscode(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper();
        wrapper.eq("hoscode", hoscode);
        return baseMapper.selectOne(wrapper);
    }
}
