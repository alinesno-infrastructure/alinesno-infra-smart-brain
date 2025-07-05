package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.controller;

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
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.*;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.prompt.ArticlePromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleManagerService;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleTemplateService;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleWriterSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ArticleChatRoleUtil;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ExcelData;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ExcelReaderUtil;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.enums.TaskResultTypeEnums;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.ArticleGenerateSceneEntity;
import com.alinesno.infra.smart.scene.entity.ArticleManagerEntity;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ExamQuestionTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
@RequestMapping("/api/infra/smart/assistant/scene/articleGenerate")
public class ArticleGeneratorSceneController extends BaseController<ArticleGenerateSceneEntity, IArticleWriterSceneService> {

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
    private IArticleWriterSceneService service ;

    @Autowired
    private IArticleManagerService articleManagerSceneService;

    @Autowired
    private IArticleTemplateService articleTemplateService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ArticleChatRoleUtil articleChatRoleUtil ;

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

        ArticleGenerateSceneDto dto = new ArticleGenerateSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        ArticleGenerateSceneEntity examPagerSceneEntity = service.getBySceneId(id, query);
        if(examPagerSceneEntity != null){

            dto.setArticleWriterEngineer(examPagerSceneEntity.getArticleWriterEngineer());
            dto.setArticleWriterEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getArticleWriterEngineer())));

            dto.setArticleLayoutDesignerEngineer(examPagerSceneEntity.getArticleLayoutDesignerEngineer());
            dto.setArticleLayoutDesignerEngineerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getArticleLayoutDesignerEngineer())));

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
    public AjaxResult chatPromptContent(@RequestBody @Validated ArticleGeneratorDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        ArticleGenerateSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long articleWriterEngineer = entity.getArticleWriterEngineer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ;

        // 引用附件不为空，则引入和解析附件
        if(!CollectionUtils.isEmpty(dto.getAttachments())){
            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(dto.getAttachments());
            taskInfo.setAttachments(attachmentList);
        }

        ArticleTemplateEntity templateEntity = null ;
        if(dto.getSelectedTemplateId() != null){
            templateEntity =  articleTemplateService.getById(dto.getSelectedTemplateId()) ;
        }
        Asserts.notNull(templateEntity , "未找到对应的模板实体");

        String promptText = ArticlePromptHandle.generatorPrompt(dto , templateEntity) ;

        taskInfo.setRoleId(articleWriterEngineer);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("genContent = {}", genContent.getGenContent());

        // 获取文章内容并保存
        String articleContent;
        if(TaskResultTypeEnums.SUMMARY.getCode().equals(taskInfo.getResultType())){ // 总结性结果输出
            articleContent = taskInfo.getFullContent() ;
        }else{
            List<CodeContent> contentList = CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()) ;
            articleContent = contentList.isEmpty()?  taskInfo.getFullContent() : contentList.get(0).getContent() ;
        }

        Long articleId = articleManagerSceneService.saveArticle(
                articleContent ,
                dto ,
                entity ,
                query) ;

        return AjaxResult.success("操作成功" , articleId) ;
    }

    /**
     * 重新润色文章
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/reChatPromptContent")
    public AjaxResult reChatPromptContent(@RequestBody @Validated ReWriterArticleGeneratorDTO dto , PermissionQuery query) {

        log.debug("dto = {}" , dto);

        ArticleGenerateSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long articleWriterEngineer = entity.getArticleWriterEngineer() ;

        MessageTaskInfo taskInfo = dto.toPowerMessageTaskInfo() ;

        ArticleManagerEntity articleManagerEntity = articleManagerSceneService.getById(dto.getArticleId()) ;
        String promptText = ArticlePromptHandle.generatorReWriterPrompt(dto , articleManagerEntity.getContent()) ;

        taskInfo.setRoleId(articleWriterEngineer);
        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("genContent = {}", genContent.getGenContent());

        // 获取文章内容
        String articleContent = taskInfo.getFullContent() ;
        List<CodeContent> contentList = CodeBlockParser.parseCodeBlocks(articleContent) ;

        return AjaxResult.success("操作成功" , contentList.isEmpty()?  articleContent : contentList.get(0).getContent() ) ;
    }

    /**
     * 重新润色文章
     * @param dto
     * @param query
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/generateImages")
    public AjaxResult generateImages(@RequestBody @Validated ImageGeneratorDTO dto , PermissionQuery query) {

        ArticleGenerateSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long articleWriterEngineer = entity.getArticleWriterEngineer() ;

        List<String> imageList = articleChatRoleUtil.generateImages(dto , articleWriterEngineer , query) ;

        return AjaxResult.success("操作成功" , imageList) ;
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
        articleManagerSceneService.savePager(dto);  // 保存试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 更新试卷问题
     */
    @DataPermissionSave
    @PostMapping("/updatePagerQuestion")
    public AjaxResult updatePagerQuestion(@RequestBody @Validated ArticleGenerateSceneDto dto) {
        log.info("dto = {}" , dto);
        articleManagerSceneService.updatePager(dto);  // 删除试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, Long sceneId , PermissionQuery query) {
        List<ArticleManagerResponseDto> list = articleManagerSceneService.pagerListByPage(page, sceneId , query);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 获取试卷详情(getPagerDetail)
     */
    @GetMapping("/getPagerDetail")
    public AjaxResult getPagerDetail(Long id) {
        ArticleGenerateSceneDto entity = articleManagerSceneService.getPagerDetail(id);
        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 通过id获取文章详情
     */
    @GetMapping("/getArticleById")
    public AjaxResult getArticleById(Long id) {
        ArticleManagerEntity entity = articleManagerSceneService.getById(id);
        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 更新文章updateArticle
     */
    @DataPermissionSave
    @PostMapping("/updateArticle")
    public AjaxResult updateArticle(@RequestBody @Validated ArticleUpdateDto dto) {
        log.info("dto = {}" , dto);
        articleManagerSceneService.updateArticle(dto);  // 删除试卷
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @DataPermissionQuery
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String type , String updateSupport , PermissionQuery query){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        List<ExcelData> dataList = ExcelReaderUtil.readExcel(targetFile.getAbsolutePath());
        for (ExcelData data : dataList) {
            log.debug("data = {}" , data);
        }

        Map<String, Object> failResult = articleTemplateService.readExcel(dataList , query) ;
        FileUtils.forceDeleteOnExit(targetFile);

        return AjaxResult.success("操作成功." , failResult);
    }

    /**
     * 与编辑角色沟通
     * @param dto
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatEditorRole")
    public AjaxResult chatEditorRole(@RequestBody @Validated ChatEditorDto dto , PermissionQuery query){

        ArticleGenerateSceneEntity entity = service.getBySceneId(dto.getSceneId(), query) ;
        Long articleWriterEngineer = entity.getArticleWriterEngineer() ;

        IndustryRoleDto roleDto =  RoleUtils.getEditors(roleService , String.valueOf(articleWriterEngineer)).get(0) ;

        articleChatRoleUtil.chat(roleDto , dto , query) ;

        return AjaxResult.success("操作成功") ;
    }

    /**
     * 导出试卷为Word文档并直接下载
     * @param articleId 试卷ID
     * @param showAnswer 是否显示答案
     * @return ResponseEntity 包含Word文件流
     */
    @PostMapping("/export/{articleId}")
    public ResponseEntity<byte[]> exportPaperToWord(
            @PathVariable Long articleId,
            @RequestParam(required = false, defaultValue = "false") boolean showAnswer) {

        // 1. 验证试卷是否存在
        ArticleManagerEntity examPager = articleManagerSceneService.getById(articleId);
        if (examPager == null) {
            throw new RuntimeException("文章不存在，ID: " + articleId);
        }

        String filePath = null;
        try {
            String title = String.valueOf(examPager.getId());

            filePath = MarkdownToWord.convertMdToDocx(examPager.getContent(), title) ; // articleManagerSceneService.generateToFile(articleId, showAnswer);
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // 使用RFC 5987标准编码文件名（支持中文和特殊字符）
            String fileName = title + ".docx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 关键设置：使用RFC 5987标准
            headers.add("Content-Disposition",
                    "attachment; filename=\"" + fileName + "\";" +
                            "filename*=UTF-8''" + encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            log.error("导出试卷失败", e);
            throw new RuntimeException("导出试卷失败", e);
        } finally {
            // 5. 删除临时文件
            try {
                assert filePath != null;
                FileUtils.forceDeleteOnExit(new java.io.File(filePath));
            } catch (IOException e) {
                log.error("删除临时文件失败", e);
            }
        }
    }

    /**
     * 通过id删除文章deleteArticle
     */
    @DeleteMapping("/deleteArticle")
    public AjaxResult deleteArticle(Long id) {
        articleManagerSceneService.removeById(id);
        return AjaxResult.success("操作成功") ;
    }

}