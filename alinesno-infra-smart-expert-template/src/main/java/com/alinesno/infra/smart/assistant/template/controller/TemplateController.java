package com.alinesno.infra.smart.assistant.template.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.enums.FileTypeEnums;
import com.alinesno.infra.smart.assistant.template.dto.TemplateParamJsonRequestDto;
import com.alinesno.infra.smart.assistant.template.dto.TemplateRequestDto;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.assistant.template.enums.WordTemplateType;
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
import org.springframework.validation.annotation.Validated;
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
    public AjaxResult importData(@RequestPart("file") MultipartFile file , String type){

        String originalFilename = file.getOriginalFilename();

        if(!"image".equals(type)){
            // 判断数据库中是否有同名的模板
            boolean isExist = service.lambdaQuery()
                    .eq(TemplateEntity::getTemplateName, originalFilename)
                    .eq(TemplateEntity::getOrgId, CurrentAccountJwt.get().getOrgId())
                    .exists();
            Assert.isFalse(isExist, "模板上传失败，模板名称已存在");
        }

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        String fileType = FileTypeUtil.getType(targetFile);
        FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;

        log.debug("fileType = {} , constants = {}" , fileType , constants);
        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile) ;

        log.debug("ajaxResult= {}" , r);
        AjaxResult result =  AjaxResult.success("上传成功." , r.getData()) ;
        result.put("type" , type) ;
        if(constants != null){
            result.put("fileType" , constants.getValue()) ;
        }
        result.put("originalFilename" , file.getOriginalFilename()) ;

        return result ;
    }

    /**
     * 保存或者更新模板
     * @return
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdateTemplate")
    public AjaxResult saveOrUpdateTemplate(@RequestBody @Validated TemplateRequestDto dto){

        TemplateEntity templateEntity = new TemplateEntity();
        CopyOptions copyOptions = new CopyOptions();
        BeanUtil.copyProperties(dto , templateEntity , copyOptions) ;

        templateEntity.setTemplateName(dto.getTemplateName());
        templateEntity.setTemplateFileName(dto.getOriginalFilename());
        templateEntity.setTemplateKey(IdUtil.nanoId(8));
        templateEntity.setCallCount(0);
        templateEntity.setTemplateDataScope(dto.getTemplateDataScope());
        templateEntity.setTemplateEngine(dto.getTemplateEngine());
        templateEntity.setTemplateParams("{}");
        templateEntity.setStorageFileId(dto.getStorageFileId());
        templateEntity.setFileType(dto.getFileType());

        service.saveOrUpdate(templateEntity);

        return AjaxResult.success("保存成功.") ;
    }

    /**
     * 获取到模型类型
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getTemplateType")
    public AjaxResult getTemplateType(){
        return AjaxResult.success("获取成功" , WordTemplateType.getAllTemplateType()) ;
    }


    @DataPermissionSave
    @PostMapping("/updateTemplateParamFormat")
    public AjaxResult updateTemplateParamFormat(@RequestBody TemplateParamJsonRequestDto dto){
        service.updateTemplateParamFormat(dto);
        return AjaxResult.success("更新成功") ;
    }


    @Override
    public ITemplateService getFeign() {
        return this.service;
    }
}