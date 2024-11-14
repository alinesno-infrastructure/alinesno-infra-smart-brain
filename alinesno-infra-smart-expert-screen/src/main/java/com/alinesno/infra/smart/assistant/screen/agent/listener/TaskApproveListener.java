package com.alinesno.infra.smart.assistant.screen.agent.listener;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskApproveEvent;
import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.screen.service.IRoleExecuteService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 监听工具执行过程的组件
 * 该类的主要作用是作为工具执行过程中的事件监听器，可以对工具执行前后的状态进行处理或记录
 */
@Slf4j
@Component
public class TaskApproveListener {

    private final ApplicationEventPublisher publisher;

    @Autowired
    private IRoleExecuteService roleExecuteService ;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private ISSEService sseService ;

    public TaskApproveListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @EventListener
    public void execute(TaskApproveEvent event) {

        RoleTaskDto task = event.getTask() ;
        log.debug("Task approve: name = {} , params = {} "  , task.getRoleName(), task.getTaskDesc());

        sendSSE(task , "我是审核人员:任务已经处理完成。");
    }

    private void sendSSE(RoleTaskDto task, String msg) throws IOException {

        IndustryRoleEntity role = task.getLeaderRole() ;

        role.setId(Long.parseLong("1851439911174524930"));  // 模拟数据
        role.setRoleAvatar("1851439744899731458");
        role.setRoleName("审核人员");

        ChatMessageDto message = AgentUtils.getChatMessageDto(role , IdUtil.getSnowflakeNextId());
        message.setChatText(msg);
        message.setLoading(false);
        sseService.send(String.valueOf(task.getScreenId()) , message);
    }
}