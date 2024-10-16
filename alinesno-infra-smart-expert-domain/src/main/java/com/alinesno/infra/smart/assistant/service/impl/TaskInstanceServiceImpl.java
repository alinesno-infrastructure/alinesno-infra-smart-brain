package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.TaskInstanceEntity;
import com.alinesno.infra.smart.assistant.mapper.TaskInstanceMapper;
import com.alinesno.infra.smart.assistant.service.ITaskInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskInstanceServiceImpl extends IBaseServiceImpl<TaskInstanceEntity, TaskInstanceMapper> implements ITaskInstanceService {

}
