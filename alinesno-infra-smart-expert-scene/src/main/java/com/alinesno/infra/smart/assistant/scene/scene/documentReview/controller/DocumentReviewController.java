package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.SmartDocumentConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.utils.AddCommentToCharacters;
import com.alinesno.infra.smart.assistant.scene.common.utils.Revision;
import com.alinesno.infra.smart.assistant.scene.common.utils.WordToPdfConverter;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.FormatterPromptTools;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.bean.DocumentInfoBean;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewSceneInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.GenAuditResultDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums.ReviewRuleGenStatusEnums;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditResultService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.AnalysisTool;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.DocReviewListGeneratorService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.DocReviewPromptTools;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.ParserDocumentTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 文档审核场景控制
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/")
public class DocumentReviewController extends SuperController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ISceneService service;

    @Autowired
    private IDocReviewSceneService docReviewSceneService ;

    @Autowired
    private IDocReviewTaskService docReviewTaskService ;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IDocReviewAuditResultService auditResultService ;

    @Autowired
    private IDocReviewRulesService ruleService; ;

    @Autowired
    private AnalysisTool analysisTool;

    @Autowired
    private ParserDocumentTool parserDocumentTool ;

    @Autowired
    private SmartDocumentConsumer smartDocumentConsumer ;

    @Autowired
    private DocReviewListGeneratorService docReviewListGeneratorService ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") Long id , Long taskId) {

        Assert.notNull(id , "参数不能为空");

        SceneEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        return AjaxResult.success("操作成功.", docReviewSceneService.getDocReviewSceneInfoDto(id, taskId == null ? 0 : taskId , entity));
    }

    /**
     * 通过Id获取到场景审核结果列表
     * @param id
     * @return
     */
    @GetMapping("/getSceneResultList")
    public AjaxResult getSceneResultList(@RequestParam("id") long id , long taskId) {

        SceneEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        DocReviewSceneInfoDto docSceneInfoDto = docReviewSceneService.getDocReviewSceneInfoDtoWithResultCount(id, taskId , entity);
        return AjaxResult.success("操作成功." , docSceneInfoDto)  ;
    }

    /**
     * 通过审核规则id和场景获取到审核结果
     * @param ruleId
     * @param sceneId
     * @param query
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAuditResultByRuleId")
    public AjaxResult getAuditResultByRuleId(@RequestParam("ruleId") long ruleId ,
                                             @RequestParam("sceneId") long sceneId,
                                             PermissionQuery query) {
        List<DocReviewAuditResultEntity> auditResultList = auditResultService.getAuditResultByRuleId(ruleId, sceneId);
        return AjaxResult.success("操作成功." , auditResultList)  ;
    }

    /**
     * 生成标注文档然后提供下载
     *
     * @param sceneId
     * @param query
     * @return
     */
    @SneakyThrows
    @GetMapping("/downloadMarkDocx")
    public AjaxResult downloadMarkDocx(@RequestParam long sceneId , long taskId , PermissionQuery query) {

        DocReviewTaskEntity entity = docReviewTaskService.getById(taskId);
        String previewUrl = storageConsumer.getPreviewUrl(entity.getDocumentId()).getData();

        String fileName = entity.getDocumentName();

        boolean isPdf = fileName.toLowerCase().endsWith(".pdf");
        Assert.isTrue(!isPdf, "当前文件不支持PDF批注.");

        log.debug("previewUrl= {}", previewUrl);

//        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

        byte[] fileBytes; // restTemplate.getForObject(previewUrl, byte[].class);

        try (InputStream inputStream = new URL(previewUrl).openStream()) {
            fileBytes = IOUtils.toByteArray(inputStream);
            // 使用 fileBytes 进行后续处理
        }

        if (fileBytes == null) {
            // 文件下载失败，返回合适的错误信息
            return AjaxResult.error() ;
        }

        File docFile = File.createTempFile("temp-docx", ".docx");
        Files.write(docFile.toPath(), fileBytes);

        List<DocReviewAuditResultEntity> auditResultList = auditResultService.getBySceneIdAndDocReviewSceneId(sceneId, sceneId , taskId) ;
        Assert.isTrue(CollectionUtils.isNotEmpty(auditResultList), "未找到对应的审核结果");

        List<Revision> combinedJson = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(auditResultList)){
            for (DocReviewAuditResultEntity auditResult : auditResultList) {
                Revision revision = new Revision(auditResult.getOriginalContent(), auditResult.getSuggestedContent() , auditResult.getModificationReason());
                combinedJson.add(revision);
            }
        }

        String tempFilePath = AddCommentToCharacters.addCommentToCharacters(docFile.getAbsolutePath(), combinedJson) ;
        assert tempFilePath != null;

        String storageId = storageConsumer.upload(new File(tempFilePath)).getData()  ;

        return AjaxResult.success("操作成功." ,  storageConsumer.getPreviewUrl(storageId).getData()) ;
    }

    @SneakyThrows
    @DataPermissionQuery
    @GetMapping("/getPreviewDocx")
    public AjaxResult getPreviewDocx(@RequestParam Long taskId, PermissionQuery query) {
        Assert.notNull(taskId, "任务ID不能为空");

        // 获取任务实体
        DocReviewTaskEntity entity = docReviewTaskService.getById(taskId);
        if (entity == null) {
            return AjaxResult.error("未找到对应的任务");
        }

        // 直接下载文件内容
        byte[] fileBytes = storageConsumer.download(entity.getDocumentId(), null);
        if (fileBytes == null || fileBytes.length == 0) {
            return AjaxResult.error("文件下载失败");
        }

        // 创建临时文件
        File tempFile = File.createTempFile("temp-", ".docx");
        try {
            Files.write(tempFile.toPath(), fileBytes);

            // 将DOCX转换为HTML
            String htmlContent = smartDocumentConsumer.convertToHtml(tempFile);
            return AjaxResult.success("获取预览成功" , htmlContent) ;
        } finally {
            // 删除临时文件
            if (!tempFile.delete()) {
                log.warn("临时文件删除失败: {}", tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * 文件上传
     * @return
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, long sceneId , PermissionQuery query){

        log.debug("sceneId = {}" , sceneId);

        // 新生成的文件名称
        String fileName = file.getOriginalFilename() ;
        String fileSuffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile) ;

        // 获取到文档的基础内容
        String content = analysisTool.analysisDocumentBaseContent(targetFile) ;

        // 保存到任务清单里面
        DocReviewTaskEntity taskEntity = new DocReviewTaskEntity() ;

        BeanUtil.copyProperties(query , taskEntity);

        taskEntity.setTaskName(fileName);
        taskEntity.setSceneId(sceneId);
        taskEntity.setTaskDescription(content);
        taskEntity.setDocumentReviewSceneId(sceneId);
        taskEntity.setTaskStartTime(new Date());

        String attachments = r.getData() ;
        taskEntity.setDocumentId(attachments); // 审核附件ID号，以逗号进行分隔
        taskEntity.setDocumentName(fileName);

        taskEntity.setReviewGenStatus(ReviewRuleGenStatusEnums.NOT_GENERATED.getCode());
        taskEntity.setResultGenStatus(ReviewRuleGenStatusEnums.NOT_GENERATED.getCode());
        taskEntity.setDocumentParseStatus(ReviewRuleGenStatusEnums.GENERATING.getCode()); // 正在解析生成中

        taskEntity.setChannelStreamId(IdUtil.getSnowflakeNextId());  // 渠道流ID

        docReviewTaskService.save(taskEntity);

        docReviewListGeneratorService.startParseDocument(taskEntity , sceneId ,  query) ;

        log.debug("ajaxResult= {}" , r);
        AjaxResult result = AjaxResult.success("上传成功." , r.getData()) ;
        result.put("taskId" , taskEntity.getId());
        result.put("channelStreamId" , taskEntity.getChannelStreamId());

        return result ;
    }

    /**
     * 文档进行内容审核
     */
    @DataPermissionQuery
    @PostMapping("/genAuditResult")
    public AjaxResult genAuditResult(@RequestBody @Validated GenAuditResultDto dto , PermissionQuery query){

        DocReviewTaskEntity taskEntity = docReviewTaskService.getById(dto.getTaskId()) ;
        List<DocumentInfoBean> contentList = parserDocumentTool.parseContent(taskEntity) ;

        List<DocReviewAuditResultEntity> auditResultList = new ArrayList<>() ;

        if(contentList == null){
            return AjaxResult.error("解析文档内容失败.") ;
        }

        log.debug("contentList = {}" , contentList.size());
        int count = 0 ;

        // 如果是aigen处理的审核
        if(taskEntity.getReviewListOption().equals(ReviewListOptionEnum.AIGEN.getValue())){

            // 解析获取得到审核内容
            List<DocReviewRulesEntity> rules = JSONArray.parseArray(taskEntity.getReviewList(), DocReviewRulesEntity.class) ;

            for(DocumentInfoBean bean : contentList){
                String contentPromptContent = bean.getContent() ;

                // 从rules中找到
                DocReviewRulesEntity rule = getRule(rules , dto.getRuleId()) ;
                if(rule != null){
                    generateTask(contentPromptContent , dto, query , rule , auditResultList);
                }
            }
        }else if(taskEntity.getReviewListOption().equals(ReviewListOptionEnum.DATASET.getValue())){
            for(DocumentInfoBean bean : contentList){
                String contentPromptContent = bean.getContent() ;
                DocReviewRulesEntity rule = ruleService.getById(dto.getRuleId()) ;
                generateTask(contentPromptContent , dto, query , rule , auditResultList);
            }
        }

        if(!CollectionUtils.isEmpty(auditResultList)){

            CopyOptions options = CopyOptions.create()
                    .setIgnoreNullValue(true)  // 忽略源对象属性为空的情况
                    .setIgnoreError(true);     // 忽略复制过程中出现的错误

            for(DocReviewAuditResultEntity entity : auditResultList){
                log.debug("entity = {}" , entity);

                entity.setId(null);
                entity.setAuditId(dto.getRuleId());
                entity.setRuleId(dto.getRuleId());
                entity.setSceneId(dto.getSceneId());
                entity.setDocReviewSceneId(dto.getSceneId());
                entity.setTaskId(taskEntity.getId());

                BeanUtil.copyProperties(query , entity , options);
            }
            log.debug("文档解析完成，审核意见为:{}" , auditResultList.size());
            auditResultService.saveAuditResult(auditResultList , dto);
        }

        return AjaxResult.success() ;
    }

    /**
     * 获取规则
     * @param rules
     * @param ruleId
     * @return
     */
    private DocReviewRulesEntity getRule(List<DocReviewRulesEntity> rules, Long ruleId) {
        for (DocReviewRulesEntity rule : rules) {
            if (rule.getId().equals(ruleId)) {
                return rule;
            }
        }
        return null;
    }

    private void generateTask(String contentPromptContent ,
                              GenAuditResultDto dto,
                              PermissionQuery query,
                              DocReviewRulesEntity rule ,
                              List<DocReviewAuditResultEntity> auditResultList) {

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        Long roleId = dto.getRoleId() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleId);
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        String prompt = DocReviewPromptTools.buildReviewPrompt(
                contentPromptContent ,
                dto,
                docReviewSceneService.getBySceneId(dto.getSceneId() , query) ,
                rule ,
                taskInfo) +
                FormatterPromptTools.FORMAT_REVIEW_OUTPUT_PROMPT ;

        taskInfo.setText(prompt);

        CompletableFuture<WorkflowExecutionDto> genContent  = industryRoleService.runRoleAgent(taskInfo) ;

//        genContent.setGenContent(taskInfo.getFullContent());
//        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));
//
//        // 解析得到代码内容
//        try{
//            if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//
//                String codeContent = genContent.getCodeContent().get(0).getContent() ;
//                List<DocReviewAuditResultEntity> dataObject = JSONArray.parseArray(codeContent , DocReviewAuditResultEntity.class) ;
//
//                log.debug("codeContent = {}", JSONUtil.toJsonPrettyStr(dataObject));
//                auditResultList.addAll(dataObject);
//            }
//        }catch (Exception e){
//            log.warn("解析代码块出错:{}", e.getMessage());
//        }
    }

}

