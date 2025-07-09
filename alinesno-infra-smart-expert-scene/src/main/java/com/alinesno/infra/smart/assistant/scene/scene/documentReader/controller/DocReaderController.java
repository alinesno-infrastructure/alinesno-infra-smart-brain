package com.alinesno.infra.smart.assistant.scene.scene.documentReader.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderSceneInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.AnalysisTool;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
@RequestMapping("/api/infra/smart/assistant/scene/documentReader")
public class DocReaderController extends SuperController {

    @Autowired
    private IDocReaderSceneService docReaderSceneService; ;

    @Autowired
    private ISceneService service;

//    @Autowired
//    private IDocReviewSceneService docReviewSceneService ;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private AnalysisTool analysisTool;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") long id) {

        Assert.isTrue(id > 0, "参数不能为空");

        SceneEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        LongTextSceneDto dto = new LongTextSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        DocReaderSceneInfoDto docSceneInfoDto = new DocReaderSceneInfoDto();
        BeanUtils.copyProperties(dto, docSceneInfoDto);

        // 查询出Entity信息
        LambdaQueryWrapper<DocReaderSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReaderSceneEntity::getSceneId, id);
        DocReaderSceneEntity docReviewSceneEntity = docReaderSceneService.getOne(wrapper);

        if(docReviewSceneEntity != null){
            docSceneInfoDto.setSummaryAgentEngineer(docReviewSceneEntity.getSummaryAgentEngineer());
            docSceneInfoDto.setSummaryAgentEngineerEntity(industryRoleService.getById(docReviewSceneEntity.getSummaryAgentEngineer()));

            docSceneInfoDto.setCaseQueryAgentEngineer(docReviewSceneEntity.getCaseQueryAgentEngineer());
            docSceneInfoDto.setCaseQueryAgentEngineerEntity(industryRoleService.getById(docReviewSceneEntity.getCaseQueryAgentEngineer()));

//            docSceneInfoDto.setContractType(docReviewSceneEntity.getContractType());
//            docSceneInfoDto.setReviewPosition(docReviewSceneEntity.getReviewPosition()) ;
//            docSceneInfoDto.setReviewListKnowledgeBase(docReviewSceneEntity.getReviewListKnowledgeBase());
//            docSceneInfoDto.setContractOverview(docReviewSceneEntity.getContractOverview());
//            docSceneInfoDto.setReviewList(docReviewSceneEntity.getReviewList());
//            docSceneInfoDto.setReviewListDtos(JSONArray.parseArray(docReviewSceneEntity.getReviewList(), DocReviewRulesDto.class));

            docSceneInfoDto.setDocumentId(docReviewSceneEntity.getDocumentId());
            docSceneInfoDto.setDocumentName(docReviewSceneEntity.getDocumentName());

        }

//        docSceneInfoDto.setContractTypes(ContractTypeEnum.getList());

        return AjaxResult.success("操作成功.", docSceneInfoDto);
    }

    @SneakyThrows
    @GetMapping("/getPreviewDocx")
    public ResponseEntity<Resource> getPreviewDocx(@RequestParam long sceneId) {

        DocReaderSceneEntity entity = docReaderSceneService.getBySceneId(sceneId);
        String previewUrl = storageConsumer.getPreviewUrl(entity.getDocumentId()).getData();

        log.debug("previewUrl= {}" , previewUrl);

        RestTemplate restTemplate = new RestTemplate();
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
     * 文件上传
     * @return
     */
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, long sceneId){

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

        DocReaderSceneEntity docReviewSceneEntity = docReaderSceneService.getBySceneId(sceneId) ;

        // 获取到文档的基础内容
        String content =  analysisTool.analysisDocumentBaseContent(targetFile) ;

//        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
//        taskInfo.setRoleId(docReviewSceneEntity.getAnalysisAgentEngineer());
//        taskInfo.setChannelId(sceneId);
//        taskInfo.setSceneId(sceneId);
//        taskInfo.setText(content);

        // 优先获取到结果内容
//        WorkflowExecutionDto genContent  = new WorkflowExecutionDto() ;
//        analysisTool.handleChapterMessage(genContent , taskInfo);

        // 更新Entity数据
//        docReviewSceneEntity.setContractOverview(genContent.getGenContent());

        docReviewSceneEntity.setDocumentId(r.getData());
        docReviewSceneEntity.setDocumentName(fileName);

        docReaderSceneService.update(docReviewSceneEntity);

        log.debug("ajaxResult= {}" , r);
        return AjaxResult.success("上传成功." , r.getData()) ;
    }

}