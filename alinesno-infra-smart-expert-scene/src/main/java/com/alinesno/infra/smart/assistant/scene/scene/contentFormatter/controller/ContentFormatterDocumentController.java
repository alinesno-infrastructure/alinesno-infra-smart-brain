package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.facade.response.ResultCodeEnum;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterDocumentDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterLayoutGroupDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.DocumentSourceEnums;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterDocumentService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.DocxHtmlFormatterUtils;
import com.alinesno.infra.smart.scene.entity.ContentFormatterDocumentEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文档操作接口服务
 */
@Slf4j
@Api(tags = "文档操作接口服务")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterDocument/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterDocumentController extends BaseController<ContentFormatterDocumentEntity, IContentFormatterDocumentService> {

    @Autowired
    private IContentFormatterDocumentService service;

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
     * 创建新的空白文档
     * @return
     */
    @DataPermissionQuery
    @GetMapping("createNewDocument")
    public AjaxResult createNewDocument(@RequestParam Long sceneId , PermissionQuery query) {


        Long documentId = service.saveNewDocument(sceneId ,  query);
        return AjaxResult.success("创建成功", documentId);
    }

    /**
     * 保存文档
     * @return
     */
    @DataPermissionSave
    @PostMapping("saveDocument")
    public AjaxResult saveDocument(@Validated @RequestBody ContentFormatterDocumentDto dto) {

        ContentFormatterDocumentEntity entity = service.getById(dto.getDocumentId()) ;
        if(entity == null) {
            return AjaxResult.error("文档不存在");
        }

        entity.setDocumentName(dto.getDocumentName());
        entity.setDocumentContent(dto.getDocumentContent());
        entity.setLastSavedTime(new Date());

        // 将html转换成文本，并截取前100个字符
        entity.setDocumentDesc(DocxHtmlFormatterUtils.htmlToText(dto.getDocumentContent(), 100));

        service.updateById(entity);
        return AjaxResult.success("保存成功");
    }

    /**
     * 文件上传
     * @return
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, long sceneId , PermissionQuery query){

        log.debug("sceneId = {}" , sceneId);

        // 新生成的文件名称
        String fileName = file.getOriginalFilename() ;
        String fileSuffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        // 保存上传的文档内容
        Long documentId =  service.saveUploadDocument(fileName , targetFile , sceneId ,  query) ;

        return AjaxResult.success("创建成功", documentId);
    }

    /**
     * 图片上传并返回base64位(uploadImage)
     * @return
     */
    @SneakyThrows
    @PostMapping("uploadImage")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file) {
        // 图片上传并返回base64位

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath, newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}", newFileName, targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile);
        log.debug("文件上传成功:{}" ,  r.getData());

        // 将上传的图片转换成base64返回给前端显示
        byte[] fileContent = FileUtils.readFileToByteArray(targetFile);

        // 如果需要data URL格式（前端可以直接用在img标签的src中）
         String imageBase64 = "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(fileContent);

        return AjaxResult.success("上传成功." , imageBase64) ;
    }

    @Override
    public IContentFormatterDocumentService getFeign() {
        return this.service;
    }

}