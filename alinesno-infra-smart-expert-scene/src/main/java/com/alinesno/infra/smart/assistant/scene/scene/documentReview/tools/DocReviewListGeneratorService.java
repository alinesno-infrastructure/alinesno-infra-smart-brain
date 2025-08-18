package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.FormatterPromptTools;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.bean.DocumentInfoBean;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums.ReviewListOptionEnum;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.*;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums.ReviewRuleGenStatusEnums;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditResultService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
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
import org.springframework.util.CollectionUtils;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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
    private IDocReviewRulesService ruleService;

    @Autowired
    private IDocReviewAuditResultService auditResultService ;

    @Autowired
    @Qualifier("chatThreadPool")
    private ThreadPoolTaskExecutor chatThreadPool;

    @Autowired
    private IAccountPointService accountPointService ;

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
                generateByAI(sceneEntity, entity, dto , query);
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
    private void generateByDocumentMeta(DocReviewSceneEntity sceneEntity,
                                        DocReviewTaskEntity entity,
                                        DocReviewGenReviewDto dto ,
                                        PermissionQuery query) {
        final MessageTaskInfo taskInfo = prepareTaskInfo(sceneEntity, entity, dto);

        String documentInfo = String.format(
                DocReviewPrompt.documentContentAnalysis ,
                entity.getDocumentName()
        );

        taskInfo.setText(documentInfo);

        // 会话开始
        accountPointService.startSession(query.getOperatorId(), query.getOrgId() , taskInfo.getRoleId());

        // 异步调用角色服务
        CompletableFuture<WorkflowExecutionDto> genContentFuture = roleService.runRoleAgent(taskInfo);
        genContentFuture.whenComplete((result, ex) -> {

            // 结束会话
            accountPointService.endSession(query.getOperatorId(), query.getOrgId() , taskInfo.getRoleId());

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

        entity.setTaskEndTime(new Date());
        docReviewTaskService.updateById(entity);

    }

    /**
     * 使用AI生成审核清单
     */
    private void generateByAI(DocReviewSceneEntity sceneEntity, DocReviewTaskEntity entity, DocReviewGenReviewDto dto, PermissionQuery query) {
        final MessageTaskInfo taskInfo = prepareTaskInfo(sceneEntity, entity, dto);

        long userId = query.getOperatorId() ;
        long orgId = query.getOrgId() ;

        // 任务会话开始
        accountPointService.startSceneTask(userId, orgId , entity.getId());

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

            // 任务会话开始
            accountPointService.endSceneTask(userId, orgId , entity.getId());
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

        entity.setTaskEndTime(new Date());
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
            generateByDocumentMeta(sceneEntity,
                    taskEntity,
                    dto ,
                    query) ;
        });
    }

    /**
     * 开始针对于清单做合同审核
     *
     * @param dto   生成参数
     * @param query 权限查询
     */
    public void startGenerateAuditList(DocReviewTaskEntity taskEntity, GenAuditResultDto dto, PermissionQuery query, List<DocumentInfoBean> contentList) {

        // 使用线程池执行生成任务
        chatThreadPool.execute(() -> {
            Map<String , String> errorMap = new HashMap<>();
            AtomicInteger successCount = new AtomicInteger(0);

            long userId = query.getOperatorId() ;
            long orgId = query.getOrgId() ;

            // 任务会话开始
            accountPointService.startSceneTask(userId, orgId , taskEntity.getId());

            for (Long rule : dto.getRuleIds()) {
                dto.setRuleId(rule);
                asyncGenerateAuditList(taskEntity, dto, query, contentList , errorMap , successCount);
            }

            log.debug("获取到审核意见结果数:{}" , successCount);

            if (successCount.get() == 0) {
                taskEntity.setResultGenStatus(ReviewRuleGenStatusEnums.FAILED.getCode());
                taskEntity.setTaskEndTime(new Date());  // 设置任务结束时间
                docReviewTaskService.updateById(taskEntity);
            }else {
                taskEntity.setResultGenStatus(ReviewRuleGenStatusEnums.SUCCESS.getCode());
                taskEntity.setTaskEndTime(new Date());  // 设置任务结束时间
                docReviewTaskService.updateById(taskEntity);
            }

            // 任务会话结束
            accountPointService.endSceneTask(userId, orgId , taskEntity.getId());

        });

    }

    /**
     * 异步生成审核结果清单
     *
     * @param dto
     * @param query
     * @param errorMap
     * @param successCount
     */
    private void asyncGenerateAuditList(DocReviewTaskEntity taskEntity,
                                        GenAuditResultDto dto,
                                        PermissionQuery query,
                                        List<DocumentInfoBean> contentList,
                                        Map<String, String> errorMap,
                                        AtomicInteger successCount) {

        taskEntity.setResultGenStatus(ReviewRuleGenStatusEnums.GENERATING.getCode());
        docReviewTaskService.updateById(taskEntity);

        List<DocReviewAuditResultEntity> auditResultList = new ArrayList<>() ;

        if(contentList == null){
            return ;
        }

        log.debug("contentList = {}" , contentList.size());
        int count = 0 ;

        // 如果是aigen处理的审核
        if(taskEntity.getReviewListOption().equals(ReviewListOptionEnum.AIGEN.getValue())){

            // 解析获取得到审核内容
            List<DocReviewRulesEntity> rules = JSONArray.parseArray(taskEntity.getReviewList(), DocReviewRulesEntity.class) ;

            for(DocumentInfoBean bean : contentList){
                String contentPromptContent = bean.getContent() ;

                // 从rules中找到
                DocReviewRulesEntity rule = getRule(rules , dto.getRuleId()) ;
                if(rule != null){
                    count ++ ;
                    String text = "正在进行["+rule.getRuleName()+"]检查，当前是第"+count+"条，还剩"+(dto.getRuleIds().size() - count)+"条";

                    taskEntity.setCurrentStepInfo(text);
                    docReviewTaskService.updateById(taskEntity);

                    generateTask(contentPromptContent , dto, query , rule , auditResultList , errorMap , successCount);
                }
            }
        } else if(taskEntity.getReviewListOption().equals(ReviewListOptionEnum.DATASET.getValue())){
            for(DocumentInfoBean bean : contentList){
                String contentPromptContent = bean.getContent() ;
                DocReviewRulesEntity rule = ruleService.getById(dto.getRuleId()) ;

                count ++ ;
                String text = "正在进行["+rule.getRuleName()+"]检查，当前是第"+count+"条，还剩"+(dto.getRuleIds().size() - count)+"条";
                taskEntity.setCurrentStepInfo(text);
                docReviewTaskService.updateById(taskEntity);

                generateTask(contentPromptContent , dto, query , rule , auditResultList , errorMap , successCount);
            }
        }

        if(!CollectionUtils.isEmpty(auditResultList)){

            CopyOptions options = CopyOptions.create()
                    .setIgnoreNullValue(true)  // 忽略源对象属性为空的情况
                    .setIgnoreError(true);     // 忽略复制过程中出现的错误

            for(DocReviewAuditResultEntity entity : auditResultList){
                log.debug("entity = {}" , entity);

                entity.setId(null);
                entity.setAuditId(dto.getRuleId());
                entity.setRuleId(dto.getRuleId());
                entity.setSceneId(dto.getSceneId());
                entity.setDocReviewSceneId(dto.getSceneId());
                entity.setTaskId(taskEntity.getId());

                BeanUtil.copyProperties(query , entity , options);
            }
            log.debug("文档解析完成，审核意见为:{}" , auditResultList.size());
            auditResultService.saveAuditResult(auditResultList , dto);
        }

    }

    /**
     * 获取规则
     * @param rules
     * @param ruleId
     * @return
     */
    private DocReviewRulesEntity getRule(List<DocReviewRulesEntity> rules, Long ruleId) {
        for (DocReviewRulesEntity rule : rules) {
            if (rule.getId().equals(ruleId)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * 生成审核任务并处理结果
     *
     * @param contentPromptContent 待审核的文档内容
     * @param dto                  生成审核结果的参数DTO
     * @param query                权限查询对象
     * @param rule                 审核规则实体
     * @param auditResultList      用于存储生成的审核结果列表
     * @param errorMap
     * @param successCount
     */
    private void generateTask(String contentPromptContent,
                              GenAuditResultDto dto,
                              PermissionQuery query,
                              DocReviewRulesEntity rule,
                              List<DocReviewAuditResultEntity> auditResultList,
                              Map<String, String> errorMap,
                              AtomicInteger successCount) {

        // 初始化任务信息对象
        MessageTaskInfo taskInfo = new MessageTaskInfo();
        Long roleId = dto.getRoleId();

        // 获取当前任务实体
        DocReviewTaskEntity taskEntity = docReviewTaskService.getById(dto.getTaskId());
        DocReviewTaskDto taskDto = DocReviewTaskDto.fromEntity(taskEntity);

        // 设置任务基本信息
        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleId);
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        // 构建审核提示内容，结合文档内容、审核规则和格式要求
        String prompt = DocReviewPromptTools.buildReviewPrompt(
                contentPromptContent,
                dto,
                docReviewSceneService.getBySceneId(dto.getSceneId(), query),
                rule,
                taskInfo ,
                taskDto) +
                FormatterPromptTools.FORMAT_REVIEW_OUTPUT_PROMPT;

        taskInfo.setText(prompt);

        // 异步执行审核任务
        WorkflowExecutionDto result;
        try {
            result = roleService.runRoleAgent(taskInfo).get();

            try {
                // 设置生成的内容和解析后的代码块
                result.setGenContent(taskInfo.getFullContent());
                result.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));

                // 处理生成的代码内容
                if (result.getCodeContent() != null && !result.getCodeContent().isEmpty()) {
                    String codeContent = result.getCodeContent().get(0).getContent();
                    // 解析JSON格式的审核结果
                    List<DocReviewAuditResultEntity> auditResults = JSONArray.parseArray(codeContent, DocReviewAuditResultEntity.class);

                    if (auditResults != null && !auditResults.isEmpty()) {
                        // 为每个审核结果设置公共属性
                        for (DocReviewAuditResultEntity entity : auditResults) {
                            entity.setAuditId(dto.getRuleId());
                            entity.setRuleId(dto.getRuleId());
                            entity.setSceneId(dto.getSceneId());
                            entity.setDocReviewSceneId(dto.getSceneId());
                            entity.setTaskId(dto.getTaskId());
                            // 复制权限相关属性
                            BeanUtil.copyProperties(query, entity,
                                    CopyOptions.create()
                                            .setIgnoreNullValue(true)  // 忽略空值
                                            .setIgnoreError(true));    // 忽略错误
                            entity.setId(IdUtil.getSnowflakeNextId());
                        }

                        // 将结果添加到集合中
                        auditResultList.addAll(auditResults);
                        auditResultService.saveAuditResult(auditResultList, dto);

                        successCount.addAndGet(auditResults.size()) ;

                        log.debug("文档解析完成，审核意见为:{}" , auditResults.size());
                        log.debug("成功生成审核结果，当前成功数量为:{}" , successCount.get());
                    }
                }
            } catch (Exception e) {
                log.error("解析审核结果失败", e);
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("生成审核结果失败", e);
        }

    }

}