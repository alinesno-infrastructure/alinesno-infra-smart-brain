package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeGroupDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamineeGroupService;
import com.alinesno.infra.smart.scene.entity.ExamineeGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考生分组控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/examineeGroup")
public class ExamineeGroupController extends BaseController<ExamineeGroupEntity, IExamineeGroupService> {

    @Autowired
    private IExamineeGroupService service;

    /**
     * 获取分组列表(DataTables)
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 获取考生分组列表
     */
    @DataPermissionQuery
    @PostMapping("/listExamineeGroup")
    public AjaxResult listExamineeGroup(@RequestParam Long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<ExamineeGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        query.toWrapper(queryWrapper);
        queryWrapper.eq(ExamineeGroupEntity::getSceneId, sceneId);
        queryWrapper.setEntityClass(ExamineeGroupEntity.class) ;

        long count = service.count(queryWrapper);
        if(count == 0){ // 如果为空，则添加默认分组
            ExamineeGroupEntity entity = new ExamineeGroupEntity();
            BeanUtils.copyProperties(query, entity);


            entity.setSceneId(sceneId);
            entity.setGroupName("默认分组");
            entity.setIcon("fa-solid fa-graduation-cap");
            entity.setFieldProp("default");

            service.save(entity);
        }

        List<ExamineeGroupEntity> list = service.list(queryWrapper);
        return AjaxResult.success(list);
    }

    /**
     * 添加考生分组
     */
    @DataPermissionSave
    @PostMapping("/addExamineeGroup")
    public AjaxResult addExamineeGroup(@RequestBody @Valid ExamineeGroupDTO dto) {
        ExamineeGroupEntity entity = new ExamineeGroupEntity();
        BeanUtils.copyProperties(dto, entity);
        service.save(entity);
        return AjaxResult.success("分组添加成功");
    }

    /**
     * 更新考生分组
     */
    @DataPermissionSave
    @PutMapping("/updateExamineeGroup")
    public AjaxResult updateExamineeGroup(@Validated @RequestBody ExamineeGroupDTO dto) {
        ExamineeGroupEntity entity = new ExamineeGroupEntity();
        BeanUtils.copyProperties(dto, entity);
        service.update(entity);
        return AjaxResult.success("分组更新成功");
    }

    /**
     * 删除考生分组
     */
    @DeleteMapping("/deleteExamineeGroup")
    public AjaxResult deleteExamineeGroup(@RequestParam Long id) {
        service.removeById(id);
        return AjaxResult.success("分组删除成功");
    }

    @Override
    public IExamineeGroupService getFeign() {
        return this.service;
    }
}