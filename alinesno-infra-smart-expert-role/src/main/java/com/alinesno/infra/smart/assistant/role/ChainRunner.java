package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowNodeExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.alinesno.infra.smart.assistant.role.common.ExecutionStatus;
import com.alinesno.infra.smart.assistant.role.common.WorkflowStatusEnum;
import com.alinesno.infra.smart.assistant.role.context.RoleChainContext;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.entity.CmpStep;
import com.yomahub.liteflow.slot.DefaultContext;
import com.yomahub.liteflow.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("chainRunner")
public class ChainRunner extends PlatformExpert implements IBaseExpertService {

    /**
     * 运行方法
     * @param params
     * @param chainName
     * @param chainId
     */
    @Async
    @Override
    public void processExpert(Map<String, Object> params, String chainName , Long chainId , NoticeDto noticeDto){

        if(params == null){
            params = new HashMap<>() ;
        }

        Long workflowId = startFlowRecord(chainName , chainId) ; // 开始执行工作流

        // 定义上下文
        RoleChainContext roleContext = new RoleChainContext() ;
        roleContext.setNoticeDto(noticeDto);

        roleContext.setBusinessId(params.get(AssistantConstants.BUSINESS_ID)+"");
        roleContext.setAssistantYamlContent(params.get(AssistantConstants.BUSINESS_ASSISTANT_YAML_CONTENT)+"");

        // 执行工作流
        LiteflowResponse response = flowExecutor.execute2Resp(chainName, params , roleContext);
        saveFlowRecord(response , workflowId) ;

        DefaultContext context = response.getFirstContextBean();

        log.debug("----->>>>>>>>>>> " + JsonUtil.toJsonString(context.getData("student")));
        log.debug("----->>>>>>>>>>> " + JsonUtil.toJsonString(context.getData("s2")));

        if (response.isSuccess()){
            log.info("执行成功");
        }else{
            log.info("执行失败");
        }
    }

    /**
     * 保存执行记录
     * @param response
     */
    protected void saveFlowRecord(LiteflowResponse response, Long workflowId) {

        boolean isSuccess = response.isSuccess() ;
        String stepWithTime = response.getExecuteStepStrWithTime() ;
        Long totalTimeSeconds = 0L ;

        // 获取到执行步骤
        Map<String , List<CmpStep>> executeSteps = response.getExecuteSteps() ;

        List<WorkflowNodeExecutionEntity> listNode = new ArrayList<>() ;

        for(String key : executeSteps.keySet()){

            for(CmpStep step : executeSteps.get(key)){
                log.debug("---->>>>>>>>>>>>>>>> key = {} , step = {}" , key , step);

                String nodeId = step.getNodeId() ;
                Long timeSpent = step.getTimeSpent() ;
                String setType = step.getStepType().name() ;
                String nodeName = step.getNodeName() ;
                boolean b = step.isSuccess() ;

                totalTimeSeconds += timeSpent ;

                // 使用日志输出变量信息
                log.info("NodeId: {}, TimeSpent: {}, SetType: {}, NodeName: {}", nodeId, timeSpent, setType, nodeName);
                WorkflowNodeExecutionEntity wne = new WorkflowNodeExecutionEntity() ;

                if(step.getException() != null){
                    wne.setLogInfo(step.getException().getMessage());
                }
                wne.setEndTime(LocalDateTime.now());
                wne.setNodeName(nodeName);
                wne.setTimeSpent(timeSpent) ;
                wne.setWorkflowExecutionId(workflowId);
                wne.setStatus(b? ExecutionStatus.SUCCESS.getStatus():ExecutionStatus.FAIL.getStatus());

                listNode.add(wne) ;
            }
        }

        // 保存和更新流程运行状态
        WorkflowExecutionEntity we = workflowExecutionService.getById(workflowId) ;

        we.setWorkflowStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        we.setEndTime(LocalDateTime.now());
        we.setUsageTimeSeconds(totalTimeSeconds) ;
        we.setStatus(isSuccess? ExecutionStatus.SUCCESS.getStatus():ExecutionStatus.FAIL.getStatus());
        we.setLogInfo(response.getMessage());
        we.setStepWithTime(stepWithTime);

        workflowExecutionService.update(we);

        workflowNodeExecutionService.saveBatch(listNode) ;

    }

    /**
     * 开始执行记录
     *
     * @param chainName
     * @param chainId
     * @return
     */
    protected Long startFlowRecord(String chainName , Long chainId){

        WorkflowExecutionEntity we = new WorkflowExecutionEntity() ;

        int buildNumber = workflowExecutionService.getByChainName(chainName) ;

        we.setWorkflowId(chainId);
        we.setBuildNumber(buildNumber+1);
        we.setWorkflowName(chainName);
        we.setStartTime(LocalDateTime.now());
        we.setWorkflowStatus(WorkflowStatusEnum.IN_PROGRESS.getStatus());

        workflowExecutionService.save(we);

        return we.getId() ;
    }

}
