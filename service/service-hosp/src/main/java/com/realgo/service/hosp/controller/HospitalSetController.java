package com.realgo.service.hosp.controller;

import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.hosp.HospitalSet;
import com.realgo.service.hosp.service.HospitalSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "医院设置API")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    // 获取所有医院设置信息
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("/findAll")
    public Result findAllHospitalSet() {

        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list) ;
    }

    // 删除医院设置
    @ApiOperation(value = "逻辑删除指定的医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@ApiParam(value = "指定删除的医院设置ID", required = true) @PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
}
