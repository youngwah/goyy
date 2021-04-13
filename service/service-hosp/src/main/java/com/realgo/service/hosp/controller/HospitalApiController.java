package com.realgo.service.hosp.controller;

import com.realgo.common.service.config.HttpRequestHelper;
import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.hosp.Department;
import com.realgo.goyy.model.hosp.Hospital;
import com.realgo.goyy.model.hosp.Schedule;
import com.realgo.goyy.vo.hosp.DepartmentQueryVo;
import com.realgo.goyy.vo.hosp.DepartmentVo;
import com.realgo.goyy.vo.hosp.ScheduleQueryVo;
import com.realgo.service.hosp.service.DepartmentService;
import com.realgo.service.hosp.service.HospitalService;
import com.realgo.service.hosp.service.HospitalSetService;
import com.realgo.service.hosp.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;



    @PostMapping("/hospital/show")
    public Result shoHospital(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        // 调用方法获取Hospital信息
        Hospital hospital = hospitalService.getByHoscode((String)paramMap.get("hoscode"));
        return Result.ok(hospital);
    }

    // 上传医院
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        // 必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail();
        }

        // logodata转换
        String logoDataString  = (String)paramMap.get("logoData");
        if(StringUtils.hasLength(logoDataString)) {
            String loggoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", loggoData);
        }

        // 签名校验
        String oldSign = (String)hospitalSetService.getSignKey(hoscode);
        if (!HttpRequestHelper.isSignEquals(paramMap, oldSign)) {
            // 签名校验失败
            return Result.fail();
        }

        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation("上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        // 参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail();
        }
        // 签名校验
        String oldSign = hospitalSetService.getSignKey(hoscode);
        if (!HttpRequestHelper.isSignEquals(paramMap, oldSign)) {
            return Result.fail();
        }

        departmentService.save(paramMap);
        return Result.ok();
    }

    // 查询科室
    @PostMapping("/department/list")
    public Result showDepartment(HttpServletRequest request) {
        // Map转换
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        // 必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail().message("hoscode is empty");
        }
        String depcode = (String)paramMap.get("depcode");
        int page =  StringUtils.hasLength((String)paramMap.get("page"))?Integer.parseInt((String)paramMap.get("page")):1;
        int limit = StringUtils.hasLength((String)paramMap.get("limit"))?Integer.parseInt((String)paramMap.get("limit")):10;
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            return Result.fail().message("Sign not match");
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        if (pageModel != null) {
            return Result.ok(pageModel);
        } else {
            return  Result.ok().message("查无数据");
        }
    }

    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        // Map 转换
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        // 必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        String depcode = (String)paramMap.get("depcode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail().message("查无此数据");
        }
        if (!HttpRequestHelper.isSignEquals(paramMap, (String)paramMap.get("sign"))) {
            return Result.fail().message("签名不匹配");
        }
        departmentService.remove(hoscode, depcode);

        return Result.ok();
    }

    @ApiOperation("添加排班")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        // map转换
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        // 参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail().message("hoscode not data");
        }
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            return Result.fail().message("Sign not match");
        }
        scheduleService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation("查询排班")
    @PostMapping("/schedule/list")
    public Result showSchedule(HttpServletRequest request) {
        // Map转换
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);

        // 校验
        String hoscode = (String)paramMap.get("hoscode");
        int page = StringUtils.hasLength((String)paramMap.get("page"))?Integer.parseInt((String)paramMap.get("page")):1;
        int limit = StringUtils.hasLength((String)paramMap.get("limit"))?Integer.parseInt((String)paramMap.get("limit")):10;
        String depcode = (String)paramMap.get("depcode");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail().message("hoscode not data");
        }
        if(!HttpRequestHelper.isSignEquals(paramMap, (String)hospitalSetService.getSignKey(hoscode))) {
            return Result.fail().message("sign not match");
        }
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setDepcode(depcode);
        scheduleQueryVo.setHoscode(hoscode);
        Page<Schedule> pageModel = scheduleService.selectPage(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
//        paramMap.put("hoscode",this.getHoscode());
//        paramMap.put("hosScheduleId",hosScheduleId);
//        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        paramMap.put("sign", HttpRequestHelper.getSign(paramMap, this.getSignKey()));
        // 校验
        String hoscode = (String)paramMap.get("hoscode");
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        String sign = (String)paramMap.get("sign");
        if (!StringUtils.hasLength(hoscode)) {
            return Result.fail().message("hoscode not data");
        }
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(sign))) {
            return Result.fail().message("sign not match");
        }
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}
