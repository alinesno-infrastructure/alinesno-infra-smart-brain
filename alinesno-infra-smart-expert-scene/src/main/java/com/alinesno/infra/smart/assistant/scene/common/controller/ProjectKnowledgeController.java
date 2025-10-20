package com.alinesno.infra.smart.assistant.scene.common.controller;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.role.utils.AttachmentReaderUtils;
import com.alinesno.infra.smart.assistant.scene.common.utils.KnowledgeUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.alinesno.infra.smart.scene.enums.KnowledgeVectorStatusEnums;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import com.alinesno.infra.smart.scene.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.scene.service.IProjectKnowledgeService;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Autowired
    private KnowledgeUtils llmUtils ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        return Optional.ofNullable(this.toPage(model, this.getFeign(), page))
                .map(tableDataInfo -> {
                    tableDataInfo.getRows().stream()
                            .filter(ProjectKnowledgeEntity.class::isInstance)
                            .map(ProjectKnowledgeEntity.class::cast)
                            .forEach(item -> item.setDocContent(null));
                    return tableDataInfo;
                })
                .orElse(null);
    }

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
//     * 文件上传（异步处理版）
//     * @return
//     */
//    @DataPermissionQuery
//    @PostMapping("/importData")
//    public DeferredResult<AjaxResult> importDataAsync(@RequestPart("file") MultipartFile file,
//                                                      @RequestParam("groupId") Long groupId,
//                                                      PermissionQuery query) {
//
//        // 创建DeferredResult，设置超时时间
//        DeferredResult<AjaxResult> deferredResult = new DeferredResult<>(600000L);
//
//        // 设置超时回调
//        deferredResult.onTimeout(() ->
//                deferredResult.setErrorResult(AjaxResult.error("文件处理超时，请稍后再试"))
//        );
//
//        // 使用线程池执行异步任务
//        CompletableFuture.runAsync(() -> {
//            try {
//                String originalFilename = file.getOriginalFilename();
//
//                // 新生成的文件名称
//                String fileSuffix = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
//                String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;
//
//                // 复制文件
//                File targetFile = new File(localPath, newFileName);
//                FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
//
//                ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity();
//                BeanUtils.copyProperties(query, entity);
//
//                entity.setDocName(originalFilename);
//                entity.setGroupId(groupId);
//
//                FileAttachmentDto attachmentDto = new FileAttachmentDto();
//                attachmentDto.setFile(targetFile);
//                attachmentDto.setFileType(fileSuffix);
//
//                String fileType = attachmentDto.getFileType();
//                entity.setFileType(fileType);
//
//                IAttachmentReaderService attachmentReaderService = attachmentReaderUtils.getAttachmentReaderService(fileType);
//                if (attachmentReaderService != null) {
//                    String fileContent = attachmentReaderService.readAttachment(attachmentDto, null);
//                    entity.setDocContent(fileContent);
//                }
//
//                ProjectKnowledgeGroupEntity group = knowledgeGroupService.getById(groupId) ;
//
//                EmbeddingOptions embeddingOptions = new EmbeddingOptions();
//                Llm llm = llmUtils.getLlm(group.getEmbeddingModelId() , embeddingOptions);
//                service.importData(entity , llm, embeddingOptions);
//
//                // 处理完成，设置结果
//                deferredResult.setResult(AjaxResult.success("知识库导入成功."));
//            } catch (Exception e) {
//                // 处理异常
//                deferredResult.setErrorResult(AjaxResult.error("文件处理失败: " + e.getMessage()));
//            }
//        }, chatThreadPool); // 使用配置的异步线程池
//
//        return deferredResult;
//    }

    /**
     * 文件上传（异步向量化，立即返回）
     */
    @DataPermissionQuery
    @PostMapping("/importData")
    public AjaxResult importDataAsync(@RequestPart("file") MultipartFile file,
                                      @RequestParam("groupId") Long groupId,
                                      PermissionQuery query) {

        try {
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("上传文件为空");
            }

            // 先判断该 group 是否已有正在导入的任务
            if (service.hasOngoingImport(groupId)) {
                return AjaxResult.error("当前分组还有文件正在导入，请等待其完成后再上传");
            }

            byte[] bytes = file.getBytes();

            // 1) 基于文件内容计算 MD5（避免仅基于文件名）
            String md5 = llmUtils.computeMd5Hex(bytes);

            // 2) 检查重复（按 groupId + md5）
            if (service.existsByMd5AndGroupId(md5, groupId)) {
                return AjaxResult.error("文件已存在，禁止重复上传");
            }

            // 3) 保存文件到磁盘
            String originalFilename = llmUtils.sanitizeFileName(file.getOriginalFilename());
            String fileSuffix = Objects.requireNonNull(originalFilename).substring(Math.max(originalFilename.lastIndexOf(".") + 1, 0));
            String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

            if (!llmUtils.isAllowedExtension(fileSuffix)) {
                return AjaxResult.error("文件类型不允许: " + fileSuffix);
            }

            File targetFile = new File(localPath, newFileName);
            FileUtils.writeByteArrayToFile(targetFile, bytes);

            // 4) 读取文件内容（使用 attachment reader 将文件转换为文本）
            FileAttachmentDto attachmentDto = new FileAttachmentDto();
            attachmentDto.setFile(targetFile);
            attachmentDto.setFileType(fileSuffix);

            IAttachmentReaderService attachmentReaderService = attachmentReaderUtils.getAttachmentReaderService(fileSuffix);
            String fileContent = null;
            if (attachmentReaderService != null) {
                fileContent = attachmentReaderService.readAttachment(attachmentDto, null);
            } else {
                // 如果没有特定解析器，尝试按文本读取（只适用于文本文件）
                try {
                    fileContent = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    log.warn("无法解析文件为文本，需添加对应的 AttachmentReaderService: {}", ex.getMessage());
                    fileContent = "";
                }
            }

            // 5) 构建并保存实体（元数据），设置 vectorStatus = 0（处理中）
            ProjectKnowledgeEntity entity = new ProjectKnowledgeEntity();
            BeanUtils.copyProperties(query, entity);

            entity.setDocName(originalFilename);
            entity.setGroupId(groupId);
            entity.setFileType(fileSuffix);
            entity.setDocMd5(md5);
            entity.setDocContent(fileContent); // 将内容暂存，service.importData 会使用
            entity.setVectorStatus(KnowledgeVectorStatusEnums.IMPORTING.getCode()); // 0 - 处理中
            entity.setChunkCount(0);

            service.save(entity); // 保存元数据并获取 id

            Long savedId = entity.getId();

            // 6) 后台异步执行向量化并更新状态（立即返回给前端）
            CompletableFuture.runAsync(() -> {
                try {
                    ProjectKnowledgeGroupEntity group = knowledgeGroupService.getById(groupId);
                    EmbeddingOptions embeddingOptions = new EmbeddingOptions();
                    Llm llm = llmUtils.getLlm(group.getEmbeddingModelId(), embeddingOptions);

                    // 从 DB 获取最新实体（以防事务/脏数据）
                    ProjectKnowledgeEntity saved = service.getById(savedId);
                    service.importData(saved, llm, embeddingOptions); // 执行向量化、存储并更新状态为成功
                } catch (Exception e) {
                    log.error("后台向量化失败, id={}", savedId, e);
                    // 更新为失败状态
                    try {
                        ProjectKnowledgeEntity errEntity = new ProjectKnowledgeEntity();
                        errEntity.setId(savedId);
                        errEntity.setVectorStatus(KnowledgeVectorStatusEnums.IMPORT_FAIL.getCode()); // 2 - 失败
                        service.updateById(errEntity);
                    } catch (Exception ex) {
                        log.error("更新失败状态失败, id={}", savedId, ex);
                    }
                }
            }, chatThreadPool);

            // 7) 立即返回
            return AjaxResult.success("文件上传成功，正在后台处理向量化。");

        } catch (Exception e) {
            log.error("上传处理失败", e);
            return AjaxResult.error("文件处理失败: " + e.getMessage());
        }
    }

    // 重命名
    @PostMapping("/renameKnowledge")
    public AjaxResult renameKnowledge(@RequestParam("id") Long id, @RequestParam("newName") String newName) {
        try {
            if (id == null || newName == null || newName.trim().isEmpty()) {
                return AjaxResult.error("参数错误");
            }
            service.renameKnowledge(id, newName.trim());
            return AjaxResult.success("重命名成功");
        } catch (Exception e) {
            log.error("重命名失败 id={}", id, e);
            return AjaxResult.error("重命名失败: " + e.getMessage());
        }
    }

    // 删除（也会删除向量）
    @DeleteMapping("/deleteKnowledge")
    public AjaxResult deleteKnowledge(@RequestParam("id") Long id) {
        try {
            if (id == null) {
                return AjaxResult.error("参数错误");
            }
            service.deleteKnowledgeById(id);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败 id={}", id, e);
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
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