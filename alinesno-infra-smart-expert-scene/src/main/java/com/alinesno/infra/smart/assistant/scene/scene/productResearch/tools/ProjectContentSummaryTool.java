package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.entity.LongTextTemplateEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 内容总结输出
 */
@Slf4j
@Component
public class ProjectContentSummaryTool {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private ModelAdapterLLM modelAdapterLLM ;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @SneakyThrows
    public String handleChapterMessage(String fullContent , String outline ,  MessageTaskInfo taskInfo) {

        taskInfo.setTraceBusId(IdUtil.getSnowflakeNextId());

        String formatContent = """
            **角色**：你是一名专业的内容整合专家，负责将多个Agent输出的内容进行系统性汇总，确保最终产出符合原始目标、结构完整且逻辑清晰。
                    
            **任务目标**：
            根据原始任务目标、各Agent提交的内容以及最初规划的大纲，整合出一份结构清晰、内容完整且符合要求的最终版本。
                    
            **输入参数**：
            1. **原始目标**：%s
            2. **Agent提交内容**：
                '''
                %s
                '''
            3. **原始大纲**：
                '''
                %s
                '''
                    
            **处理要求**：
            1. **一致性检查**：
               - 确保汇总内容完全覆盖原始目标，无遗漏或偏离
               - 对比内容与原始大纲，标记并补充缺失部分
                    
            2. **逻辑整合**：
               - 严格按原始大纲结构组织内容
               - 合并重复信息，消除矛盾表述
               - 对存在逻辑断层的内容，补充过渡语句确保连贯
                    
            3. **优化输出**：
               - 使用简洁明了的语言表达，去除冗余
               - 对关键信息采用加粗或列表等突出方式
                    
            4. **内容调整**：
               - 对与原始目标不符的内容可进行修改或删除
               - 可提出对原始大纲的优化建议（如新增/删除章节）
                    
            **输出格式**：
            - 标题：准确反映原始目标
            - 正文：按大纲层级分章节呈现，每章节包含：
              * 整合后的完整内容
              * 各Agent贡献的有效信息
              * 必要的过渡衔接内容
                    
            **示例输出框架**：
            ```
            # [最终标题]
            ## 1. [大纲章节1]
            - [整合后的内容，包含各Agent的有效信息...]
            ## 2. [大纲章节2]
            - [整合后的内容，含补充的过渡衔接...]
            ...
            ```
        """;

        taskInfo.setQueryText(taskInfo.getText());
        taskInfo.setText(String.format(formatContent ,
                taskInfo.getText(),
                fullContent,
                outline)
        );

        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(taskInfo.getRoleId(), SceneEnum.PRODUCT_RESEARCH.getSceneInfo().getId()) ;
        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(taskInfo.getRoleId());
        long messageId = IdUtil.getSnowflakeNextId() ;
        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, taskInfo.getText() , taskInfo , messageId) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        return message.getFullContent() ;
    }

}
