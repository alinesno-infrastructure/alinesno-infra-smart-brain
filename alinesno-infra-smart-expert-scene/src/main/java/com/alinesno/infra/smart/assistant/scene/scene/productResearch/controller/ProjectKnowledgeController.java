package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.role.utils.AttachmentReaderUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/projectKnowledge")
public class ProjectKnowledgeController extends BaseController<ProjectKnowledgeEntity, IProjectKnowledgeService> {

    @Autowired
    private IProjectKnowledgeService service;

    @Autowired
    private AttachmentReaderUtils attachmentReaderUtils ;

    @Autowired
    private IProjectKnowledgeGroupService knowledgeGroupService;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 新增加规则
     * @return
     */
    @DataPermissionScope
    @PostMapping("/saveOrUpdateRule")
    public AjaxResult addRule(@RequestBody @Validated DocReviewRulesDto dto) {

        Assert.isTrue(RiskLevelEnums.contains(dto.getRiskLevel()) , "风险级别错误");
        Assert.isTrue(ReviewPositionEnums.contains(dto.getReviewPosition()) , "审核立场错误");

        log.debug("新增规则：{}", ToStringBuilder.reflectionToString(dto));

        ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity() ;
        BeanUtils.copyProperties(dto , entity) ;

        service.saveOrUpdate(entity);

        return ok() ;
    }


    /**
     * 文件上传
     * @return
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file,
                                 @RequestParam("groupId") Long groupId ,
                                 @RequestParam("sceneId") Long sceneId ,
                                 PermissionQuery query) {

        String originalFilename = file.getOriginalFilename();

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath, newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity() ;
        BeanUtils.copyProperties(query ,  entity);

        entity.setDocName(originalFilename);
        entity.setGroupId(groupId);
        entity.setSceneId(sceneId);

        FileAttachmentDto attachmentDto = new FileAttachmentDto();
        attachmentDto.setFile(targetFile);
        attachmentDto.setFileType(fileSuffix);

        String fileType = attachmentDto.getFileType() ;

        entity.setFileType(fileType);

        IAttachmentReaderService attachmentReaderService = attachmentReaderUtils.getAttachmentReaderService(fileType) ;
        if(attachmentReaderService != null) {
            String fileContent = attachmentReaderService.readAttachment(attachmentDto, null);
            entity.setDocContent(fileContent);
        }

        service.importData(entity);

        return AjaxResult.success("审核规则导入成功");
    }

    /**
     * 通过ids查询出规则列表
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listRuleByIds")
    public AjaxResult listRuleByIds(@RequestParam String idsStr) {

        // 使用 Stream API 分割字符串并转换为 Long 类型
        List<Long> idsList = Arrays.stream(idsStr.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return AjaxResult.success(service.listByIds(idsList)) ;
    }

    @Override
    public IProjectKnowledgeService getFeign() {
        return this.service;
    }
}    