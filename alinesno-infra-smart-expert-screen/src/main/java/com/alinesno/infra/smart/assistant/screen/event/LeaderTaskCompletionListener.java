package com.alinesno.infra.smart.assistant.screen.event;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.screen.service.IRoleExecuteService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LeaderTaskCompletionListener {

    private final ApplicationEventPublisher publisher;

    private static final String ALL_TASK_FINISH = "[ALL_TASK_FINISH]" ;

    @Autowired
    private IRoleExecuteService roleExecuteService ;

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

         boolean isCompleted = event.isCompleted() ;

        if (isCompleted) {
            log.debug("Leader confirmed that task: " + event.getTask().getId() + " has been completed.");

            // 更新当前的任务状态为完成
            roleExecuteService.finishTask(task.getId());

            ChatMessageDto message = AgentUtils.getChatMessageDto(task.getLeaderRole(), IdUtil.getSnowflakeNextId());
            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果完成，任务完成.");
            message.setLoading(false);
            sseService.send(String.valueOf(task.getScreenId()) , message);

            // 判断所有工作是否完成
            boolean isAllCompleted = roleExecuteService.isAllTaskCompleted(task.getScreenId());
            if(isAllCompleted){
                message.setChatText(ALL_TASK_FINISH);
                sseService.send(String.valueOf(task.getScreenId()) , message);
            }

        } else {
            log.debug("Leader found that task: " + event.getTask().getId() + " was not completed.");

            ChatMessageDto message = AgentUtils.getChatMessageDto(task.getLeaderRole(), IdUtil.getSnowflakeNextId());
            message.setChatText("验证" + task.getWorkerRole().getRoleName() + "执行结果未完成，将记录异常结果，并重新分配任务.");
            message.setLoading(false);
            sseService.send(String.valueOf(task.getScreenId()) , message);

            // 如果任务未完成，重新分配任务
            publisher.publishEvent(new TaskAssignedEvent(this, event.getTask()));
        }
    }
}