package com.alinesno.infra.base.im.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.ChannelAgentDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.alinesno.infra.smart.im.service.IChannelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@RequestMapping("/api/infra/base/im/channel")
public class ChannelController extends BaseController<ChannelEntity, IChannelService> {

    @Autowired
    private IChannelService service;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IChannelRoleService channelRoleService ;

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


        TableDataInfo tableDataInfo = this.toPage(model, this.getFeign(), page);

        List<ChannelEntity> channelEntities = (List<ChannelEntity>) tableDataInfo.getRows();
        List<JSONObject> rowsJsonList = new ArrayList<>() ;

        if(channelEntities != null){
            for (ChannelEntity channelEntity : channelEntities) {
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(channelEntity)) ;

                // 查询出频道的角色
                List<IndustryRoleEntity> roles = channelRoleService.getChannelAgent(channelEntity.getId()+"") ;
                List<Long> roleIds = roles.stream().map(IndustryRoleEntity::getId).toList() ;

                jsonObject.put("roles", roleIds);

                rowsJsonList.add(jsonObject);
            }

            tableDataInfo.setRows(rowsJsonList);
        }

        return tableDataInfo;
    }

    /**
     * 更新频道角色 updateChannelAgent
     */
    @PostMapping("/updateChannelAgent")
    public AjaxResult updateChannelAgent(@RequestBody ChannelAgentDto dto){
        log.debug("dto = {}" , dto);

        channelRoleService.updateChannelAgent(dto) ;

        return AjaxResult.success() ;
    }


    /**
     * createChannel
     * @return
     */
    @DataPermissionSave
    @PostMapping("/createChannel")
    public AjaxResult createChannel(@RequestBody ChannelEntity entity){

       log.debug("entity = {}" , JSONObject.toJSONString(entity));

        String channelId = service.createChannel(entity) ;

        return AjaxResult.success("操作成功" , channelId) ;
    }

    /**
     * 逻辑删除聊天窗口
     * @return
     */
    @DeleteMapping("/removeChannel")
    public AjaxResult removeChannel(Long channelId){

        service.removeChannel(channelId) ;

        return AjaxResult.success() ;
    }

    /**
     * 获取到用户默认的频道
     * @return
     */
    @GetMapping("/getDefaultChannelId")
    public AjaxResult getDefaultChannelId(){

        long userId = CurrentAccountJwt.getUserId() ;
        Long channelId = service.getDefaultChannelId(userId) ;

        return AjaxResult.success("操作成功." , channelId) ;
    }

    /**
     * 查询出推荐频道
     * @return
     */
    @GetMapping("/getRecommendChannel")
    public AjaxResult getRecommendChannel(){
        List<ChannelEntity> channelEntities = service.getRecommendChannel() ;
        return AjaxResult.success(channelEntities) ;
    }

    /**
     * 查询出我所有的渠道
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/allMyChannel")
    public AjaxResult allMyChannel(PermissionQuery query){

        long orgId = CurrentAccountJwt.get().getOrgId() ;
        service.initOrgChannel(orgId);

        List<ChannelEntity> channelEntities = service.allMyChannel(query) ;

        // 查询当前组织推荐角色
        IndustryRoleEntity industryRoleEntity = roleService.getRecommendRole(orgId) ;

        AjaxResult result = AjaxResult.success(channelEntities) ;
        result.put("recommendRole", industryRoleEntity) ;

        return result ;
    }

    /**
     * 加入频道
     * @param channelId
     * @return
     */
    @GetMapping("/joinChannel")
    public AjaxResult joinChannel(long channelId){

        long accountId = 1L ;
        service.accountJoinChannel(accountId, channelId) ;

        return AjaxResult.success() ;
    }

    /**
     * 查看所有的公开频道
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/allPublicChannel")
    public AjaxResult allPublicChannel(PermissionQuery query){

        List<ChannelEntity> channelEntities = service.allPublicChannel(query) ;
        return AjaxResult.success(channelEntities) ;
    }

    @Override
    public IChannelService getFeign() {
        return this.service;
    }
}

