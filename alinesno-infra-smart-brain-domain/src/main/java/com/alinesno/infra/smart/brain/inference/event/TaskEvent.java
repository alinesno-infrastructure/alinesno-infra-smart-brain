package com.alinesno.infra.smart.brain.inference.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskEvent extends ApplicationEvent {

    private long id ;

    private String businessId ; // 业务ID

    private String promptId ; // GPT角色ID

    private String systemContent ;  // GPT角色设定

    private String userContent ;  // GPT用户信息

    private String assistantContent ;  // 生成内容

    private int taskStatus ;  // 0排队中/1运行中/2已完成/9失败

    public TaskEvent(Object source) {
        super(source);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void setPromptId(String promptId) {
        this.promptId = promptId;
    }

    public void setSystemContent(String systemContent) {
        this.systemContent = systemContent;
    }

    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    public void setAssistantContent(String assistantContent) {
        this.assistantContent = assistantContent;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
}
