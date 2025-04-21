package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.common.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.scene.common.utils.WordToPdfConverter;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.ChapterEditorDto;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/longText/")
public class LongTextController extends SuperController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ISceneService service;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IChapterService chapterService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILongTextSceneService longTextSceneService ;

    /**
     * 更新章节编辑人员
     *
     * @return
     */
    @PostMapping("/updateChapterEditor")
    public AjaxResult updateChapterEditor(@RequestBody @Validated ChapterEditorDto dto) {

        long id = dto.getId();
        String editors = dto.getEditorsAsString() ;
        String type = dto.getType();

        log.debug("id = {}, editors = {}, type = {}", id, editors, type);

        // 添加为空判断处理
        if (editors == null || editors.isEmpty()) {
            return error("参数不能为空");
        }

        // 处理type类型
        Assert.isTrue(type.equals("chapter") || type.equals("content"), "参数类型不正确");

        SceneEntity entity = service.getById(id);

//        if(type.equals("chapter")){
//            entity.setChapterEditor(editors);
//        }else {
//            entity.setContentEditor(editors);
//        }

        service.updateById(entity);
        return ok();
    }

    /**
     * 更新章节内容编辑人员
     *
     * @return
     */
    @PostMapping("/updateContentEditor")
    public AjaxResult updateContentEditor(@RequestParam("id") long id, @RequestParam("editors") String editors) {

        // 添加为空判断处理
        if (editors == null || editors.isEmpty()) {
            return error("参数不能为空");
        }

//        SceneEntity entity = service.getById(id);
//        entity.setContentEditor(editors);
//
//        service.updateById(entity);
        return ok();
    }

//    /**
//     * 设置Agent的任务
//     * @return
//     */
//    @PostMapping("/initAgents")
//    public AjaxResult initAgents(@RequestBody @Validated InitAgentsDto dto){
//        log.debug("dto = {}" , dto) ;
//
//        chapterService.initAgents(dto) ;
//
//        return AjaxResult.success("操作成功.") ;
//    }

    /**
     * 更新用户编辑章节，即每个章节需要指定编辑人员
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterContentEditor")
    public AjaxResult updateChapterContentEditor(@RequestBody @Validated ChatContentEditDto dto , PermissionQuery query) {
        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(dto.getSceneId() , query) ;
        chapterService.updateChapterEditor(dto , longTextSceneEntity.getId());
        return ok() ;
    }

    /**
     * 分配智能助手到每个章节内容
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/dispatchAgent")
    public AjaxResult dispatchAgent(@RequestParam long sceneId , PermissionQuery query) {

        SceneEntity entity = service.getById(sceneId) ;
        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;

        ChatContentEditDto dto = new ChatContentEditDto() ;
        dto.setSceneId(sceneId);
        dto.setRoleId(Long.parseLong(longTextSceneEntity.getContentEditor().split(",")[0]));

        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper =  new LambdaQueryWrapper<ChapterEntity>()
                .eq(ChapterEntity::getSceneId, sceneId)
                .eq(ChapterEntity::getLongTextSceneId, longTextSceneEntity.getId());

        List<ChapterEntity> chapters = chapterService.list(lambdaQueryWrapper) ;
        List<Long> chapterIds = chapters.stream()
                .map(ChapterEntity::getId)
                .toList();

        List<String> chapterIdsStr = chapterIds.stream()
                .map(String::valueOf)
                .toList();

        dto.setChapters(chapterIdsStr);
        chapterService.updateChapterEditor(dto, longTextSceneEntity.getId());

        SceneDto sceneEntity = new SceneDto();
        BeanUtils.copyProperties(entity, sceneEntity);

        AjaxResult result = AjaxResult.success("操作成功");
        result.put("sceneInfo", sceneEntity) ;

        return result ;
    }


    /**
     * 下载文件并返回文件下载
     * @param sceneId
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/uploadOss")
    public AjaxResult uploadOss(@RequestParam("sceneId") long sceneId , PermissionQuery query) {

        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;
        String markdownContent = service.genMarkdownContent(sceneId ,query , longTextSceneEntity.getId()) ;
        String filename = IdUtil.fastSimpleUUID() ;

        log.debug("markdownContent = {}", markdownContent);
        Assert.notNull(markdownContent, "markdownContent为空") ;

        String filePath = MarkdownToWord.convertMdToDocx(markdownContent, filename) ;
        Assert.notNull(filePath, "文件路径为空") ;

//        R<String> r = storageConsumer.uploadCallbackUrl(new File(filePath), "qiniu-kodo-pub") ;
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
    @DataPermissionQuery
    @GetMapping("/getPreviewDocx")
    public ResponseEntity<Resource> getPreviewDocx(@RequestParam String storageId) {

        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
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
}
