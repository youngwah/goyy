package com.realgo.goyy.servcie.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
public interface DictFeignClient {

    // 获取数据字典名称
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value);

    // 获取数据字典名称
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable("value") String value);
}
