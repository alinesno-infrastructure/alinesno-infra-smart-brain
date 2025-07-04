package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTemplateGroupService;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTemplateGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/api/infra/smart/assistant/scene/generalAgentTemplateGroup")
public class GeneralAgentTemplateGroupController extends BaseController<GeneralAgentTemplateGroupEntity, IGeneralAgentTemplateGroupService> {

    @Autowired
    private IGeneralAgentTemplateGroupService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
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
     * 获取所有文章分类
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllLongTextTemplateGroup")
    public AjaxResult getAllLongTextTemplate(PermissionQuery query) {

        LambdaQueryWrapper<GeneralAgentTemplateGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(GeneralAgentTemplateGroupEntity.class);
        query.toWrapper(queryWrapper);
        queryWrapper.eq(GeneralAgentTemplateGroupEntity::getHasDelete, 0);

        List<GeneralAgentTemplateGroupEntity> allLongTextTemplateGroup = service.list(queryWrapper);

        return AjaxResult.success(allLongTextTemplateGroup) ;
    }

    /**
     * 保存模板 saveLongTextTemplateGroup
     * @return
     */
    @DataPermissionSave
    @PostMapping("/saveLongTextTemplateGroup")
    public AjaxResult saveLongTextTemplateGroup(@RequestBody GeneralAgentTemplateGroupEntity entity) {
        boolean save = service.save(entity);
        return save ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 更新模板
     * @return
     */
    @DataPermissionSave
    @PutMapping("/updateLongTextTemplateGroup")
    public AjaxResult updateLongTextTemplateGroup(@RequestBody GeneralAgentTemplateGroupEntity entity) {
        boolean update = service.updateById(entity);
        return update ? AjaxResult.success() : AjaxResult.error();
    }


    @Override
    public IGeneralAgentTemplateGroupService getFeign() {
        return this.service;
    }
}