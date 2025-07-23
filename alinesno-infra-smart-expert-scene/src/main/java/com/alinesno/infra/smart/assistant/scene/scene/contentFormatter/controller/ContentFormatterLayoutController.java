package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.ResultCodeEnum;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterLayoutDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentTemplateDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentTemplateInfoDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分析规划控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterLayout/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterLayoutController extends BaseController<ContentFormatterLayoutEntity, IContentFormatterLayoutService> {

    @Autowired
    private IContentFormatterLayoutService service;

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

    @ApiOperation("查询排版模板列表")
    @GetMapping("/listLayoutTemplates")
    public AjaxResult listLayoutTemplates(ContentFormatterLayoutDto query) {
        log.debug("查询排版模板列表，参数：{}" , query);
        List<ContentFormatterLayoutEntity> list = service.list();
        return AjaxResult.success(list);
    }

    @ApiOperation("查询排版模板详情")
    @DataPermissionQuery
    @GetMapping("/getLayoutTemplate")
    public AjaxResult getLayoutTemplate(@RequestParam Long id) {
        ContentFormatterLayoutEntity entity = service.findById(id);
        ContentFormatterLayoutDto dto = new ContentFormatterLayoutDto() ;
        BeanUtils.copyProperties(entity, dto);

        DocumentTemplateDTO templateDTO = DocumentTemplateDTO.fromContentFormatterLayoutDto(dto);

        return  AjaxResult.success(templateDTO) ;
    }

    @ApiOperation("新增排版模板")
    @DataPermissionSave
    @PostMapping("/addLayoutTemplate")
    public AjaxResult addLayoutTemplate(@RequestBody @Valid DocumentTemplateDTO dto) {

        ContentFormatterLayoutDto layoutDto = dto.toContentFormatterLayoutDto() ;
        ContentFormatterLayoutEntity entity = new ContentFormatterLayoutEntity();
        BeanUtils.copyProperties(layoutDto, entity);

        service.save(entity);
        dto.setId(entity.getId());

        return AjaxResult.success(dto);
    }

    @ApiOperation("修改排版模板")
    @DataPermissionSave
    @PutMapping("/updateLayoutTemplate")
    public AjaxResult updateLayoutTemplate(@RequestBody @Valid DocumentTemplateDTO dto) {

        ContentFormatterLayoutDto layoutDto = dto.toContentFormatterLayoutDto() ;
        ContentFormatterLayoutEntity entity = service.getById(dto.getId()) ;
        BeanUtils.copyProperties(layoutDto, entity);

        service.update(entity);

        return AjaxResult.success();
    }

    @ApiOperation("修改排版模板")
    @DataPermissionSave
    @PutMapping("/updateLayoutInfoTemplate")
    public AjaxResult updateLayoutInfoTemplate(@RequestBody @Valid DocumentTemplateInfoDTO dto) {

        ContentFormatterLayoutEntity entity = service.getById(dto.getId()) ;
        entity.setName(dto.getName());
        entity.setLayoutDesc(dto.getDescription());

        service.update(entity);

        return AjaxResult.success();
    }

    @ApiOperation("删除排版模板")
    @DeleteMapping("/delLayoutTemplate")
    public AjaxResult delLayoutTemplate(@RequestParam Long id) {
        service.removeById(id);
        return AjaxResult.success();
    }

    @ApiOperation("预览排版模板")
    @GetMapping("/previewLayoutTemplate")
    public AjaxResult previewLayoutTemplate(@RequestParam Long id) {
        ContentFormatterLayoutEntity entity = service.findById(id);
        if (entity == null) {
            return AjaxResult.error("无法找到资源" , ResultCodeEnum.NOT_FOUND);
        }
        // Here you would typically add preview logic
        return AjaxResult.success(entity.getLayoutConfig());
    }

    @Override
    public IContentFormatterLayoutService getFeign() {
        return this.service;
    }
}