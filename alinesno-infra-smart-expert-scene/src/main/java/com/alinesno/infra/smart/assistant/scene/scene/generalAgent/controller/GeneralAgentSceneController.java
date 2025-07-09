package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.scene.common.utils.WordToPdfConverter;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.GeneralAgentContentRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.GeneralAgentSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.GeneralAgentTaskDto;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentPlanService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.tools.GeneralAgentFormatMessageTool;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.FormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.*;
import com.alinesno.infra.smart.scene.entity.GeneralAgentPlanEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentSceneEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTaskEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据分析场景控制器
 */
@Slf4j
@Api(tags = "通用智能体场景")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/generalAgent/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class GeneralAgentSceneController extends BaseController<GeneralAgentSceneEntity, IGeneralAgentSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private IGeneralAgentSceneService service;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private IGeneralAgentPlanService generalAgentPlanService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IGeneralAgentTaskService generalAgentTaskService ;

    @Autowired
    private GeneralAgentFormatMessageTool formatMessageTool;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

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
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") long id , PermissionQuery query) {

        Assert.isTrue(id > 0, "参数不能为空");

        SceneEntity entity = sceneService.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        GeneralAgentSceneDto dto = new GeneralAgentSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        GeneralAgentSceneEntity dataAnalysisSceneEntity = service.getBySceneId(id, query);
        if(dataAnalysisSceneEntity != null){

            dto.setBusinessProcessorEngineer(dataAnalysisSceneEntity.getBusinessProcessorEngineer());
            dto.setBusinessExecuteEngineer(dataAnalysisSceneEntity.getBusinessExecuteEngineer());
            dto.setDataViewerEngineer(dataAnalysisSceneEntity.getDataViewerEngineer());

            dto.setGenStatus(dataAnalysisSceneEntity.getGenStatus());
            dto.setPromptContent(dataAnalysisSceneEntity.getPromptContent());

            dto.setBusinessProcessorEngineers(RoleUtils.getEditors(roleService , dataAnalysisSceneEntity.getBusinessProcessorEngineer())); // 查询出当前的数据分析编辑人员
            dto.setDataViewerEngineers(RoleUtils.getEditors(roleService, dataAnalysisSceneEntity.getDataViewerEngineer())); // 查询出当前的内容编辑人员
            dto.setBusinessExecuteEngineers(RoleUtils.getEditors(roleService, dataAnalysisSceneEntity.getBusinessExecuteEngineer()));

            dto.setChapterTree(generalAgentPlanService.getPlanTree(entity.getId() , dataAnalysisSceneEntity.getId())); // 数据分析树信息
        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 获取场景信息 getGeneralAgentScene
     */
    @DataPermissionQuery
    @GetMapping("/getGeneralAgentScene")
    public AjaxResult getGeneralAgentScene(
            @RequestParam("sceneId") Long sceneId,
            @RequestParam("taskId") Long taskId,
            PermissionQuery query) {

        Assert.isTrue( sceneId != null , "参数不能为空");
        Assert.isTrue( taskId != null , "参数不能为空");

        GeneralAgentTaskEntity taskEntity = generalAgentTaskService.getById(taskId);
        if (taskEntity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        GeneralAgentTaskDto dto = new GeneralAgentTaskDto();
        BeanUtils.copyProperties(taskEntity, dto);

        GeneralAgentSceneEntity generalAgentSceneEntity = service.getBySceneId(sceneId, query) ;
        if(generalAgentSceneEntity != null){

            dto.setBusinessProcessorEngineer(generalAgentSceneEntity.getBusinessProcessorEngineer());
            dto.setBusinessExecuteEngineer(generalAgentSceneEntity.getBusinessExecuteEngineer());
            dto.setDataViewerEngineer(generalAgentSceneEntity.getDataViewerEngineer());

            dto.setGenStatus(taskEntity.getGenStatus());
            dto.setPromptContent(taskEntity.getTaskName());

            dto.setBusinessProcessorEngineers(RoleUtils.getEditors(roleService , generalAgentSceneEntity.getBusinessProcessorEngineer())); // 查询出当前的数据分析编辑人员
            dto.setDataViewerEngineers(RoleUtils.getEditors(roleService, generalAgentSceneEntity.getDataViewerEngineer())); // 查询出当前的内容编辑人员
            dto.setBusinessExecuteEngineers(RoleUtils.getEditors(roleService, generalAgentSceneEntity.getBusinessExecuteEngineer()));

            dto.setChapterTree(generalAgentPlanService.getPlanTree(taskEntity.getId() , generalAgentSceneEntity.getId())); // 数据分析树信息
        }

        return AjaxResult.success("操作成功.", dto);
    }


    /**
     * 通过ID查询章节内容
     */
    @GetMapping("/getChapterContent")
    public AjaxResult getChapterContent(@RequestParam("chapterId") long chapterId) {
        GeneralAgentPlanEntity chapterEntity = generalAgentPlanService.getById(chapterId) ;
        return AjaxResult.success("操作成功" , chapterEntity.getContent()) ;
    }

//    /**
//     * 与角色交互，获取到内容结果
//     */
//    @DataPermissionSave
//    @SneakyThrows
//    @PostMapping("/chatRole")
//    public AjaxResult chatRole(@RequestBody @Validated ChatRoleDto chatRole) {
//
//        MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo() ; // new MessageTaskInfo() ;
//
//        GeneralAgentTaskEntity taskEntity = generalAgentTaskService.getById(chatRole.getTaskId()) ;
//        handleAttachment(taskEntity, taskInfo);
//
//        taskInfo.setRoleId(chatRole.getRoleId());
//        taskInfo.setChannelStreamId(chatRole.getChannelStreamId());
//        taskInfo.setChannelId(chatRole.getSceneId());
//        taskInfo.setSceneId(chatRole.getSceneId());
//        taskInfo.setText(chatRole.getMessage());
//
//        // 优先获取到结果内容
//        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
//
//       // 获取到格式化的内容
//       formatMessageTool.handleChapterMessage(genContent , taskInfo);
//
//        // 解析得到代码内容
//        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//            String codeContent = genContent.getCodeContent().get(0).getContent() ;
//            JSONArray dataObject = JSONArray.parseArray(codeContent) ;
//
//            // 验证是否可以正常解析json
//            try{
//                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
//                log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));
//            }catch (Exception e){
//                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成.") ;
//            }
//
//            return AjaxResult.success("操作成功" , dataObject) ;
//        }
//
//        return AjaxResult.success("操作成功" , genContent) ;
//    }

    private void handleAttachment(GeneralAgentTaskEntity taskEntity, MessageTaskInfo taskInfo) {
        // 引用附件不为空，则引入和解析附件
        if(!StringUtils.isEmpty(taskEntity.getAttachments())){
            // 以逗号进行切分
            List<Long> attachments = Arrays.stream(taskEntity.getAttachments().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(attachments);
            taskInfo.setAttachments(attachmentList);
        }
    }

    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterPromptContent")
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated GeneralAgentContentRequestDto dto , PermissionQuery query) {

        GeneralAgentSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setPromptContent(dto.getPromptContent());

        sceneEntity.setId(null);
        sceneEntity.setGenStatus(0);

        service.save(sceneEntity);  // 保存一个新的场景?是否需要调整，暂时如此

        return AjaxResult.success("操作成功" , sceneEntity.getId()) ;
    }

    /**
     * 更新生成状态
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/updateSceneGenStatus")
    public AjaxResult updateSceneGenStatus(@RequestParam("sceneId") long sceneId ,
                                           @RequestParam("genStatus") int genStatus,
                                           PermissionQuery query) {

        GeneralAgentSceneEntity sceneEntity = service.getBySceneId(sceneId , query) ;
        sceneEntity.setGenStatus(genStatus);

        service.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 接收JSON数据并保存到数据库
     * @param nodes JSON格式的章节列表
     * @return 操作结果
     */
    @DataPermissionQuery
    @PostMapping("/saveDataPlan")
    public AjaxResult saveChapters(@RequestBody List<TreeNodeDto> nodes,
                                   @RequestParam("sceneId") long sceneId ,
                                   @RequestParam("taskId") long taskId ,
                                   PermissionQuery query) {

        if(nodes == null || nodes.isEmpty()){
            return error("参数错误") ;
        }

        GeneralAgentSceneEntity generalAgentSceneEntity = service.getBySceneId(sceneId , query) ;
        generalAgentPlanService.saveChaptersWithHierarchy(nodes, null,
                1 ,
                sceneId ,
                taskId ,
                generalAgentSceneEntity.getId());

        return ok() ;
    }

    /**
     * 分配智能助手到每个章节内容
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/dispatchAgent")
    public AjaxResult dispatchAgent(
            @RequestParam Long sceneId ,
            @RequestParam Long taskId,
            PermissionQuery query) {

        SceneEntity entity = sceneService.getById(sceneId) ;
        GeneralAgentSceneEntity generalAgentSceneEntity = service.getBySceneId(sceneId , query) ;

        ChatContentEditDto dto = new ChatContentEditDto() ;
        dto.setSceneId(sceneId);
        dto.setRoleId(Long.parseLong(generalAgentSceneEntity.getBusinessExecuteEngineer().split(",")[0]));

        LambdaQueryWrapper<GeneralAgentPlanEntity> lambdaQueryWrapper = new LambdaQueryWrapper<GeneralAgentPlanEntity>()
                .eq(GeneralAgentPlanEntity::getSceneId, sceneId)
                .eq(GeneralAgentPlanEntity::getTaskId, taskId) ;

        List<GeneralAgentPlanEntity> chapters = generalAgentPlanService.list(lambdaQueryWrapper) ;
        List<Long> chapterIds = chapters.stream()
                .map(GeneralAgentPlanEntity::getId)
                .toList();

        List<String> chapterIdsStr = chapterIds.stream()
                .map(String::valueOf)
                .toList();

        dto.setChapters(chapterIdsStr);
        generalAgentPlanService.updateChapterEditor(dto, taskId, taskId) ; // generalAgentSceneEntity.getId());

        SceneDto sceneEntity = new SceneDto();
        BeanUtils.copyProperties(entity, sceneEntity);

        AjaxResult result = AjaxResult.success("操作成功");
        result.put("sceneInfo", sceneEntity) ;

        return result ;
    }

    /**
     * 获取到角色的编辑章节
     * @return
     */
    @GetMapping("/getChapterByRole")
    public AjaxResult getChapterByRole(@RequestParam("roleId") long roleId ,
                                       @RequestParam("sceneId") long sceneId) {

        LambdaQueryWrapper<GeneralAgentPlanEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeneralAgentPlanEntity::getSceneId, sceneId);
        wrapper.eq(GeneralAgentPlanEntity::getBusinessExecutorEngineerId, roleId);

        List<GeneralAgentPlanEntity> allChapters = generalAgentPlanService.list(wrapper);

        return AjaxResult.success("操作成功." , allChapters) ;
    }

    /**
     * 更新用户编辑章节，即每个章节需要指定编辑人员
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterContentEditor")
    public AjaxResult updateChapterContentEditor(@RequestBody @Validated ChatContentEditDto dto , PermissionQuery query) {
        GeneralAgentSceneEntity longTextSceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        generalAgentPlanService.updateChapterEditor(dto , longTextSceneEntity.getId(), dto.getTaskId());
        return ok() ;
    }

    /**
     * 生成章节内容
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/chatRoleSync")
    public AjaxResult chatRoleSync(@RequestBody @Validated ChapterGenFormDto dto , PermissionQuery query) {

        log.debug("dto = {}", dto);

        long chapterId = dto.getChapterId() ;
        GeneralAgentPlanEntity chapterEntity = generalAgentPlanService.getById(chapterId) ;
        Long roleId = chapterEntity.getBusinessExecutorEngineerId() ;

        GeneralAgentSceneEntity longTextSceneEntity = service.getBySceneId(dto.getSceneId() , query) ;

        if(roleId != null){
            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

            GeneralAgentTaskEntity taskEntity = generalAgentTaskService.getById(dto.getTaskId()) ;
            handleAttachment(taskEntity, taskInfo);

            taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
            taskInfo.setRoleId(roleId);
            taskInfo.setChannelId(dto.getSceneId());
            taskInfo.setSceneId(dto.getSceneId());

            String allChapterContent = generalAgentPlanService.getAllChapterContent(dto.getSceneId()) ;
            String chapterPromptContent = longTextSceneEntity.getPromptContent() ;

            taskInfo.setText(FormatMessageTool.getChapterPrompt(
                    allChapterContent ,
                    dto ,
                    chapterPromptContent ,
                    taskInfo
            )) ;

            WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
            log.debug("chatRole = {}" , genContent);

            // 更新章节内容
            chapterEntity.setContent(taskInfo.getFullContent());
            generalAgentPlanService.update(chapterEntity);

            return AjaxResult.success("生成成功",chapterEntity.getContent()) ;
        }

        return AjaxResult.success("操作成功" , "此章节未指定编辑人员");
    }

    /**
     * 生成数据报告genDataReport
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/genDataReport")
    public AjaxResult genDataReport(
            @RequestParam("sceneId") long sceneId ,
            @RequestParam("taskId") long taskId,
            PermissionQuery query) {
        GeneralAgentSceneEntity generalAgentSceneEntity = service.getBySceneId(sceneId , query) ;

        String markdownContent = service.genMarkdownContent(sceneId , taskId , query , generalAgentSceneEntity.getId()) ;

        String filename = IdUtil.fastSimpleUUID() ;

        log.debug("markdownContent = {}", markdownContent);
        Assert.notNull(markdownContent, "markdownContent为空") ;

        String filePath = MarkdownToWord.convertMdToDocx(markdownContent, filename) ;
        Assert.notNull(filePath, "文件路径为空") ;

        R<String> r = storageConsumer.upload(new File(filePath)) ;
        String storageId = r.getData() ;

        // 删除文件
        FileUtil.del(filePath);

        return AjaxResult.success("操作成功" , storageId);
    }

    /**
     * 获取预览地址
     * @param storageId
     * @return
     */
    @GetMapping("/getPreviewUrl")
    public AjaxResult getPreviewUrl(@RequestParam String storageId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        return AjaxResult.success("操作成功" , previewUrl);
    }

    /**
     * 获取预览文件
     * @param storageId
     * @return
     */
    @SneakyThrows
    @DataPermissionQuery
    @GetMapping("/getPreviewDocx")
    public ResponseEntity<Resource> getPreviewDocx(@RequestParam String storageId) {

        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
//        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

        byte[] fileBytes; // restTemplate.getForObject(previewUrl, byte[].class);

        try (InputStream inputStream = new URL(previewUrl).openStream()) {
            fileBytes = IOUtils.toByteArray(inputStream);
            // 使用 fileBytes 进行后续处理
        }


        if (fileBytes == null) {
            // 文件下载失败，返回合适的错误信息
            return ResponseEntity.notFound().build();
        }

        // 检查文件字节数组长度是否合理，这里只是简单示例，你可能需要更准确的判断
        if (fileBytes.length < 1024) { // 假设小于 1KB 的文件不太可能是有效的.docx
            return ResponseEntity.badRequest().build();
        }

        File docFile = null;
        File pdfFile = null;
        try {
            // 创建临时 docx 文件
            docFile = File.createTempFile("temp-docx", ".docx");
            Files.write(docFile.toPath(), fileBytes);

            // 创建临时 pdf 文件
            pdfFile = File.createTempFile("temp-pdf", ".pdf");
            WordToPdfConverter.convertToPdf(docFile, pdfFile);

            // 读取 PDF 文件内容
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            HttpHeaders headers = new HttpHeaders();
            // 设置正确的 MIME 类型
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=preview.pdf");

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("文件处理出错", e);
            return ResponseEntity.status(500).build();
        } finally {
            // 删除临时文件
            if (docFile != null) {
                docFile.delete();
            }
            if (pdfFile != null) {
                pdfFile.delete();
            }
        }
    }



    @Override
    public IGeneralAgentSceneService getFeign() {
        return this.service;
    }
}