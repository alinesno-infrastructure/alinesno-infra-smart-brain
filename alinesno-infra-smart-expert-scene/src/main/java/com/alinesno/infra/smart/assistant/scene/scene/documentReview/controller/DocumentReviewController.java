package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.service.ISceneService;
import com.alinesno.infra.smart.assistant.scene.core.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocSceneInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.AnalysisTool;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import java.util.Objects;

/**
 * 文档审核场景控制
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/")
public class DocumentReviewController extends SuperController {

    @Autowired
    private ISceneService service;

    @Autowired
    private IDocReviewSceneService docReviewSceneService ;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private AnalysisTool analysisTool;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取合同类型列表
     */
    @GetMapping("/getContractType")
    public AjaxResult getContractType() {
        return AjaxResult.success("操作成功", ContractTypeEnum.getList());
    }

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

        DocSceneInfoDto docSceneInfoDto = new DocSceneInfoDto();
        BeanUtils.copyProperties(dto, docSceneInfoDto);

        // 查询出Entity信息
        LambdaQueryWrapper<DocReviewSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewSceneEntity::getSceneId, id);
        DocReviewSceneEntity docReviewSceneEntity = docReviewSceneService.getOne(wrapper);

        if(docReviewSceneEntity != null){
            docSceneInfoDto.setAnalysisAgentEngineer(docReviewSceneEntity.getAnalysisAgentEngineer());
            docSceneInfoDto.setAnalysisAgentEngineerEntity(industryRoleService.getById(docReviewSceneEntity.getAnalysisAgentEngineer()));

            docSceneInfoDto.setLogicReviewerEngineer(docReviewSceneEntity.getLogicReviewerEngineer());
            docSceneInfoDto.setLogicReviewerEngineerEntity(industryRoleService.getById(docReviewSceneEntity.getLogicReviewerEngineer()));

            docSceneInfoDto.setContractType(docReviewSceneEntity.getContractType());
            docSceneInfoDto.setReviewPosition(docReviewSceneEntity.getReviewPosition()) ;
            docSceneInfoDto.setReviewListKnowledgeBase(docReviewSceneEntity.getReviewListKnowledgeBase());
            docSceneInfoDto.setContractOverview(docReviewSceneEntity.getContractOverview());
            docSceneInfoDto.setDocumentId(docReviewSceneEntity.getDocumentId());

            docSceneInfoDto.setReviewList(docReviewSceneEntity.getReviewList());
            docSceneInfoDto.setDocumentName(docReviewSceneEntity.getDocumentName());
            docSceneInfoDto.setReviewListDtos(JSONArray.parseArray(docReviewSceneEntity.getReviewList(), DocReviewRulesDto.class));
        }

        docSceneInfoDto.setContractTypes(ContractTypeEnum.getList());

        return AjaxResult.success("操作成功.", docSceneInfoDto);
    }

    @GetMapping("/getPreviewDocx")
    public ResponseEntity<Resource> getPreviewDocx(@RequestParam long sceneId) {

        DocReviewSceneEntity entity = docReviewSceneService.getBySceneId(sceneId);
        String previewUrl = storageConsumer.getPreviewUrl(entity.getDocumentId()).getData();

        log.debug("previewUrl= {}" , previewUrl);

        RestTemplate restTemplate = new RestTemplate();
        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

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

        DocReviewSceneEntity docReviewSceneEntity = docReviewSceneService.getBySceneId(sceneId) ;

        // 获取到文档的基础内容
        String content =  analysisTool.analysisDocumentBaseContent(targetFile) ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
        taskInfo.setRoleId(docReviewSceneEntity.getAnalysisAgentEngineer());
        taskInfo.setChannelId(sceneId);
        taskInfo.setSceneId(sceneId);
        taskInfo.setText(content);

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = new WorkflowExecutionDto() ;
        analysisTool.handleChapterMessage(genContent , taskInfo);

        // 更新Entity数据
        docReviewSceneEntity.setContractOverview(genContent.getGenContent());
        docReviewSceneEntity.setDocumentId(r.getData());
        docReviewSceneEntity.setDocumentName(fileName);

        docReviewSceneService.update(docReviewSceneEntity);

        log.debug("ajaxResult= {}" , r);
        return AjaxResult.success("上传成功." , r.getData()) ;
    }

}

