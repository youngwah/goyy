package com.realgo.service.hosp.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realgo.goyy.model.hosp.HospitalSet;
import com.realgo.service.hosp.mapper.HospitalSetMapper;
import com.realgo.service.hosp.service.HospitalSetService;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>  implements HospitalSetService{
}
