package com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.scene.dto.RoleTaskDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 工作任务服务接口，提供任务执行、代理运行和服务器发送事件的方法。
 */
public interface IWorkerTaskService {

    /**
     * 根据提供的任务信息及其前置任务执行任务。
     *
     * @param task 当前需要执行的任务。
     * @param preTaskList 需要在当前任务之前完成的任务列表。
     */
    void executeTask(RoleTaskDto task, List<RoleTaskDto> preTaskList);

    /**
     * 运行特定任务的代理，包括必要的前置任务。
     *
     * @param task 需要运行代理的任务。
     * @param preTaskList 需要在运行代理之前完成的任务列表。
     * @return 返回工作流的执行详情。
     */
    CompletableFuture<WorkflowExecutionDto> runAgent(RoleTaskDto task, List<RoleTaskDto> preTaskList);

    /**
     * 发送特定任务的服务器发送事件（SSE）消息。
     *
     * @param task 需要发送SSE消息的任务。
     * @param msg SSE消息的内容。
     * @throws IOException 如果在发送消息时发生I/O错误。
     */
    void sendSSE(RoleTaskDto task, String msg) throws IOException;
}
