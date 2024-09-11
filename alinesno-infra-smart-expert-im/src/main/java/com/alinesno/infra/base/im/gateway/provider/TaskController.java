package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.base.im.dto.MessageTaskInfo;
import com.alinesno.infra.base.im.service.ITaskService;
import com.alinesno.infra.common.facade.response.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TaskController 主要用于处理任务，包括添加任务、获取任务等
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/task/")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    /**
     * 添加一个新的任务到队列中
     * @param messageTaskInfo 包含任务信息的对象
     * @return 任务添加成功与否的响应
     */
    @PostMapping("/addTask")
    public AjaxResult addTask(@RequestBody MessageTaskInfo messageTaskInfo) {
        taskService.addTask(messageTaskInfo);
        return AjaxResult.success("Task added successfully");
    }

    /**
     * 获取当前队列中的所有任务信息
     *
     * @return 当前队列中的任务列表
     */
    @GetMapping("/getTasks")
    public AjaxResult getTasks(String channelId) {
        // 注意：这里的实现假设了一个不存在的方法getTaskQueue，您需要替换为实际的服务方法
        List<MessageTaskInfo> tasks = taskService.getTaskMessage(channelId);
        return AjaxResult.success(tasks);
    }
}