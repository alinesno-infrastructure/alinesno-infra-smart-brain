package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record.DeepSearchTaskRecordManager;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DeepsearchSummaryMessageTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class BaseHandler {

    private String goal ;

    private StreamMessagePublisher streamMessagePublisher;

    private IndustryRoleEntity role;

    private DeepSearchFlow deepSearchFlow;

    private StepEventUtil stepEventUtil;

    private MessageTaskInfo taskInfo;

    private IToolService toolService;

    private Map<String , String> secretKey ; // 组织配置密钥

    private List<ToolDto> tools ;  // 工具列表

    private String datasetKnowledgeDocument ; // 知识库文档内容

    private ReActServiceTool reActServiceTool  ;

    private DeepsearchSummaryMessageTool summaryMessageTool ;

    private UploadData uploadData  ; // 上传数据

    private ContextEngineeringData contextEngineeringData ; // 上下文工程

    private DeepSearchTaskEntity deepSearchTask ; // 深度检索任务

    private DeepSearchTaskRecordManager statusManager; // 状态管理

}
