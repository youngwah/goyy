package com.realgo.service.hosp.service;

import com.realgo.goyy.model.hosp.Hospital;
import com.realgo.goyy.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalSetQueryVo hospitalSetQueryVo);

    void updateStatus(String id, Integer status);

    Hospital show(String id);

    Object findByHosname(String hosname);

    Object item(String hoscode);
}
