package com.realgo.goyy.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realgo.goyy.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> findAll(Long userId);
}
