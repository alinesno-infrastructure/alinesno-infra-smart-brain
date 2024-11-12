package com.alinesno.infra.smart.assistant.screen.event;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WorkerTaskListener {

    private final ApplicationEventPublisher publisher;

    @Autowired
    private ISSEService sseService ;

    @Autowired
    private IRoleExecuteService roleExecuteService;

    @Autowired
    private IIndustryRoleService roleService;

    public WorkerTaskListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @EventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {

        RoleTaskDto task = event.getTask() ;
        log.debug("Worker received task: " + task);

        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
        message.setChatText("我已经接收到任务计划，工作内容是【"+task.getTaskDesc()+"】在执行.");
        message.setRoleType("person");
        message.setLoading(true);
        sseService.send(String.valueOf(task.getScreenId()) , message);

        // 模拟任务处理

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(task.getWorkerRoleId());
        taskInfo.setChannelId(task.getScreenId());
        taskInfo.setScreenId(task.getScreenId());

        taskInfo.setText(task.getTaskDesc()); // 输入任务要求
        Map<String, String> params = new HashMap<>();

        // 查询是否包含前一个任务内容
        if(StringUtils.isNotEmpty(task.getPreRoleId())){
            String preContent = roleExecuteService.getPreContent(task.getPreRoleId() , task.getScreenId()) ;

            params.put("label1", preContent);
            taskInfo.setParams(params);
        }

        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;

        message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
        message.setChatText(genContent.getGenContent());
        message.setLoading(false);
        message.setRoleType("person");
        sseService.send(String.valueOf(task.getScreenId()) , message);

        message.setChatText("我已经接收到任务计划，已处理完成.");
        sseService.send(String.valueOf(task.getScreenId()) , message);

        // 发送任务完成事件
        boolean isCompleted = true; // 假设任务总是成功完成
        publisher.publishEvent(new TaskCompletionEvent(this, event.getTask(), isCompleted));
    }
}