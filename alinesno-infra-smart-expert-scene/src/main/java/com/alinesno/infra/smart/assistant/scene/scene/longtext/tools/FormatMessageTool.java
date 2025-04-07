package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

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
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class FormatMessageTool {

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
    public void handleChapterMessage(WorkflowExecutionDto genContent, MessageTaskInfo taskInfo) {

        taskInfo.setTraceBusId(IdUtil.getSnowflakeNextId());

        String formatContent = """
         ##角色：你是一个专业的大纲创作智能体，非常善于针对大纲领域，创作一个超长标书的大纲，并且对大纲中每一部分的编写要求给出具体指导，目的是方便将创作任务拆分为多个部分，可以分配给不同的智能体去完成。
              
         ##任务：请你针对用户的需求还有对应知识内容，严格遵循下面的规则要求，调取你的专业领域数据，创作一份满足用户大纲，注意不是内容本身，而是大纲，具体内容将会有多个智能体按照你创作的大纲进行编写。
          
         ##规则：
          1-在创作大纲的时候，请你主要【附件资料】为主，调取你这个领域的专业数据进行创作大纲。
          2-在创作大纲的时候，请你平均拆分为N个部分，未来将有N个不同的智能体按照你的大纲进行创作具体的文字内容。请你保证N个部分边界清晰，没有重叠，避免冲突。
          3-在你创作的大纲的N个部分的时候，每个部分需要给出一个标题，然后给出一个具体的编写这部分内容编写要求，目的是给下一步拆分后的创作智能体参考。
          4-在你创作的N个部分编写要求中，除了对这部分的内容进行描述，也就是创作什么具体的内容。
          5-在编写要求中，请你根据用户的文字总数进行计算，对每一个部分创作具体内容时候要求的字数，给出要求。如果用户没有给出字数要求，请你按照每一部分要求在2000字以上。
          6-最终请你下面的格式进行直接输出，不要输出其他信息，格式：
           ```json
          [
              {
                  "label": "第一部分大纲标题",
                  "description": "第一部分编写要求",
                  "children": [
                      {
                          "label": "第一部分大纲子标题1",
                          "description": "第一部分大纲子标题1编写要求"
                      },
                      {
                          "label": "第一部分大纲子标题2",
                          "description": "第一部分大纲子标题2编写要求"
                      },
                  ]
              },
              {
                  "label": "第二部分大纲标题",
                  "description": "第二部分编写要求"
              }
          ]
         ##需求=%s
         ##附件资料=%s
         """;

        taskInfo.setText(String.format(formatContent , taskInfo.getText() , taskInfo.getFullContent()));
        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(taskInfo.getRoleId(), SceneEnum.LONG_TEXT.getSceneInfo().getId()) ;

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

        genContent.setGenContent(message.getFullContent());
        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(message.getFullContent()));
    }

}
