package com.alinesno.infra.smart.assistant.chain;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// 创建 Expert 接口
public interface IBaseExpertService {

    /**
     * 专家运行
     *
     * @param role                    角色信息
     * @param message 工作流执行实体
     * @param taskInfo                消息任务信息
     * @return
     */
    CompletableFuture<WorkflowExecutionDto> runRoleAgent(IndustryRoleEntity role, MessageEntity message, MessageTaskInfo taskInfo);

    /**
     * 获取到PromptMessage信息列表
     * @param askInfo 用户咨询信息
     * @return
     */
    List<PromptMessage> promptMessages(String askInfo , IndustryRoleEntity role) ;

}