package com.alinesno.infra.smart.assistant.screen.agent.listener;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.screen.agent.bean.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskApproveEvent;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskCompletionEvent;
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

        RoleTaskDto task = event.getTask() ;
        log.debug("Worker received task: " + task);

        sendSSE(task , "我已经收到内容:" + task.getTaskDesc());

        WorkflowExecutionDto dto = runAgent(task);

        if(dto.getCodeContent() != null && !dto.getCodeContent().isEmpty()) {
            CodeContent codeContent = dto.getCodeContent().get(0);
            WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);

            String answer = reactResponse.getAnswer(); // 答案，如果已经获取到答案，则表示任务已经结束

            String thought = reactResponse.getThought();  // 推理思考
            String question = reactResponse.getQuestion(); // 问题咨询

            boolean isCompleted = StringUtils.isNotEmpty(answer);

            sendSSE(task, thought + question);

            // --->>>>>  ReAct参数 ---->>>>>
            task.setAnswer(answer);
            task.setThought(thought);
            task.setQuestion(question);

            if (isCompleted) {
                publisher.publishEvent(new TaskApproveEvent(this, task));
            } else {
                publisher.publishEvent(new TaskCompletionEvent(this, task, false));
            }
        }

//        // 判断任务是否有目标
//        String goal = task.getTaskGoal() ;
//        IndustryRoleEntity leaderRole = task.getLeaderRole();
//        IndustryRoleEntity workerRole = task.getWorkerRole() ;
//
//        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
//        taskInfo.setRoleId(leaderRole.getId());
//        taskInfo.setChannelId(task.getScreenId());
//        taskInfo.setScreenId(task.getScreenId());
//
//        Map<String, String> params = new HashMap<>();
//        params.put("goal", goal);
//        taskInfo.setParams(params);
//
//        taskInfo.setText(workerRole.getRoleName() + ":\r\n" + task.getExecuteResult());  // 下属反馈
//        WorkflowExecutionDto leaderRoleGenContent  = roleService.runRoleAgent(taskInfo) ;
//        log.debug("leaderRoleGenContent = {}" , leaderRoleGenContent);
//
//        boolean isCompleted = leaderRoleGenContent.getGenContent().contains("SUCCESS") ; // 包含SUCCESS表示已经完成
//
//        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getLeaderRole(), IdUtil.getSnowflakeNextId());
//        message.setChatText(leaderRoleGenContent.getGenContent()) ;
//        message.setLoading(false);
//        sseService.send(String.valueOf(task.getScreenId()) , message);
//
//        if (isCompleted) {
//
//            log.debug("Leader confirmed that task: " + event.getTask().getId() + " has been completed.");
//
//            // 更新当前的任务状态为完成
//            roleExecuteService.finishTask(task.getId());
//
//            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果完成，任务完成.");
//            sseService.send(String.valueOf(task.getScreenId()) , message);
//
//            // 判断所有工作是否完成
//            boolean isAllCompleted = roleExecuteService.isAllTaskCompleted(task.getScreenId());
//            if(isAllCompleted){
//                message.setChatText(ALL_TASK_FINISH);
//                sseService.send(String.valueOf(task.getScreenId()) , message);
//            }
//
//        } else {
//            log.debug("Leader found that task: " + event.getTask().getId() + " was not completed.");
//
//            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果未完成，将记录异常结果，并重新分配任务.");
//            sseService.send(String.valueOf(task.getScreenId()) , message);
//            task.setCallbackMsg(true);
//
//            // 如果任务未完成，重新分配任务
//            task.setTaskDesc(leaderRoleGenContent.getGenContent());
//            publisher.publishEvent(new TaskAssignedEvent(this, task));
//        }

//        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
//
//        taskInfo.setRoleId(leaderRole.getId());
//        taskInfo.setChannelId(task.getScreenId());
//        taskInfo.setScreenId(task.getScreenId());
//
//        if(isCompleted){
//            taskInfo.setText(task.getExecuteResult()); // 用户答案
//        }else{
//            taskInfo.setText(workerRole.getRoleName() + ":\r\n" + task.getExecuteResult());  // 用户提问
//        }
//
//        Map<String, String> params = new HashMap<>();
//        params.put("goal", goal);
//
//        taskInfo.setParams(params);
//
//        WorkflowExecutionDto leaderRoleGenContent  = roleService.runRoleAgent(taskInfo) ;
//        log.debug("leaderRoleGenContent = {}" , leaderRoleGenContent);
//
//        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getLeaderRole(), IdUtil.getSnowflakeNextId());
//        message.setChatText(leaderRoleGenContent.getGenContent()) ;
//        message.setLoading(false);
//        sseService.send(String.valueOf(task.getScreenId()) , message);
//
//        task.setTaskDesc(leaderRoleGenContent.getGenContent());
//
//        if (isCompleted) {
//
//            log.debug("Leader confirmed that task: " + event.getTask().getId() + " has been completed.");
//
//            // 更新当前的任务状态为完成
//            roleExecuteService.finishTask(task.getId());
//
//            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果完成，任务完成.");
//            sseService.send(String.valueOf(task.getScreenId()) , message);
//
//            // 判断所有工作是否完成
//            boolean isAllCompleted = roleExecuteService.isAllTaskCompleted(task.getScreenId());
//            if(isAllCompleted){
//                message.setChatText(ALL_TASK_FINISH);
//                sseService.send(String.valueOf(task.getScreenId()) , message);
//            }
//
//        } else {
//            log.debug("Leader found that task: " + event.getTask().getId() + " was not completed.");
//
//            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果未完成，将记录异常结果，并重新分配任务.");
//            sseService.send(String.valueOf(task.getScreenId()) , message);
//            task.setCallbackMsg(true);
//
//            // 如果任务未完成，重新分配任务
//            publisher.publishEvent(new TaskAssignedEvent(this, task));
//        }

    }

    private WorkflowExecutionDto runAgent(RoleTaskDto task) {

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(task.getLeaderRoleId());
        taskInfo.setChannelId(task.getScreenId());
        taskInfo.setScreenId(task.getScreenId());

        taskInfo.setText(task.getThought() + " " + task.getQuestion()); // 输入任务要求

        Map<String, String> params = new HashMap<>();
        params.put("question", task.getQuestion());
        params.put("thought", task.getThought());
        taskInfo.setParams(params);

        return roleService.runRoleAgent(taskInfo);
    }

    private void sendSSE(RoleTaskDto task, String msg) throws IOException {
        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
        message.setChatText(msg);
        message.setRoleType("person");
        message.setLoading(false);
        sseService.send(String.valueOf(task.getScreenId()) , message);
    }

}