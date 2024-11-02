package com.alinesno.infra.smart.assistant.template.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.assistant.template.mapper.TemplateMapper;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.assistant.template.utils.WordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
public class TemplateServiceImpl extends IBaseServiceImpl<TemplateEntity, TemplateMapper> implements ITemplateService {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @SneakyThrows
    @Override
    public String parseTemplate(String templateKey, Map<String, Object> params) {

        String tempDir = System.getProperty("java.io.tmpdir");

        // 通过key查询模板信息
        log.info("templateKey = {}" , templateKey);
        LambdaQueryWrapper<TemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateEntity::getTemplateKey, templateKey);
        TemplateEntity templateEntity = getOne(queryWrapper) ;

        Assert.notNull(templateEntity,"模板不存在") ;

        // 解析出模板内容并返回本地路径
        // 下载模板
        byte[] byteBody = storageConsumer.download(templateEntity.getStorageFileId() , progress -> {
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");
        });

        File sourceFile = new File(tempDir + File.separator + "templates" , templateEntity.getStorageFileId() +"." + templateEntity.getTemplateType()) ;
        File targetFile = new File(tempDir , templateKey +".docx") ;
        FileUtils.writeByteArrayToFile(sourceFile , byteBody);

        log.debug("sourceFile = {} , targetFile = {}" , sourceFile.getAbsoluteFile() , targetFile.getAbsoluteFile());
        WordUtil.createDoc(params, sourceFile.getName() , targetFile.getAbsolutePath()) ;

        R<String> r = storageConsumer.uploadCallbackUrl(targetFile , "qiniu-kodo-pub" , progress -> {
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");
        }) ;

        return r.getData() ;
    }
}
