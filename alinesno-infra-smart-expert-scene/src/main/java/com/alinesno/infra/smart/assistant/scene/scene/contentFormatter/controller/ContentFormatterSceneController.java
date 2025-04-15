package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.cache.RedisUtils;
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
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ReviewRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.DocxConvertPdf;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.FormatterPromptTools;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.dto.DataPromptContentRequestDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.ChapterGenFormDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

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

            dto.setGenStatus(dataAnalysisSceneEntity.getGenStatus());
            dto.setContentPromptContent(dataAnalysisSceneEntity.getContentPromptContent());

            dto.setTemplateExtractorEngineers(RoleUtils.getEditors(roleService , dataAnalysisSceneEntity.getTemplateExtractorEngineer())); // 查询出当前的数据分析编辑人员

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
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated DataPromptContentRequestDto dto , PermissionQuery query) {

        ContentFormatterSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setContentPromptContent(dto.getPromptContent());

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
     * 生成章节内容
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("/chatRoleSync")
    public AjaxResult chatRoleSync(@RequestBody @Validated ReviewRequestDTO dto , PermissionQuery query) {
        log.debug("dto = {}", dto);

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

        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;

        log.debug("genContent = {}" ,genContent) ;
        log.debug("fullContent = {}" , taskInfo.getFullContent());

        genContent.setGenContent(taskInfo.getFullContent());
        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(taskInfo.getFullContent()));

        // 解析得到代码内容
        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
            String codeContent = genContent.getCodeContent().get(0).getContent() ;
            JSONObject dataObject = JSONArray.parseObject(codeContent) ;

            log.debug("codeContent = {}", JSONUtil.toJsonPrettyStr(dataObject));

            // 与模板结合，生成返回的文件url路径
            String storageId = templateService.genTemplate(dataObject , dto.getTemplateId());

//            String fileKey = IdUtil.getSnowflakeNextIdStr() ;
//            RedisUtils.setCacheObject(fileKey , abcPath , Duration.ofMinutes(5));

            // TODO 待处理路径问题
            return AjaxResult.success("操作成功" , storageId) ;
        }


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

//        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
//        log.debug("previewUrl= {}" , previewUrl);
//        RestTemplate restTemplate = new RestTemplate();
//        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

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
    @GetMapping("/getPreviewDocxByPdf")
    public ResponseEntity<Resource> getPreviewDocxByPdf(@RequestParam String storageId) {

        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        log.debug("previewUrl= {}", previewUrl);
        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

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
            DocxConvertPdf.convert(docFile, pdfFile);

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
    public IContentFormatterSceneService getFeign() {
        return this.service;
    }
}