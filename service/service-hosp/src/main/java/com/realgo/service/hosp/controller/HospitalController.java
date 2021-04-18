package com.realgo.service.hosp.controller;

import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.hosp.Hospital;
import com.realgo.goyy.vo.hosp.HospitalSetQueryVo;
import com.realgo.service.hosp.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    // 分页和模糊查找医院
    @GetMapping("/list/{page}/{limit}")
    public Result hospitalPages(@PathVariable Integer page,
                                @PathVariable Integer limit,
                                HospitalSetQueryVo hospitalSetQueryVo){
        hospitalService.selectPage(page, limit, hospitalSetQueryVo);
        return Result.ok();
    }

    // 更新医院上线状态
    @GetMapping("/updateStatus/{id}/{status}")
    public Result lock(@PathVariable String id, @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return  Result.ok();
    }

    // 获取医院详情
    @GetMapping("/show/{id}")
    public Result show(@PathVariable String id) {
        return Result.ok(hospitalService.show(id));
    }
}
