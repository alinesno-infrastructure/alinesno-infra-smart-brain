package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.common.utils.WordToPdfConverter;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterPromptRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ReviewContentRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.CheckRulesEnum;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.FormatterPromptTools;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 数据分析场景控制器
 */
@Slf4j
@Api(tags = "数据分析场景")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatter/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterSceneController extends BaseController<ContentFormatterSceneEntity, IContentFormatterSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private IContentFormatterSceneService service;

    @Autowired
    private IContentFormatterParseService contentFormatterParseService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private ITemplateService templateService ;

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

        ContentFormatterSceneDto dto = new ContentFormatterSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        ContentFormatterSceneEntity dataAnalysisSceneEntity = service.getBySceneId(id, query);
        if(dataAnalysisSceneEntity != null){

            dto.setTemplateExtractorEngineer(dataAnalysisSceneEntity.getTemplateExtractorEngineer());
            dto.setContentReviewerEngineer(dataAnalysisSceneEntity.getContentReviewerEngineer());

            dto.setGenStatus(dataAnalysisSceneEntity.getGenStatus());
            dto.setContentPromptContent(dataAnalysisSceneEntity.getContentPromptContent());

            dto.setTemplateExtractorEngineers(RoleUtils.getEditors(roleService , dataAnalysisSceneEntity.getTemplateExtractorEngineer())); // 查询出当前的数据分析编辑人员
            dto.setContentReviewerEngineers(RoleUtils.getEditors(roleService, dataAnalysisSceneEntity.getContentReviewerEngineer()));

            dto.setChapterTree(contentFormatterParseService.getPlanTree(entity.getId() , dataAnalysisSceneEntity.getId())); // 数据分析树信息
        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterPromptContent")
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated ContentFormatterPromptRequestDto dto , PermissionQuery query) {

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setContentPromptContent(dto.getPromptContent());
        sceneEntity.setGenStatus(1);

        service.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
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

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(sceneId , query) ;
        sceneEntity.setGenStatus(genStatus);

        service.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 获取模板信息
     * @param query
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getTemplates")
    public AjaxResult getTemplates(PermissionQuery query) {
        log.debug("templateService = {}" , templateService);
        return AjaxResult.success("操作成功" , templateService.getTemplates(query));
    }

    /**
     * 获取检查项目检查
     */
    @DataPermissionQuery
    @GetMapping("/getReviewRules")
    public AjaxResult getReviewRules(PermissionQuery query) {
        return AjaxResult.success("操作成功" , CheckRulesEnum.getAllRule()) ;
    }

    /**
     * 生成章节内容
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/chatRoleSync")
    public AjaxResult chatRoleSync(@RequestBody @Validated ContentFormatterRequestDTO dto , PermissionQuery query) {
        log.debug("dto = {}", dto);

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setGenStatus(1);
        sceneEntity.setContentPromptContent(dto.getContentPromptContent());
        service.updateById(sceneEntity);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        Long roleId = dto.getRoleId() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleId);
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        String prompt =  FormatterPromptTools.buildPrompt(dto ,
                service.getBySceneId(dto.getSceneId() , query) ,
                templateService.getById(dto.getTemplateId()) ,
                taskInfo) ;

        taskInfo.setText(prompt);

        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

        log.debug("genContent = {}" ,genContent) ;
        log.debug("fullContent = {}" , taskInfo.getFullContent());

//        genContent.setGenContent(taskInfo.getFullContent());
//        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));
//
//        // 解析得到代码内容
//        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//            String codeContent = genContent.getCodeContent().get(0).getContent() ;
//            JSONObject dataObject = JSONArray.parseObject(codeContent) ;
//
//            log.debug("codeContent = {}", JSONUtil.toJsonPrettyStr(dataObject));
//
//            // 与模板结合，生成返回的文件url路径
//            String storageId = templateService.genTemplate(dataObject , dto.getTemplateId());
//            return AjaxResult.success("操作成功" , storageId) ;
//        }

        return AjaxResult.success("操作成功") ;
    }

    @GetMapping("/getPreviewDocxPreviewUrl")
    public AjaxResult getPreviewDocxPreviewUrl(@RequestParam String storageId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        log.debug("previewUrl= {}" , previewUrl);
        return AjaxResult.success("操作成功" , previewUrl) ;
    }

    @GetMapping("/getPreviewDocx")
    public ResponseEntity<Resource> getPreviewDocx(@RequestParam String storageId) {

        byte[] fileBytes = storageConsumer.download(storageId , (onProgress) -> {
            log.debug("onProgress = {}" , onProgress);
        }) ;

        if (fileBytes == null) {
            // 文件下载失败，返回合适的错误信息
            return ResponseEntity.notFound().build();
        }

        // 检查文件字节数组长度是否合理，这里只是简单示例，你可能需要更准确的判断
        if (fileBytes.length < 1024) { // 假设小于 1KB 的文件不太可能是有效的.docx
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        // 设置正确的 MIME 类型
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.add("Content-Disposition", "inline; filename=demo.docx");

        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * 临时解决docx预览的问题
     * @param storageId
     * @return
     */
    @SneakyThrows
    @GetMapping("/getPreviewDocxByPdf")
    public ResponseEntity<Resource> getPreviewDocxByPdf(@RequestParam String storageId) {

        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        log.debug("previewUrl= {}", previewUrl);
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

    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/reviewChatRoleSync")
    public AjaxResult reviewChatRoleSync(@RequestBody @Validated ReviewContentRequestDTO dto , PermissionQuery query) {
        log.debug("dto = {}", dto);

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setGenStatus(1);
        sceneEntity.setContentPromptContent(dto.getContentPromptContent());
        service.updateById(sceneEntity);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        Long roleId = dto.getRoleId() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleId);
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        JSONArray checkOutput = new JSONArray() ;

        List<Long> rules = dto.getRuleIds() ;

        for (Long ruleId : rules) {

            if(CheckRulesEnum.getById(ruleId).isEmpty()){
               continue;
            }

            try{
                CheckRulesEnum checkRulesEnum = CheckRulesEnum.getById(ruleId).get() ;
                String prompt = FormatterPromptTools.buildReviewPromptByRule(checkRulesEnum ,dto ,
                        service.getBySceneId(dto.getSceneId() , query) ,
                        templateService.getById(dto.getTemplateId()) ,
                        taskInfo) +
                        FormatterPromptTools.FORMAT_REVIEW_OUTPUT_PROMPT ;

                taskInfo.setText(prompt);

                CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

                log.debug("genContent = {}" ,genContent) ;
                log.debug("fullContent = {}" , taskInfo.getFullContent());

//                genContent.setGenContent(taskInfo.getFullContent());
//                genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));
//
//                // 解析得到代码内容
//                if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//
//                    String codeContent = genContent.getCodeContent().get(0).getContent() ;
//                    JSONArray dataObject = JSONArray.parseArray(codeContent) ;
//
//                    checkOutput.addAll(dataObject) ;
//                }
            }catch(Exception e){
                log.error("reviewChatRoleSync error" , e) ;
            }
        }

        return AjaxResult.success("操作成功" , checkOutput) ;
    }

    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/reviewChatRoleSingleSync")
    public AjaxResult reviewChatRoleSingleSync(@RequestBody @Validated ReviewContentRequestDTO dto , PermissionQuery query) {
        log.debug("dto = {}", dto);

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setGenStatus(1);
        sceneEntity.setContentPromptContent(dto.getContentPromptContent());
        service.updateById(sceneEntity);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        Long roleId = dto.getRoleId() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleId);
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        String prompt = FormatterPromptTools.buildReviewPrompt(dto ,
                service.getBySceneId(dto.getSceneId() , query) ,
                templateService.getById(dto.getTemplateId()) ,
                taskInfo) + FormatterPromptTools.FORMAT_REVIEW_OUTPUT_PROMPT ;

        taskInfo.setText(prompt);

        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

//        log.debug("genContent = {}" ,genContent) ;
//        log.debug("fullContent = {}" , taskInfo.getFullContent());
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
//            log.debug("codeContent = {}", JSONUtil.toJsonPrettyStr(dataObject));
//
//            return AjaxResult.success("操作成功" , dataObject) ;
//        }

        return AjaxResult.success("操作成功") ;
    }

    /**
     * 导出数据
     * @param data
     * @return
     */
    @PostMapping("/reviewCheckExpert")
    public AjaxResult exportToExcel(@RequestBody List<Map<String, Object>> data) {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("Data");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"riskLevel", "suggestedContent", "modificationReason", "id", "originalContent"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> item = data.get(i);
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                Object value = item.get(headers[j]);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile("excel_export", ".xlsx");
            // 将工作簿写入临时文件
            FileOutputStream fileOut = new FileOutputStream(tempFile);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            // 处理异常
            log.error("Failed to create and write temp file", e);
            // 这里假设 AjaxResult 有合适的构造方法来表示失败，你可能需要根据实际情况调整
            return AjaxResult.error("Failed to create and write temp file", null);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("Failed to close workbook", e);
            }
        }

        String storageId =  storageConsumer.upload(tempFile).getData() ;
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData() ;

        return AjaxResult.success("Successfully created temp file", previewUrl) ;
    }

    /**
     * 接收JSON数据并保存到数据库
     * @param nodes JSON格式的章节列表
     * @return 操作结果
     */
    @DataPermissionQuery
    @PostMapping("/saveChapters")
    public AjaxResult saveChapters(@RequestBody List<TreeNodeDto> nodes, @RequestParam("sceneId") long sceneId , PermissionQuery query) {

        if(nodes == null || nodes.isEmpty()){
            return error("参数错误") ;
        }

        ContentFormatterSceneEntity longTextSceneEntity = service.getBySceneId(sceneId , query) ;
        contentFormatterParseService.saveChaptersWithHierarchy(nodes, null, 1 , sceneId , longTextSceneEntity.getId());
        return ok() ;
    }


    @Override
    public IContentFormatterSceneService getFeign() {
        return this.service;
    }
}