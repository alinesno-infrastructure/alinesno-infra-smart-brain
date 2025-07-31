package com.alinesno.infra.smart.assistant.template.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.template.dto.TemplateParamJsonRequestDto;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.assistant.template.mapper.TemplateMapper;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.assistant.template.utils.TemplateProcessor;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TemplateServiceImpl extends IBaseServiceImpl<TemplateEntity, TemplateMapper> implements ITemplateService {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private TemplateProcessor templateProcessor ;

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

        File sourceFile = new File(tempDir, templateEntity.getStorageFileId() +"." + templateEntity.getTemplateType()) ;
        File targetFile = new File(tempDir , templateKey  +"." + templateEntity.getTemplateType()) ;
        FileUtils.writeByteArrayToFile(sourceFile , byteBody);

        log.debug("params = {}" , params);
        log.debug("sourceFile = {} , targetFile = {}" , sourceFile.getAbsoluteFile() , targetFile.getAbsoluteFile());

        generateWord(params, sourceFile.getAbsolutePath() , targetFile.getAbsolutePath()) ;

        R<String> r = storageConsumer.uploadCallbackUrl(targetFile) ;
        return r.getData() ;
    }

    /**
     * 更新模板参数格式
     * @param dto
     */
    @Override
    public void updateTemplateParamFormat(TemplateParamJsonRequestDto dto) {
       TemplateEntity templateEntity = getById(dto.getTemplateId()) ;
       templateEntity.setParamFormat(dto.getJsonStr());
       updateById(templateEntity) ;
    }

    /**
     * 生成word
     * @param dataMap
     * @param sourcePath
     * @param outputName
     */
    public static void generateWord(Map<String , Object> dataMap , String sourcePath,String outputName){
//        XWPFTemplate template = XWPFTemplate
//                .compile(sourcePath)
//                .render(dataMap);
//        FileOutputStream out;
//        try {
//            out = new FileOutputStream(outputName);
//            template.write(out);
//            out.flush();
//            out.close();
//            template.close();
//        } catch (IOException e) {
//            log.error("模板解析异常" , e);
//        }
    }

    @Override
    public List<TemplateEntity> getTemplates(PermissionQuery query) {
        LambdaQueryWrapper<TemplateEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.and(wrapper -> wrapper
                .eq(TemplateEntity::getOrgId, query.getOrgId())
                .eq(TemplateEntity::getTemplateDataScope,SceneScopeType.ORG_SCENE.getValue()))
        .or()
        .eq(TemplateEntity::getTemplateDataScope, SceneScopeType.PUBLIC_SCENE.getValue());

        return this.list(queryWrapper);
    }

    @Override
    public String genTemplate(JSONObject dataObject, String templateId) {

        TemplateEntity templateEntity = getById(templateId) ;

        String templateUrl = storageConsumer.getPreviewUrl(templateEntity.getStorageFileId()).getData() ;
        String docAbcPath = templateProcessor.processTemplateFromUrl(templateUrl, dataObject);

        log.debug("docAbcPath = {}" , docAbcPath);

        return storageConsumer.upload(new File(docAbcPath)).getData();
    }
}
