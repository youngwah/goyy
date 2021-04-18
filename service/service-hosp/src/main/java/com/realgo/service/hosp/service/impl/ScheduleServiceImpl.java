package com.realgo.service.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.realgo.goyy.model.hosp.Schedule;
import com.realgo.goyy.vo.hosp.BookingScheduleRuleVo;
import com.realgo.goyy.vo.hosp.ScheduleQueryVo;
import com.realgo.service.hosp.repository.ScheduleRepository;
import com.realgo.service.hosp.service.DepartmentService;
import com.realgo.service.hosp.service.HospitalService;
import com.realgo.service.hosp.service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    //  根据医院编号和科室编号，查询排班规则数据
    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode) {

        // 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        // 根据工作日workDate日期分组
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),  // 匹配条件
                Aggregation.group("workDate")
                        .first("workDate").as("workDate") // 分组字段
                .count().as("docCount")   // 统计号源数量
                .sum("reservedNumber").as("reserverdNumber")
                .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.DESC, "workDate"),  // 排序
                Aggregation.skip((page-1)*limit), // 分页
                Aggregation.limit(limit)
        );
        // 调用方法
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();

        // 分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(totalAgg,Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();
        // 讲日期转为星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList
                ) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终数据
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);

        // 获取医院名称

        return result;
    }

    //  查询排班详细信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {

        // 查询Mongodb
        List<Schedule> list = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //  遍历设置医院名称 科室名称 日期对应星期
        list.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return list;
    }

    // 设置医院名称 科室名称 日期对应星期
    private void packageSchedule(Schedule item) {
        item.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(item.getWorkDate())));
    }

    // 日期转为星期
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants
                    .SUNDAY:
                dayOfWeek = "周日";
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
            default:
                return dayOfWeek;
        }
    }
}
