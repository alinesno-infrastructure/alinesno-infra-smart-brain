package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.dto.PrototypeChapterDto;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service.IPrototypeSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.PrototypeSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.alinesno.infra.smart.utils.RoleUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/infra/smart/assistant/scene/prototypeScene")
public class PrototypeSceneController extends BaseController<PrototypeSceneEntity, IPrototypeSceneService> {

    @Autowired
    private IPrototypeSceneService service;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IIndustryRoleService roleService ;

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
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(Long id , PermissionQuery query) {

        SceneEntity entity = sceneService.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        PrototypeChapterDto dto = new PrototypeChapterDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        PrototypeSceneEntity examPagerSceneEntity = service.getBySceneId(id, query);
        if(examPagerSceneEntity != null){

            dto.setRequirementAnalyzer(examPagerSceneEntity.getRequirementAnalyzer());
            dto.setRequirementAnalyzerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getRequirementAnalyzer())));

            dto.setPrototypeDesigner(examPagerSceneEntity.getPrototypeDesigner());
            dto.setPrototypeDesignerEntity(RoleUtils.getEditors(roleService , String.valueOf(examPagerSceneEntity.getPrototypeDesigner())));

        }

        return AjaxResult.success("操作成功.", dto);
    }

    @Override
    public IPrototypeSceneService getFeign() {
        return this.service;
    }
}