package com.realgo.service.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realgo.goyy.model.cmn.Dict;
import org.ehcache.spi.service.ServiceDependencies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);
}
