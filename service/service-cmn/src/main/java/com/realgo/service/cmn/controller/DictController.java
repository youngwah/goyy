package com.realgo.service.cmn.controller;

import com.realgo.common.utils.result.Result;
import com.realgo.goyy.model.cmn.Dict;
import com.realgo.service.cmn.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Api("数据字典接口")
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    // 根据ID查找子数据列表
    @ApiOperation("根据ID查找资数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    // 导出数据
    @ApiOperation("导出excel数据")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    // 导入Excel数据
    @ApiOperation("导入Excel数据")
    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importData(file);
        return Result.ok();
    }

    // 获取数据字典名称
    @ApiOperation("获取数据字典名称")
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        return dictService.getNameByDictCodeAndValue(dictCode, value);
    }

    // 获取数据字典名称
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable String value){
        return dictService.getNameByDictCodeAndValue("", value);

    }
}
