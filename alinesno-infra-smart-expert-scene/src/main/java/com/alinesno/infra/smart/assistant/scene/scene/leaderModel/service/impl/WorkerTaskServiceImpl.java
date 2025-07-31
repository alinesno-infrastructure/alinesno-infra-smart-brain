package com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.IRoleExecuteService;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.IWorkerTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.scene.dto.RoleTaskDto;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 监听工作线程任务的组件
 * 本类主要用于监听和处理工作线程的任务，通过日志记录和异步处理等方式，确保任务的高效和可靠执行
 */
@Slf4j
@Component
public class WorkerTaskServiceImpl implements IWorkerTaskService {

    @Autowired
    private ISSEService sseService ;

    @Autowired
    private IRoleExecuteService roleExecuteService;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IMessageService messageService ;

    @Override
    @SneakyThrows
    public void executeTask(RoleTaskDto task, List<RoleTaskDto> preTaskList) {

        sendSSE(task , "我已经收到内容:" + task.getTaskDesc() + "\r\n当前知识库:" + task.getKnowledgeContent());
        task.setQuestion("");
        task.setThought("");

//        CompletableFuture<WorkflowExecutionDto> dto = runAgent(task , preTaskList);
//        sendSSE(task , dto.getGenContent()); // 生成完整任务给前端
//
//        log.debug("--->>>> dto = {}", JSONUtil.toJsonPrettyStr(dto));
//
//        MessageEntity message = messageService.selectByTraceBusId(dto.getTraceBusId()) ;
//        log.debug("--->>>> message = {}", JSONUtil.toJsonPrettyStr(message));

//        task.setOutputMessage(message);

        roleExecuteService.finishTask(task.getId());
    }


    @Override
    public CompletableFuture<WorkflowExecutionDto> runAgent(RoleTaskDto task, List<RoleTaskDto> preTaskList) {

        MessageTaskInfo taskInfo = new MessageTaskInfo();

        taskInfo.setRoleId(task.getWorkerRoleId());
        taskInfo.setChannelId(task.getSceneId());
        taskInfo.setSceneId(task.getSceneId());

        taskInfo.setText(task.getTaskDesc()); // 输入任务要求
        Map<String, Object> params = new HashMap<>();

        // 查询是否包含前一个任务内容
        if (StringUtils.isNotEmpty(task.getPreRoleId())) {
            // 查询出上一条Message

            RoleTaskDto preTask = null;
            if (preTaskList != null && !preTaskList.isEmpty()) {
                for (RoleTaskDto t : preTaskList) {
                    if (task.getPreRoleId().equals(t.getWorkerRoleId() + "")) {
                        preTask = t;
                        break;
                    }
                }
            }

            if (preTask != null && preTask.getOutputMessage() != null) {
                taskInfo.setPreBusinessId(preTask.getOutputMessage().getId() + "");
            }

        }

        params.put("question", task.getQuestion());
        params.put("preTaskList", preTaskList);
        params.put("workerGoal", task.getTaskDesc());
        params.put("thought", task.getThought());
        params.put("knowledgeContent", task.getKnowledgeContent());

        taskInfo.setRoleType("agent");

        taskInfo.setParams(params);

        return roleService.runRoleAgent(taskInfo);
    }


    @Override
    public void sendSSE(RoleTaskDto task, String msg) throws IOException {
        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
        message.setChatText(msg);
        message.setRoleType("agent");
        message.setLoading(false);
        sseService.send(String.valueOf(task.getSceneId()), message);
    }

}

