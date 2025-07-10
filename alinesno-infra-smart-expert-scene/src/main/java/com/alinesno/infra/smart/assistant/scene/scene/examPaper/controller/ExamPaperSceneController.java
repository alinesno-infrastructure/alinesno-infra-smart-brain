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
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools.JsonEscapeFixer;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatPromptContent")
    public AjaxResult chatPromptContent(@RequestBody @Validated ExamPagerGeneratorDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        ExamStructureItem examStructure = dto.getExamStructureItem() ;
        ExamPagerSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long questionGeneratorEngineer = entity.getQuestionGeneratorEngineer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ; // new MessageTaskInfo() ;

        // 引用附件不为空，则引入和解析附件
        if(!CollectionUtils.isEmpty(dto.getAttachments())){
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
            taskInfo.setAttachments(attachmentList);
        }

        String promptText = ExamPagerPromptHandle.generatorPrompt(dto.getPromptText() ,
                dto.getDifficultyLevel() ,
                examStructure) ;

        taskInfo.setRoleId(questionGeneratorEngineer);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("genContent = {}", genContent.getGenContent());

        // 获取到格式化的内容
        examPagerFormatMessageTool.handleChapterMessage(genContent , taskInfo);

        // 解析得到代码内容
        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
            String codeContent = genContent.getCodeContent().get(0).getContent() ;
            JSONArray dataObject = JSONArray.parseArray(codeContent) ;
            return AjaxResult.success("操作成功" , dataObject) ;
        }

        return AjaxResult.success("操作成功") ;
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