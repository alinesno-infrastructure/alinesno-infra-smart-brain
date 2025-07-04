package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.controller;

import cn.hutool.core.util.IdUtil;
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
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTOutlineDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.prompt.AIPPTPromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTGeneratorSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTManagerService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.PPTGenerateSceneEntity;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ExamQuestionTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
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
@RequestMapping("/api/infra/smart/assistant/scene/pptGenerate")
public class PPTGeneratorSceneController extends BaseController<PPTGenerateSceneEntity, IPPTGeneratorSceneService> {

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
    private IPPTGeneratorSceneService service ;

    @Autowired
    private IPPTManagerService pptManagerSceneService;

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

        PPTGenerateSceneDto dto = new PPTGenerateSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        PPTGenerateSceneEntity examPagerSceneEntity = service.getBySceneId(id, query);
        if(examPagerSceneEntity != null){

            dto.setPptPlannerEngineer(examPagerSceneEntity.getPptPlannerEngineer());
            dto.setPptPlannerEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getPptPlannerEngineer())));

            dto.setPptGeneratorEngineer(examPagerSceneEntity.getPptGeneratorEngineer());
            dto.setPptGeneratorEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getPptGeneratorEngineer())));

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
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatPromptContent")
    public AjaxResult chatPromptContent(@RequestBody @Validated PPTGeneratorDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        PPTGenerateSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long pptPlannerEngineer = entity.getPptPlannerEngineer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ;

        // 引用附件不为空，则引入和解析附件
        if(!CollectionUtils.isEmpty(dto.getAttachments())){
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
            taskInfo.setAttachments(attachmentList);
        }

        String promptText = AIPPTPromptHandle.generatorPrompt(dto) ;

        taskInfo.setRoleId(pptPlannerEngineer);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("genContent = {}", genContent.getGenContent());

        return AjaxResult.success("操作成功" , taskInfo.getFullContent()) ;
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
        pptManagerSceneService.savePager(dto);  // 保存试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 更新试卷问题
     */
    @DataPermissionSave
    @PostMapping("/updatePagerQuestion")
    public AjaxResult updatePagerQuestion(@RequestBody @Validated PPTGenerateSceneDto dto) {
        log.info("dto = {}" , dto);
        pptManagerSceneService.updatePager(dto);  // 删除试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
        List<PPTManagerEntity> list = pptManagerSceneService.pagerListByPage(page, query);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 获取试卷详情(getPagerDetail)
     */
    @GetMapping("/getPagerDetail")
    public AjaxResult getPagerDetail(Long id) {
        PPTGenerateSceneDto entity = pptManagerSceneService.getPagerDetail(id);
        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 保存PPT大纲
     * @param dto
     * @return
     */
    @DataPermissionSave
    @PostMapping("/savePPTOutline")
    public AjaxResult savePPTOutline(@RequestBody PPTOutlineDto dto){

        log.debug("dto = {}" , dto);
        Long pptId = pptManagerSceneService.savePPTOutline(dto) ;

        return AjaxResult.success("操作成功." , pptId) ;
    }

}