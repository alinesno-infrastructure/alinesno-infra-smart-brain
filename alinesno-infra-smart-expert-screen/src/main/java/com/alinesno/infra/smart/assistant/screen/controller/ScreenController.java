package com.alinesno.infra.smart.assistant.screen.controller;

import cn.hutool.core.lang.Assert;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.service.IChapterService;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
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
@RequestMapping("/api/infra/smart/assistant/screen")
public class ScreenController extends BaseController<ScreenEntity, IScreenService> {

    @Autowired
    private IScreenService service;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IChapterService chapterService;

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
     * 保存或更新屏幕场景
     *
     * @param screenDto 屏幕场景的DTO
     * @return 操作结果
     */
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody @Validated ScreenDto screenDto) {
        log.debug("screenDto = {}", ToStringBuilder.reflectionToString(screenDto));



        ScreenEntity screenEntity =service.saveScreen(screenDto);

        return AjaxResult.success("操作成功.", screenEntity.getId());
    }

    /**
     * 更新章节编辑人员
     *
     * @return
     */
    @PostMapping("/updateChapterEditor")
    public AjaxResult updateChapterEditor(@RequestParam("id") long id,
                                          @RequestParam("editors") String editors ,
                                          @RequestParam("type") String type
                                          ) {

        log.debug("id = {}, editors = {}, type = {}", id, editors, type);

        // 添加为空判断处理
        if (editors == null || editors.isEmpty()) {
            return error("参数不能为空");
        }

        // 处理type类型
        Assert.isTrue(type.equals("chapter") || type.equals("content"), "参数类型不正确");

        ScreenEntity entity = service.getById(id);

        if(type.equals("chapter")){
            entity.setChapterEditor(editors);
        }else if(type.equals("content")){
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

        ScreenEntity entity = service.getById(id);
        entity.setContentEditor(editors);

        service.updateById(entity);
        return ok();
    }

    /**
     * 通过Id获取到场景
     *
     * @return
     */
    @GetMapping("/getScreen")
    public AjaxResult getScreen(@RequestParam("id") long id) {

        Assert.isTrue(id > 0, "参数不能为空");

        ScreenEntity entity = service.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的屏幕实体");
        }

        ScreenDto dto = new ScreenDto();
        BeanUtils.copyProperties(entity, dto);


        // 查询出当前的章节编辑人员
        dto.setChapterEditors(getEditors(entity.getChapterEditor()));

        // 查询出当前的内容编辑人员
        dto.setContentEditors(getEditors(entity.getContentEditor()));

        // 章节树信息
        dto.setChapterTree(chapterService.getChapterTree(entity.getId()));

        return AjaxResult.success("操作成功.", dto);
    }

    // 查询编辑人员的公共方法
    List<IndustryRoleDto> getEditors(String editorString) {
        if (editorString == null) {
            return Collections.emptyList();
        }
        try {
            String[] split = editorString.split(",");
            List<Long> ids = Arrays.stream(split).map(s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("无效的编辑人员ID: " + s, e);
                }
            }).toList();
            List<IndustryRoleEntity> roles = roleService.listByIds(ids);

            return roles.stream().map(role -> {
                IndustryRoleDto dto1 = new IndustryRoleDto();
                BeanUtils.copyProperties(role, dto1);
                return dto1;
            }).toList();
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }


    @Override
    public IScreenService getFeign() {
        return this.service;
    }
}