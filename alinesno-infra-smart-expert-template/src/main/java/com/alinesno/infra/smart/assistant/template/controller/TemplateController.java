package com.alinesno.infra.smart.assistant.template.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.assistant.enums.FileTypeEnums;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/template")
public class TemplateController extends BaseController<TemplateEntity, ITemplateService> {

    @Autowired
    private ITemplateService service;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 测试模板
     */
    @PostMapping("/testTemplate")
    public AjaxResult testTemplate(String templateKey , @RequestBody JSONObject dataJson){

        if (dataJson == null || dataJson.toJSONString().trim().isEmpty()) {
            throw new IllegalArgumentException("Input JSON string is null or empty");
        }

        Map<String, Object> params = JSONObject.parseObject(dataJson.toString(), Map.class);

        return AjaxResult.success("解析成功" , service.parseTemplate(templateKey , params)) ;
    }

    /**
     * 文件上传
     * @return
     */
    @DataPermissionSave
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, TemplateEntity templateEntity){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        String fileType = FileTypeUtil.getType(targetFile);
        FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;
        assert constants != null;

        log.debug("fileType = {} , constants = {}" , fileType , constants);

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile , "qiniu-kodo" , progress -> {
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        }) ;

        // 保存模板信息到数据库
        templateEntity.setTemplateName(file.getOriginalFilename());
        templateEntity.setTemplateDesc("模板描述");
        templateEntity.setTemplateKey(IdUtil.nanoId(8));
        templateEntity.setCallCount(0);
        templateEntity.setTemplateParams("{}");
        templateEntity.setStorageFileId(r.getData());
        templateEntity.setTemplateType(constants.getValue());

        service.save(templateEntity);

        log.debug("ajaxResult= {}" , r);
        return AjaxResult.success("上传成功." , r.getData()) ;

    }


    @Override
    public ITemplateService getFeign() {
        return this.service;
    }
}