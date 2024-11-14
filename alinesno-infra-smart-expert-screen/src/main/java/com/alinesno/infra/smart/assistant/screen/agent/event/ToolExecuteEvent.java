package com.alinesno.infra.smart.assistant.screen.agent.event;

import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务完成事件
 */
@Getter
public class ToolExecuteEvent extends ApplicationEvent {

    private final RoleTaskDto workerTask;
    private final RoleTaskDto leaderTask;
    private final boolean isLeader ;

    public ToolExecuteEvent(Object source, RoleTaskDto workerTask , RoleTaskDto leaderTask, boolean isLeader) {
        super(source);
        this.leaderTask = leaderTask;
        this.workerTask = workerTask;
        this.isLeader = isLeader ;
    }

}
