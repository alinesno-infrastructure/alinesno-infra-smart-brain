package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskValidateDto;
import com.alinesno.infra.smart.assistant.api.task.TaskInfoBean;
import com.alinesno.infra.smart.assistant.entity.ProcessDefinitionEntity;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;
import com.alinesno.infra.smart.assistant.mapper.ProcessDefinitionMapper;
import com.alinesno.infra.smart.assistant.service.IProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProcessDefinitionServiceImpl extends IBaseServiceImpl<ProcessDefinitionEntity, ProcessDefinitionMapper> implements IProcessDefinitionService {
    @Override
    public void runProcess(TaskInfoBean task, List<TaskDefinitionEntity> taskDefinitionList) {

    }

    @Override
    public long commitProcessDefinition(ProcessDefinitionDto dto) {
        return 0;
    }

    @Override
    public void runProcessTask(ProcessTaskValidateDto dto) {

    }

    @Override
    public List<ProcessDefinitionEntity> queryRecentlyProcess(int count) {
        return null;
    }

    @Override
    public void updateProcessDefinition(ProcessDefinitionDto dto) {

    }

//    @Autowired
//    private IQuartzSchedulerService distSchedulerService;
//
//    @Autowired
//    private IEnvironmentService environmentService;
//
//    @Autowired
//    private ITaskDefinitionService taskDefinitionService;
//
//    @Autowired
//    private IProcessInstanceService processInstanceService;
//
//    @Autowired
//    private ITaskInstanceService taskInstanceService;
//
//    @Autowired
//    private ISecretsService secretsService;
//
//    @Autowired
//    protected CloudStorageConsumer storageConsumer;
//
//    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
//    protected String localPath;
//
//    @Autowired
//    private IResourceService resourceService;
//
//
//    @Value("${alinesno.data.scheduler.workspacePath:#{systemProperties['java.io.tmpdir']}}")
//    private String workspacePath;
//
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)  // 此方法不需要开启事务
//    @Override
//    public void runProcess(TaskInfoBean task, List<TaskDefinitionEntity> taskDefinition) {
//
//        // 任务实例运行
//        ProcessDefinitionEntity process = task.getProcess();
//        process.setRunCount(process.getRunCount() + 1);
//        updateById(process);
//
//        // 执行运行实例及任务
//        runProcessInstance(task, taskDefinition, process);
//
//        // 更新流程运行成功次数
//        process.setSuccessCount(process.getSuccessCount() + 1);
//        updateById(process);
//    }
//
//    /**
//     * 运行流程实例
//     *
//     * @param task
//     * @param taskDefinition
//     * @param process
//     */
//    @SneakyThrows
//    private void runProcessInstance(TaskInfoBean task, List<TaskDefinitionEntity> taskDefinition, ProcessDefinitionEntity process) {
//        IExecutorService executorService;
//
//        // 记录任务流程实例开始
//        long count = processInstanceService.count(new LambdaQueryWrapper<ProcessInstanceEntity>().eq(ProcessInstanceEntity::getProcessId, process.getId())) + 1;
//        ProcessInstanceEntity processInstance = ProcessUtils.fromTaskToProcessInstance(process, count);
//        processInstance.setProcessId(process.getId());
//        processInstance.setState(ProcessStatusEnums.RUNNING.getCode());
//
//        // 生成实例工作空间
//        String instanceWorkspace = process.getId() + File.separator + count;
//        FileUtils.forceMkdir(new File(workspacePath + File.separator + instanceWorkspace)); // 创建工作空间
//        processInstance.setWorkspace(instanceWorkspace);
//
//        processInstanceService.save(processInstance);
//        task.setWorkspace(instanceWorkspace);
//
//        boolean isFailTask = false;
//
//        // 任务开始更新状态
//        for (TaskDefinitionEntity t : taskDefinition) {
//            task.setTask(t);
//
//            // 记录任务实例开始
//            TaskInstanceEntity taskInstance = ProcessUtils.fromTaskToTaskInstance(process, t, processInstance.getId());
//            taskInstance.setState(ProcessStatusEnums.RUNNING.getCode());
//            taskInstance.setStartTime(new Date());
//            taskInstanceService.save(taskInstance);
//
//            String beanName = t.getTaskType() + "Executor";
//            log.debug("TaskExecutor BeanName:{}", beanName);
//
//            executorService = SpringUtils.getBean(beanName);
//            try {
//                // 配置任务参数
//                configTaskParams(task, executorService);
//
//                executorService.execute(task);
//
//                // 更新任务实例结束
//                taskInstance.setState(ProcessStatusEnums.END.getCode());
//                taskInstance.setEndTime(new Date());
//                taskInstanceService.update(taskInstance);
//
//            } catch (Exception e) {
//
//                log.error("任务执行异常：", e);
//
//                taskInstance.setState(ProcessStatusEnums.FAIL.getCode());
//                taskInstance.setEndTime(new Date());
//                taskInstance.setErrorMsg(e.getMessage());
//                taskInstanceService.update(taskInstance);
//
//                isFailTask = true;
//
//                // 如果异常中断则直接跳出，否则继续执行
//                if (!t.isContinueIgnore()) {
//                    break;
//                }
//            }
//        }
//
//        if (isFailTask) {
//            processInstance.setState(ProcessStatusEnums.FAIL.getCode());
//        } else {
//            // 任务流程结束，更新任务实例
//            processInstance.setState(ProcessStatusEnums.END.getCode());
//        }
//
//        processInstance.setEndTime(new Date());
//        processInstanceService.update(processInstance);
//    }
//
//    /**
//     * 配置任务参数
//     *
//     * @param task
//     * @param executorService
//     */
//    private void configTaskParams(TaskInfoBean task, IExecutorService executorService) {
//
//        // 配置参数
//        ParamsDto paramsDto = JSONObject.parseObject(task.getTask().getTaskParams(), ParamsDto.class);
//        if(paramsDto == null){
//            paramsDto = new ParamsDto();
//        }
//        executorService.setParams(paramsDto);
//
//        // 配置空间
//        String workspace = new File(workspacePath, task.getWorkspace()).getAbsolutePath();
//        executorService.setWorkspace(workspace);
//
//        // 配置数据库源
//        try{
//            IDataSourceService dataSourceService = SpringUtils.getBean(IDataSourceService.class);
//            executorService.setDataSource(dataSourceService.getDataSource(paramsDto.getDataSourceId()));
//        }catch (Exception e){
//            log.warn("没有配置数据源：{}",e.getMessage());
//        }
//
//        EnvironmentEntity environment = environmentService.getById(task.getProcess().getEnvId());
//        if(environment == null){
//            environment = environmentService.getDefaultEnv() ;
//        }
//        executorService.setEnvironment(environment);
//
//        // 配置资源
//        List<String> resources = downloadResource(paramsDto.getResourceId(), workspace);
//        executorService.setResource(resources);
//
//        // 配置任务环境
//        executorService.setTaskInfoBean(task);
//
//        // 添加自定义密钥值
//        Map<String , String> secretsMap = secretsService.secretMap() ;
//        executorService.setSecretMap(secretsMap);
//
//        // 替换环境变量
//        executorService.replaceGlobalParams(environment ,
//                task.getProcess().getGlobalParams() ,
//                paramsDto.getCustomParams());
//    }
//
//    /**
//     * 下载文件资源并返回文件名称
//     *
//     * @param resourceIds
//     * @return
//     */
//    @SneakyThrows
//    protected List<String> downloadResource(List<String> resourceIds, String workspace) {
//
//
//        List<String> fileNameList = new ArrayList<>();
//        if(resourceIds == null || resourceIds.isEmpty()){
//            return fileNameList ;
//        }
//
//        List<ResourceEntity> resourceEntities = resourceService.listByIds(resourceIds);
//
//        for (ResourceEntity resource : resourceEntities) {
//            byte[] bytes = storageConsumer.download(String.valueOf(resource.getStorageId()), progress -> log.debug("下载进度：" + progress.getRate()));
//
//            File targetFile = new File(workspace, resource.getFileName());
//            FileUtils.writeByteArrayToFile(targetFile, bytes);
//
//            fileNameList.add(targetFile.getAbsolutePath());
//        }
//
//        return fileNameList;
//    }
//
//
//    @Override
//    public long commitProcessDefinition(ProcessDefinitionDto dto) {
//        long projectId = dto.getProjectId();
//
//        ProcessDefinitionEntity processDefinition = ProcessUtils.fromDtoToEntity(dto);
//        processDefinition.setOnline(false);
//        this.save(processDefinition);
//
//        long processId = processDefinition.getId();
//        List<TaskDefinitionEntity> taskDefinitionList = ProcessUtils.fromDtoToTaskInstance(dto, processId, projectId);
//        taskDefinitionService.saveBatch(taskDefinitionList);
//
//        log.debug("saveProcessDefinition:{}", processId);
//
//        // 生成job任务
//        distSchedulerService.addJob(processId + "", dto.getContext().getCronExpression());
//
//        return processId;
//    }
//
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)  // 此方法不需要开启事务
//    @SneakyThrows
//    @Override
//    public void runProcessTask(ProcessTaskValidateDto dto) {
//
//        ParamsDto params = dto.getTaskParams() ;
//        String type = ExecutorTypeEnums.fromType(Integer.parseInt(dto.getTaskType())).getCode();
//
//        String beanName = type + "Executor";
//        log.debug("TaskExecutor BeanName:{}", beanName);
//        IExecutorService executorService = SpringUtils.getBean(beanName);;
//
//        TaskInfoBean task = new TaskInfoBean() ;
//        TaskDefinitionEntity taskDefinition = new TaskDefinitionEntity() ;
//
//        taskDefinition.setTaskParams(JSONObject.toJSONString(params));
//        task.setTask(taskDefinition);
//
//        String fileName = IdUtil.getSnowflakeNextIdStr() ;
//        FileUtils.forceMkdir(new File(workspacePath, fileName)); // 创建工作空间
//        task.setWorkspace(fileName);
//
//        log.debug("workspace = {}" , fileName);
//
//        ProcessDefinitionEntity process = new ProcessDefinitionEntity() ;
//        process.setEnvId(dto.getContext().getEnvId()) ;
//        process.setGlobalParams(JSONObject.toJSONString((dto.getContext().getGlobalParams()))) ;
//        task.setProcess(process) ;
//
//        // 配置任务参数
//        configTaskParams(task, executorService);
//
//        // 执行任务
//        executorService.execute(task);
//
//        FileUtils.forceDeleteOnExit(new File(workspacePath, task.getWorkspace()));
//    }
//
//    @Override
//    public List<ProcessDefinitionEntity> queryRecentlyProcess(int count) {
//
//        LambdaQueryWrapper<ProcessDefinitionEntity> queryWrapper = Wrappers.lambdaQuery();
//        queryWrapper
//                .orderByDesc(ProcessDefinitionEntity::getAddTime)
//                .last("limit " + count);
//
//        return this.list(queryWrapper);
//    }
//
//    /**
//     * 更新流程定义信息
//     *
//     * 此方法用于根据传入的流程定义DTO更新流程定义的信息它允许修改流程定义的各种属性，
//     * 比如流程名称、描述等这个方法不对更新操作的成功与否进行返回，因此不包含返回值说明
//     *
//     * @param dto 包含要更新的流程定义信息的DTO（数据传输对象）不能为空这个DTO包含了所有
//     *            需要更新的流程定义的信息，如流程定义ID、新流程名称、新描述等
//     */
//    @Override
//    public void updateProcessDefinition(ProcessDefinitionDto dto) {
//
//        ProcessContextDto context = dto.getContext() ;
//        Assert.notNull(context, "流程定义上下文不能为空");
//
//        long processId = context.getId();
//        ProcessDefinitionEntity processDefinition = ProcessUtils.fromDtoToEntity(dto);
//        processDefinition.setOnline(false);
//        processDefinition.setId(processId);
//
//        this.updateById(processDefinition);
//
//        List<ProcessTaskDto> taskFlow = dto.getTaskFlow() ;
//        Assert.isTrue(taskFlow.size() > 1 , "流程定义为空,请定义流程.");
//
//        long projectId = dto.getProjectId();
//
//        List<TaskDefinitionEntity> taskDefinitionList = ProcessUtils.fromDtoToTaskInstance(dto, processId, projectId);
//        taskDefinitionList.forEach(t -> t.setProcessId(processId));
//
//        taskDefinitionService.updateBatchById(taskDefinitionList);
//
//    }

}

