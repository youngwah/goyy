package com.realgo.service.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.realgo.goyy.model.cmn.Dict;
import com.realgo.goyy.vo.cmn.DictEeVo;
import com.realgo.service.cmn.mapper.DictMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;

import java.sql.Timestamp;
import java.util.Date;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    //  一行一行读
    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        // 调用数据库添加方法
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        dict.setCreateTime(timestamp);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
