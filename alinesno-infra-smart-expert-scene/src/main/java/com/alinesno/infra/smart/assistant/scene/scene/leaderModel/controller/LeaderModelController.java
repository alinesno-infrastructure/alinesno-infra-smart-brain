package com.alinesno.infra.smart.assistant.scene.scene.leaderModel.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerAccountDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleTaskUtils;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.IRoleExecuteService;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.IWorkerTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.scene.dto.LeaderPlanDto;
import com.alinesno.infra.smart.scene.dto.RoleTaskDto;
import com.alinesno.infra.smart.scene.entity.RoleExecuteEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatus;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.AgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/leaderModel/")
public class LeaderModelController extends BaseController<RoleExecuteEntity, IRoleExecuteService> {

    @Autowired
    private IRoleExecuteService service;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IMessageService messageService;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private IWorkerTaskService workerTaskService ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 生成计划
     *
     * @return
     */
    @SneakyThrows
    @PostMapping("/leaderPlan")
    public AjaxResult leaderPlan(@RequestBody @Validated LeaderPlanDto dto) {

        long sceneId = dto.getSceneId();

        SceneEntity sceneEntity = sceneService.getById(sceneId) ;
        IndustryRoleEntity leaderRole = roleService.getById(sceneEntity.getLeaderRole());

        // 发送Event通知给Worker
        ManagerAccountDto currentAccount = CurrentAccountJwt.get() ;

        IndustryRoleEntity currentAccountRole = new IndustryRoleEntity() ;
        currentAccountRole.setRoleName(currentAccount.getName());
        currentAccountRole.setRoleAvatar(leaderRole.getRoleAvatar());
        currentAccountRole.setId(currentAccount.getId());

        ChatMessageDto message = AgentUtils.getChatMessageDto(currentAccountRole, IdUtil.getSnowflakeNextId());
        message.setChatText(dto.getMessage());
        message.setRoleType("person");
        message.setLoading(false);
        sseService.send(String.valueOf(sceneId), message);

        // 发送Event通知给Worker
        message = AgentUtils.getChatMessageDto(leaderRole, IdUtil.getSnowflakeNextId());
        message.setChatText("收到任务在计划中.");
        message.setLoading(false);
        sseService.send(String.valueOf(sceneId), message);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(leaderRole.getId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(dto.getMessage());

        Map<String, Object> params = new HashMap<>();

        List<IndustryRoleEntity> workerRoleList = RoleUtils.getRoleEntity(roleService , sceneEntity.getWorkerRole()) ;
        List<JSONObject> paramsList = workerRoleList.stream().map(item -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("roleId", item.getId());
            jsonObject.put("roleName", item.getRoleName());
            jsonObject.put("roleAvatar", item.getRoleAvatar());
            return jsonObject;
        }).toList() ;

        params.put("label1", JSONArray.toJSONString(paramsList));
        taskInfo.setParams(params);

        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

//        MessageEntity messageCallback = messageService.selectByTraceBusId(genContent.getTraceBusId()) ;
//
//        genContent.setGenContent(messageCallback.getContent());
//        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(genContent.getGenContent());
//        genContent.setCodeContent(codeContentList);
//
//        log.debug("messageCallback = {}" , messageCallback);
//        message.setChatText(genContent.getGenContent());
//        sseService.send(String.valueOf(sceneId), message);
//
//        // 解析得到代码内容
//        List<RoleTaskDto> tasks = new ArrayList<>();
//
//        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//            String codeContent = genContent.getCodeContent().get(0).getContent() ;
//            // 验证是否可以正常解析json
//            try{
//                tasks = JSON.parseArray(codeContent, RoleTaskDto.class);
//                log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(tasks));
//
//                for(RoleTaskDto task : tasks){  // 设置任务目标
//                    task.setTaskGoal(genContent.getGenContent());
//                    task.setLeaderRoleId(leaderRole.getId());
//                    task.setSceneId(sceneId);
//                }
//
//                // 更新保存到数据库中
//                service.saveNewTasks(sceneId , tasks);
//            }catch (Exception e){
//                log.error("出现异常",e);
//                message.setChatText("生成人员安排格式不正确，请点击重新生成，异常:" + e.getMessage());
//                sseService.send(String.valueOf(sceneId), message);
//                throw new RpcServiceRuntimeException("生成人员安排格式不正确，请点击重新生成.") ;
//            }
//        }

        // 优化处理流程
        // executeTask(tasks, message, sceneEntity, sceneId, leaderRole , genContent.getGenContent());

//        return AjaxResult.success("操作成功." , tasks);

        return AjaxResult.success("操作成功.") ;
    }

    /**
     * 执行任务
     * @param task
     * @param uId  前端传递过程，每次执行任务为唯一值,规避获取到前置任务不正确。
     * @return
     * @throws Exception
     */
    @PostMapping("/executeSceneTask")
    public AjaxResult executeSceneTask(@RequestBody RoleTaskDto task, @RequestParam String uId) throws Exception {

        List<RoleTaskDto> preTaskList = RoleTaskUtils.getPreTaskList(uId) ;

        long sceneId = task.getSceneId();
        long leaderRoleId = task.getLeaderRoleId();

        IndustryRoleEntity worker = roleService.getById(task.getWorkerRoleId());
        IndustryRoleEntity leaderRole = roleService.getById(leaderRoleId);

        if(worker != null){
            task.setLeaderRoleId(leaderRoleId);

            task.setSceneId(sceneId);
            task.setIsFinished(TaskStatus.NOT_STARTED.getCode());

            task.setTaskGoal(task.getTaskGoal()); // 设置目标
            task.setLeaderRole(leaderRole);
            task.setWorkerRole(worker);

            ChatMessageDto workerMessage = AgentUtils.getChatMessageDto(leaderRole, IdUtil.getSnowflakeNextId());
            workerMessage.setChatText("分配任务给:" + task.getWorkerRole().getRoleName());
            workerMessage.setLoading(false);
            sseService.send(String.valueOf(sceneId), workerMessage);

            workerTaskService.executeTask(task , preTaskList);
            RoleTaskUtils.add(task , uId) ;

            log.debug("task = \r\n{}" , task);
        }else{
            log.warn("未找到对应的角色信息:{}" , task.getWorkerRoleId());
        }

        return AjaxResult.success("操作成功");
    }

    @Override
    public IRoleExecuteService getFeign() {
        return this.service;
    }
}