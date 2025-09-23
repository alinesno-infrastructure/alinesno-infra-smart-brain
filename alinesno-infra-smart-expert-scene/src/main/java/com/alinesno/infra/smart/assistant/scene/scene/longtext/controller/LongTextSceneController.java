package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.ChapterPromptContentRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
@RequestMapping("/api/infra/smart/assistant/scene/longText/")
public class LongTextSceneController {

    @Autowired
    private ISceneService service;

    @Autowired
    private ILongTextSceneService longTextSceneService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IChapterService chapterService;

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") Long id ,
                               Long taskId ,
                               PermissionQuery query) {

        Assert.isTrue(id > 0, "参数不能为空");

        SceneEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        LongTextSceneDto dto = new LongTextSceneDto();
        BeanUtils.copyProperties(entity, dto);

        if(StringUtils.isNotEmpty(entity.getGreetingQuestion())){
            try{
                dto.setGreetingQuestion(JSONArray.parseArray(entity.getGreetingQuestion(), String.class));
            }catch (Exception e){
                dto.setGreetingQuestion(new ArrayList<>());
            }
        }

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(id, query);
        if(longTextSceneEntity != null){

            dto.setChapterEditor(longTextSceneEntity.getChapterEditor());
            dto.setContentEditor(longTextSceneEntity.getContentEditor());
            dto.setGenStatus(longTextSceneEntity.getGenStatus());
            dto.setChapterPromptContent(longTextSceneEntity.getChapterPromptContent());

            dto.setChapterEditors(RoleUtils.getEditors(roleService , longTextSceneEntity.getChapterEditor())); // 查询出当前的章节编辑人员
            dto.setContentEditors(RoleUtils.getEditors(roleService, longTextSceneEntity.getContentEditor())); // 查询出当前的内容编辑人员

            if(taskId != null){
                dto.setChapterTree(chapterService.getChapterTree(entity.getId() , longTextSceneEntity.getId() , taskId)); // 章节树信息
            }
        }

        return AjaxResult.success("操作成功.", dto);
    }

    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterPromptContent")
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated ChapterPromptContentRequestDto dto , PermissionQuery query) {

        LongTextSceneEntity sceneEntity = longTextSceneService.getBySceneId(dto.getSceneId() , query) ;
        Assert.notNull(sceneEntity, "未找到对应的场景实体");
        sceneEntity.setChapterPromptContent(dto.getPromptContent());

        longTextSceneService.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
    }


}