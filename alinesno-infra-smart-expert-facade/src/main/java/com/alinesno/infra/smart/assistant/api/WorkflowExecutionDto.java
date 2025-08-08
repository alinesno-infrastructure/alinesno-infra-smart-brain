package com.alinesno.infra.smart.assistant.api;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

// 工作流执行记录表实体类
@Data
public class WorkflowExecutionDto implements Serializable {

    private long id ; // 处理返回结果的id
    private long roleId ;  // 角色ID
    private long channelId ; // 频道ID
    private Integer buildNumber; // 构建次数
    private String workflowName; // 节点名称或标识符
    private long startTime; // 工作流执行开始时间
    private long endTime; // 工作流执行结束时间
    private String status; // 工作流执行状态
    public String usageTimeSeconds ;
    private String logInfo; // 节点执行的日志信息
    private String genContent ; // 节点生成的内容
    private List<CodeContent> codeContent ; // 生成的代码工程列表
    private boolean isCoding ; // 是否是代码编辑
    private String chatType ;

    private boolean hasException = false;  // 是否有异常
    private String exceptionMsg ; // 异常信息

    private Long traceBusId ; // 消息业务跟踪ID
    private boolean isPrint = false ; // 是否打印消息
    private boolean isSync = false; // 是否同步，默认是异步(同步true|异步false)

}
