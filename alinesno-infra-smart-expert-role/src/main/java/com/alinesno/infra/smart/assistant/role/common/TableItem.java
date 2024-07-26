package com.alinesno.infra.smart.assistant.role.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Data
public class TableItem {
    private String taskStatus;
    private String businessId;
    private String channel ;
    private String taskType;
    private String taskName;
    private String assistantContent;
    private String usageTime;

    public TableItem(String taskStatus, String businessId, String taskType, String taskName, String assistantContent, String usageTime) {
        this.taskStatus = taskStatus;
        this.businessId = businessId;
        this.taskType = taskType;
        this.taskName = taskName;
        this.assistantContent = assistantContent;
        this.usageTime = usageTime;
    }
}
