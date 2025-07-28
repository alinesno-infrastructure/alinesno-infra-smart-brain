package com.alinesno.infra.smart.assistant.scene.common.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectKnowledgeGroupDto;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/infra/smart/assistant/scene/projectKnowledgeGroup")
public class ProjectKnowledgeGroupController extends BaseController<ProjectKnowledgeGroupEntity, IProjectKnowledgeGroupService> {

    @Autowired
    private IProjectKnowledgeGroupService service;

    @Autowired
    private IProjectKnowledgeService projectKnowledgeService  ;

    /**
     * 添加分组
     * @return
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdateRuleGroup")
    public AjaxResult saveOrUpdateRuleGroup(@RequestBody @Validated ProjectKnowledgeGroupDto dto) {

        ProjectKnowledgeGroupEntity entity = new ProjectKnowledgeGroupEntity();

        // 如果id不为空
        if(entity.getId() != null){
            entity = service.getById(entity.getId());
            entity.setGroupName(dto.getGroupName());
            entity.setGroupType(dto.getGroupType());
        }else{
            BeanUtil.copyProperties(dto , entity);
        }

        if(StringUtils.isEmpty(entity.getVectorDatasetName())){
            entity.setVectorDatasetName(IdUtil.nanoId(10));
        }

        service.saveOrUpdate(entity);
        return ok();
    }

    /**
     * 查询所有分组
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listGroup")
    public AjaxResult listGroup(PermissionQuery query) {
        List<ProjectKnowledgeGroupDto> list = service.listGroup(query) ;
        return AjaxResult.success("操作成功", list)  ;
    }

    /**
     * 删除分组
     * @return
     */
    @DeleteMapping("/deleteGroup")
    public AjaxResult deleteGroup(@RequestParam Long id) {
        service.removeGroup(id);
        return ok();
    }

    /**
     * 获取所有文章分类
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllArticleTemplate")
    public AjaxResult getAllArticleTemplate(PermissionQuery query , @RequestParam(value = "sceneId") Long sceneId) {

       // 获取到所有的分类
        LambdaQueryWrapper<ProjectKnowledgeGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ProjectKnowledgeGroupEntity.class);
        query.toWrapper(queryWrapper);
        queryWrapper.eq(ProjectKnowledgeGroupEntity::getSceneId, sceneId);

       List<ProjectKnowledgeGroupEntity> list = service.list(queryWrapper) ;
       return AjaxResult.success(list);
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/getTemplateDetail")
    public AjaxResult getTemplateDetail(@RequestParam(value = "templateId") Long templateId) {
        return AjaxResult.success(service.getById(templateId));
    }

    /**
     * 获取所有的模板
     *
     * @param query
     * @return
     * @throws Exception
     */
    @DataPermissionQuery
    @GetMapping("/getTemplateByType")
    public AjaxResult getTemplateByType(PermissionQuery query ,
                                        @RequestParam(value = "groupId") String groupId ,
                                        @RequestParam(value = "sceneId") Long sceneId) throws Exception {

        LambdaQueryWrapper<ProjectKnowledgeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ProjectKnowledgeEntity.class);
        query.toWrapper(queryWrapper);
        queryWrapper.eq(ProjectKnowledgeEntity::getSceneId, sceneId);

        if(StringUtils.isNotBlank(groupId)){
            queryWrapper.eq(ProjectKnowledgeEntity::getGroupId, groupId);
        }

        List<ProjectKnowledgeEntity> list = projectKnowledgeService.list(queryWrapper) ;

        // list里面的docContent字段截取50个字符
        list.forEach(item -> {
            item.setDocContent(StringUtils.substring(item.getDocContent(), 0, 50));
        });

        return AjaxResult.success(list);
    }

    @Override
    public IProjectKnowledgeGroupService getFeign() {
        return this.service;
    }

}    