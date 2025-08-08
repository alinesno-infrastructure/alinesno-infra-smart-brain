package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.*;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums.ReviewRuleGenStatusEnums;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 审核清单生成服务
 */
@Slf4j
@Service
public class DocReviewListGeneratorService {

    @Resource
    private IDocReviewSceneService docReviewSceneService;

    @Resource
    private IDocReviewTaskService docReviewTaskService;

    @Resource
    private IIndustryRoleService roleService;

    @Resource
    private AnalysisTool analysisTool;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    @Qualifier("chatThreadPool")
    private ThreadPoolTaskExecutor chatThreadPool;

    /**
     * 启动审核清单生成任务
     *
     * @param dto   生成参数
     * @param query 权限查询
     */
    public void startGenerateReviewList(DocReviewGenReviewDto dto, PermissionQuery query) {
        String taskId = IdUtil.fastSimpleUUID();

        // 使用线程池执行生成任务
        chatThreadPool.execute(() -> {
            asyncGenerateReviewList(taskId, dto, query);
        });

    }

    /**
     * 异步生成审核清单
     */
    public void asyncGenerateReviewList(String taskId, DocReviewGenReviewDto dto, PermissionQuery query) {
        long sceneId = dto.getSceneId();

        try {
            DocReviewSceneEntity sceneEntity = docReviewSceneService.getBySceneId(sceneId, query);
            DocReviewTaskEntity entity = docReviewTaskService.getById(dto.getTaskId());

            // 更新任务状态
            updateTaskStatus(entity, ReviewRuleGenStatusEnums.GENERATING, dto);
            docReviewTaskService.update(entity);

            if (dto.getReviewListOption().equals("aigen")) {
                generateByAI(sceneEntity, entity, dto);
            } else {
                // 非AI生成逻辑
                entity.setReviewGenStatus(ReviewRuleGenStatusEnums.SUCCESS.getCode());
                docReviewTaskService.updateById(entity);
            }
        } catch (Exception e) {
            log.error("生成审核清单异常", e);
            handleGenerationError(dto.getTaskId(), e.getMessage());
        }
    }

    /**
     * 使用AI生成文档的元数据
     */
    private void generateByDocumentMeta(DocReviewSceneEntity sceneEntity, DocReviewTaskEntity entity, DocReviewGenReviewDto dto) {
        final MessageTaskInfo taskInfo = prepareTaskInfo(sceneEntity, entity, dto);

        String documentInfo = String.format(
                DocReviewPrompt.documentContentAnalysis ,
                entity.getDocumentName()
        );

        taskInfo.setText(documentInfo);

        // 异步调用角色服务
        CompletableFuture<WorkflowExecutionDto> genContentFuture = roleService.runRoleAgent(taskInfo);
        genContentFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("生成审核清单失败", ex);
                handleGenerationError(dto.getTaskId(), "生成审核清单失败: " + ex.getMessage());
                return;
            }

            try {
                result.setGenContent(taskInfo.getFullContent());
                result.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));

                processGeneratedMeta(result, entity);
            } catch (Exception e) {
                log.error("解析审核规则失败, taskId: {}, content: {}", dto.getTaskId(),  taskInfo.getFullContent(), e);
                handleGenerationError(dto.getTaskId(), "生成审核规则格式不正确，请点击重新生成.");
            }
        });
    }

    private void processGeneratedMeta(WorkflowExecutionDto result, DocReviewTaskEntity entity) {

        if (result.getCodeContent() != null && !result.getCodeContent().isEmpty()) {
            String codeContent = result.getCodeContent().get(0).getContent();

            // 验证JSON格式
            List<Map<String, String>> contentMeta = JSON.parseObject(codeContent, new TypeReference<List<Map<String, String>>>() {});
            log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(contentMeta));

            entity.setContractMetadata(JSONUtil.toJsonStr(contentMeta));
            entity.setDocumentParseStatus(ReviewRuleGenStatusEnums.SUCCESS.getCode());
        } else {
            entity.setDocumentParseStatus(ReviewRuleGenStatusEnums.FAILED_NO_CONTENT.getCode());
        }

        docReviewTaskService.updateById(entity);

    }

    /**
     * 使用AI生成审核清单
     */
    private void generateByAI(DocReviewSceneEntity sceneEntity, DocReviewTaskEntity entity, DocReviewGenReviewDto dto) {
        final MessageTaskInfo taskInfo = prepareTaskInfo(sceneEntity, entity, dto);

        ContractTypeEnum contractType = ContractTypeEnum.getByScenarioId(entity.getContractType());
        if (contractType == null) {
            throw new RpcServiceRuntimeException("合同类型不正确，请点击重新生成.");
        }

        String documentInfo = String.format(
                DocReviewPrompt.formatContent ,
                entity.getDocumentName(),
                contractType.getTitle() + ":" + contractType.getDesc()
        );

        taskInfo.setText(documentInfo);

        // 异步调用角色服务
        CompletableFuture<WorkflowExecutionDto> genContentFuture = roleService.runRoleAgent(taskInfo);
        genContentFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("生成审核清单失败", ex);
                handleGenerationError(dto.getTaskId(), "生成审核清单失败: " + ex.getMessage());
                return;
            }

            try {
                result.setGenContent(taskInfo.getFullContent());
                result.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));

                processGeneratedContent(result, entity);
            } catch (Exception e) {
                log.error("解析审核规则失败, taskId: {}, content: {}", dto.getTaskId(),  taskInfo.getFullContent(), e);
                handleGenerationError(dto.getTaskId(), "生成审核规则格式不正确，请点击重新生成.");
            }
        });
    }

    /**
     * 准备任务信息
     */
    private MessageTaskInfo prepareTaskInfo(DocReviewSceneEntity sceneEntity, DocReviewTaskEntity entity, DocReviewGenReviewDto dto) {
        MessageTaskInfo taskInfo = new MessageTaskInfo();

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(sceneEntity.getAnalysisAgentEngineer());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText("合同类型的审查清单项");

        // 处理附件
        if (StringUtil.isNotBlank(entity.getDocumentId())) {
            List<Long> attachmentIds = Arrays.stream(entity.getDocumentId().split(","))
                    .map(Long::parseLong)
                    .toList();
            List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
            taskInfo.setAttachments(attachments);
        }


        return taskInfo;
    }

    /**
     * 处理生成的内容
     */
    private void processGeneratedContent(WorkflowExecutionDto result, DocReviewTaskEntity entity) {
        if (result.getCodeContent() != null && !result.getCodeContent().isEmpty()) {
            String codeContent = result.getCodeContent().get(0).getContent();

            // 验证JSON格式
            List<DocReviewRulesDto> nodeDtos = JSONArray.parseArray(codeContent, DocReviewRulesDto.class);
            log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));

            // 设置ID并保存
            for (DocReviewRulesDto d : nodeDtos) {
                d.setId(IdUtil.getSnowflakeNextId());
            }
            entity.setReviewList(JSONUtil.toJsonStr(nodeDtos));
            entity.setReviewGenStatus(ReviewRuleGenStatusEnums.SUCCESS.getCode());
        } else {
            entity.setReviewGenStatus(ReviewRuleGenStatusEnums.FAILED_NO_CONTENT.getCode());
        }

        docReviewTaskService.updateById(entity);
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus(DocReviewTaskEntity entity, ReviewRuleGenStatusEnums status, DocReviewGenReviewDto dto) {
        entity.setReviewGenStatus(status.getCode());
        entity.setContractType(dto.getContractType());
        entity.setReviewPosition(dto.getReviewPosition());
        entity.setReviewListKnowledgeBase(dto.getReviewListKnowledgeBase());
        entity.setReviewListOption(dto.getReviewListOption());
    }

    /**
     * 处理生成错误
     */
    private void handleGenerationError(Long taskId, String errorMessage) {
        DocReviewTaskEntity entity = docReviewTaskService.getById(taskId);
        if (entity != null) {
            entity.setReviewGenStatus(ReviewRuleGenStatusEnums.FAILED.getCode());
            docReviewTaskService.updateById(entity);
        }
        log.error(errorMessage);
    }

    /**
     * 解析文档
     * @param taskEntity
     */
    public void startParseDocument(DocReviewTaskEntity taskEntity, long sceneId, PermissionQuery query) {
        // 使用线程池执行生成任务
        chatThreadPool.execute(() -> {
            DocReviewGenReviewDto dto = new DocReviewGenReviewDto();

            dto.setChannelStreamId(taskEntity.getChannelStreamId());
            dto.setSceneId(sceneId);

            DocReviewSceneEntity sceneEntity = docReviewSceneService.getBySceneId(sceneId, query);
            generateByDocumentMeta(sceneEntity, taskEntity,  dto) ;
        });
    }

}