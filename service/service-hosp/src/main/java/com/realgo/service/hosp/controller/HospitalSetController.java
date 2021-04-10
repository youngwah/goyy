package com.realgo.service.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.realgo.common.service.config.MD5;
import com.realgo.common.utils.exception.GoyyException;
import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.hosp.HospitalSet;
import com.realgo.goyy.vo.hosp.HospitalSetQueryVo;
import com.realgo.service.hosp.service.HospitalSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Api(value = "医院设置API")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    // 获取所有医院设置信息
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("/findAll")
    public Result findAllHospitalSet() {
        try {
            int a = 10/0;
        }catch (Exception e) {
            throw new GoyyException("失败!!!!!", 201);
        }

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

    // 条件查询带分页
    @ApiOperation(value = "分页查找医院设置")
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建Page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<HospitalSet>(current, limit);
        // 构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hosname);
        }
        if (StringUtils.hasLength(hoscode)) {
            wrapper.eq("hoscode", hoscode);
        }
        // 实现分页查找
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
//        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page);
        return Result.ok(hospitalSetPage);
    }

    // 添加医院设置
    @ApiOperation(value = "添加医院设置")
    @PostMapping("/addHospitalSet")
    public Result addHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态，1 使用 0不能使用
        hospitalSet.setStatus(1);
        // 设置创建时间
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        hospitalSet.setCreateTime(timestamp);
        // 签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);

        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 根据ID查询医院设置
    @GetMapping("/getHospitalSet/{id}")
    public Result getHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    // 修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 批量删除医院设置
    @DeleteMapping("/batch/Remove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        boolean flag = hospitalSetService.removeByIds(idList);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 医院设置锁定和解锁
    @ApiOperation(value = "医院设置状态变动")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {
        // 查询对应医院设置
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 更新医院是这状态
        hospitalSet.setStatus(status);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 发送签名密钥
    @PutMapping("/sendSignKey/{id}")
    public Result sendSignKey(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hosCode = hospitalSet.getHoscode();
        // TODO 发送短信
        return Result.ok();
    }

}
