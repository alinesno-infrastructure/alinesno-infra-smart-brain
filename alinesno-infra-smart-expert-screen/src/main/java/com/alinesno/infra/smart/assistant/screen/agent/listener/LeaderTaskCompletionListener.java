package com.alinesno.infra.smart.assistant.screen.agent.listener;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.agent.bean.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskAssignedEvent;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskCompletionEvent;
import com.alinesno.infra.smart.assistant.screen.agent.function.FunctionCall;
import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.screen.service.IRoleExecuteService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 领导任务完成监听器
 * 该监听器负责监听领导任务的完成情况，并在任务完成后执行相应的处理逻辑
 */
@Slf4j
@Component
public class LeaderTaskCompletionListener {

    private final ApplicationEventPublisher publisher;

    private static final String ALL_TASK_FINISH = "[ALL_TASK_FINISH]" ;

    @Autowired
    private IRoleExecuteService roleExecuteService ;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private ISSEService sseService ;

    public LeaderTaskCompletionListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @EventListener
    public void handleTaskCompletion(TaskCompletionEvent event) {

        RoleTaskDto leaderTask = event.getLeaderTask() ;
        RoleTaskDto workerTask = event.getWorkerTask() ;

        RoleTaskDto workerTaskCopy = workerTask ;

        boolean isCompleted = false ;

        while(!isCompleted){

            sendSSE(leaderTask.getLeaderRole() ,
                    leaderTask.getScreenId() ,
                    "在处理疑问:" + workerTaskCopy.getQuestion() + "\r\n当前知识库:" + leaderTask.getKnowledgeContent());

            WorkflowExecutionDto dto = runAgent(leaderTask , workerTaskCopy.getQuestion());
            log.debug("runLeader result = {}" , dto);

            // 开始搜索知识库
            if(dto.getCodeContent() != null && !dto.getCodeContent().isEmpty()) {
                CodeContent codeContent = dto.getCodeContent().get(0);
                WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);

                String answer = reactResponse.getAnswer(); // 答案，如果已经获取到答案，则表示任务已经结束

                String thought = reactResponse.getThought();  // 推理思考
                String question = reactResponse.getQuestion(); // 问题咨询

                leaderTask.setThought(thought);
                leaderTask.setQuestion(question);
                leaderTask.setAnswer(answer);

                isCompleted = StringUtils.isNotEmpty(answer) ;

                if(isCompleted){
                    sendSSE(leaderTask.getLeaderRole() ,
                            leaderTask.getScreenId() ,
                            "已找到答案:"+answer);

                    // 将答案提交给下属(增加它的知识库)
                    workerTask.setKnowledgeContent(workerTask.getKnowledgeContent()+"\r\n" + answer);

                    publisher.publishEvent(new TaskAssignedEvent(this , workerTask));
                }else{
                    // 如果使用工具，则先使用工具查询
                    List<String> toolNames = reactResponse.getToolNames();
                    if(toolNames != null && !toolNames.isEmpty()){

                        StringBuilder toolResult = new StringBuilder() ;

                        for (String toolName : toolNames) {
                            Map<String , Object> toolParams = reactResponse.getToolParams(toolName);

                            sendSSE(leaderTask.getLeaderRole() ,
                                    leaderTask.getScreenId() ,
                                    "使用当前工具搜索:" + toolName);

                            // 获取到知识
                            String funRest = FunctionCall.invokeTool2(toolName , toolParams);
                            toolResult.append(toolName)
                                    .append(":")
                                    .append(funRest);

                            sendSSE(leaderTask.getLeaderRole() ,
                                    leaderTask.getScreenId() ,
                                    "获取到内容:" + funRest);
                        }

                        leaderTask.setKnowledgeContent(leaderTask.getKnowledgeContent() + "\r\n" + toolResult);
                    }

                    // 重新调用Leader处理
                    // publisher.publishEvent(new TaskCompletionEvent(this, workerTask , leaderTask, false));
                }

            }

        }
    }

    private WorkflowExecutionDto runAgent(RoleTaskDto task, String workerGoal) {

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(task.getLeaderRoleId());
        taskInfo.setChannelId(task.getScreenId());
        taskInfo.setScreenId(task.getScreenId());

        taskInfo.setText(task.getThought() + " " + task.getQuestion()); // 输入任务要求

        Map<String, Object> params = new HashMap<>();
        params.put("question", task.getQuestion());
        params.put("thought", task.getThought());
        params.put("goal", task.getTaskGoal());
        params.put("workerGoal", workerGoal);
        taskInfo.setParams(params);

        taskInfo.setRoleType("agent");

        return roleService.runRoleAgent(taskInfo);
    }

    private void sendSSE(IndustryRoleEntity role , long screenId, String msg) throws IOException {
        ChatMessageDto message = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());
        message.setChatText(msg);
        message.setLoading(false);
        sseService.send(String.valueOf(screenId) , message);
    }

}

