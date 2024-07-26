package com.alinesno.infra.smart.assistant.gateway.provider;

import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.HttpStatus;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.IndustryRoleCatalogDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个助手控制器类
 * 该类用于提供助手功能的接口
 * 作者：luoxiaodong
 * 版本：1.0.0
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/agent")
public class AssistantController extends SuperController {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IIndustryRoleCatalogService catalogService;

    /**
     * 获取到所有的agent
     *
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo agentList(int pageNow , int pageSize , String agentName){

        TableDataInfo tableDataInfo = new TableDataInfo() ;

        Page<IndustryRoleEntity> page = new Page<>(pageNow , pageSize) ;
        LambdaQueryWrapper<IndustryRoleEntity> queryWrapper = new LambdaQueryWrapper<>() ;

        if(StringUtils.isNotBlank(agentName)){
            queryWrapper.like(IndustryRoleEntity::getRoleName , agentName) ;
        }

        page = roleService.page(page , queryWrapper) ;

        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setRows(page.getRecords());
        tableDataInfo.setTotal(page.getTotal());

        return tableDataInfo ;
    }

    /**
     * 根据id获取到agent的列表
     * @return
     */
    @PostMapping("/listByIds")
    public List<IndustryRoleEntity> agentListByIds(@RequestBody List<Long> ids){
        log.debug("listByIds = {}" , ids);
        if(ids.isEmpty()){
            return new ArrayList<IndustryRoleEntity>() ;
        }
        return roleService.listByIds(ids) ;
    }

    /**
     * 查询Agent详情
     * @return
     */
    @GetMapping("/getById")
    public IndustryRoleEntity getById(String roleId){
        return roleService.getById(roleId);
    }

    /**
     * 查询角色与类型
     *
     * @return
     */
    @GetMapping("/allCatalog")
    public List<IndustryRoleCatalogDto> allCatalog(){
        return catalogService.allCatalog() ;
    }
}
