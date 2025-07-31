package com.alinesno.infra.smart.assistant.scene.common.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.role.utils.AttachmentReaderUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

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


//    /**
//     * 文件上传
//     * @return
//     */
//    @DataPermissionQuery
//    @SneakyThrows
//    @PostMapping("/importData")
//    public AjaxResult importData(@RequestPart("file") MultipartFile file,
//                                 @RequestParam("groupId") Long groupId ,
//                                 PermissionQuery query) {
//
//        String originalFilename = file.getOriginalFilename();
//
//        // 新生成的文件名称
//        String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
//        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;
//
//        // 复制文件
//        File targetFile = new File(localPath, newFileName);
//        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
//
//        ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity() ;
//        BeanUtils.copyProperties(query ,  entity);
//
//        entity.setDocName(originalFilename);
//        entity.setGroupId(groupId);
//
//        FileAttachmentDto attachmentDto = new FileAttachmentDto();
//        attachmentDto.setFile(targetFile);
//        attachmentDto.setFileType(fileSuffix);
//
//        String fileType = attachmentDto.getFileType() ;
//
//        entity.setFileType(fileType);
//
//        IAttachmentReaderService attachmentReaderService = attachmentReaderUtils.getAttachmentReaderService(fileType) ;
//        if(attachmentReaderService != null) {
//            String fileContent = attachmentReaderService.readAttachment(attachmentDto, null);
//            entity.setDocContent(fileContent);
//        }
//
//        service.importData(entity);
//
//        return AjaxResult.success("审核规则导入成功");
//    }


    /**
     * 文件上传（异步处理版）
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/importData")
    public DeferredResult<AjaxResult> importDataAsync(@RequestPart("file") MultipartFile file,
                                                      @RequestParam("groupId") Long groupId,
                                                      PermissionQuery query) {

        // 创建DeferredResult，设置超时时间
        DeferredResult<AjaxResult> deferredResult = new DeferredResult<>(600000L);

        // 设置超时回调
        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(AjaxResult.error("文件处理超时，请稍后再试"))
        );

        // 使用线程池执行异步任务
        CompletableFuture.runAsync(() -> {
            try {
                String originalFilename = file.getOriginalFilename();

                // 新生成的文件名称
                String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
                String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

                // 复制文件
                File targetFile = new File(localPath, newFileName);
                FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

                ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity();
                BeanUtils.copyProperties(query, entity);

                entity.setDocName(originalFilename);
                entity.setGroupId(groupId);

                FileAttachmentDto attachmentDto = new FileAttachmentDto();
                attachmentDto.setFile(targetFile);
                attachmentDto.setFileType(fileSuffix);

                String fileType = attachmentDto.getFileType();
                entity.setFileType(fileType);

                IAttachmentReaderService attachmentReaderService = attachmentReaderUtils.getAttachmentReaderService(fileType);
                if (attachmentReaderService != null) {
                    String fileContent = attachmentReaderService.readAttachment(attachmentDto, null);
                    entity.setDocContent(fileContent);
                }

                service.importData(entity);

                // 处理完成，设置结果
                deferredResult.setResult(AjaxResult.success("审核规则导入成功"));
            } catch (Exception e) {
                // 处理异常
                deferredResult.setErrorResult(AjaxResult.error("文件处理失败: " + e.getMessage()));
            }
        }, chatThreadPool); // 使用配置的异步线程池

        return deferredResult;
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