package com.realgo.service.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.realgo.goyy.model.hosp.Schedule;
import com.realgo.goyy.vo.hosp.ScheduleQueryVo;
import com.realgo.service.hosp.repository.ScheduleRepository;
import com.realgo.service.hosp.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 将Map转换为Schedule实体类对象
       Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
       // 查询数据库判断是新增还是修改
       Schedule targetSchedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
       if (targetSchedule != null) {
           // 修改
           scheduleRepository.save(schedule);
       } else {
           // 新增
           schedule.setCreateTime(new Date());
           schedule.setUpdateTime(new Date());
           schedule.setIsDeleted(0);
           scheduleRepository.save(schedule);
       }
    }

    @Override
    public Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 分页
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        // 模糊查询
        // 构建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建对象
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)  // 改变默认字符串匹配方式：模糊查询
            .withIgnoreCase(true); // 改变默认大小写忽略方式：忽略大小写
        // 创建实例
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
        return pages;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.delete(schedule);
        }
    }
}
