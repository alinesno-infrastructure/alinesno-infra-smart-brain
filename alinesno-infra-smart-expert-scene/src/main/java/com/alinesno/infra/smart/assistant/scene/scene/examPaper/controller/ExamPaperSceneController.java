package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.*;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.prompt.ExamPagerPromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools.ExamPagerFormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ExamQuestionTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
@RequestMapping("/api/infra/smart/assistant/scene/examPaper")
public class ExamPaperSceneController extends BaseController<ExamPagerSceneEntity, IExamPagerSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IExamPagerService examPagerService ;

    @Autowired
    private IExamPagerSceneService service ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ExamPagerFormatMessageTool examPagerFormatMessageTool;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(Long id , PermissionQuery query) {

        SceneEntity entity = sceneService.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        ExamPagerSceneDto dto = new ExamPagerSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        ExamPagerSceneEntity examPagerSceneEntity = service.getBySceneId(id, query);
        if(examPagerSceneEntity != null){

            dto.setQuestionGeneratorEngineer(examPagerSceneEntity.getQuestionGeneratorEngineer());
            dto.setQuestionGeneratorEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getQuestionGeneratorEngineer())));

            dto.setAnswerCheckerEngineer(examPagerSceneEntity.getAnswerCheckerEngineer());
            dto.setAnswerCheckerEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getAnswerCheckerEngineer())));

            dto.setPaperGeneratorEngineer(examPagerSceneEntity.getPaperGeneratorEngineer());
            dto.setPaperGeneratorEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getPaperGeneratorEngineer())));

        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/uploadAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult sceneData(@RequestPart("file") MultipartFile file) throws Exception {

        // 新生成的文件名称
        String fileName = file.getOriginalFilename() ;
        String fileSuffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile) ;
        return AjaxResult.success("上传成功" , r.getData()) ;
    }

    /**
     * 聊天提示内容
     * @param dto 请求参数
     * @param query 权限查询参数
     * @return 异步处理结果
     */
    @DataPermissionQuery
    @PostMapping("/chatPromptContent")
    public DeferredResult<AjaxResult> chatPromptContent(@RequestBody @Validated ExamPagerGeneratorDTO dto, PermissionQuery query) {
        // 设置超时时间（例如5分钟）
        DeferredResult<AjaxResult> deferredResult = new DeferredResult<>(600 * 1000L);

        // 在异步流程外部创建taskInfo
        MessageTaskInfo taskInfo = new MessageTaskInfo();

        // 构建异步处理流程
        CompletableFuture<Void> processFuture = CompletableFuture.runAsync(() -> {
                    // 1. 获取场景信息
                    ExamPagerSceneEntity entity = service.getBySceneId(dto.getSceneId(), query);
                    Long questionGeneratorEngineer = entity.getQuestionGeneratorEngineer();

                    // 2. 构建任务信息
                    BeanUtils.copyProperties(dto.toPowerMessageTaskInfo(), taskInfo);

                    // 3. 处理附件（如果有）
                    if (!CollectionUtils.isEmpty(dto.getAttachments())) {
                        List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
                        taskInfo.setAttachments(attachmentList);
                    }

                    // 4. 生成提示文本
                    String promptText = ExamPagerPromptHandle.generatorPrompt(
                            dto.getPromptText(),
                            dto.getDifficultyLevel(),
                            dto.getExamStructureItem()
                    );

                    // 5. 设置任务参数
                    taskInfo.setRoleId(questionGeneratorEngineer);
                    taskInfo.setChannelStreamId(dto.getChannelStreamId());
                    taskInfo.setChannelId(dto.getSceneId());
                    taskInfo.setSceneId(dto.getSceneId());
                    taskInfo.setText(promptText);
                }, chatThreadPool)
                .thenComposeAsync(v -> {
                    // 6. 异步调用角色服务获取内容
                    return roleService.runRoleAgent(taskInfo);
                }, chatThreadPool)
                .thenComposeAsync(genContent -> {
                    // 7. 异步格式化章节消息
                    return examPagerFormatMessageTool.handleChapterMessageAsync(genContent, taskInfo)
                            .thenApply(v -> genContent);
                }, chatThreadPool)
                .thenAcceptAsync(genContent -> {
                    // 8. 处理最终结果
                    if (genContent.getCodeContent() != null && !genContent.getCodeContent().isEmpty()) {
                        String codeContent = genContent.getCodeContent().get(0).getContent();
                        JSONArray dataObject = JSONArray.parseArray(codeContent);
                        deferredResult.setResult(AjaxResult.success("操作成功", dataObject));
                    } else {
                        deferredResult.setResult(AjaxResult.success("操作成功"));
                    }
                }, chatThreadPool)
                .exceptionally(ex -> {
                    // 异常处理
                    log.error("处理失败", ex);
                    deferredResult.setErrorResult(AjaxResult.error("处理失败: " + ex.getMessage()));
                    return null;
                });

        // 设置超时回调
        deferredResult.onTimeout(() -> {
            log.error("请求超时: {}", dto.getSceneId());
            deferredResult.setErrorResult(AjaxResult.error("请求超时，请稍后重试"));
        });

        return deferredResult;
    }

    /**
     * 获取所有题型信息的Map列表
     */
    @GetMapping("/getQuestionTypes")
    public AjaxResult getQuestionTypes() {
        List<Map<String, Object>> questionTypes = ExamQuestionTypeEnum.getAllTypesList();
        return AjaxResult.success("操作成功", questionTypes);
    }

    /**
     * 获取所有题型信息的Map列表
     */
    @GetMapping("/getQuestionCategoryList")
    public AjaxResult getQuestionCategoryList() {
        List<Map<String, Object>> questionTypes = ExamQuestionTypeEnum.getCategoryList();
        return AjaxResult.success("操作成功", questionTypes);
    }

    /**
     * 保存试卷问题
     */
    @DataPermissionSave
    @PostMapping("/savePagerQuestion")
    public AjaxResult savePagerQuestion(@RequestBody @Validated ExamPaperDTO dto) {
        log.info("dto = {}" , dto);
        Long pageId = examPagerService.savePager(dto);  // 保存试卷
        return AjaxResult.success("操作成功" , pageId) ;
    }

    /**
     * 更新试卷问题
     */
    @DataPermissionSave
    @PostMapping("/updatePagerQuestion")
    public AjaxResult updatePagerQuestion(@RequestBody @Validated ExamPaperUpdateDTO dto) {
        log.info("dto = {}" , dto);
        examPagerService.updatePager(dto);  // 删除试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query , String sceneId) {
        List<ExamPagerEntity> list = examPagerService.pagerListByPage(page, query , sceneId);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 获取试卷详情(getPagerDetail)
     */
    @GetMapping("/getPagerDetail")
    public AjaxResult getPagerDetail(Long id) {
        ExamPaperDTO entity = examPagerService.getPagerDetail(id);
        return AjaxResult.success("操作成功." ,entity);
    }

}