package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean;

import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.api.config.DeepSearchPromptData;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record.DeepSearchTaskRecordManager;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DsSummaryMessageTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 深度搜索上下文（不可变，显式传递所有依赖）
 */
@Getter
@Builder(toBuilder = true)
public final class DeepSearchContext {

    // 基础信息
    private final Long orgId;
    private final String sessionId ; // 会话ID
    private final String goal;
    private final String datasetKnowledgeDocument;
    private final HistoriesPrompt historyPrompt;
    private final String oneChatId;
    private final Integer maxLoopCount;
    private final boolean hasOutsideKnowledge ;
    private final boolean hasUploadKnowledge ;

    // 实体与配置
    private final IndustryRoleEntity role;
    private final DeepSearchTaskEntity deepSearchTask;
    private final UploadData uploadData;
    private final DeepSearchPromptData deepSearchPromptData;
    private final ContextEngineeringData contextEngineeringData;
    private final Map<String, String> secretKey;

    // 工具与服务
    private final ReActServiceTool reActServiceTool;
    private final DsSummaryMessageTool summaryMessageTool;
    private final IToolService toolService;
    private final List<ToolDto> tools;

    // 流程与事件
    private final DeepSearchFlow deepSearchFlow;
    private final StepEventUtil stepEventUtil;
    private final DeepSearchTaskRecordManager recordManager;
    private final MessageTaskInfo taskInfo;

    // 外部服务
    private final ILLmAdapterService llmAdapter;
    private final ILlmModelService llmModelService;
    private final CloudStorageConsumer cloudStorageConsumer;
}