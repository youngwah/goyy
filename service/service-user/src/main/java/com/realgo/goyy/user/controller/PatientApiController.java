package com.realgo.goyy.user.controller;

import com.realgo.common.service.config.AuthContextHolder;
import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.user.Patient;
import com.realgo.goyy.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    // 获取就诊人列表
    @GetMapping("/auth/findAll")
    public Result findAll(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAll(userId);
        return Result.ok(list);
    }
}
