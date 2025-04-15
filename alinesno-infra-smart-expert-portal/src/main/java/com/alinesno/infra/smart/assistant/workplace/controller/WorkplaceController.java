package com.alinesno.infra.smart.assistant.workplace.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceAddDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceItemDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceSourceEnums;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceItemService;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/infra/smart/assistant/workplace")
public class WorkplaceController extends BaseController<WorkplaceEntity, IWorkplaceService> {

    @Autowired
    private IWorkplaceService service;

    @Autowired
    private IWorkplaceItemService workplaceItemService;

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

        TableDataInfo tableDataInfo =  this.toPage(model, this.getFeign(), page);

        List<?> list =  tableDataInfo.getRows() ;
        if(CollectionUtil.isNotEmpty(list)){
            List<WorkplaceResponseDto> dtos = CollectionUtil.newArrayList() ;
            List<WorkplaceEntity> entities = (List<WorkplaceEntity>) list ;

            for (WorkplaceEntity entity : entities) {
                WorkplaceResponseDto dto = new WorkplaceResponseDto() ;
                BeanUtils.copyProperties(entity , dto) ;

                WorkplaceResponseDto workplaceItemDto = workplaceItemService.getWorkplaceItem(entity.getId()) ;
                dto.setAgentsList(workplaceItemDto.getAgentsList());
                dto.setChannelsList(workplaceItemDto.getChannelsList());
                dto.setScenesList(workplaceItemDto.getScenesList());

                dtos.add(dto) ;
            }

            tableDataInfo.setRows(dtos) ;
        }

        return tableDataInfo;
    }

    @ResponseBody
    @DeleteMapping("deleteWorkplace/{ids}")
    public AjaxResult deleteWorkplace(@PathVariable String ids) {
        service.deleteWorkplace(ids);
        return AjaxResult.success("删除成功");
    }

    /**
     * createChannel
     * @return
     */
    @DataPermissionSave
    @PostMapping("/createWorkplace")
    public AjaxResult createWorkplace(@RequestBody @Validated WorkplaceAddDto dto){

//        Assert.isTrue(WorkplaceSourceEnums.isValidate(dto.getWorkplaceType()) , "工作区类型错误");

        dto.setWorkplaceSource(WorkplaceSourceEnums.BACKEND.getCode());
        Long workplaceId = service.createWorkplace(dto) ;

        return AjaxResult.success("操作成功" , workplaceId) ;
    }

    /**
     * 更新工作区Item信息
     * @param dto
     * @return
     */
    @DataPermissionSave
    @PostMapping("/updateWorkplaceItem")
    public AjaxResult updateWorkplaceItem(@RequestBody @Validated WorkplaceItemDto dto){
        return AjaxResult.success("操作成功" , workplaceItemService.updateWorkplaceItem(dto)) ;
    }


    @Override
    public IWorkplaceService getFeign() {
        return this.service;
    }
}

