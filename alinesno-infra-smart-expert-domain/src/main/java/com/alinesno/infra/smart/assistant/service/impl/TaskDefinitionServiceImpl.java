package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskValidateDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.mapper.TaskDefinitionMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ITaskDefinitionService;
import com.alinesno.infra.smart.assistant.utils.ProcessUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @version 1.0.0
 * @autor luoxiaodong
 */
@Slf4j
@Service
public class TaskDefinitionServiceImpl extends IBaseServiceImpl<TaskDefinitionEntity, TaskDefinitionMapper> implements ITaskDefinitionService {

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Override
    public long commitProcessDefinition(ProcessDefinitionDto dto) {

        long projectId = dto.getProjectId();

        // 删除之前所有的流程脚本，重新保存
        LambdaUpdateWrapper<TaskDefinitionEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TaskDefinitionEntity::getRoleId, dto.getRoleId());
        remove(wrapper) ;

        long roleId = dto.getRoleId();
        List<TaskDefinitionEntity> taskDefinitionList = ProcessUtils.fromDtoToTaskInstance(dto, roleId, projectId);
        saveBatch(taskDefinitionList);

        log.debug("saveRoleId:{}", roleId);

        // 更新产品服务调用方式
        IndustryRoleEntity industryRole = industryRoleService.getById(roleId) ;
        industryRole.setChainId(AssistantConstants.PREFIX_ASSISTANT_FLOW);

        return roleId;
    }

    @Override
    public void runProcessTask(ProcessTaskValidateDto dto) {

    }

    @Override
    public long validateProcessDefinition(ProcessDefinitionDto dto) {
        return 0;
    }

//    @Override
//    public void updateProcessDefinition(ProcessDefinitionDto dto) {
//
//    }

}
