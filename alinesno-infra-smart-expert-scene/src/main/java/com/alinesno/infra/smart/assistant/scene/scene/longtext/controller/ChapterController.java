package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TaskProgressDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTemplateService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.FormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.ChapterGenFormDto;
import com.alinesno.infra.smart.scene.dto.ChatContentDto;
import com.alinesno.infra.smart.scene.dto.ParagraphProcessRequestDTO;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
@RequestMapping("/api/infra/smart/assistant/scene/longText/")
public class ChapterController extends BaseController<ChapterEntity, IChapterService> {

    @Autowired
    private IChapterService service;

    @Autowired
    private ILongTextSceneService longTextSceneService ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILongTextTemplateService longTextTemplateService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private FormatMessageTool formatMessageTool ;

    @Autowired
    private ILongTextTaskService longTextTaskService ;

    @Autowired
    private ISceneService sceneService ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
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
     * 接收JSON数据并保存到数据库
     * @param nodes JSON格式的章节列表
     * @return 操作结果
     */
    @DataPermissionQuery
    @PostMapping("/saveChapters")
    public AjaxResult saveChapters(@RequestBody List<TreeNodeDto> nodes,
                                   @RequestParam("sceneId") long sceneId ,
                                   @RequestParam("taskId") long taskId,
                                   PermissionQuery query) {

        if(nodes == null || nodes.isEmpty()){
            return error("参数错误") ;
        }

        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;
        LongTextTaskEntity longTextTaskEntity = longTextTaskService.getById(taskId) ;
        service.saveChaptersWithHierarchy(nodes,
                null,
                1 ,
                sceneId ,
                longTextSceneEntity.getId() ,
                query ,
                longTextTaskEntity);

        return ok() ;
    }

    /**
     * 异步生成章节内容
     */
    @Deprecated
    @PostMapping("/chatRoleSync")
    @Async
    public CompletableFuture<AjaxResult> chatRoleSyncAsync(@RequestBody @Validated ChapterGenFormDto dto, PermissionQuery query) {
        try {
            log.info("开始异步生成章节内容，chapterId: {}, sceneId: {}", dto.getChapterId(), dto.getSceneId());

            ChapterEntity chapterEntity = service.getById(dto.getChapterId());
            if (chapterEntity == null) {
                return CompletableFuture.completedFuture(AjaxResult.error("章节不存在"));
            }

            Long roleId = chapterEntity.getChapterEditor();
            if (roleId == null) {
                return CompletableFuture.completedFuture(AjaxResult.success("此章节未指定编辑人员"));
            }

            LongTextTaskEntity longTextTaskEntity = longTextTaskService.getById(dto.getTaskId());
            LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(dto.getSceneId(), query);

            // 准备任务信息
            MessageTaskInfo taskInfo = new MessageTaskInfo();
            taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
            taskInfo.setRoleId(roleId);
            taskInfo.setChannelId(dto.getSceneId());
            taskInfo.setSceneId(dto.getSceneId());
            taskInfo.setQueryText(dto.getChapterTitle() + ":" + dto.getChapterDescription());

            // 处理附件
            if (StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
                List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
                        .map(Long::parseLong)
                        .toList();
                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
                taskInfo.setAttachments(attachments);
            }

            // 准备章节内容
            String allChapterContent = service.getAllChapterContent(dto.getSceneId());
            String chapterPromptContent = longTextTaskEntity.getTaskName();
            taskInfo.setText(FormatMessageTool.getChapterPrompt(
                    allChapterContent,
                    dto,
                    chapterPromptContent,
                    taskInfo
            ));

            // 执行角色生成
            CompletableFuture<WorkflowExecutionDto> genContent = roleService.runRoleAgent(taskInfo);
            log.info("章节内容生成完成，chapterId: {}", dto.getChapterId());

            // 更新章节内容
            chapterEntity.setContent(taskInfo.getFullContent());
            service.update(chapterEntity);

            return CompletableFuture.completedFuture(AjaxResult.success("生成成功", chapterEntity.getContent()));

        } catch (Exception e) {
            log.error("生成章节内容时发生异常", e);
            return CompletableFuture.completedFuture(
                    AjaxResult.error("生成失败: " + e.getMessage()));
        }
    }

    /**
     * 通过ID查询章节内容
     */
    @GetMapping("/getChapterContent")
    public AjaxResult getChapterContent(@RequestParam("chapterId") long chapterId) {
        ChapterEntity chapterEntity = service.getById(chapterId) ;
        return AjaxResult.success("操作成功" , chapterEntity.getContent()) ;
    }

//    /**
//     * 查询任务状态
//     */
//    @GetMapping("/getTaskStatus")
//    public AjaxResult getTaskStatus(@RequestParam("taskId") long taskId) {
//        LongTextTaskEntity longTextTaskEntity = longTextTaskService.getById(taskId) ;
//
//        String taskStatus = longTextTaskEntity.getTaskStatus() ;
//
//        if(taskStatus.equals(TaskStatusEnum.RUNNING.getCode()+"")){  // 运行中的任务
//
//        }
//
//        // 获取到当前需要编写的章节内容
//
//        return AjaxResult.success("操作成功" , longTextTaskEntity) ;
//    }

    /**
     * 更新章节内容
     */
    @PostMapping("/updateChapterContent")
    public AjaxResult updateChapterContent(@RequestBody @Validated ChatContentDto dto) {

        ChapterEntity chapterEntity = service.getById(dto.getId()) ;
        chapterEntity.setContent(dto.getContent());
        service.update(chapterEntity);

        return AjaxResult.success("操作成功") ;
    }

    /**
     * 对文章某段的处理，包括扩写、重写、润色。
     */
    @PostMapping("/processParagraph")
    public AjaxResult processParagraph(@RequestBody @Validated ParagraphProcessRequestDTO dto) {
        log.debug("ParagraphProcessRequestDTO = {}" , dto) ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(dto.getRoleId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(dto.getAction() +":"+dto.getRequirement()+ ":"+ dto.getModifyContent());
        taskInfo.setModify(true);
        taskInfo.setModifyPreBusinessId(false); ;

        Map<String , Object> paramMap = new java.util.HashMap<>(Map.of("modifyContent", dto.getModifyContent()));
        paramMap.put("action" , dto.getAction()) ;
        paramMap.put("requirement" , dto.getRequirement()) ;

        taskInfo.setParams(paramMap);

        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("chatRole = {}" , genContent);

//        return AjaxResult.success("操作成功" , genContent.getGenContent()) ;
        return AjaxResult.success("操作成功") ;
    }

//    /**
//     * 与角色交互，获取内容结果（异步版本）
//     */
//    @Deprecated
//    @PostMapping("/chatRole")
//    public AjaxResult chatRoleAsync(@RequestBody @Validated ChatRoleDto chatRole) {
//            log.info("开始处理chatRole异步请求，taskId: {}", chatRole.getTaskId());
//
//            Long taskId = chatRole.getTaskId();
//            LongTextTaskEntity longTextTaskEntity = longTextTaskService.getById(taskId);
//
//            // 构建任务信息
//            MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo();
//
//            // 处理附件（如果有）
//            if(StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
//                List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
//                        .map(Long::parseLong)
//                        .toList();
//                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
//                taskInfo.setAttachments(attachments);
//            }
//
//            taskInfo.setRoleId(chatRole.getRoleId());
//            taskInfo.setChannelStreamId(chatRole.getChannelStreamId());
//            taskInfo.setChannelId(chatRole.getSceneId());
//            taskInfo.setSceneId(chatRole.getSceneId());
//            taskInfo.setText(chatRole.getMessage());
//            taskInfo.setQueryText(chatRole.getMessage());
//
//            // 调用角色服务生成内容
//            WorkflowExecutionDto genContent = roleService.runRoleAgent(taskInfo);
//            log.info("角色服务调用完成，taskId: {}", taskId);
//
//            // 获取模板信息
//            LongTextTemplateEntity template = longTextTemplateService.getById(
//                    longTextTaskEntity.getSelectedTemplateId());
//
//            // 格式化内容
//            formatMessageTool.handleChapterMessage(genContent, taskInfo, template);
//
//            // 处理代码内容（如果有）
//            if(genContent.getCodeContent() != null && !genContent.getCodeContent().isEmpty()) {
//                String codeContent = genContent.getCodeContent().get(0).getContent();
//
//                // 验证JSON格式
//                try {
//                    JSONArray.parseArray(codeContent);
//                    List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
//                    log.debug("解析成功，章节节点数: {}", nodeDtos.size());
//                    return AjaxResult.success("操作成功", JSONArray.parseArray(codeContent));
//                } catch (Exception e) {
//                    log.error("JSON解析失败", e);
//                    throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成");
//                }
//            }
//
//            return AjaxResult.success("操作成功", genContent);
//
//    }

    /**
     * 查询任务处理进度
     */
    @GetMapping("/taskProgress/{taskId}")
    public AjaxResult getTaskProgress(@PathVariable Long taskId) {
        // 实现中可以从Redis或数据库查询进度
        TaskProgressDto progress = longTextTaskService.getProgress(taskId);
        return AjaxResult.success(progress);
    }

    /**
     * 获取章节的树结构
     * @return 章节的树结构
     */
    @SneakyThrows
    @DataPermissionQuery
    @GetMapping("/getChapterTree")
    public AjaxResult getChapterTree(
            @RequestParam("sceneId") Long sceneId ,
            @RequestParam("taskId") Long taskId,
            PermissionQuery query) {
        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;
        List<TreeNodeDto> tree = service.getChapterTree(sceneId ,longTextSceneEntity.getId(), taskId);
        return AjaxResult.success(tree);

    }

    /**
     * 获取到角色的编辑章节
     * @return
     */
    @GetMapping("/getChapterByRole")
    public AjaxResult getChapterByRole(@RequestParam("roleId") long roleId ,
                                       @RequestParam("sceneId") long sceneId) {

        LambdaQueryWrapper<ChapterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterEntity::getSceneId, sceneId);
        wrapper.eq(ChapterEntity::getChapterEditor, roleId);

        List<ChapterEntity> allChapters = service.list(wrapper);

        return AjaxResult.success("操作成功." , allChapters) ;
    }

    /**
     * 通过id获取到章节
     * @return
     */
    @GetMapping("/getChapterById")
    public AjaxResult getChapterById(@RequestParam("id") long id) {
        ChapterEntity chapterEntity = service.getById(id) ;
        return AjaxResult.success("操作成功" , chapterEntity) ;
    }

    @Override
    public IChapterService getFeign() {
        return this.service;
    }
}