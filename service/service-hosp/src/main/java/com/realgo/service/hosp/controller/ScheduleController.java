package com.realgo.service.hosp.controller;

import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.hosp.Schedule;
import com.realgo.goyy.vo.hosp.DepartmentVo;
import com.realgo.service.hosp.service.DepartmentService;
import com.realgo.service.hosp.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Api(tags = "医院排版")
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DepartmentService departmentService;

    //  根据医院编号和科室编号， 查询排版规则数据
    @GetMapping("/getSchedule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page,
                                  @PathVariable Long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){

        Map<String, Object> map = scheduleService.getScheduleRule(page,limit,hoscode,depcode);
        return Result.ok();
    }

    // 根据医院编号、科室编号、和工作日期，查询排班详细信息
    @ApiOperation("查询排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode, depcode, workDate);
        return Result.ok(list);
    }

    // 根据医院编号，查询医院所有科室列表
    @GetMapping("/getDepList/{hoscode}")
    public Result getDepList(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDepTree(hoscode);
        return Result.ok(list   );

    }
}
