package com.alinesno.infra.smart.assistant.utils;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.api.task.ParamsDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskDto;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;
import com.alinesno.infra.smart.assistant.enums.ExecutorTypeEnums;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

    /**
     * 将流程定义转换为任务实体
     * @param dto
     * @param roleId
     * @return
     */
    public static List<TaskDefinitionEntity> fromDtoToTaskInstance(ProcessDefinitionDto dto, long roleId , long projectId) {

        List<TaskDefinitionEntity> taskDefinitionList = new ArrayList<>() ;

        List<ProcessTaskDto> taskFlow = dto.getTaskFlow();

        int orderNum = 1 ;
        for (ProcessTaskDto task : taskFlow) {

            TaskDefinitionEntity entity = new TaskDefinitionEntity();
            ParamsDto params = task.getParams();

            String name = task.getName() ;
            String desc = task.getDescription() ;
            int retryCount = 0 ;
            String taskParams = null;
            String resourceId = null ;
            if(params != null){
                if(StringUtils.isNoneBlank(params.getName())){
                    name = params.getName() ;
                }
                if(StringUtils.isNoneBlank(params.getDesc())){
                    desc = params.getDesc() ;
                }
                retryCount = params.getRetryCount() ;
                taskParams = JSONObject.toJSONString(params) ;
                resourceId = JSONObject.toJSONString(params.getResourceId()) ;
            }

            entity.setCode(task.getId());
            entity.setRoleId(roleId);
            entity.setName(name) ;
            entity.setProjectId(projectId);
            entity.setDescription(desc) ;
            entity.setTaskType(ExecutorTypeEnums.fromType(task.getType()).getCode());
            entity.setTaskParams(taskParams) ;
            entity.setFailRetryTimes(retryCount) ;
            entity.setResourceId(resourceId) ;
            entity.setOrderNum(orderNum);
            entity.setAttr(JSONObject.toJSONString(task.getAttr()));

            taskDefinitionList.add(entity);
            orderNum ++ ;
        }

        return taskDefinitionList ;
    }
}
