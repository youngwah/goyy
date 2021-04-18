package com.realgo.service.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.realgo.goyy.model.hosp.Department;
import com.realgo.goyy.vo.hosp.DepartmentQueryVo;
import com.realgo.goyy.vo.hosp.DepartmentVo;
import com.realgo.service.hosp.repository.DepartmentRepository;
import com.realgo.service.hosp.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        // 将Map转换为Department实体类对象
        String departmentString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(departmentString, Department.class);
        // 存入Mongodb数据库
        Department targetDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (null != targetDepartment) {
            // 更新
            departmentRepository.save(targetDepartment);
        } else {
            // 新增
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0 为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建对象
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
            .withIgnoreCase(true); // 改变默认大小写忽略方式：忽略大小写
        // 创建实例
        Example<Department> example = Example.of(department, matcher);
        Page<Department> pages = departmentRepository.findAll(example, pageable);
        return pages;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (null != department) {
            departmentRepository.delete(department);
        }
    }

    // 根据医院编号查询所有科室
    @Override
    public List<DepartmentVo> findDepTree(String hoscode) {

        List<DepartmentVo>  resultList = new ArrayList<>();

        // 根据医院编号，查询所有科室
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        List<Department> departmentList = departmentRepository.findAll(example);

        // 获取每个大科室下所有子科室，按照大科室编号分组
        Map<String, List<Department>> department = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        // 遍历map集合
        for (Map.Entry<String, List<Department>> entry:department.entrySet()
             ) {
            String bigcode = entry.getKey();
            List<Department> departmentList1 = entry.getValue();
            // 封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(departmentList1.get(0).getBigname());

            // 封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department dep:departmentList1
                 ) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(dep.getDepcode());
                departmentVo1.setDepname(dep.getDepname());
                children.add(departmentVo1);
            }

            // 把小科室放到大科室里
            departmentVo.setChildren(children);
            resultList.add(departmentVo);

        }
        return resultList;
    }
}