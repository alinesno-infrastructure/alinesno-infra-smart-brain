package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectResearchSceneDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectSearchDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt.ProjectPromptHandle;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ExamQuestionTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 项目跟进场景管理
 */
@Slf4j
@Api(tags = "项目跟进场景管理")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/productResearchScene")
public class ProjectResearchSceneController extends BaseController<ProjectResearchSceneEntity, IProjectResearchSceneService> {

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
    private IProjectTaskService examProjectService ;

    @Autowired
    private IProjectKnowledgeGroupService projectKnowledgeGroupService ;

    @Autowired
    private IProjectResearchSceneService service ;

    @Autowired
    private IIndustryRoleService roleService ;

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

        ProjectResearchSceneDTO dto = new ProjectResearchSceneDTO();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        ProjectResearchSceneEntity examProjectSceneEntity = service.getBySceneId(id, query);
        if(examProjectSceneEntity != null){

            dto.setProcessCollectorEngineer(String.valueOf(examProjectSceneEntity.getProcessCollectorEngineer()));
            dto.setProcessCollectorEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examProjectSceneEntity.getProcessCollectorEngineer())));

            dto.setProgressAnalyzerEngineer(String.valueOf(examProjectSceneEntity.getProgressAnalyzerEngineer()));
            dto.setProgressAnalyzerEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examProjectSceneEntity.getProgressAnalyzerEngineer())));

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
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query , @RequestParam Long sceneId) {
        List<ProjectTaskEntity> list = examProjectService.pagerListByPage(page, query , sceneId);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 获取项目详情(getProjectDetail)
     */
    @GetMapping("/getProjectDetail")
    public AjaxResult getProjectDetail(Long id) {
        ProjectManagerDTO entity = examProjectService.getProjectDetail(id);
        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 聊天提示内容
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatPromptContent")
    public AjaxResult chatPromptContent(@RequestBody @Validated ProjectSearchDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        ProjectResearchSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long pptPlannerEngineer = entity.getProcessCollectorEngineer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ;

        // 引用附件不为空，则引入和解析附件
        if(!CollectionUtils.isEmpty(dto.getAttachments())){
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
            taskInfo.setAttachments(attachmentList);
        }

        String promptText = ProjectPromptHandle.generatorPrompt(dto) ;

        taskInfo.setRoleId(pptPlannerEngineer);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);
        taskInfo.setQueryText(dto.getPromptText());

        Long datasetGroupId = dto.getDatasetGroupId() ;
        ProjectKnowledgeGroupEntity groupEntity =  projectKnowledgeGroupService.getById(datasetGroupId) ;

        taskInfo.setCollectionIndexName(groupEntity.getVectorDatasetName());
        taskInfo.setCollectionIndexLabel(groupEntity.getGroupName());

        // 优先获取到结果内容
        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;
//        log.debug("genContent = {}", genContent.getGenContent());
//
//        genContent.setGenContent(taskInfo.getFullContent());
//        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));
//
//        // 解析得到代码内容
//        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//
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

        return AjaxResult.success("操作成功") ;
    }

}