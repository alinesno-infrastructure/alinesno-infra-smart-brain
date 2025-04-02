package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.scene.common.service.ISceneService;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import com.alinesno.infra.smart.assistant.scene.core.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.ChapterEditorDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/longText/")
public class LongTextController extends SuperController {

    @Autowired
    private ISceneService service;

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

}
