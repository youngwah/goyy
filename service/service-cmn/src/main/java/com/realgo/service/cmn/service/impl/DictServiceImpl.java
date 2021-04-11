package com.realgo.service.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realgo.goyy.model.cmn.Dict;
import com.realgo.goyy.vo.cmn.DictEeVo;
import com.realgo.service.cmn.listener.DictListener;
import com.realgo.service.cmn.mapper.DictMapper;
import com.realgo.service.cmn.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl  extends ServiceImpl<DictMapper, Dict> implements DictService{

    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        // 根据ID查出所有子数据
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> list = baseMapper.selectList(wrapper);
        // dict对象设置hasChild
        for (Dict dict: list) {
            Long dictId = dict.getId();
            boolean hasChilren = hasChildren(dictId);
            dict.setHasChildren(hasChilren);
        }
        return list;
    }

    // 导出Excel数据
    @Override
    public void exportData(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 使用URLEncoder防止中文乱码
        String fileName = null;
        try {
            fileName = URLEncoder.encode("数据字典", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");

        List<Dict> list = baseMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<>();
        for (Dict dict : list) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo, DictEeVo.class);
            dictEeVoList.add(dictEeVo);
        }
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 判断dict对象是否有子对象
    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count >0;
    }
}
