package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.common.service.ISceneService;
import com.alinesno.infra.smart.assistant.scene.core.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import com.alinesno.infra.smart.assistant.scene.core.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.scene.core.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.InitAgentsDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.ChapterEditorDto;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/longText/")
public class LongTextController extends SuperController {

    @Autowired
    private ISceneService service;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IChapterService chapterService;

    @Autowired
    private IIndustryRoleService roleService ;

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

        // 查询出当前的章节编辑人员
        dto.setChapterEditors(RoleUtils.getEditors(roleService , entity.getChapterEditor()));

        // 查询出当前的内容编辑人员
        dto.setContentEditors(RoleUtils.getEditors(roleService, entity.getContentEditor()));

        // 章节树信息
        dto.setChapterTree(chapterService.getChapterTree(entity.getId()));

        return AjaxResult.success("操作成功.", dto);
    }

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

        if(type.equals("chapter")){
            entity.setChapterEditor(editors);
        }else {
            entity.setContentEditor(editors);
        }

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

        SceneEntity entity = service.getById(id);
        entity.setContentEditor(editors);

        service.updateById(entity);
        return ok();
    }

    /**
     * 设置Agent的任务
     * @return
     */
    @PostMapping("/initAgents")
    public AjaxResult initAgents(@RequestBody @Validated InitAgentsDto dto){
        log.debug("dto = {}" , dto) ;

        chapterService.initAgents(dto) ;

        return AjaxResult.success("操作成功.") ;
    }

    /**
     * 更新用户编辑章节，即每个章节需要指定编辑人员
     */
    @PostMapping("/updateChapterContentEditor")
    public AjaxResult updateChapterContentEditor(@RequestBody @Validated ChatContentEditDto dto) {
        chapterService.updateChapterEditor(dto);
        return ok() ;
    }

    /**
     * 分配智能助手到每个章节内容
     * @return
     */
    @GetMapping("/dispatchAgent")
    public AjaxResult dispatchAgent(@RequestParam long sceneId) {

        SceneEntity entity = service.getById(sceneId) ;

        ChatContentEditDto dto = new ChatContentEditDto() ;
        dto.setSceneId(sceneId);
        dto.setRoleId(Long.parseLong(entity.getContentEditor()));

        List<ChapterEntity> chapters = chapterService.list(new LambdaQueryWrapper<ChapterEntity>().eq(ChapterEntity::getSceneId, sceneId)) ;
        List<Long> chapterIds = chapters.stream()
                .map(ChapterEntity::getId)
                .toList();

        List<String> chapterIdsStr = chapterIds.stream()
                .map(String::valueOf)
                .toList();

        dto.setChapters(chapterIdsStr);
        chapterService.updateChapterEditor(dto);

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
    @GetMapping("/uploadOss")
    public AjaxResult uploadOss(@RequestParam("sceneId") long sceneId) {

        String markdownContent = service.genMarkdownContent(sceneId) ;
        String filename = IdUtil.fastSimpleUUID() ;

        log.debug("markdownContent = {}", markdownContent);
        Assert.notNull(markdownContent, "markdownContent为空") ;

        String filePath = MarkdownToWord.convertMdToDocx(markdownContent, filename) ;
        Assert.notNull(filePath, "文件路径为空") ;

        R<String> r = storageConsumer.uploadCallbackUrl(new File(filePath), "qiniu-kodo-pub") ;

        String downloadUrl = r.getData() ;

        return AjaxResult.success("操作成功" , downloadUrl);
    }
}
