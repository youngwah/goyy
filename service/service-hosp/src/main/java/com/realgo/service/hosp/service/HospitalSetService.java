package com.realgo.service.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realgo.goyy.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
